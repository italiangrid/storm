package it.grid.storm.balancer.http;

import it.grid.storm.balancer.AbstractNode;
import it.grid.storm.https.HTTPPluginManager;
import it.grid.storm.https.HTTPSPluginInterface;
import it.grid.storm.namespace.model.Protocol;

public class HTTPSNode extends AbstractNode {

	private static final String prefix = Protocol.HTTPS.getProtocolPrefix();

	public HTTPSNode(String hostname, int port) {

		super(hostname, port);
	}

	public HTTPSNode(String hostname, int port, int weight) {

		super(hostname, port, weight);
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(prefix);
		sb.append(hostname);
		sb.append(":" + port);
		return sb.toString();
	}

	@Override
	public boolean checkServer() throws Exception {

		return HTTPPluginManager.getHTTPSPluginInstance().getServiceStatus(
			hostname, port, HTTPSPluginInterface.Protocol.HTTPS) == HTTPSPluginInterface.ServiceStatus.RUNNING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		return result;
	}

}
