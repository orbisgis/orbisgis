/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.omanager.ui;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.concurrent.ExecutorService;
import javax.swing.*;

import org.orbisgis.commons.progress.SwingWorkerPM;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Requirement;
import org.osgi.service.obr.Resolver;
import org.osgi.service.obr.Resource;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Download plug-in process.
 * @author Nicolas Fortin
 */
public class ActionDeploy  extends ActionBundle {
    private static final I18n I18N = I18nFactory.getI18n(ActionDeploy.class);
    private boolean start;
    private Resource resource;
    private BundleContext bundleContext;
    private Component frame;
    private ExecutorService executorService;

    public ActionDeploy(String label, String toolTipText, boolean start, Resource resource, BundleContext bundleContext,
                        Component frame, Icon icon, boolean warnUser, ExecutorService executorService) {
        super(label, toolTipText, icon, frame, warnUser);
        this.start = start;
        this.resource = resource;
        this.bundleContext = bundleContext;
        this.frame = frame;
        this.executorService = executorService;
        setActionListener(EventHandler.create(ActionListener.class, this, "doWork"));
    }
    private void deployBundle(Resolver resolver) {
        long bytes = getSize(resource);
        StringBuilder resourcesNames = new StringBuilder();

        // If the bundle has dependencies, store the dependency name, version and size
        Resource[] resources = resolver.getRequiredResources();
        StringBuilder dependencyStr = new StringBuilder();
        if ((resources != null) && (resources.length > 0)) {
            for (Resource dependency : resources) {
                dependencyStr.append(dependency.getPresentationName());
                dependencyStr.append(" (");
                dependencyStr.append(dependency.getVersion());
                dependencyStr.append(")\n");
                bytes += getSize(dependency);
            }
        }

        // If the bundle has optional dependencies, store the dependency name, version and size
        resources = resolver.getOptionalResources();
        if ((resources != null) && (resources.length > 0)) {
            for (Resource dependency : resources) {
                bytes += getSize(dependency);
                dependencyStr.append("Optional, ");
                dependencyStr.append(dependency.getPresentationName());
                dependencyStr.append(" (");
                dependencyStr.append(dependency.getVersion());
                dependencyStr.append(")\n");
            }
        }
        // If there is hidden dependency (needed & optional)
        // Ask user for validating additional download
        boolean deployBundle = true;
        if(dependencyStr.length() != 0) {
            if (dependencyStr.length() > 0) {
                resourcesNames.insert(0, ") ?\n");
                resourcesNames.insert(0, BundleItem.getHumanReadableBytes(bytes));
                resourcesNames.insert(0, "Do you want to download the following dependencies (Size : ");
            }
            if (DependencyMessageDialog.showModal(SwingUtilities.getWindowAncestor(frame),
                    I18N.tr("Dependencies downloading"), resourcesNames.toString(), dependencyStr.toString())
                    .equals(DependencyMessageDialog.CHOICE.CANCEL)) {
                deployBundle = false;
            }
        }
        if(deployBundle){
            DeployBundleSwingWorker worker = new DeployBundleSwingWorker(resolver);
            if(executorService != null){
                executorService.execute(worker);
            }
            else{
                worker.execute();
            }
        }
    }
    /**
     * Called by user on action launch.
     */
    public void doWork() {
        ServiceReference<RepositoryAdmin> repositoryAdminServiceReference = bundleContext.getServiceReference(RepositoryAdmin.class);
        if(repositoryAdminServiceReference==null) {
            LOGGER.error(I18N.tr("OSGi repository service is not available"));
            return;
        }
        RepositoryAdmin repositoryAdmin = bundleContext.getService(repositoryAdminServiceReference);
        if(repositoryAdmin==null) {
            LOGGER.error(I18N.tr("OSGi repository service is not available"));
            return;
        }
        try {
            // Specify the bundle reference to the dependency resolver
            // Resolver class is able to fetch cached repositories and
            // fetch dependencies for one or more bundles.
            Resolver resolver = repositoryAdmin.resolver();
            resolver.add(resource);

            if ((resolver.getAddedResources() != null) &&
                    (resolver.getAddedResources().length > 0)) {
                // Find dependencies
                if (resolver.resolve()) {
                    // OSGi Bundle Repository is able to deploy this bundle
                    deployBundle(resolver);
                } else {
                    // Some dependency is missing
                    // OSGi Bundle Repository is not able to deploy this bundle
                    // Log missing packages
                    Requirement[] requirements = resolver.getUnsatisfiedRequirements();
                    if ((requirements != null) && (requirements.length > 0)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(I18N.trn("Unsatisfied requirement :\n","Unsatisfied requirements :\n",requirements.length+1));
                        for (Requirement requirement : requirements) {
                            sb.append("\t");
                            sb.append(requirement.getFilter());
                            sb.append("\n");
                            for (Resource resource : resolver.getResources(requirement)) {
                                sb.append("\t");
                                sb.append(resource.getPresentationName());
                                sb.append(" (");
                                sb.append(resource.getVersion());
                                sb.append(")\n");
                            }
                        }
                        LOGGER.error(sb.toString());
                    } else {
                        LOGGER.error(I18N.tr("Could not resolve plugin dependencies for an unknown reason"));
                    }
                }
            }
        } catch(Exception ex) {
            LOGGER.error(ex.getLocalizedMessage(),ex);
        } finally {
            bundleContext.ungetService(repositoryAdminServiceReference);
        }
    }

    private long getSize(Resource resource) {
        Object depSize = resource.getProperties().get(Resource.SIZE);
        if(depSize instanceof Long) {
            return(Long) depSize;
        } else {
            return 0L;
        }
    }

    private class DeployBundleSwingWorker extends SwingWorkerPM {

        private Resolver resolver;

        public DeployBundleSwingWorker(Resolver resolver){
            this.resolver = resolver;
        }

        @Override
        protected Object doInBackground() throws Exception {
            setTaskName(I18N.tr("Downloading and installing dependencies"));
            try {
                // Download the bundle and dependencies
                resolver.deploy(start);
            } catch (IllegalStateException ex) {
                LOGGER.error(ex.getLocalizedMessage(),ex);
            }
            return null;
        }
    }
}
