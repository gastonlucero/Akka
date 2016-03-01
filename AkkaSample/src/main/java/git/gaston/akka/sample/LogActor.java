package git.gaston.akka.sample;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Este actor simplemente logea los mensajes que le llegan
 *
 * @author gaston
 */
public class LogActor extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(context().system(), this);

	@Override
	public void onReceive(Object message) throws Exception {
		log.info("Mensaje {} desde {}", message, getSender().path());
	}

}
