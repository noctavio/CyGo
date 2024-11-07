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

    public ResponseEntity<String> endGame(Integer lobbyId) {
        Optional<Lobby> lobbyOptional = lobbyRepository.findById(lobbyId);
        if (lobbyOptional.isPresent()) {
            Lobby lobby = lobbyOptional.get();
            Team team1 = lobby.getTeam1();
            // player number is not based on order, it's just a variable name
            Player player1 = team1.getPlayer1();
            TheProfile profile1 = player1.getProfile();
            Player player2 = team1.getPlayer2();
            TheProfile profile2 = player2.getProfile();
            Team team2 = lobby.getTeam2();
            Player player3 = team2.getPlayer1();
            TheProfile profile3 = player3.getProfile();
            Player player4 = team2.getPlayer2();
            TheProfile profile4 = player4.getProfile();
            Goban board = lobby.getGoban();

            String teamWinner;
            if (team1.getTeamScore() > team2.getTeamScore()) {
                teamWinner = team1.getTeamName() + "("+ player1.getUsername() + "-|-" + player2.getUsername() + ") win's the game.";
                profile1.setWins(profile1.getWins() + 1);
                profile2.setWins(profile2.getWins() + 1);
                profile3.setLoss(profile3.getLoss() + 1);
                profile4.setLoss(profile4.getLoss() + 1);
            }
            else {
                teamWinner = team2.getTeamName() + "("+ player1.getUsername() + "-|-" + player2.getUsername() + ") win's the game.";
                profile3.setWins(profile3.getWins() + 1);
                profile4.setWins(profile4.getWins() + 1);
                profile1.setLoss(profile1.getLoss() + 1);
                profile2.setLoss(profile2.getLoss() + 1);
            }
            String finaleScores = "Final scores are, " + team1.getTeamName() + ": " + team1.getTeamScore() +
                    " and " + team2.getTeamName() + ": " + team2.getTeamScore();

            profile1.setGames(profile1.getGames() + 1);
            profile2.setGames(profile2.getGames() + 1);
            profile3.setGames(profile3.getGames() + 1);
            profile4.setGames(profile4.getGames() + 1);
            lobby.setGoban(null);
            lobby.setIsGameInitialized(false);

            team1.setTeamScore(0.0);
            team1.setStoneCount(41);

            team2.setTeamScore(0.0);
            team2.setStoneCount(41);

            //player1.setStartTime(null);
            //player1.setEndTime(null);
            player1.setIsTurn(false);
            player1.setIsReady(false);

            //player2.setStartTime(null);
            //player2.setEndTime(null);
            player2.setIsTurn(false);
            player2.setIsReady(false);

            //player3.setStartTime(null);
            //player3.setEndTime(null);
            player3.setIsTurn(false);
            player3.setIsReady(false);

            //player4.setStartTime(null);
            //player4.setEndTime(null);
            player4.setIsTurn(false);
            player4.setIsReady(false);

            gobanRepository.delete(board);
            lobbyRepository.save(lobby);
            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);
            playerRepository.save(player4);
            theProfileRepository.save(profile1);
            theProfileRepository.save(profile2);
            theProfileRepository.save(profile3);
            theProfileRepository.save(profile4);
            teamRepository.save(team1);
            teamRepository.save(team2);

            return ResponseEntity.ok("Game over,\n " + teamWinner + "\n" + finaleScores);
        }
        return ResponseEntity.ok("Wrong lobby id");
    }

    public ResponseEntity<String> placeAStone(Integer userId, Integer x, Integer y) {
        TheProfile profile = userService.findProfileById(userId);
        Player currentPlayer = playerRepository.findByProfile(profile);
        Goban goban = currentPlayer.getTeam().getLobby().getGoban();
        Team currentTeam = currentPlayer.getTeam();

        if (x < 0 || y < 0) {
            //off the board placement check
            return ResponseEntity.ok("Cannot place a a stone at (" + x + ", " + y + ")," );
        }

        if (!currentPlayer.getTeam().getLobby().getIsGameInitialized()) {
            return ResponseEntity.ok("Game is no longer active" );
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

            //timerService.stopTimerForTeam(currentTeam);
            Integer stones = currentTeam.getStoneCount();
            currentTeam.setStoneCount(stones - 1);
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
            Goban goban = currentPlayer.getTeam().getLobby().getGoban();
            String nextPlayerName = switchToNextPlayer(currentPlayer, goban.getPlayerIdTurnList());

            return ResponseEntity.ok(currentPlayer.getUsername() + " passed, it is now " + nextPlayerName + "'s turn." );
        }

        return ResponseEntity.ok("It is not " + currentPlayer.getUsername() + "'s turn, cannot pass." );
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
