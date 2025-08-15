package com.example.listings.dataFetchers;

import com.example.listings.dataSources.ListingService;
import com.example.listings.generated.types.Amenity;
import com.example.listings.generated.types.CreateListingInput;
import com.example.listings.generated.types.CreateListingResponse;
import com.example.listings.models.ListingModel;
import com.netflix.graphql.dgs.*;
import graphql.execution.DataFetcherResult;
import graphql.execution.Execution;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@DgsComponent
public class ListingDataFetcher {

	private final ListingService listingService;

	@Autowired
	public ListingDataFetcher(ListingService listingService) {
		this.listingService = listingService;
	}

	@DgsData(parentType = "Query", field = "featuredListings")
	public DataFetcherResult<List<ListingModel>> featuredListings() throws IOException {
		List<ListingModel> listings = listingService.getFeaturedListings();
		return DataFetcherResult.<List<ListingModel>> newResult()
				.data(listings)
				.localContext(Map.of("hasAmenityData", false))
				.build();
	}

	@DgsData(parentType = "Query", field="listing")
	public DataFetcherResult<ListingModel> listing(@InputArgument String id) throws IOException {
		ListingModel listing = listingService.getListing(id);
		return DataFetcherResult.<ListingModel>newResult()
				.data(listing)
				.localContext(Map.of("hasAmenityData", true))
				.build();
	}

	@DgsData(parentType = "Listing", field = "amenities")
	public List<Amenity> amenities(DgsDataFetchingEnvironment dfe) throws IOException {
		ListingModel listingModel = dfe.getSource();
		Map<String, Boolean> localContext = dfe.getLocalContext();
		if(localContext != null && localContext.get("hasAmenityData")) {
			return listingModel.getAmenities();
		}
		String id = listingModel.getId();
		return listingService.getAmenities(id);
	}

	@DgsMutation
	public CreateListingResponse createListing(@InputArgument CreateListingInput input) throws IOException {
		CreateListingResponse createListingResponse = new CreateListingResponse();
		try {
			ListingModel listingModel = listingService.createListing(input);
			createListingResponse.setCode(200);
			createListingResponse.setSuccess(true);
			createListingResponse.setMessage("success");
			createListingResponse.setListing(listingModel);
		} catch (Exception e){
			e.printStackTrace();
			createListingResponse.setCode(500);
			createListingResponse.setSuccess(false);
			createListingResponse.setMessage("failed");
		}
		return createListingResponse;
	}
}
