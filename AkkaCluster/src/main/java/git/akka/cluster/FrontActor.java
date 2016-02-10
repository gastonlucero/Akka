package git.akka.cluster;

import akka.actor.ActorPath;
import akka.actor.ActorPaths;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientReceptionist;
import akka.cluster.client.ClusterClientSettings;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author gaston
 */
public class FrontActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	List<ActorRef> nodes = new ArrayList<>();
	int calls = 0;

	public FrontActor() {
		ClusterClientReceptionist.get(getContext().system()).registerService(getSelf());
	}

	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Terminated) {
			Terminated terminated = (Terminated) message;
			nodes.remove(terminated.getActor());
			getContext().unwatch(terminated.getActor());
		} else if (message.equals("REGISTRO")) {
			getContext().watch(getSender());
			nodes.add(getSender());
		} else if (message instanceof Integer) {
			log.info("ServiceActor : Numero  a enviar {}"+message, getSender().path());
			nodes.get(calls % nodes.size()).forward(message, getContext());			
			calls++;
		}
	}
	
}
