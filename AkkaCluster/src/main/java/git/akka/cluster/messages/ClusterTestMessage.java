package git.akka.cluster.messages;

import akka.actor.ActorRef;

/**
 *
 * @author gaston
 */
public class ClusterTestMessage  implements scala.Serializable{

	private Integer number;
	private ActorRef originalSender;

	public ClusterTestMessage(Integer number, ActorRef originalSender) {
		this.number = number;
		this.originalSender = originalSender;
	}

	public Integer getNumber() {
		return number;
	}

	public ActorRef getOriginalSender() {
		return originalSender;
	}

}
