package git.gaston.akka.sample;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnSuccess;
import static akka.pattern.Patterns.ask;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.concurrent.TimeUnit;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 *
 * @author gaston
 */
public class AkkaSampleMain {
	public static void main(String[] args) {
		Config config = ConfigFactory.load("sample.conf");
		ActorSystem system = ActorSystem.create("akkaSystem", config);
		ActorRef sampleActor= system.actorOf(Props.create(SampleActor.class),"sampleActor");		
		ActorRef sampleActorLambda=  system.actorOf(Props.create(SampleActorLambda.class),"sampleActorLambda");
		
		final FiniteDuration interval = Duration.create(1, TimeUnit.SECONDS);
		final Timeout timeout = new Timeout(Duration.create(1, TimeUnit.SECONDS));
		final ExecutionContext ec = system.dispatcher();
		system.scheduler().schedule(interval, interval, () -> {			
			ask(sampleActor, Math.random(),
					timeout);
			ask(sampleActorLambda, Math.random(),
					timeout);
		}, ec);

	}
}
