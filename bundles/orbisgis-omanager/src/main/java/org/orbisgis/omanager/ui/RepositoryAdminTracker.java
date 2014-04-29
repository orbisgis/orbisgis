/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Resource;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author Nicolas Fortin
 */
public class RepositoryAdminTracker implements ServiceTrackerCustomizer<RepositoryAdmin,RepositoryAdmin> {
    public static final String PROP_REPOSITORIES = "repositories";
    public static final String PROP_RESOURCES = "resources";
    private static final I18n I18N = I18nFactory.getI18n(RepositoryAdminTracker.class);
    private static final Logger LOGGER = Logger.getLogger(RepositoryAdminTracker.class);
    private BundleContext bundleContext;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private RepositoryAdmin repoAdmin;
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

    public Collection<Repository> getRepositories() {
        // Use of Set instead of list, to let propertyChangeSupport find difference on set, without sorting importance
        if(isRepositoryAdminAvailable()) {
            return new HashSet(Arrays.asList(repoAdmin.listRepositories()));
        } else {
            return new HashSet();
        }
    }
    public List<URL> getRepositoriesURL() {
        if(!isRepositoryAdminAvailable()) {
            return new ArrayList<URL>();
        }
        List<URL> repoURLS = new ArrayList<URL>();
        for(Repository repository : repoAdmin.listRepositories()) {
            repoURLS.add(repository.getURL());
        }
        return Collections.unmodifiableList(repoURLS);
    }

    /**
     * @return Parsed resource information on the repository XML.
     */
    public Collection<Resource> getResources() {
        // Use of Set instead of list, to let propertyChangeSupport find difference on set, without sorting importance
        if(isRepositoryAdminAvailable()) {
            Set<Resource> resourceList = new HashSet<Resource>();
            for(Repository newRepo : getRepositories()) {
                // Copy resources reference to the resource list
                resourceList.addAll(Arrays.asList(newRepo.getResources()));
            }
            return resourceList;
        } else {
            return new HashSet<Resource>();
        }
    }

    public RepositoryAdmin addingService(ServiceReference<RepositoryAdmin> reference) {
        repoAdmin = bundleContext.getService(reference);
        propertyChangeSupport.firePropertyChange(PROP_REPOSITORIES,new HashSet<Repository>(),getRepositories());
        propertyChangeSupport.firePropertyChange(PROP_RESOURCES,new HashSet<Resource>(),getResources());
        return repoAdmin;
    }

    public void modifiedService(ServiceReference<RepositoryAdmin> reference, RepositoryAdmin service) {
        Collection<Repository> repositories = getRepositories();
        repoAdmin = bundleContext.getService(reference);
    }

    public void removedService(ServiceReference<RepositoryAdmin> reference, RepositoryAdmin service) {
        repoAdmin = null;
    }

    /**
     * Reload the list of resource by downloading and parsing all repositories XML again.
     */
    public void refresh() {
        if(isRepositoryAdminAvailable()) {
            List<URL> repoURLS = new ArrayList<URL>();
            for(Repository repository : repoAdmin.listRepositories()) {
                repoURLS.add(repository.getURL());
            }
            for(URL repoURL : repoURLS) {
                if(repoAdmin!=null) {
                    repoAdmin.removeRepository(repoURL);
                }
                if(repoAdmin!=null) {
                    try {
                        // Download the repository XML
                        repoAdmin.addRepository(repoURL);
                    } catch (Exception ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                    }
                }
            }
        }
    }
    private boolean isRepositoryAdminAvailable() {
        if(repoAdmin==null) {
            LOGGER.error(I18N.tr("RepositoryAdmin bundle is not available"));
            return false;
        } else {
            return true;
        }
    }
    public void addRepository(URL repository) {
        if(isRepositoryAdminAvailable()) {
            Collection<Repository> oldValue = getRepositories();
            try {
                repoAdmin.addRepository(repository);
                propertyChangeSupport.firePropertyChange(PROP_REPOSITORIES,oldValue,getRepositories());
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(),ex);
            }
        }
    }
    public boolean removeRepository(URL repository) {
        if(isRepositoryAdminAvailable()) {
            Collection<Repository> oldValue = getRepositories();
            try {
                boolean res = repoAdmin.removeRepository(repository);
                propertyChangeSupport.firePropertyChange(PROP_REPOSITORIES,oldValue,getRepositories());
                return res;
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(),ex);
            }
        }
        return false;
    }
}
