package git.akka.cluster.client;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.client.ClusterClient;
import cl.gps.drivers.objects.DeviceEvent;

/**
 * Esta clase representa el actor que trabaja como cliente, encargado de mandar peticiones a los servicios dentro del
 * cluster
 *
 * @author gaston
 */
public class TestClient extends UntypedActor {

	/**
	 * ClusterClient http://doc.akka.io/docs/akka/2.4.1/java/cluster-client.html
	 */
	private ActorRef clusterClient;

	/**
	 * Nombre del servicio dentro del cluster al cual se quiere consultar
	 */
	private String serviceName;

	public TestClient(ActorRef clusterClient, String serviceName) {
		this.clusterClient = clusterClient;
		this.serviceName = serviceName;
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof DeviceEvent) {
			((DeviceEvent)msg).put("sender",getSelf());
			//Mensaje simulado que va hacia el cluster
			clusterClient.tell(new ClusterClient.Send("/user/" + serviceName,msg, true), ActorRef.noSender());

		} else if (msg instanceof Integer) {
			//mensaje simulando la respuesta desde algun actor conectado al cluster
			System.out.println("OK =" + msg);
		} else {
			unhandled(msg);
		}
	}

}
