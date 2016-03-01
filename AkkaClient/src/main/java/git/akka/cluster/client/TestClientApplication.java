package git.akka.cluster.client;

import akka.actor.ActorPath;
import akka.actor.ActorPaths;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientSettings;
import akka.dispatch.OnSuccess;
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
import static akka.pattern.Patterns.ask;
import cl.gps.drivers.objects.DeviceEvent;

/**
 *
 * @author gaston
 */
public class TestClientApplication {

	public static void main(String[] args) throws Exception {
		Config config = ConfigFactory.parseString(
				"akka.remote.netty.tcp.port=0").
				withFallback(ConfigFactory.load("application.conf"));

		ActorSystem system = ActorSystem.create("ClientSystem", config);

		//Configuramos el cliente del cluster, indicandole las direcciones de donde estan los recepcionistas del cluster		
		Set<ActorPath> initialContacts = new HashSet<>();
		for (String contactAddress : config.getStringList("contact-points")) {
			initialContacts.add(ActorPaths.fromString(contactAddress + "/system/receptionist"));
		}
		final ActorRef clusterClient = system.actorOf(
				ClusterClient.props(ClusterClientSettings.create(system).withInitialContacts(initialContacts)), "client");

		ActorRef client = system.actorOf(Props.create(TestClient.class, clusterClient, "receptionist"));

		final FiniteDuration interval = Duration.create(10, TimeUnit.SECONDS);
		final Timeout timeout = new Timeout(Duration.create(20, TimeUnit.SECONDS));
		final ExecutionContext ec = system.dispatcher();
		system.scheduler().schedule(interval, interval, () -> {
			ask(client, getDeviceEvent(),
					timeout).onSuccess(new OnSuccess<Object>() {
						@Override
						public void onSuccess(Object result) {
							System.out.println(result);
						}
					}, ec);
		}, ec);

	}
	
	private static DeviceEvent getDeviceEvent(){
		DeviceEvent de=new DeviceEvent();
		de.setLatitude(Math.random());
		de.setLongitude(Math.random());
		return de;
	}

}
