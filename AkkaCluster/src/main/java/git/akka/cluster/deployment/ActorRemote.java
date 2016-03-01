package git.akka.cluster.deployment;

import akka.actor.UntypedActor;

/**
 *
 * @author gaston
 */
public class ActorRemote extends UntypedActor{

	@Override
	public void onReceive(Object o) throws Exception {
		System.out.println(o);
	}

}
