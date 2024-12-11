package com.example.sb.Service;

import com.example.sb.Controller.ChatController;
import com.example.sb.Model.*;
import com.example.sb.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

        if (currentPlayer.getTeam().getTerritoryCount() != null) {
            return ResponseEntity.ok("In counting phase cannot, place a stone." );
        }

        if (currentPlayer.getIsTurn()) {
            goban.loadMatrixFromBoardString();
            Stone[][] board = goban.getBoard();

            if (x == 22 && y == 22) { // TODO clears board for testing only
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
            Team oppositeTeam;
            if (currentTeam.equals(currentTeam.getLobby().getTeam1())) {
                oppositeTeam = currentTeam.getLobby().getTeam2();
            }
            else {
                oppositeTeam = currentTeam.getLobby().getTeam1();
            }

            boolean timeElapsed = handleTimer(currentTeam, oppositeTeam, currentPlayer, chat);
            if (timeElapsed) {
                return ResponseEntity.ok("Time depleted, you forfeit");
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

        if (currentPlayer.getTeam().getTerritoryCount() != null) {
            return ResponseEntity.ok("In counting phase cannot, pass." );
        }

        if (currentPlayer.getIsTurn()) {
            int passCount = 0;
            Lobby currentLobby = currentPlayer.getTeam().getLobby();
            ChatController chat = new ChatController();
            Team currentTeam = currentPlayer.getTeam();
            Lobby lobby = currentTeam.getLobby();

            currentPlayer.setRecentPassTurn(true);
            playerRepository.save(currentPlayer);

            Team oppositeTeam = currentTeam.equals(lobby.getTeam1()) ? lobby.getTeam2() : lobby.getTeam1();
            boolean timeElapsed = handleTimer(currentTeam, oppositeTeam, currentPlayer, chat);
            if (timeElapsed) {
                return ResponseEntity.ok("Time depleted, you forfeit");
            }
            List<Player> playersInGame = currentLobby.getPlayersInLobby();
            for (Player player : playersInGame) {
                if (player.getRecentPassTurn()){
                    passCount++;
                }
            }

            Goban goban = currentLobby.getGoban();
            if (passCount == 4) {
                goban.loadMatrixFromBoardString();
                Stone[][] board = goban.getBoard();

                Team blackTeam = currentTeam.getIsBlack() ? currentTeam : oppositeTeam;
                Team whiteTeam = currentTeam.getIsBlack() ? oppositeTeam : currentTeam;
                int blackCount = 0;
                int whiteCount = 0;

                for (int x = 0; x < 9; x++) {
                    for (int y = 0; y < 9; y++) {
                        if (board[x][y].getStoneType().equals("B")) {
                            blackTeam.setTerritoryCount(blackCount++);
                        }
                        else if (board[x][y].getStoneType().equals("W")) {
                            whiteTeam.setTerritoryCount(whiteCount++);
                        }
                    }
                }

                blackTeam.setTimeRemaining(2147483647L);
                whiteTeam.setTimeRemaining(2147483647L);
                blackTeam.setTerritoryCount(blackCount);
                whiteTeam.setTerritoryCount(whiteCount);
                teamRepository.save(blackTeam);
                teamRepository.save(whiteTeam);
                chat.sendGameUpdateToPlayers("[ANNOUNCER]: " + currentPlayer.getUsername() + " passed, everyone in the game has now passed in sequence + \n Now entering Counting Phase!.");
                return ResponseEntity.ok(currentPlayer.getUsername() + " passed, everyone in the game has now passed in sequence GAME OVER.");
            }
            String nextPlayerName = switchToNextPlayer(currentPlayer, goban.getPlayerIdTurnList());

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

        if (playerAbandoning.getTeam().getTerritoryCount() == null) {
            return ResponseEntity.ok("In counting phase cannot, abandon." );
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

        endGame(currentLobby.getLobby_id());
        return ResponseEntity.ok("You abandoned the game.");
    }

    public ResponseEntity<String> disputeTerritory (Integer userId, Integer x, Integer y) {
        TheProfile profile = userService.findProfileById(userId);
        Player currentPlayer = playerRepository.findByProfile(profile);
        if (currentPlayer != null) {

            if (!currentPlayer.equals(currentPlayer.getTeam().getPlayer1())) {
                return ResponseEntity.ok("Only the teamLeader can claim territory!");
            }

            if (currentPlayer.getTeam().getTerritoryCount() == null) {
                return ResponseEntity.ok("Not in counting phase.");
            }

            Team currentTeam = currentPlayer.getTeam();
            Lobby lobby = currentTeam.getLobby();
            Team oppositeTeam = currentTeam.equals(lobby.getTeam1()) ? lobby.getTeam2() : lobby.getTeam1();
            Team blackTeam = currentTeam.getIsBlack() ? currentTeam : oppositeTeam;
            Team whiteTeam = currentTeam.getIsBlack() ? oppositeTeam : currentTeam;

            Goban goban = lobby.getGoban();
            goban.loadMatrixFromBoardString();
            Stone[][] board = goban.getBoard();

            // Attempting to claim territory
            if (board[x][y].getStoneType().equals("X")) {
                if (currentTeam.equals(blackTeam)) {
                    board[x][y].setStoneType("Bc");
                    blackTeam.setTerritoryCount(blackTeam.getTerritoryCount() + 1);
                }
                else {
                    board[x][y].setStoneType("Wc");
                    whiteTeam.setTerritoryCount(whiteTeam.getTerritoryCount() + 1);
                }
            }
            // Attempting to claim prisoner
            else if (board[x][y].getStoneType().equals("B") || board[x][y].getStoneType().equals("W")) {
                if (currentTeam.equals(blackTeam) && board[x][y].getStoneType().equals("W")) {
                    board[x][y].setStoneType("Bp");
                    blackTeam.setTerritoryCount(blackTeam.getTerritoryCount() + 1);
                    whiteTeam.setTerritoryCount(whiteTeam.getTerritoryCount() - 1);
                }
                else if (currentTeam.equals(whiteTeam) && board[x][y].getStoneType().equals("B")) {
                    board[x][y].setStoneType("Wp");
                    whiteTeam.setTerritoryCount(whiteTeam.getTerritoryCount() + 1);
                    blackTeam.setTerritoryCount(blackTeam.getTerritoryCount() - 1);
                }
            }
            // Attempting to dispute prisoner
            else if (board[x][y].getStoneType().equals("Bp") || board[x][y].getStoneType().equals("Wp")) {
                if (currentTeam.equals(blackTeam) && board[x][y].getStoneType().equals("Wp") ) {
                    board[x][y].setStoneType("B");
                    blackTeam.setTerritoryCount(blackTeam.getTerritoryCount() + 1);
                    whiteTeam.setTerritoryCount(whiteTeam.getTerritoryCount() - 1);
                }
                else if (currentTeam.equals(whiteTeam) && board[x][y].getStoneType().equals("Bp")) {
                    board[x][y].setStoneType("W");
                    whiteTeam.setTerritoryCount(whiteTeam.getTerritoryCount() + 1);
                    blackTeam.setTerritoryCount(blackTeam.getTerritoryCount() - 1);
                }
            }
            // Attempting to reset a territory.
            else {
                if (currentTeam.equals(blackTeam)) {
                    if (board[x][y].getStoneType().equals("Bc")) {
                        board[x][y].setStoneType("X");
                        blackTeam.setTerritoryCount(blackTeam.getTerritoryCount() - 1);
                    }
                }
                else {
                    if (board[x][y].getStoneType().equals("Wc")) {
                        board[x][y].setStoneType("X");
                        whiteTeam.setTerritoryCount(whiteTeam.getTerritoryCount() - 1);
                    }
                }
            }

            goban.saveBoardString();
            gobanRepository.save(goban);
            teamRepository.save(blackTeam);
            teamRepository.save(whiteTeam);
            return ResponseEntity.ok("temp, should be good!");
        }
        return ResponseEntity.ok("User with ID: " + userId + " does not exist.");
    }

    public ResponseEntity<String> toggleFinalizeClaim(Integer userId) {
        ChatController chat = new ChatController();

        TheProfile profile = userService.findProfileById(userId);
        Player currentPlayer = playerRepository.findByProfile(profile);

        if (currentPlayer != null) {
            Team currentTeam = currentPlayer.getTeam();
            if (!currentPlayer.equals(currentTeam.getPlayer1())) {
                return ResponseEntity.ok("Specified player not a team leader!");
            }

            if (currentPlayer.getTeam().getTerritoryCount() == null) {
                return ResponseEntity.ok("Not in counting phase.");
            }

            currentPlayer.setFinalizeClaim(!currentPlayer.getFinalizeClaim());
            playerRepository.save(currentPlayer);

            Lobby lobby = currentTeam.getLobby();
            Team oppositeTeam = currentTeam.equals(lobby.getTeam1()) ? lobby.getTeam2() : lobby.getTeam1();

            if (oppositeTeam.getPlayer1().getFinalizeClaim() && currentPlayer.getFinalizeClaim()) {
                endGame(lobby.getLobby_id());
                return ResponseEntity.ok("Both players have finished claiming territory!");
            }
            if (!currentPlayer.getFinalizeClaim()) {
                chat.sendGameUpdateToPlayers("[ANNOUNCER]: " + currentPlayer.getUsername() + " has started to claim territory again!");
                return ResponseEntity.ok("Player started claiming again.");
            }
            chat.sendGameUpdateToPlayers("[ANNOUNCER]: " + currentPlayer.getUsername() + " has finished claiming territory!");
            return ResponseEntity.ok("Player finished claiming territory!");
        }
        return ResponseEntity.ok("Specified player does not exist!");
    }

    public boolean handleTimer(Team currentTeam, Team oppositeTeam, Player currentPlayer, ChatController chat) {
        boolean timeElapsed = false;
        LocalDateTime currentMoveTimestamp = LocalDateTime.now(); // Time at which the move was made
        long currentTimeRemaining; //Time remaining for current Team

        Duration timePassed; // Time elapsed since last move
        Long newTimeRemaining;

        if (oppositeTeam.getLastMoveTimestamp() == null) {
            currentTimeRemaining = currentTeam.getTimeRemaining(); //Time remaining for current Team

            timePassed = Duration.between(currentTeam.getLastMoveTimestamp(), currentMoveTimestamp); // Time elapsed since last move
            newTimeRemaining = currentTimeRemaining - timePassed.toMillis();
        }
        else {
            currentTimeRemaining = currentTeam.getTimeRemaining(); // Time remaining for current Team

            timePassed = Duration.between(oppositeTeam.getLastMoveTimestamp(), currentMoveTimestamp); // Time elapsed since last move by opposite team
            newTimeRemaining = currentTimeRemaining - timePassed.toMillis();
        }

        if (newTimeRemaining < 0) {
            currentTeam.setTimeRemaining(0L);
            teamRepository.save(currentTeam);
            chat.sendGameUpdateToPlayers("[ANNOUNCER]: " + currentPlayer.getUsername() + "has run out of time, game forfeit!");
            endGame(currentTeam.getLobby().getLobby_id());
            return true;
        }
        else if (newTimeRemaining < 30000) {
            currentTeam.setTimeRemaining(30000L);
            chat.sendGameUpdateToPlayers("[ANNOUNCER]: " + currentPlayer.getUsername() + "has entered byo-yomi(sudden death)!");
        }
        else {
            currentTeam.setTimeRemaining(newTimeRemaining);
        }

        currentTeam.setLastMoveTimestamp(currentMoveTimestamp);
        return timeElapsed;
    }

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
                currentTeam.setTeamCaptures(currentTeam.getTeamCaptures() + 1);
            }
            return true;
        }

        return false;
    }

    public String calculateScores(Team team1, Team team2) {
        double cumulativeBlackScore;
        double cumulativeWhiteScore;

        Team blackTeam = team1.getIsBlack() ? team1 : team2;
        Team whiteTeam = team1.getIsBlack() ? team2 : team1;

        // Determine the scores based on which team is black

        cumulativeBlackScore = blackTeam.getTeamCaptures() + blackTeam.getTerritoryCount();
        cumulativeWhiteScore = whiteTeam.getTeamCaptures() + whiteTeam.getTerritoryCount() + 6.5;

        if (cumulativeBlackScore > cumulativeWhiteScore) {
            return "Black wins by " + (cumulativeBlackScore - cumulativeWhiteScore) + " points." + "\n" + "Final scores are, " +
                    blackTeam.getTeamName() + ": " + cumulativeBlackScore +
                    " and " + whiteTeam.getTeamName() + ": " + cumulativeWhiteScore;
        }
        else {
            return "White wins by " + (cumulativeWhiteScore - cumulativeBlackScore) + " points." +
                    "\n" + "Final scores are, " + blackTeam.getTeamName() + ": " + cumulativeBlackScore +
                    " and " + whiteTeam.getTeamName() + ": " + cumulativeWhiteScore;
        }
    }

    public void endGame(Integer lobbyId) {
        boolean forfeit = false;
        String teamWinsBy = "";
        String teamWinner = "";
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        boolean isTeam1Winner = false;

        Lobby lobby = lobbyOptional.get();
        Team team1 = lobby.getTeam1();
        Team team2 = lobby.getTeam2();
        Goban goban = lobby.getGoban();

        if (team1.getTimeRemaining() == 0L || team2.getTimeRemaining() == 0L) {
            // Some team ran out of time
            if (team1.getTimeRemaining() == 0L) {
                String winningTeamName = team2.getTeamName();
                String winningPlayers = team2.getPlayer1().getUsername() + "-|-" + team2.getPlayer2().getUsername();
                teamWinner = winningTeamName + " (" + winningPlayers + ") wins the game by a time loss from " + team1.getTeamName();
            }
            else {
                String winningTeamName = team1.getTeamName();
                String winningPlayers = team1.getPlayer1().getUsername() + "-|-" + team1.getPlayer2().getUsername();
                teamWinner = winningTeamName + " (" + winningPlayers + ") wins the game by a time loss from " + team2.getTeamName();
                isTeam1Winner = true;
            }
        }
        // Determine winner if some player abandoned
        else if (team1.getPlayer1().getHasAbandoned() || team1.getPlayer2().getHasAbandoned()) {
            forfeit = true;
            // Team 1 has a missing player; Team 2 wins
            String winningTeamName = team2.getTeamName();
            String winningPlayers = team2.getPlayer1().getUsername() + "-|-" + team2.getPlayer2().getUsername();
            teamWinner = winningTeamName + " (" + winningPlayers + ") wins the game due to teammate abandonment by " + team1.getTeamName();
        }
        else if (team2.getPlayer1().getHasAbandoned() || team2.getPlayer2().getHasAbandoned()) {
            forfeit = true;
            // Team 2 has a missing player; Team 1 wins
            isTeam1Winner = true;
            String winningTeamName = team1.getTeamName();
            String winningPlayers = team1.getPlayer1().getUsername() + "-|-" + team1.getPlayer2().getUsername();
            teamWinner = winningTeamName + " (" + winningPlayers + ") wins the game due to teammate abandonment by " + team2.getTeamName();
        }
        else {
            teamWinsBy = calculateScores(team1, team2);
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

        // Reset the lobby state
        resetLobbyState(lobby);

        // Save updated entities
        gobanRepository.delete(goban);
        lobbyRepository.save(lobby);
        teamRepository.save(team1);
        teamRepository.save(team2);
        ChatController chat = new ChatController();
        chat.sendGameUpdateToPlayers("[ANNOUNCER]: Game over. \n " + teamWinner + "\n" + teamWinsBy);
    }

    private void updateTeamProfiles(Team team, boolean isWinner, Team opponentTeam) {
        List<Player> players = List.of(team.getPlayer1(), team.getPlayer2());
        int averageOpponentElo = (opponentTeam.getPlayer1().getProfile().getElo() +
                opponentTeam.getPlayer2().getProfile().getElo()) / 2;

        for (Player player : players) {
            TheProfile profile = player.getProfile();
            if (!team.getLobby().getIsFriendly()) {
                theProfileService.updateAfterGame(profile, isWinner, averageOpponentElo);
            }
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
        team.setTeamCaptures(0.0);
        team.setTerritoryCount(null);
        team.setLastMoveTimestamp(null);
        team.setIsFinishedCounting(false);
        team.setIsTeamTurn(false);
        team.setTimeRemaining((team.getLobby().getGameTime() / 2) * 60 * 1000);

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
            player.setFinalizeClaim(false);
            player.setRecentPassTurn(false);
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
}
