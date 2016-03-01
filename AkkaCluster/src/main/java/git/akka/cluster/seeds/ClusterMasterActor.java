package git.akka.cluster.seeds;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.routing.ClusterRouterPool;
import akka.cluster.routing.ClusterRouterPoolSettings;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ActorRefRoutee;
import akka.routing.BroadcastPool;
import akka.routing.RoundRobinPool;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import cl.gps.drivers.objects.DeviceEvent;
import git.akka.cluster.messages.ClusterTestMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gaston
 */
public class ClusterMasterActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	/**
	 * Conexión al cluster, el primero que se conecta toma el papel de primario
	 */
	Cluster cluster = Cluster.get(getContext().system());
	Router clusterPool;
ActorRef receptionist;
	
	public ClusterMasterActor(ActorRef receptionist) {		
		this.receptionist=receptionist;
		log.info("\n\nClusterMasterActor {}",getSelf().path().address());
		Cluster.get(getContext().system()).subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberUp.class);
		List<Routee> routees = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			ActorRef r = context().actorOf(Props.create(WorkerActor.class));
			routees.add(new ActorRefRoutee(r));
		}
		clusterPool = new Router(new RoundRobinRoutingLogic(), routees);	
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return SupervisorStrategy.stoppingStrategy();
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof DeviceEvent) {
			log.info("\n\nLlego DeviceEvent desde receptionist{} , enviando a worker por medio del pool",getSender().path().address());			
			clusterPool.route(msg, getSelf());
		} else if (msg instanceof ClusterTestMessage) {
			log.info("\n\nRespueta de worker {}, enviadno a receptionist para enviarle al cliente",getSender().path().address());						
			receptionist.tell(msg, getSelf());
		} else {
			unhandled(msg);
		}
	}

}
