package git.akka.cluster.seeds;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.Arrays;

/**
 *
 * @author gaston
 */
public class ClusterSeed {

	//tiene que levantar todos los seeds en 2551, 2552, 2553
	public static void main(String[] args) throws Exception {
		ClusterSeed seed = new ClusterSeed();
		for (Integer port : Arrays.asList(2551, 2552, 2553)) {
			seed.startSeed(port);
			Thread.sleep(5000);
		}

	}

	private void startSeed(int port) {
		Config config = ConfigFactory.parseString(
				"akka.remote.netty.tcp.port=" + port).
				withFallback(ConfigFactory.load("seeds.conf"));
		ActorSystem system = ActorSystem.create("ClusterSystem", config);
	}

}
