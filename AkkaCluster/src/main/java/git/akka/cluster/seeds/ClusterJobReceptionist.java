package git.akka.cluster.seeds;

import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import static akka.actor.SupervisorStrategy.escalate;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.client.ClusterClientReceptionist;
import akka.cluster.client.ClusterReceptionist;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import cl.gps.drivers.objects.DeviceEvent;
import git.akka.cluster.messages.ClusterTestMessage;
import scala.concurrent.duration.Duration;

/**
 *
 * @author gaston
 */
public class ClusterJobReceptionist extends UntypedActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private ActorRef master;

	public ClusterJobReceptionist() {
		log.info("\n\nClusterJobReceptionist {}", getSelf().path().address());
		ClusterClientReceptionist.get(getContext().system()).registerService(getSelf());
		master = getContext().actorOf(Props.create(ClusterMasterActor.class, getSelf()));
		log.info("\n\nMaster iniciado en  {}", master.path().address());
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return new OneForOneStrategy(-1, Duration.Inf(),
				new Function<Throwable, SupervisorStrategy.Directive>() {
					@Override
					public SupervisorStrategy.Directive apply(Throwable t) {
						if (t instanceof Exception) {
							return SupervisorStrategy.restart();
						} else {
							return escalate();
						}
					}
				});
	}

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof DeviceEvent) {
			log.info("\n\nLlego DeviceEvent desde {} , enviando a clsuter master", getSender().path().address());
			master.tell(msg, getSelf());
		} else if (msg instanceof ClusterTestMessage) {
			log.info("\n\nRespuesta desde masterNode");			
			((ClusterTestMessage)msg).getOriginalSender().tell(200,ActorRef.noSender());
		}else {
			unhandled(msg);
		}
	}

}
