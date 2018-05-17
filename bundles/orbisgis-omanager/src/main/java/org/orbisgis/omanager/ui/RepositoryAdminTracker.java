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

import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.orbisgis.omanager.plugin.api.Plugin;
import org.orbisgis.omanager.plugin.api.RepositoryPluginHandler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.obr.Resolver;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Component(service = RepositoryPluginHandler.class)
public class RepositoryAdminTracker implements ServiceTrackerCustomizer<RepositoryAdmin,RepositoryAdmin>, RepositoryPluginHandler {
    public static final String PROP_REPOSITORIES = "repositories";
    public static final String PROP_RESOURCES = "resources";
    private static final I18n I18N = I18nFactory.getI18n(RepositoryAdminTracker.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryAdminTracker.class);
    private BundleContext bundleContext;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private RepositoryAdmin repoAdmin;
    private Set<Plugin> resourceList = new HashSet<>();
    private ServiceTracker<RepositoryAdmin,RepositoryAdmin> repositoryAdminTracker;

    @Activate
    public void activate(BundleContext bc) {
        bundleContext = bc;
        repositoryAdminTracker = new ServiceTracker<>(bundleContext,
                RepositoryAdmin.class,this);
        repositoryAdminTracker.open();
    }

    @Deactivate
    public void deactivate(BundleContext bc) {
        repositoryAdminTracker.close();
        bundleContext = null;
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
    public Collection<Plugin> getResources() {
        return resourceList;
    }

    public RepositoryAdmin addingService(ServiceReference<RepositoryAdmin> reference) {
        repoAdmin = bundleContext.getService(reference);
        if(propertyChangeSupport.hasListeners(PROP_REPOSITORIES)) {
            propertyChangeSupport.firePropertyChange(PROP_REPOSITORIES, new HashSet<Repository>(), getRepositories());
        }
        if(propertyChangeSupport.hasListeners(PROP_RESOURCES)) {
            propertyChangeSupport.firePropertyChange(PROP_RESOURCES, new HashSet<Resource>(), getResources());
        }
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
    public void refresh(ProgressMonitor pm) {
        if(isRepositoryAdminAvailable()) {
            List<URL> repoURLS = new ArrayList<URL>();
            for(Repository repository : repoAdmin.listRepositories()) {
                repoURLS.add(repository.getURL());
            }
            ProgressMonitor refresh = pm.startTask(2);
            ProgressMonitor download = refresh.startTask(I18N.tr("Download plugins list from server"),
                    repoURLS.size());
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
                download.endTask();
            }
            // Update cached resources
            // cache only resolvable resources
            resourceList = new HashSet<>();
            int pluginCount = 0;
            for(Repository newRepo : getRepositories()) {
                // Copy resources reference to the resource list
                pluginCount += newRepo.getResources().length;
            }
            ProgressMonitor filterTask = refresh.startTask(I18N.tr("Check plugins dependencies") ,pluginCount);
            for(Repository newRepo : getRepositories()) {
                // Copy resources reference to the resource list
                for(Resource plugin : newRepo.getResources()) {
                    Resolver resolver = repoAdmin.resolver();
                    resolver.add(plugin);
                    BundleItem bundle = new BundleItem(bundleContext);
                    bundle.setObrResource(plugin, resolver.resolve());
                    resourceList.add(bundle);
                    filterTask.endTask();
                    if(filterTask.isCancelled()) {
                        return;
                    }
                }
            }
            if(propertyChangeSupport.hasListeners(PROP_RESOURCES)) {
                propertyChangeSupport.firePropertyChange(PROP_RESOURCES, new HashSet<Resource>(), getResources());
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
