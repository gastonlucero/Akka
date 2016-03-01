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
 * Main class que se encarga de crear las referencias a los actores de prueba y en via mensajes a cada uno de ellos cada
 * cierto tiempo (valor configurado en las propiedades)
 *
 * @author gaston
 */
public class AkkaSampleMain {

	public static void main(String[] args) {
		//Se lean las configuraciones del archivo sample.conf
		Config config = ConfigFactory.load("sample.conf");
		ActorSystem system = ActorSystem.create("akkaSystem", config);
		
		//Creo las referencias a los dos actores de prueba
		ActorRef sampleActor = system.actorOf(Props.create(SampleActor.class), "sampleActor");
		ActorRef sampleActorLambda = system.actorOf(Props.create(SampleActorLambda.class), "sampleActorLambda");

		//Cada N segundos se envian un mensajes a cada uno de ellos
		final FiniteDuration interval = Duration.create(config.getInt("akka.interval"), TimeUnit.SECONDS);		
		final ExecutionContext ec = system.dispatcher();
		system.scheduler().schedule(interval, interval, () -> {
			sampleActor.tell(Math.random(),ActorRef.noSender());
			sampleActorLambda.tell(Math.random(),ActorRef.noSender());					
		}, ec);

	}
}
