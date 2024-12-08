package com.example.sb.Service;

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

    public ResponseEntity<String> endGame(Integer lobbyId, boolean forfeit) {
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        if (lobbyOptional.isEmpty()) {
            return ResponseEntity.ok("Wrong lobby id");
        }
        String teamWinner;
        boolean isTeam1Winner = false;

        Lobby lobby = lobbyOptional.get();
        Team team1 = lobby.getTeam1();
        Team team2 = lobby.getTeam2();
        Goban board = lobby.getGoban();

        // Determine winner if some player abandoned
        if (team1.getPlayer1().getHasAbandoned() || team1.getPlayer2().getHasAbandoned()) {
            // Team 1 has a missing player; Team 2 wins
            String winningTeamName = team2.getTeamName();
            String winningPlayers = team2.getPlayer1().getUsername() + "-|-" + team2.getPlayer2().getUsername();
            teamWinner = winningTeamName + " (" + winningPlayers + ") wins the game due to abandonment by " + team1.getTeamName() + ".";
        }
        else if (team2.getPlayer1().getHasAbandoned() || team2.getPlayer2().getHasAbandoned()) {
            // Team 2 has a missing player; Team 1 wins
            isTeam1Winner = true;
            String winningTeamName = team1.getTeamName();
            String winningPlayers = team1.getPlayer1().getUsername() + "-|-" + team1.getPlayer2().getUsername();
            teamWinner = winningTeamName + " (" + winningPlayers + ") wins the game due to abandonment by " + team2.getTeamName() + ".";
        }
        else {
            // Determine the winner based on scores
            isTeam1Winner = team1.getTeamScore() > team2.getTeamScore();
            String winningTeamName = isTeam1Winner ? team1.getTeamName() : team2.getTeamName();
            String winningPlayers = isTeam1Winner ?
                    team1.getPlayer1().getUsername() + "-|-" + team1.getPlayer2().getUsername() :
                    team2.getPlayer1().getUsername() + "-|-" + team2.getPlayer2().getUsername();
            teamWinner = winningTeamName + " (" + winningPlayers + ") wins the game.";
        }

        String finalScores = "Final scores are, " + team1.getTeamName() + ": " + team1.getTeamScore() +
                " and " + team2.getTeamName() + ": " + team2.getTeamScore();
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
        // Reset the lobby state
        resetLobbyState(lobby);

        // Save updated entities
        gobanRepository.delete(board);
        lobbyRepository.save(lobby);
        teamRepository.save(team1);
        teamRepository.save(team2);
        return ResponseEntity.ok("Game over,\n " + teamWinner + "\n" + finalScores);
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
                        stone.setIsCaptured(false);
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

            //timerService.stopTimerForTeam(currentTeam);
            currentTeam.setIsTeamTurn(false);
            board[x][y] = stone;

            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    // Check if it's not already set to captured, but has been deemed captured then proceed and adjust board/points!
                    if (!board[i][j].getIsCaptured() && checkIfCaptured(board, i, j)) {
                        Stone capturedStone = board[i][j];
                        capturedStone.setIsCaptured(true);
                        if (capturedStone.getStoneType().equals("B")) {
                            capturedStone.setStoneType("Wc");
                        }
                        else if (capturedStone.getStoneType().equals("W")) {
                            capturedStone.setStoneType("Bc");
                        }
                        board[i][j] = capturedStone;
                        currentTeam.setTeamScore(currentTeam.getTeamScore() + 1);
                    }
                }
            }
            goban.saveBoardString();
            String nextPlayerName = switchToNextPlayer(currentPlayer,goban.getPlayerIdTurnList());
            teamRepository.save(currentTeam);
            gobanRepository.save(goban);
            return ResponseEntity.ok(currentPlayer.getUsername() + " placed a stone, it is now " + nextPlayerName + "'s turn.");
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
        return ResponseEntity.ok(playerAbandoning.getUsername() + " abandoned, game forfeited.");
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

    // Checks if a stone is captured
    public boolean checkIfCaptured(Stone[][] board, int x, int y) {
        String stone = board[x][y].getStoneType();
        if (stone.equals("X")) {
            return false;  // No stone at position
        }

        // Perform a DFS/BFS to find the group of stones
        boolean[][] visited = new boolean[board.length][board[0].length];
        Set<String> group = new HashSet<>();
        Set<String> liberties = new HashSet<>();
        exploreGroup(board, x, y, stone, visited, group, liberties);

        // If there are no liberties, the group is captured
        return liberties.isEmpty();
    }

    // Explores a group of connected stones and its liberties
    private void exploreGroup(Stone[][] board, int x, int y, String stone, boolean[][] visited, Set<String> group, Set<String> liberties) {
        if (x < 0 || x >= board.length || y < 0 || y >= board[0].length || visited[x][y] || !board[x][y].getStoneType().equals(stone)) {
            return;  // Out of bounds, already visited, or not the same stone
        }

        visited[x][y] = true;
        group.add(x + "," + y);

        // Check all adjacent positions for liberties or more stones in the group
        for (int i = 0; i < 4; i++) {
            int newX = x + DIR_X[i];
            int newY = y + DIR_Y[i];

            if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length) {
                if (board[newX][newY].getStoneType().equals("X")) {
                    liberties.add(newX + "," + newY);  // Add liberty (empty space)
                } else if (board[newX][newY].getStoneType().equals(stone)) {
                    exploreGroup(board, newX, newY, stone, visited, group, liberties);  // Recursively explore group
                }
            }
        }
    }

    // Capture the group of stones(not complete)
    public void captureGroup(Stone[][] board, Set<String> group) {
        for (String position : group) {
            String[] pos = position.split(",");
            int x = Integer.parseInt(pos[0]);
            int y = Integer.parseInt(pos[1]);
            board[x][y].setStoneType("X");  // Remove the stone //TODO implement capture type, Bc/Wc
        }
    }
}
