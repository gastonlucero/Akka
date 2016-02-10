package git.akka.cluster.client;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.UntypedActor;
import akka.cluster.client.ClusterClient;
import akka.pattern.Patterns;
import git.akka.cluster.messages.ClusterTestMessage;

/**
 *
 * @author gaston
 */
public class TestClient extends UntypedActor {

	private ActorRef clusterClient;
	private String serviceName;

	public TestClient(ActorRef clusterClient, String serviceName) {
		this.clusterClient = clusterClient;
		this.serviceName = serviceName;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof String) {
		//Mensaje simulado que va hacia el cluster
			clusterClient.tell(new ClusterClient.Send("/user/serviceFrontEnd", new ClusterTestMessage(((String) msg).length(), getSelf()), true), ActorRef.noSender());
		} 
		else if (msg instanceof Integer) {
			//mensaje simulando la respuesta desde algun actor conectado al cluster
			System.out.println("OK =" + msg);			
		} else {
			unhandled(msg);
		}
	}

}
