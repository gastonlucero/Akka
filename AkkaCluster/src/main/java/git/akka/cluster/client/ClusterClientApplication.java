package git.akka.cluster.client;

import akka.actor.ActorPath;
import akka.actor.ActorPaths;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientSettings;
import akka.dispatch.OnSuccess;
import static akka.pattern.Patterns.ask;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 *
 * @author gaston
 */
public class ClusterClientApplication {

	public static void main(String[] args) throws Exception {
		Config config = ConfigFactory.parseString(
				"akka.remote.netty.tcp.port=0").
				withFallback(ConfigFactory.parseString("akka.cluster.roles = [front]")).
				withFallback(ConfigFactory.load());

		ActorSystem system = ActorSystem.create("ClusterSystem", config);

		final ActorRef clusterClient = system.actorOf(ClusterClient.props(
				ClusterClientSettings.create(system).withInitialContacts(initialContacts())),
				"client");

		ActorRef hijo = system.actorOf(Props.create(AkkaClusterClient.class, clusterClient),
				"frontClient");

		final FiniteDuration interval = Duration.create(30, TimeUnit.SECONDS);
		final Timeout timeout = new Timeout(Duration.create(10, TimeUnit.SECONDS));
		final ExecutionContext ec = system.dispatcher();
		system.scheduler().schedule(interval, interval, () -> {
			ask(hijo, "Va un mensaje",
					timeout).onSuccess(new OnSuccess<Object>() {
						@Override
						public void onSuccess(Object result) {
							System.out.println(result);
						}
					}, ec);
		}, ec);

	}

	static Set<ActorPath> initialContacts() {
		return new HashSet<ActorPath>(Arrays.asList(
				ActorPaths.fromString("akka.tcp://ClusterSystem@127.0.0.1:2551/system/receptionist"),
				ActorPaths.fromString("akka.tcp://ClusterSystem@127.0.0.1:2552/system/receptionist")));
	}
}
