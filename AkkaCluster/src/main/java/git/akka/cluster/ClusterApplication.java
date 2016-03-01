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
		startBackEndNode(Integer.valueOf(args[0]), "backend");
//		Thread.sleep(10000);
//		startBackEndNode(2552, "backend");
//		Thread.sleep(10000);
//		startFrontEndNode(0, "frontend");
		
	}

	/**
	 * Levanta nodos backend en el cluster
	 *
	 * @param port Puerto en que inicia el nodo
	 * @param role Rol del actor
	 */
	private static void startBackEndNode(int port, String role) {
		Config config = ConfigFactory.parseString(
				"akka.remote.netty.tcp.port=" + port).
				withFallback(ConfigFactory.parseString("akka.cluster.roles = [" + role + "]")).
				withFallback(ConfigFactory.load("backend.conf"));

		ActorSystem system = ActorSystem.create("ClusterSystem", config);
		system.actorOf(Props.create(ClusterBackendActor.class, "frontend"), role);
	}

	/**
	 * Levanta nodos frontend en el cluster, que son lo que son visibles a las aplicaciones externas
	 *
	 * @param port Puerto en que inicia el backend
	 * @param role Rol del actor
	 */
	private static void startFrontEndNode(int port, String role) {
		Config config = ConfigFactory.parseString(
				"akka.remote.netty.tcp.port=" + port).
				withFallback(ConfigFactory.parseString("akka.cluster.roles = [" + role + "]")).
				withFallback(ConfigFactory.load("frontend.conf"));

		ActorSystem system = ActorSystem.create("ClusterSystem", config);
		system.actorOf(Props.create(ClusterFrontendActor.class), "serviceFrontEnd");
	}
}
