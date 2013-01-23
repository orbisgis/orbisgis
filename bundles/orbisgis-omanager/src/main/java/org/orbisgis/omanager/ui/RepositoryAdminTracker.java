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

import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Resource;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Nicolas Fortin
 */
public class RepositoryAdminTracker implements ServiceTrackerCustomizer<RepositoryAdmin,RepositoryAdmin> {
    public static final String PROP_REPOSITORIES = "repositories";
    public static final String PROP_RESOURCES = "resources";

    private List<URL> repositories = new ArrayList<URL>();
    private List<Resource> resources = new ArrayList<Resource>();

    private static final Logger LOGGER = Logger.getLogger(RepositoryAdminTracker.class);
    private BundleContext bundleContext;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    RepositoryAdmin repoAdmin;
    public RepositoryAdminTracker(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * Add or remove listener on repository updates.
     * @return
     */
    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    public List<URL> getRepositories() {
        return Collections.unmodifiableList(repositories);
    }

    public List<Resource> getResources() {
        return Collections.unmodifiableList(resources);
    }

    public RepositoryAdmin addingService(ServiceReference<RepositoryAdmin> reference) {
        RepositoryAdmin repoAdmin = bundleContext.getService(reference);
        return repoAdmin;
    }

    public void modifiedService(ServiceReference<RepositoryAdmin> reference, RepositoryAdmin service) {

    }

    public void removedService(ServiceReference<RepositoryAdmin> reference, RepositoryAdmin service) {
        repoAdmin = null;
    }

    /**
     * Reload the list of resource by downloading and parsing all repositories XML again.
     */
    public void refresh() {
        if(repoAdmin!=null) {
            List<URL> repoURLS = new ArrayList<URL>();
            for(Repository repository : repoAdmin.listRepositories()) {
                repoURLS.add(repository.getURL());
            }
            List<Resource> resourceList = new ArrayList<Resource>(resources.size());
            for(URL repoURL : repoURLS) {
                if(repoAdmin!=null) {
                    repoAdmin.removeRepository(repoURL);
                }
                if(repoAdmin!=null) {
                    try {
                        // Download the repository XML
                        Repository newRepo = repoAdmin.addRepository(repoURL);
                        // Copy resources reference to the resource list
                        resourceList.addAll(Arrays.asList(newRepo.getResources()));
                    } catch (Exception ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                    }
                }
            }
            setRepositories(repoURLS);
            setResources(resourceList);
        }
    }

    public void setRepositories(List<URL> repositories) {
        List<URL> oldValue = repositories;
        this.repositories = repositories;
        propertyChangeSupport.firePropertyChange(PROP_REPOSITORIES,oldValue,repositories);
    }

    private void setResources(List<Resource> resources) {
        List<Resource> oldValue = resources;
        this.resources = resources;
        propertyChangeSupport.firePropertyChange(PROP_RESOURCES,oldValue,resources);
    }
}
