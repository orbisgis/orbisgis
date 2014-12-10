package org.orbisgis.wkgui;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingWorker;

/**
 * Registers services provided by this plugin bundle.
 */
public class Activator implements BundleActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    /**
     * Starting bundle, register services.
     *
     * @param bc
     * @throws Exception
     */
    @Override
    public void start(BundleContext bc) throws Exception {
        Version bundleVersion = bc.getBundle().getVersion();
        new LogState(bc).execute();
    }

    private static void logBundleState(BundleContext context) {
        LOGGER.info("Built-In bundle list :");
        LOGGER.info("ID\tState\t\tBundle name");
        for (Bundle bundle : context.getBundles()) {
            LOGGER.info("[" + String.format("%02d", bundle.getBundleId()) + "]\t" + getStateString(bundle.getState())
                    + "\t" + bundle.getSymbolicName());
            // Print services
            ServiceReference[] refs = bundle.getRegisteredServices();
            if (refs != null) {
                for (ServiceReference ref : refs) {
                    String refDescr = ref.toString();
                    if (!refDescr.contains("org.osgi") && !refDescr.contains("org.apache")) {
                        LOGGER.info("\t\t\t\t" + ref);
                    }
                }
            }
        }
    }

    private static String getStateString(int i) {
        switch (i) {
            case Bundle.ACTIVE:
                return "Active   ";
            case Bundle.INSTALLED:
                return "Installed";
            case Bundle.RESOLVED:
                return "Resolved ";
            case Bundle.STARTING:
                return "Starting ";
            case Bundle.STOPPING:
                return "Stopping ";
            default:
                return "Unknown  ";
        }
    }

    /**
     * Called before the bundle is unloaded.
     *
     * @param bc
     * @throws Exception
     */
    @Override
    public void stop(BundleContext bc) throws Exception {

    }

    private static class LogState extends SwingWorker {
        private BundleContext bc;

        private LogState(BundleContext bc) {
            this.bc = bc;
        }

        @Override
        protected Object doInBackground() throws Exception {
            logBundleState(bc);
            return null;
        }
    }
}
