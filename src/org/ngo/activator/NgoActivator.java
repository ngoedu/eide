package org.ngo.activator;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NgoActivator extends AbstractUIPlugin {
	private final static Logger LOGGER = LoggerFactory.getLogger(NgoActivator.class);

	
	public static final String PLUGIN_ID = "org.ngo.eclipse.plugin";

	private static NgoActivator plugin;

	public NgoActivator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		LOGGER.info("NgoActivator started");
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static NgoActivator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public static void logError(Object source, String msg, Throwable error) {
		String src = "";
		if (source instanceof Class)
			src = ((Class) source).getName();
		else if (source != null)
			src = source.getClass().getName();
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 1, "[" + src + "] " + msg, error));
	}
	
	public static void logInfo(Object source, String msg) {
		String src = "";
		if (source instanceof Class)
			src = ((Class) source).getName();
		else if (source != null)
			src = source.getClass().getName();
		getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, 1, "[" + src + "] " + msg, null));
	}
}
