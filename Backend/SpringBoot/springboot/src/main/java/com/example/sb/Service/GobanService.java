package com.example.sb.Service;

import com.example.sb.Controller.ChatController;
import com.example.sb.Model.*;
import com.example.sb.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GobanService {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GobanRepository gobanRepository;
    @Autowired
    private LobbyRepository lobbyRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TheProfileRepository theProfileRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TheProfileService theProfileService;

    private static final int[] DIR_X = {-1, 1, 0, 0};
    private static final int[] DIR_Y = {0, 0, -1, 1};

    private static final int BOARD_SIZE = 9;
    private enum PointState { EMPTY, BLACK, WHITE, NEUTRAL }

    public Map<PointState, Integer> countTerritories(Stone[][] board) {
        boolean[][] visited = new boolean[BOARD_SIZE][BOARD_SIZE];
        Map<PointState, Integer> territoryCount = new HashMap<>();
        territoryCount.put(PointState.BLACK, 0);
        territoryCount.put(PointState.WHITE, 0);
        territoryCount.put(PointState.NEUTRAL, 0);

        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (!visited[x][y] && board[x][y].getStoneType().equals("X")) {
                    PointState owner = floodFill(x, y, visited, board);
                    System.out.println(owner.name() + "captured at " +  "(" + x + "," + y + ")");
                    territoryCount.put(owner, territoryCount.get(owner) + 1);
                }
            }
        }

        return territoryCount;
    }

    private PointState floodFill(int x, int y, boolean[][] visited, Stone[][] board) {
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{x, y});
        visited[x][y] = true;

        Set<Integer> borderingColors = new HashSet<>();
        boolean isNeutral = true;  // Default assumption: the region is neutral

        while (!queue.isEmpty()) {
            int[] point = queue.poll();
            int px = point[0], py = point[1];

            for (int[] dir : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
                int nx = px + dir[0], ny = py + dir[1];

                if (nx >= 0 && ny >= 0 && nx < BOARD_SIZE && ny < BOARD_SIZE) {
                    if (board[nx][ny].getStoneType().equals("X") && !visited[nx][ny]) {
                        // Continue flood-fill for empty spots
                        visited[nx][ny] = true;
                        queue.add(new int[]{nx, ny});
                    }
                    else if (!board[nx][ny].getStoneType().equals("X")) {
                        // Track bordering colors (Black or White stones)
                        String stoneType = board[nx][ny].getStoneType();
                        if (stoneType.equals("B")) {
                            borderingColors.add(1); // Black stone
                        }
                        else if (stoneType.equals("W")) {
                            borderingColors.add(2); // White stone
                        }
                    }
                }
            }
        }

        // Determine ownership based on the bordering colors
        if (borderingColors.isEmpty()) {
            return PointState.NEUTRAL;
        }

        // If the region is bordered only by black stones
        if (borderingColors.size() == 1) {
            return borderingColors.contains(1) ? PointState.BLACK : PointState.WHITE;
        }

        // If the region is bordered by both black and white stones, it's neutral
        return PointState.NEUTRAL;
    }

    public String calculateScores(Stone[][] board, Team team1, Team team2) {
        double blackScore;
        double whiteScore;

        // Count the territories
        Map<PointState, Integer> territories = countTerritories(board);
        int blackTerritory = territories.getOrDefault(PointState.BLACK, 0);
        int whiteTerritory = territories.getOrDefault(PointState.WHITE, 0);

        // Assuming team scores are the number of captured stones (prisoners)
        double blackPrisoners = team1.getTeamScore(); // Assuming method that returns black prisoners count
        double whitePrisoners = team2.getTeamScore(); // Assuming method that returns white prisoners count

        // Determine the scores based on which team is black
        if (team1.getIsBlack()) {
            // Team1 is Black
            whiteScore = whiteTerritory + whitePrisoners;
            blackScore = blackTerritory + blackPrisoners;
        }
        else {
            // Team2 is Black
            whiteScore = whiteTerritory + whitePrisoners;
            blackScore = blackTerritory + blackPrisoners;
        }

        // Output the scores for debugging
        System.out.println("Black territory: " + blackTerritory + ", Black prisoners: " + blackPrisoners + ", Black score: " + blackScore);
        System.out.println("White territory: " + whiteTerritory + ", White prisoners: " + whitePrisoners + ", White score: " + whiteScore);

        // Determine the winner
        if (blackScore > whiteScore) {
            return "Black wins by " + (blackScore - whiteScore) + " points.";
        } else {
            return "White wins by " + (whiteScore - blackScore) + " points.";
        }
    }

    public String getBoardState(Integer lobbyId) {
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        if (lobbyOptional.isPresent()) {
            Lobby lobby = lobbyOptional.get();
            if (!lobby.getIsGameInitialized()) {
                return null;
            }
            Goban board = lobby.getGoban();

            return board.getBoardState();
        }
        return null;
    }

    public void endGame(Integer lobbyId, boolean forfeit) {
        String xWinsBy = "";
        String bonusMessage = "";
        String teamWinner = "";
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        boolean isTeam1Winner = false;

        Lobby lobby = lobbyOptional.get();
        Team team1 = lobby.getTeam1();
        Team team2 = lobby.getTeam2();
        Goban goban = lobby.getGoban();

        // Determine winner if some player abandoned
        if (team1.getPlayer1().getHasAbandoned() || team1.getPlayer2().getHasAbandoned()) {
            // Team 1 has a missing player; Team 2 wins
            String winningTeamName = team2.getTeamName();
            String winningPlayers = team2.getPlayer1().getUsername() + "-|-" + team2.getPlayer2().getUsername();
            teamWinner = winningTeamName + " (" + winningPlayers + ") wins the game due to teammate abandonment by " + team1.getTeamName();
        }
        else if (team2.getPlayer1().getHasAbandoned() || team2.getPlayer2().getHasAbandoned()) {
            // Team 2 has a missing player; Team 1 wins
            isTeam1Winner = true;
            String winningTeamName = team1.getTeamName();
            String winningPlayers = team1.getPlayer1().getUsername() + "-|-" + team1.getPlayer2().getUsername();
            teamWinner = winningTeamName + " (" + winningPlayers + ") wins the game due to teammate abandonment by " + team2.getTeamName();
        }
        else {
            goban.loadMatrixFromBoardString();
            Stone[][] board = goban.getBoard();
            xWinsBy = calculateScores(board, team1, team2);
            xWinsBy = xWinsBy + "\n";
            bonusMessage = xWinsBy + "Final scores are, " + team1.getTeamName() + ": " + team1.getTeamScore() +
                    " and " + team2.getTeamName() + ": " + team2.getTeamScore();
        }

        // Update profiles
        updateTeamProfiles(team1, isTeam1Winner, team2);
        updateTeamProfiles(team2, !isTeam1Winner, team1);
        saveAllProfiles(team1, team2);

        if (forfeit) {
            for (Player currentPlayer: lobby.getPlayersInLobby()){
                if (currentPlayer.getHasAbandoned()) {
                    Team currentTeam = currentPlayer.getTeam();
                    if (currentTeam.getPlayer1() != null && currentTeam.getPlayer1().equals(currentPlayer)) {
                        currentTeam.setPlayer1(null);
                    }
                    else if (currentTeam.getPlayer2() != null && currentTeam.getPlayer2().equals(currentPlayer)) {
                        currentTeam.setPlayer2(null);
                    }
                    teamRepository.save(currentTeam);
                    playerRepository.delete(currentPlayer);
                }
            }
        }

        System.out.println(bonusMessage);
        // Reset the lobby state
        resetLobbyState(lobby);

        // Save updated entities
        gobanRepository.delete(goban);
        lobbyRepository.save(lobby);
        teamRepository.save(team1);
        teamRepository.save(team2);
        ChatController chat = new ChatController();
        chat.sendGameUpdateToPlayers("[ANNOUNCER]: Game over. \n " + teamWinner + "\n" + bonusMessage);
    }

    private void updateTeamProfiles(Team team, boolean isWinner, Team opponentTeam) {
        List<Player> players = List.of(team.getPlayer1(), team.getPlayer2());
        int averageOpponentElo = (opponentTeam.getPlayer1().getProfile().getElo() +
                opponentTeam.getPlayer2().getProfile().getElo()) / 2;

        for (Player player : players) {
            TheProfile profile = player.getProfile();
            theProfileService.updateAfterGame(profile, isWinner, averageOpponentElo);
            profile.setGames(profile.getGames() + 1); // Increment games played
        }
    }

    private void resetLobbyState(Lobby lobby) {
        lobby.setGoban(null);
        lobby.setIsGameInitialized(false);

        resetTeamState(lobby.getTeam1());
        resetTeamState(lobby.getTeam2());
    }

    private void resetTeamState(Team team) {
        team.setTeamScore(0.0);

        List<Player> players = new ArrayList<>();
        if (team.getPlayer1() != null) {
            players.add(team.getPlayer1());
        }
        if (team.getPlayer2() != null) {
            players.add(team.getPlayer2());
        }
        for (Player player : players) {
            player.setIsTurn(false);
            player.setIsReady(false);
        }
    }

    private void saveAllProfiles(Team team1, Team team2) {
        List<TheProfile> profiles = List.of(
                team1.getPlayer1().getProfile(),
                team1.getPlayer2().getProfile(),
                team2.getPlayer1().getProfile(),
                team2.getPlayer2().getProfile()
        );

        theProfileRepository.saveAll(profiles);
    }

    public ResponseEntity<String> placeAStone(Integer userId, Integer x, Integer y) {
        ChatController chat = new ChatController();
        TheProfile profile = userService.findProfileById(userId);
        Player currentPlayer = playerRepository.findByProfile(profile);
        currentPlayer.setRecentPassTurn(false);
        Goban goban = currentPlayer.getTeam().getLobby().getGoban();
        Team currentTeam = currentPlayer.getTeam();

        if (x < 0 || y < 0 || x > 8 || y > 8) {
            // Consolidated off-the-board placement check
            return ResponseEntity.ok("Cannot place a stone at (" + x + ", " + y + "), as it is off the board.");
        }

        if (!currentPlayer.getTeam().getLobby().getIsGameInitialized()) {
            return ResponseEntity.ok("Game is no longer active" );
        }

        if (currentPlayer.getIsTurn()) {
            goban.loadMatrixFromBoardString();
            Stone[][] board = goban.getBoard();

            if (x == 22 && y == 22) { // TODO clears board for testing only, CURRENTLY HAVE SET TO NOT WORK(out of bounds check)
                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[i].length; j++) {
                        Stone stone = board[i][j];
                        stone.setStoneType("X");
                    }
                }
                goban.saveBoardString();
                gobanRepository.save(goban);
                return ResponseEntity.ok("Matrix clear method here temp");
            }

            Stone stone = new Stone(goban, x, y);
            if (currentPlayer.getTeam().getIsBlack()) {
                if (board[x][y].getStoneType().equals("X")) {
                    stone.setStoneType("B");
                }
                else if (!board[x][y].getStoneType().equals("X")) {
                    return ResponseEntity.ok("Cannot place a a stone at (" + x + ", " + y + "), as it is occupied." );
                }
            }
            else if (!currentPlayer.getTeam().getIsBlack()){
                if (board[x][y].getStoneType().equals("X")) {
                    stone.setStoneType("W");
                }
                else if (!board[x][y].getStoneType().equals("X")) {
                    return ResponseEntity.ok("Cannot place a a stone at (" + x + ", " + y + "), as it is occupied." );
                }
            }
            chat.sendGameUpdateToPlayers("[ANNOUNCER]: " + currentPlayer.getUsername() + " placed a stone!");

            //timerService.stopTimerForTeam(currentTeam);
            currentTeam.setIsTeamTurn(false);
            board[x][y] = stone;

            // Focus only on opposite color groups
            String lastStoneType = stone.getStoneType();
            String oppositeStoneType = lastStoneType.equals("B") ? "W" : "B";

            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    // Only check opposite color stones that haven't been captured
                    if (board[i][j].getStoneType().equals(oppositeStoneType)) {

                        if (checkIfCaptured(board, i, j, currentTeam)) {
                            chat.sendGameUpdateToPlayers("[ANNOUNCER]: " + currentPlayer.getUsername() + " captured stone(s)!");
                            break;  // Stop after capturing a group to avoid multiple captures
                        }
                    }
                }
            }
            goban.saveBoardString();
            String nextPlayerName = switchToNextPlayer(currentPlayer,goban.getPlayerIdTurnList());
            teamRepository.save(currentTeam);
            gobanRepository.save(goban);
            chat.sendGameUpdateToPlayers("[ANNOUNCER]: It is now " + nextPlayerName + "'s turn!");

            return ResponseEntity.ok("Stone placed successfully.");
        }
        return ResponseEntity.ok("It is not " + currentPlayer.getUsername() + "'s turn, cannot place a stone." );
    }

    public ResponseEntity<String> pass(Integer userId) {
        TheProfile profile = userService.findProfileById(userId);
        Player currentPlayer = playerRepository.findByProfile(profile);

        if (!currentPlayer.getTeam().getLobby().getIsGameInitialized()) {
            return ResponseEntity.ok("Game is no longer active" );
        }

        if (currentPlayer.getIsTurn()) {
            int passCount = 0;
            currentPlayer.setRecentPassTurn(true);
            playerRepository.save(currentPlayer);

            Lobby currentLobby = currentPlayer.getTeam().getLobby();
            List<Player> playersInGame = currentLobby.getPlayersInLobby();
            
            Goban goban = currentLobby.getGoban();
            for (Player player : playersInGame) {
                if (player.getRecentPassTurn()){
                    passCount++;
                }
            }
            if (passCount == 4) {
                endGame(currentLobby.getLobby_id(), false);
                return ResponseEntity.ok(currentPlayer.getUsername() + " passed, everyone in the game has now passed in sequence GAME OVER.");
            }
            String nextPlayerName = switchToNextPlayer(currentPlayer, goban.getPlayerIdTurnList());

            ChatController chat = new ChatController();
            chat.sendGameUpdateToPlayers("[ANNOUNCER]: " + currentPlayer.getUsername() + " passed, it is now " + nextPlayerName + "'s turn!");

            return ResponseEntity.ok(currentPlayer.getUsername() + " passed, it is now " + nextPlayerName + "'s turn." );
        }

        return ResponseEntity.ok("It is not " + currentPlayer.getUsername() + "'s turn, cannot pass." );
    }

    public ResponseEntity<String> abandonGame(Integer userId) {
        TheProfile profile = userService.findProfileById(userId);
        Player playerAbandoning = playerRepository.findByProfile(profile);
        Lobby currentLobby = playerAbandoning.getTeam().getLobby();

        if (!currentLobby.getIsGameInitialized()) {
            return ResponseEntity.ok("Cannot abandon a game that has not been initialized, you must leave lobby instead.");
        }
        playerAbandoning.setHasAbandoned(true);
        if (playerAbandoning.getUsername().equals(currentLobby.getHostName())) {
            if (playerAbandoning.equals(playerAbandoning.getTeam().getPlayer1())) {
                currentLobby.setHostName(playerAbandoning.getTeam().getPlayer2().getUsername());
            }
            else {
                currentLobby.setHostName(playerAbandoning.getTeam().getPlayer1().getUsername());
            }
        }
        playerRepository.save(playerAbandoning);

        endGame(currentLobby.getLobby_id(), true);
        return ResponseEntity.ok("You abandoned the game.");
    }

    //Helper Methods
    public String switchToNextPlayer(Player currentPlayer, List<Integer> turnSequence) {
        // Find the current player's ID and index in the turn sequence
        Integer currentTurnId = currentPlayer.getProfile().getUser().getUser_id();
        int currentIndex = turnSequence.indexOf(currentTurnId);

        // Determine the next index in a circular manner
        int nextIndex = (currentIndex + 1) % turnSequence.size();
        Integer nextTurnId = turnSequence.get(nextIndex);

        // Retrieve the next player based on the ID
        TheProfile nextProfile = userService.findProfileById(nextTurnId);
        Player nextPlayer = playerRepository.findByProfile(nextProfile);
        Team nextTeam = nextPlayer.getTeam();

        // Update turn states
        nextTeam.setIsTeamTurn(true);
        nextPlayer.setIsTurn(true);
        currentPlayer.setIsTurn(false);

        // Save updated player states if needed (optional, depending on persistence setup)
        playerRepository.save(currentPlayer);
        playerRepository.save(nextPlayer);
        teamRepository.save(nextTeam);
        return nextPlayer.getUsername();
    }

    public boolean checkIfCaptured(Stone[][] board, int x, int y, Team currentTeam) {
        String stone = board[x][y].getStoneType();
        if (stone.equals("X")) {
            return false;  // No stone at position
        }

        // Perform a breadth-first search to find the group of stones
        Set<String> visited = new HashSet<>();
        Set<String> group = new HashSet<>();
        Set<String> liberties = new HashSet<>();

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{x, y});

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int curX = current[0];
            int curY = current[1];
            String key = curX + "," + curY;

            if (visited.contains(key) || !board[curX][curY].getStoneType().equals(stone)) {
                continue;
            }

            visited.add(key);
            group.add(key);

            // Check all adjacent positions for liberties or more stones in the group
            for (int i = 0; i < 4; i++) {
                int newX = curX + DIR_X[i];
                int newY = curY + DIR_Y[i];

                if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length) {
                    String newKey = newX + "," + newY;

                    if (board[newX][newY].getStoneType().equals("X")) {
                        liberties.add(newKey);  // Add liberty (empty space)
                    }
                    else if (board[newX][newY].getStoneType().equals(stone) && !visited.contains(newKey)) {
                        queue.offer(new int[]{newX, newY});  // Add to exploration queue
                    }
                }
            }
        }

        // If there are no liberties, capture the group
        if (liberties.isEmpty()) {
            // Use the group set to specifically capture only the stones in this group
            for (String groupKey : group) {
                String[] coords = groupKey.split(",");
                int groupX = Integer.parseInt(coords[0]);
                int groupY = Integer.parseInt(coords[1]);

                Stone capturedStone = board[groupX][groupY];
                capturedStone.setStoneType("X");
                board[groupX][groupY] = capturedStone;
                currentTeam.setTeamScore(currentTeam.getTeamScore() + 1);
            }
            return true;
        }

        return false;
    }
}
