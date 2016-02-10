package git.akka.cluster.messages;

/**
 *
 * @author gaston
 */
public class ClusterRegistryMessage implements scala.Serializable{

	public ClusterRegistryMessage() {
	}

	private final String message = "REGISTRY";

	public String getMessage() {
		return message;
	}
}
