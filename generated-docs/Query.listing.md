# Query.listing: Listing
                 
## Arguments
| Name | Description | Required | Type |
| :--- | :---------- | :------: | :--: |
| id |  | ✅ | ID! |
            
## Example
```graphql
{
  listing(id: "random12345") {
    id
    title
    numOfBeds
    costPerNight
    closedForBookings
    description
    amenities
  }
}

```