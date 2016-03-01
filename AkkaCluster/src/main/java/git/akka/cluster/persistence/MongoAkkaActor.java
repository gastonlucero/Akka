package git.akka.cluster.persistence;

import akka.actor.UntypedActor;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

/**
 *
 * @author gaston
 */
public class MongoAkkaActor extends UntypedActor {

	MongoClient mongoClient = MongoClients.create("mongodb://172.16.120.22:27117");
	MongoDatabase database;
	MongoCollection<Document> collection;

	public MongoAkkaActor() {
		database = mongoClient.getDatabase("data");
		collection = database.getCollection("history");
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		System.out.println(System.currentTimeMillis());
		List<Document> docs = new ArrayList<>();
		collection.find().limit(10000).into(docs, printDocument);
		
	}

	SingleResultCallback<List<Document>> printDocument = (final List<Document> document, final Throwable t) -> {		
		getSender().tell(document.get(9999).toJson(),null);
	};
}
