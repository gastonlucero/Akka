package git.akka.cluster;

import akka.actor.ActorRef;
import akka.actor.Props;
import git.akka.cluster.messages.ClusterRegistryMessage;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.Member;
import akka.cluster.client.ClusterClientReceptionist;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import git.akka.cluster.messages.ClusterTestMessage;
import git.akka.cluster.persistence.MongoAkkaActor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Actor que representa los nodos master del cluster, son los encargados de recibir las peticiones de los nodos frontend
 * y derivarlos a los clientes que hicieron las peticiones
 *
 * @author gaston
 */
public class ClusterBackendActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	/**
	 * Conexión al cluster, el primero que se conecta toma el papel de primario
	 */
	Cluster cluster = Cluster.get(getContext().system());

	/**
	 * EL actor se suscribe a los eventos que se produzcan en el cluster, como MemberUp, que se envia automaticamente
	 * cuando un nuevo actor se conecta al cluster Además se registra como recepcionista de peticiones, para desacoplar
	 * las llamadas desde los clientes a los servicios frontend que realizan el trabajo solicitado por los clientes
	 */
	@Override
	public void preStart() {
		cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), MemberUp.class);
		ClusterClientReceptionist.get(getContext().system()).registerService(getSelf());
	}

	private List<Member> members;

	private String frontEndRole;
	ActorRef mongo;

	public ClusterBackendActor(String frontEndRole) {
		members = Collections.synchronizedList(new ArrayList<>());
		this.frontEndRole = frontEndRole;
		mongo = getContext().actorOf(Props.create(MongoAkkaActor.class));
	}

	@Override
	public void postStop() {
		cluster.unsubscribe(getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MemberUp) {
			MemberUp mUp = (MemberUp) message;
			log.info("\n MemberUp - Nuevo nodo del cluster {}", mUp.member());
			registerMember(mUp.member());
		} else if (message instanceof ClusterTestMessage) {
			log.info("\n Mensaje recibido " + message + " {}", getSender().path());
			mongo.tell("dale", getSelf());
			((ClusterTestMessage) message).getOriginalSender().tell(200, ActorRef.noSender());
		}else if(message instanceof String){
			log.info("\n Mensaje recibido " + message + " {}", getSender().path());
		} 
		else {
			unhandled(message);
		}
	}

	/**
	 * A cada member que tenga el rol de actor frontend, se le notifica que ha sido registrado en el cluster, para que
	 * este actor guarde al referencia a esta nodo del cluster. Este mismo mensaje se manda al actor front end por cada
	 * nodo backend que este registrado en el cluster
	 *
	 * @param memberUp
	 */
	private void registerMember(Member memberUp) {
		if (memberUp.hasRole(frontEndRole)) {
			members.add(memberUp);
			getContext().actorSelection(memberUp.address() + "/user/serviceFrontEnd").tell(
					new ClusterRegistryMessage(), getSelf());
		}
	}

	public Supplier<ClusterRegistryMessage> supplier() {
		Supplier<ClusterRegistryMessage> supplier = ClusterRegistryMessage::new;
		return supplier;
	}
}
