package git.akka.cluster;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 *
 * @author gaston
 */
public class ClusterApplication {

	public static void main(String[] args) throws Exception {
		startBackEndNode(2551, "back");
		Thread.sleep(10000);
		startBackEndNode(2552, "back");
		Thread.sleep(10000);
		startFrontEndNode(0, "front");

	}

	private static void startBackEndNode(int port, String role) {
		Config config = ConfigFactory.parseString(
				"akka.remote.netty.tcp.port=" + port).
				withFallback(ConfigFactory.parseString("akka.cluster.roles = [" + role + "]")).
				withFallback(ConfigFactory.load());

		ActorSystem system = ActorSystem.create("TastetsReactiveCluster", config);

		system.actorOf(Props.create(ClusterBackActor.class),role);
	}

	private static void startFrontEndNode(int port, String role) {
		Config config = ConfigFactory.parseString(
				"akka.remote.netty.tcp.port=" + port).
				withFallback(ConfigFactory.parseString("akka.cluster.roles = [" + role + "]")).
				withFallback(ConfigFactory.load());

		ActorSystem system = ActorSystem.create("TastetsReactiveCluster", config);

		system.actorOf(Props.create(FrontActor.class), role);
	}
}
