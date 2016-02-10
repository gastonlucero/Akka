package git.akka.cluster.client;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientSettings;

/**
 *
 * @author gaston
 */
public class AkkaClusterClient extends UntypedActor {

	ActorRef clusterClient;

	public AkkaClusterClient(ActorRef clusterClient) {
		this.clusterClient = clusterClient;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof String){
			clusterClient.tell(new ClusterClient.Send("/user/hijo", (int)(Math.random()*100/10), true), ActorRef.noSender());
		}else{
			unhandled(msg);
		}
	}

}
