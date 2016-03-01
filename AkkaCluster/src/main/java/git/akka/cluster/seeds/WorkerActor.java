package git.akka.cluster.seeds;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import cl.gps.drivers.objects.DeviceEvent;
import git.akka.cluster.messages.ClusterTestMessage;

/**
 *
 * @author gaston
 */
public class WorkerActor extends UntypedActor {

	public WorkerActor() {
		System.out.println(getSender());
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof DeviceEvent) {
			System.out.println(((DeviceEvent) msg).toString());
			getSender().tell(new ClusterTestMessage(200,(ActorRef)((DeviceEvent) msg).get("sender")), ActorRef.noSender());
		}
	}

}
