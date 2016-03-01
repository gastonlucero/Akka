package git.akka.cluster.deployment;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 *
 * @author gaston
 */
public class RemoteDeployApplication {

	public static void main(String[] args) throws Exception {
		startBackEndNode(Integer.valueOf(args[0]), "backend");
		Thread.sleep(10000);		
	}

	private static void startBackEndNode(int port, String role) {
		Config config = ConfigFactory.parseString(
				"akka.remote.netty.tcp.port=" + port).
				withFallback(ConfigFactory.parseString("akka.cluster.roles = [" + role + "]")).
				withFallback(ConfigFactory.load("remote.conf"));

		ActorSystem system = ActorSystem.create("ClusterSystem", config);
		if (port == 2555) {
			ActorRef actor = system.actorOf(Props.create(ActorRemote.class), "sampleActor");
			actor.tell("hola sos el actor sos el remoto", ActorRef.noSender());
		}
	}

}
