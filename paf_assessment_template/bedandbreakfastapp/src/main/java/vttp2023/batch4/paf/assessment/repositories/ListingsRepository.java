package vttp2023.batch4.paf.assessment.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch4.paf.assessment.Utils;
import vttp2023.batch4.paf.assessment.models.Accommodation;
import vttp2023.batch4.paf.assessment.models.AccommodationSummary;

@Repository
public class ListingsRepository {
	
	// You may add additional dependency injections
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private MongoTemplate template;

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 * 
		db.listings.aggregate([
			{$match: {'address.suburb': {$ne: ["", null]}}},
			{$group: {_id: '$address.suburb'}}
		])
	 *
	 */
	public List<String> getSuburbs(String country) {
		Criteria criteria = Criteria.where("address.suburb")
				.ne("")
				.andOperator(Criteria.where("address.suburb").ne(null));
		MatchOperation suburbMatch = Aggregation.match(criteria);
		GroupOperation groupBySuburb = Aggregation
				.group("address.suburb");

		Aggregation pipeline = Aggregation.newAggregation(suburbMatch, groupBySuburb);

		List<Document> results = mongoTemplate.aggregate(pipeline, "listings", Document.class)
				.getMappedResults();

		List<String> suburbsList = new ArrayList<>();
		for (Document d: results){
			suburbsList.add(d.getString("_id"));
		}

		return suburbsList;
	}

	/*
	 * Write the native MongoDB query that you will be using for this method
	 * inside this comment block
	 * eg. db.bffs.find({ name: 'fred }) 
	 *
	 * db.listings.find(
			{'address.suburb': {$regex: 'Darlinghurst', $options: 'i'},
			price: {$lte: 250.00},
			accommodates: {$gte: 1},
			min_nights: {$lte: 2}
			}
		)
		.projection({_id: 1, name: 1, accommodates: 1, price: 1})
		.sort({price: -1})
	 *
	 */
	public List<AccommodationSummary> findListings(String suburb, int persons, int duration, float priceRange) {
		Criteria criteria = Criteria.where("address.suburb")
				.regex(suburb, "i")
				.andOperator(Criteria.where("price").lte(priceRange),
					Criteria.where("accommodates").gte(persons), 
					Criteria.where("min_nights").lte(duration));

		Query query = Query.query(criteria);
		query.fields().include("_id", "name", "accommodates", "price");
		query.with(Sort.by(Direction.DESC, "price"));

		List<Document> results = mongoTemplate.find(query, Document.class, "listings");

		List<AccommodationSummary> resultsList = new ArrayList<>();
		for (Document d: results){
			AccommodationSummary as = new AccommodationSummary();
			as.setId(d.getString("_id"));
			as.setName(d.getString("name"));
			as.setAccomodates(d.getInteger("accommodates"));
			as.setPrice(d.get("price", Number.class).floatValue());
			
			resultsList.add(as);
		}
		
		return resultsList;
	}

	// IMPORTANT: DO NOT MODIFY THIS METHOD UNLESS REQUESTED TO DO SO
	// If this method is changed, any assessment task relying on this method will
	// not be marked
	public Optional<Accommodation> findAccommodatationById(String id) {
		Criteria criteria = Criteria.where("_id").is(id);
		Query query = Query.query(criteria);

		List<Document> result = template.find(query, Document.class, "listings");
		if (result.size() <= 0)
			return Optional.empty();

		return Optional.of(Utils.toAccommodation(result.getFirst()));
	}

}
