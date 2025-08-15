package com.example.listings.dataSources;

import com.example.listings.generated.types.Amenity;
import com.example.listings.generated.types.CreateListingInput;
import com.example.listings.models.ListingModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

@Component
public class ListingService {
	private static final String LISTING_API_URL = "https://rt-airlock-services-listing.herokuapp.com";
	private static final String LOCAL_HOST_API_URL = "localhost:50000";

	private final WebClient listingWebClient;
	private final WebClient localListingWebClient;

	private final ObjectMapper mapper = new ObjectMapper();

	public ListingService(@Qualifier("listingWebClient") WebClient listingWebClient,
	                      @Qualifier("localListingWebClient") WebClient localListingWebClient) {
		this.listingWebClient = listingWebClient;
		this.localListingWebClient = localListingWebClient;
	}

	public List<ListingModel> getFeaturedListings() throws IOException {
		return listingWebClient
				.get()
				.uri("/featured-listings")
				.retrieve()
//				.bodyToMono(JsonNode.class)
				.bodyToFlux(ListingModel.class)
				.collectList()
				.block();
	}

	public ListingModel getListing(String id){
		return listingWebClient
				.get()
				.uri("/listings/{listing_id}", id)
				.retrieve()
				.bodyToMono(ListingModel.class)
				.block();
	}

	public List<Amenity> getAmenities(String listingId){
		return listingWebClient
				.get()
				.uri("/listings/{listing_id}/amenities", listingId)
				.retrieve()
				.bodyToFlux(Amenity.class)
				.collectList()
				.block();
	}

	public ListingModel createListing(CreateListingInput createListingInput) throws JsonProcessingException {
		String requestBody = mapper.writeValueAsString(createListingInput);
		try {
			return localListingWebClient
					.post()
					.uri("/listings")
					.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
					.accept(org.springframework.http.MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(requestBody))
					.retrieve()
					.bodyToMono(ListingModel.class)
					.block();
		} catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
			System.err.println("Status: " + e.getStatusCode());
			System.err.println("Body: " + e.getResponseBodyAsString()); // shows JSON from the mappers
			throw e;
		}
	}
}
