package org.orbisgis.omanager.plugin.api;

import org.orbisgis.commons.progress.ProgressMonitor;
import org.osgi.service.obr.Repository;

import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.Collection;
import java.util.List;

public interface RepositoryPluginHandler {

    Collection<Repository> getRepositories();
    List<URL> getRepositoriesURL();
    /**
     * @return Parsed resource information on the repository XML.
     */
    Collection<Plugin> getResources();


    /**
     * Add or remove listener on repository updates.
     * @return
     */
    PropertyChangeSupport getPropertyChangeSupport();


    /**
     * Reload the list of resource by downloading and parsing all repositories XML again.
     */
    void refresh(ProgressMonitor pm);

    void addRepository(URL repository);

    boolean removeRepository(URL repository);
}
