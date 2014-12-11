package org.orbisgis.wkgui;

import org.orbisgis.framework.CoreWorkspaceImpl;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.wkgui.gui.ViewWorkspaceImpl;
import org.orbisgis.wkgui.gui.WorkspaceSelectionDialog;
import org.orbisgis.wkguiapi.ViewWorkspace;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingWorker;
import java.util.concurrent.ExecutionException;

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
        new ShowWorkspaceSelectionDialog(bc).execute();
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

    private static class ShowWorkspaceSelectionDialog extends SwingWorker<CoreWorkspaceImpl, CoreWorkspaceImpl> {
        private BundleContext bc;

        public ShowWorkspaceSelectionDialog(BundleContext bc) {
            this.bc = bc;
        }

        @Override
        protected CoreWorkspaceImpl doInBackground() throws Exception {
            Version bundleVersion = bc.getBundle().getVersion();
            logBundleState(bc);
            return new CoreWorkspaceImpl(bundleVersion.getMajor(), bundleVersion.getMinor(),
                    bundleVersion.getMicro(), bundleVersion.getQualifier());
        }

        @Override
        protected void done() {
            try {
                CoreWorkspaceImpl coreWorkspace = get();
                if(WorkspaceSelectionDialog.showWorkspaceFolderSelection(null, coreWorkspace)) {
                    // User validate publish CoreWorkspace to OSGi
                    try {
                        bc.registerService(CoreWorkspace.class, coreWorkspace, null);
                        ViewWorkspace viewWorkspace = new ViewWorkspaceImpl(coreWorkspace);
                        bc.registerService(ViewWorkspace.class, viewWorkspace, null);
                    } catch (Exception ex) {
                        LOGGER.error(ex.getLocalizedMessage(), ex);
                        bc.getBundle(0).stop();
                    }
                } else {
                    // User cancel, stop OrbisGIS
                    bc.getBundle(0).stop();
                }
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.error("Could not init workspace", ex);
            } catch (BundleException ex) {
                LOGGER.error("Could not stop OrbisGIS", ex);
            }
        }
    }
}
