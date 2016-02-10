package git.akka.cluster;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.cluster.client.ClusterClientReceptionist;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import git.akka.cluster.messages.ClusterRegistryMessage;
import git.akka.cluster.messages.ClusterTestMessage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gaston
 */
public class ClusterFrontendActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	List<ActorRef> nodes = new ArrayList<>();
	int calls = 0;

	public ClusterFrontendActor() {
		ClusterClientReceptionist.get(getContext().system()).registerService(getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof ClusterRegistryMessage) { //String && message.equals("REGISTRY")
			getContext().watch(getSender());
			nodes.add(getSender());
			log.info("\n ClusterFrontendActor : Registrando nodo " + message + " desde {}", getSender().path());
		} else if (message instanceof ClusterTestMessage) {
			log.info("\n ClusterFrontendActor : Mensaje recibido de cliente externo, enviando a cluster " + message + " desde {}", getSender().path());
			nodes.get(calls++ % nodes.size()).forward(message, getContext());
		} else if (message instanceof Terminated) {
			Terminated terminated = (Terminated) message;
			nodes.remove(terminated.getActor());
			getContext().unwatch(terminated.getActor());
		}
	}

}
