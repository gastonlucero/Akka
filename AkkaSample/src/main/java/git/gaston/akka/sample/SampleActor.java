package git.gaston.akka.sample;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 *
 * @author gaston
 */
public class SampleActor extends UntypedActor {

	private ActorRef logActor;

	public SampleActor() {
		logActor = getContext().actorOf(Props.create(LogActor.class));
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof Double){
			logActor.tell("DoubleMsg="+message, self());
		}else{
			unhandled(message);
		}
	}

}