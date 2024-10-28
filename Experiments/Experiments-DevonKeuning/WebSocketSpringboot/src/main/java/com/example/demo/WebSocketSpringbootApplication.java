package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * The main application class for the WebSocket server.
 *
 * This class uses the `@ComponentScan` annotation to specify the base package
 * for component scanning. It scans the "com.example.demo.websocket" package
 * and its subpackages for Spring components, such as WebSocket endpoints and
 * controllers.
 *
 * Include this annotation as per your project structure needs.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo.websocket"})
public class WebSocketSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebSocketSpringbootApplication.class, args);
	}

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
}