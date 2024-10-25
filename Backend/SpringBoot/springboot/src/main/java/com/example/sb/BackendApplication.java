package com.example.sb;

import com.example.sb.Model.Lobby;
import com.example.sb.Model.Player;
import com.example.sb.Model.Team;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(BackendApplication.class, args);
		Player player1 = new Player(1);
		Player player2 = new Player(2);
		Team team1 = new Team();
		team1.setPlayer1(player1);
		team1.setPlayer2(player2);

		Player player3 = new Player(3);
		Player player4 = new Player(4);
		Team team2 = new Team();
		team2.setPlayer1(player3);
		team2.setPlayer2(player4);

		Lobby lobbyTest = new Lobby();
		lobbyTest.setTeam1(team1);
		lobbyTest.setTeam2(team2);


	}
}