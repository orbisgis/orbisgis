/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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

import com.sun.imageio.plugins.common.I18N;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import javax.swing.JOptionPane;
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

    public ActionDeploy(String label, String toolTipText, boolean start, Resource resource,BundleContext bundleContext,Component frame) {
        super(label, toolTipText);
        this.start = start;
        this.resource = resource;
        this.bundleContext = bundleContext;
        this.frame = frame;
        setActionListener(EventHandler.create(ActionListener.class, this, "doWork"));
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
            Resolver resolver = repositoryAdmin.resolver();
            resolver.add(resource);

            if ((resolver.getAddedResources() != null) &&
                    (resolver.getAddedResources().length > 0))
            {
                // Find dependencies
                if (resolver.resolve()) {
                    long bytes = getSize(resource);
                    StringBuilder resourcesNames = new StringBuilder();

                    Resource[] resources = resolver.getRequiredResources();
                    if ((resources != null) && (resources.length > 0)) {
                        for (int resIdx = 0; resIdx < resources.length; resIdx++) {
                            Resource dependency = resources[resIdx];
                            resourcesNames.append(dependency.getPresentationName());
                            resourcesNames.append(" (");
                            resourcesNames.append(dependency.getVersion());
                            resourcesNames.append(")\n");
                            bytes += getSize(dependency);
                        }
                    }
                    resources = resolver.getOptionalResources();
                    if ((resources != null) && (resources.length > 0)) {
                        for (int resIdx = 0; resIdx < resources.length; resIdx++) {
                            Resource dependency = resources[resIdx];
                            bytes += getSize(dependency);
                            resourcesNames.append("Optional, ");
                            resourcesNames.append(dependency.getPresentationName());
                            resourcesNames.append(" (");
                            resourcesNames.append(dependency.getVersion());
                            resourcesNames.append(")\n");
                        }
                    }
                    // If there is hidden dependency
                    // Ask user for validating download
                    boolean deploy = true;
                    if(resourcesNames.length()>0) {
                        resourcesNames.insert(0,") ?\n");
                        resourcesNames.insert(0,BundleItem.getHumanReadableBytes(bytes));
                        resourcesNames.insert(0,"Do you want to download the following dependencies (Size : ");
                        String[] options = {I18N.tr("Yes"),
                                I18N.tr("Cancel")};
                        int n = JOptionPane.showOptionDialog(frame, resourcesNames.toString(), I18N.tr("Dependencies downloading"),
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                        deploy = n==JOptionPane.YES_OPTION;
                    }
                    if(deploy) {
                        try
                        {
                            resolver.deploy(start);
                        }
                        catch (IllegalStateException ex)
                        {
                            LOGGER.error(ex.getLocalizedMessage(),ex);
                        }
                    }
                }
                else
                {
                    Requirement[] reqs = resolver.getUnsatisfiedRequirements();
                    if ((reqs != null) && (reqs.length > 0))
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append(I18N.trn("Unsatisfied requirement :\n","Unsatisfied requirements :\n",reqs.length+1));
                        for (int reqIdx = 0; reqIdx < reqs.length; reqIdx++)
                        {
                            sb.append("\t");
                            sb.append(reqs[reqIdx].getFilter());
                            sb.append("\n");
                            Resource[] resources = resolver.getResources(reqs[reqIdx]);
                            for (int resIdx = 0; resIdx < resources.length; resIdx++)
                            {
                                sb.append("\t");
                                sb.append(resources[resIdx].getPresentationName());
                                sb.append(" (");
                                sb.append(resources[resIdx].getVersion());
                                sb.append(")\n");
                            }
                        }
                        LOGGER.error(sb.toString());
                    }
                    else
                    {
                        LOGGER.error(I18N.tr("Could not resolve plug-in for unknown reason"));
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
}