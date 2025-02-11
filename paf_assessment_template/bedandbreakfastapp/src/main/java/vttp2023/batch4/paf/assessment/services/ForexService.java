package vttp2023.batch4.paf.assessment.services;

import java.io.StringReader;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@Service
public class ForexService {

	private final String baseUrl = "https://api.frankfurter.app/latest";

	// TODO: Task 5 
	public float convert(String from, String to, float amount) {

		String url = UriComponentsBuilder
				.fromUriString(baseUrl)
				.queryParam("base", from)
				.queryParam("symbols", to)
				.toUriString();

		RequestEntity<Void> req = RequestEntity.get(url)
				.build();

		RestTemplate template = new RestTemplate();
		ResponseEntity<String> res = null;
		try {
			res = template.exchange(req, String.class);

			String payload = res.getBody();

			JsonObject jObject = Json.createReader(new StringReader(payload)).readObject();
			JsonObject rateObject = jObject.getJsonObject("rates");
			double rate = rateObject.getJsonNumber(to.toUpperCase()).doubleValue();

			float convertedAmount = amount * (float)rate;

			return convertedAmount;
			
		} catch (Exception e) {
			
			return -1000f;
		}

	}
}
