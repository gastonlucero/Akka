package git.akka.cluster;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.client.ClusterClientReceptionist;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 *
 * @author gaston
 */
public class ClusterBackActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	Cluster cluster = Cluster.get(getContext().system());
	
	
	@Override
	public void preStart() {
		cluster.subscribe(getSelf(),  MemberUp.class);
		ClusterClientReceptionist.get(getContext().system()).registerService(getSelf());		
	}

	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MemberUp) {
			MemberUp mUp = (MemberUp) message;
			log.info("Nuevo nodo del cluster {}", mUp.member());
			if (mUp.member().hasRole("hijo")) {						
				getContext().actorSelection(mUp.member().address() + "/user/hijo").tell("REGISTRO", getSelf());
			}
		} else if (message instanceof Integer) {
			log.info("ClusterActor : Numero recibido {} " + message, getSender().path());
		} else {
			unhandled(message);
		}
	}
}
