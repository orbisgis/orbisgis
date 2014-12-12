package org.orbisgis.wkgui;

import org.orbisgis.corejdbc.DataSourceService;
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
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Registers services provided by this plugin bundle.
 */
public class Activator implements BundleActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);
    private static final I18n I18N = I18nFactory.getI18n(Activator.class);

    /**
     * Starting bundle, register services.
     *
     * @param bc
     * @throws Exception
     */
    @Override
    public void start(BundleContext bc) throws Exception {
        SwingUtilities.invokeLater(new ShowWorkspaceSelectionDialog(bc));
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

    private static class ShowWorkspaceSelectionDialog implements Runnable {
        private BundleContext bc;

        public ShowWorkspaceSelectionDialog(BundleContext bc) {
            this.bc = bc;
        }

        @Override
        public void run() {
            Version bundleVersion = bc.getBundle().getVersion();
            try {
                try {
                    boolean connectionValid = false;
                    // Create a local DataSourceService to check connection properties
                    DataSourceService dataSourceService = new DataSourceService();
                    // Get OSGi service
                    Collection<ServiceReference<DataSourceFactory>> serviceReferences = bc.getServiceReferences
                            (DataSourceFactory.class, null);
                    try {
                        for(ServiceReference<DataSourceFactory> sr : serviceReferences) {
                            Map<String, String> properties = new HashMap<>();
                            properties.put(DataSourceFactory.OSGI_JDBC_DRIVER_NAME, (String) sr.getProperty
                                    (DataSourceFactory.OSGI_JDBC_DRIVER_NAME));
                            dataSourceService.addDataSourceFactory(bc.getService(sr), properties);
                        }
                        String errorMessage = "";
                        do {
                            CoreWorkspaceImpl coreWorkspace = new CoreWorkspaceImpl(bundleVersion.getMajor(), bundleVersion.getMinor(),
                                    bundleVersion.getMicro(), bundleVersion.getQualifier());
                            if (WorkspaceSelectionDialog.showWorkspaceFolderSelection(null, coreWorkspace, errorMessage)) {
                                /////////////////////
                                // Check connection
                                dataSourceService.setCoreWorkspace(coreWorkspace);
                                try {
                                    dataSourceService.activate();
                                    try(Connection connection = dataSourceService.getConnection()) {
                                        DatabaseMetaData meta = connection.getMetaData();
                                        LOGGER.info(I18N.tr("Data source available {0} version {1}", meta
                                                .getDriverName(), meta.getDriverVersion()));
                                        connectionValid = true;
                                    }
                                } catch (SQLException ex) {
                                    errorMessage = ex.getLocalizedMessage();
                                    connectionValid = false;
                                }
                            } else {
                                // User cancel, stop OrbisGIS
                                bc.getBundle(0).stop();
                                break;
                            }
                            if(connectionValid) {
                                // User validate with valid connection publish CoreWorkspace to OSGi
                                bc.registerService(CoreWorkspace.class, coreWorkspace, null);
                                ViewWorkspace viewWorkspace = new ViewWorkspaceImpl(coreWorkspace);
                                bc.registerService(ViewWorkspace.class, viewWorkspace, null);
                            }
                        } while (!connectionValid);
                    } finally {
                        dataSourceService = null;
                        // Unget services
                        for(ServiceReference<DataSourceFactory> sr : serviceReferences) {
                            bc.ungetService(sr);
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.error("Could not init workspace", ex);
                    bc.getBundle(0).stop();
                }
            } catch (BundleException ex) {
                LOGGER.error("Could not stop OrbisGIS", ex);
            }
        }
    }
}
