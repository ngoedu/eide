package org.ngo.eide;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimElement;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.progress.UIJob;
import org.ngo.eide.host.NgoEndpoint;
import org.ngo.ether.endpoint.EndpointHandler;
import org.ngo.ether.endpoint.EndpointSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NgoStartup implements IStartup {
	private final static Logger LOGGER = LoggerFactory.getLogger(NgoStartup.class);
	private EndpointHandler handler;

	private void startAetherEndpoint() {
		int port = Integer.valueOf(System.getProperty("ngo.bridge.port", "60001"));
		String host = System.getProperty("ngo.bridge.host", "127.0.0.1");

		SocketAddress address = new InetSocketAddress(host, port);
		

		handler = new EndpointHandler(NgoEndpoint.instance, (short) NgoEndpoint.NGO_ID);
		EndpointSupport client = new EndpointSupport("IDE", handler);
		NgoEndpoint.instance.setClient(client);

		if (!client.connect(address, false)) {
			LOGGER.error(String.format("failed to connect to %s", address));
		} else {
			LOGGER.info(String.format("successfully connected to %s", address));
		}
	}

	@Override
	public void earlyStartup() {
		startAetherEndpoint();	
	}
}
