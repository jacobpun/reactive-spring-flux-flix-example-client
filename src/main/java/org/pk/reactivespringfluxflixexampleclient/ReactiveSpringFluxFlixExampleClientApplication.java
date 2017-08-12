package org.pk.reactivespringfluxflixexampleclient;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import static java.text.MessageFormat.format;

@SpringBootApplication
public class ReactiveSpringFluxFlixExampleClientApplication {

	@Value("${movies.url}")
	private String moviesUrl;
	@Value("${movie.events.url}")
	private String movieEventsUrl;

	public static void main(String[] args) {
		SpringApplication.run(
				ReactiveSpringFluxFlixExampleClientApplication.class, args);
	}

	@Bean
	public WebClient client() {
		return WebClient.create();
	}

	@Bean
	public CommandLineRunner runner(WebClient client) {
		return args -> client
				.get()
				.uri(moviesUrl)
				.exchange()
				.subscribe(
						cr -> cr.bodyToFlux(Movie.class)
								.filter(this::isCastAway)
								.subscribe(
										castAway -> client
												.get()
												.uri(format(movieEventsUrl,
														castAway.getId()))
												.exchange()
												.subscribe(
														cr2 -> cr2
																.bodyToFlux(
																		MovieEvent.class)
																.subscribe(
																		System.out::println))

								));
	}

	private boolean isCastAway(Movie movie) {
		return movie.getTitle().trim().equalsIgnoreCase("Cast Away");
	}
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class MovieEvent {
	private Movie movie;
	private Date when;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
class Movie {
	private String id;
	private String title;
}