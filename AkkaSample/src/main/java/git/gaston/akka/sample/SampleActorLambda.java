package git.gaston.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

/**
 * Actor com lambdas, que recibe los mensajes que se le envian desde la clase main y los reenvia al logActor, que es
 * creado en el constructor del mismo
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
