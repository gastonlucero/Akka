package git.akka.cluster.client.streams;

import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import scala.runtime.BoxedUnit;

/**
 *
 * @author gaston
 */
public class ReactiveTweetActor {

	class Author extends HashMap {

		public String getString(String key) {
			return (String) this.get(key);
		}

	}

	class Hashtag extends HashMap {

		public String getString(String key) {
			return (String) this.get(key);
		}
	}

	class Alerts extends HashMap {

		public Alerts() {
			super();
		}

		public String getString(String key) {
			return (String) this.get(key);
		}

		private Author getAuthor() {
			Author author = new Author();
			author.put("data", this);			
			author.put("_t", LocalDateTime.now().toString());
			return author;
		}

	}

		MongoClient mongoClient = new MongoClient("localhost");
		MongoDatabase mongoDatabase = mongoClient.getDatabase("wazeTest");
		
	public static void main(String[] args) throws Exception {
		new ReactiveTweetActor().readTweet();
	}

	public void readTweet() throws Exception {
		try {
			final ActorSystem system = ActorSystem.create("reactive-tweets");
			final Materializer mat = ActorMaterializer.create(system);

			RestTemplate rest = new RestTemplate();
			rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			HttpHeaders headers = new org.springframework.http.HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.ALL));
			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
			while (true) {
				List<Alerts> alerts = new ArrayList<>();
				String mapAlerts = rest.exchange("https://www.waze.com/row-rtserver/web/TGeoRSS?ma=600&mj=100&mu=100&left=-70.80817222595213&right=-70.44425010681152&bottom=-33.51356380208907&top=-33.37651484864821&_=1455221093878", HttpMethod.GET, entity, String.class).getBody();
				ObjectMapper mapper = new ObjectMapper();
				Map alertas = mapper.readValue(mapAlerts, Map.class);
				((List) alertas.get("alerts")).forEach(t -> {
					Alerts tw = new Alerts();
					tw.putAll((Map) t);
					alerts.add(tw);
				});

				Source<Author, BoxedUnit> authors = Source.from(alerts)
//												.filter(t -> {
//													return t.getString("city").equals("Providencia");
//												}
//												)
						.map(t -> {
							return t.getAuthor();
						});
				authors.runWith(Sink.foreach(alert -> {
					mongoDatabase.getCollection("alerts").insertOne(new Document(alert));					
				}), mat).onComplete(new OnComplete<BoxedUnit>() {
					@Override
					public void onComplete(Throwable failure, BoxedUnit success) throws Exception {
//					system.shutdown();
					}
				}, system.dispatcher());
//			((List) mapAlerts.get("alerts")).forEach(t -> {
//				Alerts tw = new Alerts();
//				tw.putAll((Map)t);
//				alerts.add(tw);
//			});			
//			Source<Author, BoxedUnit> authors = Source.from(alerts)
//					.filter(t -> {
//						return t.getString("city").toUpperCase().contains("PROVIDENCIA");
//					}
//					)
//					.map(t -> t.getAuthor());			
//			authors.runWith(Sink.foreach(alert -> {				
//				System.out.println("CALLE "+alert.getString("street"));
//				System.out.println("TIPO ALERTA "+alert.getString("type"));
//				System.out.println("PUNTO  "+ alert.get("location"));
//			}), mat);
//			System.exit(0);
				Thread.sleep(1000*60*2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
