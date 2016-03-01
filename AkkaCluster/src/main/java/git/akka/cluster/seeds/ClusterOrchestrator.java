package git.akka.cluster.seeds;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 *
 * @author gaston
 */
public class ClusterOrchestrator extends UntypedActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public ClusterOrchestrator() {
		Cluster.get(getContext().system()).subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberUp.class);
	}

	@Override
	public void postStop() throws Exception {
		Cluster.get(getContext().system()).unsubscribe(getSelf());
		super.postStop();
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof ClusterEvent.MemberUp) {
			ClusterEvent.MemberUp mUp = (ClusterEvent.MemberUp) msg;
			log.info("Clsuter Node Member UP {}", mUp.member());
		} else if (msg instanceof ClusterEvent.MemberRemoved) {
			ClusterEvent.MemberRemoved mRvd = (ClusterEvent.MemberRemoved) msg;
			log.info("Clsuter Node Member Remove {}", mRvd.member());
		} else if (msg instanceof ClusterEvent.UnreachableMember) {
			ClusterEvent.UnreachableMember uMb = (ClusterEvent.UnreachableMember) msg;
			log.info("Clsuter Node Unrecheable Member {}", uMb.member());
		} else if (msg instanceof ClusterEvent.ReachableMember) {

		} else if (msg instanceof ClusterEvent.CurrentClusterState) {

		}
	}

}
