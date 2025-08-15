package com.example.listings.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;

@Configuration
public class WebClientConfig {
	private static final String BASE_URL = "https://rt-airlock-services-listing.herokuapp.com";
	private static final String LOCAL_HOST_URL = "http://localhost:50000/api/v1/";

	@Bean
	public WebClient listingWebClient() throws SSLException {
		// Build an SslContext that trusts all certificates (dev only!)
		SslContext sslContext = SslContextBuilder.forClient()
				.trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();

		// Create Reactor Netty HttpClient with that SslContext
		HttpClient httpClient = HttpClient.create()
				.secure(spec -> spec.sslContext(sslContext));

		// Use ReactorClientHttpConnector so WebClient uses our HttpClient
		ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

		return WebClient.builder()
				.baseUrl(BASE_URL)
				.clientConnector(connector)
				.build();
	}

	@Bean
	public WebClient localListingWebClient() throws SSLException {
		// Build an SslContext that trusts all certificates (dev only!)
		SslContext sslContext = SslContextBuilder.forClient()
				.trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();

		// Create Reactor Netty HttpClient with that SslContext
		HttpClient httpClient = HttpClient.create()
				.secure(spec -> spec.sslContext(sslContext));

		// Use ReactorClientHttpConnector so WebClient uses our HttpClient
		ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

		return WebClient.builder()
				.baseUrl(LOCAL_HOST_URL)
				.clientConnector(connector)
				.build();
	}

}
