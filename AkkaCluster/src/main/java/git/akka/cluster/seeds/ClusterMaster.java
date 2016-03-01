package git.akka.cluster.seeds;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.Arrays;

/**
 *
 * @author gaston
 */
public class ClusterMaster {

	//tiene que levantar todos los seeds en 2551, 2552, 2553
	public static void main(String[] args) throws Exception {
		ClusterMaster master = new ClusterMaster();
		for (Integer back : Arrays.asList(3000,4000)) {
			master.startMasterNode(back, "master");
			Thread.sleep(5000);
		}
	}

	private void startMasterNode(int port, String role) {
		Config config = ConfigFactory.parseString(
				"akka.remote.netty.tcp.port=" + port).
				withFallback(ConfigFactory.parseString("akka.cluster.roles = [" + role + "]")).
				withFallback(ConfigFactory.load("master.conf"));
		ActorSystem system = ActorSystem.create("ClusterSystem", config);
		system.actorOf(Props.create(ClusterJobReceptionist.class), "receptionist");
//		Cluster.get(system).registerOnMemberUp(() -> {
//			//Cuando se levanta , crea el recep
//			system.actorOf(Props.create(ClusterJobReceptionist.class), "receptionist");
//		});	
	}

}
