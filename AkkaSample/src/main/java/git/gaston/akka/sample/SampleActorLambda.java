package git.gaston.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

/**
 *
 * @author gaston
 */
public class SampleActorLambda extends AbstractActor {

	private ActorRef logActor;

	public SampleActorLambda() {
		logActor = getContext().actorOf(Props.create(LogActor.class));
		receive(ReceiveBuilder.				
				match(Double.class, intMsg -> {
					logActor.tell("DoubleMsg=" + intMsg, self());
				}).build());
	}

}
