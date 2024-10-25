package com.example.sb;

import com.example.sb.Model.Lobby;
import com.example.sb.Model.Player;
import com.example.sb.Model.Team;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(BackendApplication.class, args);
		Player player1 = new Player(1);
		player1.setUsername("Dummy1");
		Player player2 = new Player(2);
		player2.setUsername("Dummy2");

		Team team1 = new Team();
		team1.setTeam(Arrays.asList(player1,player2));

		Player player3 = new Player(3);
		player3.setUsername("Dummy3");
		Player player4 = new Player(4);
		player4.setUsername("Dummy4");
		Team team2 = new Team();
		team2.setTeam(Arrays.asList(player3, player4));

		Lobby lobbyTest = new Lobby();
		lobbyTest.setTeam1(team1);
		lobbyTest.setTeam2(team2);

		player3.muteAllEnemies(List.of("Dummy1", "Dummy2"));
		System.out.println(player3.getMutedPlayers());
	}
}