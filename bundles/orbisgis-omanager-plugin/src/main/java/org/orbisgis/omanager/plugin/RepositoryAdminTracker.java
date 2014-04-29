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

package org.orbisgis.omanager.plugin;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.obr.Repository;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.osgi.service.obr.RepositoryAdmin;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Wait for the activation of the RepositoryAdmin.
 * Register the provided list of repository url to the RepositoryAdmin
 * @author Nicolas Fortin
 */
public class RepositoryAdminTracker implements ServiceTrackerCustomizer<RepositoryAdmin,RepositoryAdmin> {
    private BundleContext bc;
    private static final String OSGI_REPOSITORY_FILENAME = "repositories.properties";
    private final URI ORBISGIS_OSGI_REPOSITORY = URI.create("http://plugins.orbisgis.org/.meta/obr.xml");
    private final Logger logger = Logger.getLogger(RepositoryAdminTracker.class);
    private AtomicBoolean addingRepositories = new AtomicBoolean(false);

    /**
     * Constructor
     * @param bc BundleContext instance
     */
    public RepositoryAdminTracker(BundleContext bc) {
        this.bc = bc;
    }

    private void readRepositoryListFile(Set<URI> obrRepositories) {
        File repoListFile = bc.getDataFile(OSGI_REPOSITORY_FILENAME);
        if(repoListFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader((repoListFile)));
                try {
                    while(reader.ready()) {
                        String line = reader.readLine();
                        if(!line.trim().isEmpty()) {
                            try {
                                URI obrURI = URI.create(line);
                                obrRepositories.add(obrURI);
                            } catch (IllegalArgumentException ex) {
                                logger.error(ex.getLocalizedMessage(),ex);
                            }
                        }
                    }
                } finally {
                    reader.close();
                }
            } catch (IOException ex) {
                //Invalid property file, remove it
                logger.error(ex.getLocalizedMessage(),ex);
                if(!repoListFile.delete()) {
                    logger.error("Could not remove repository file "+repoListFile.getAbsolutePath());
                }
            }
        }
    }
    @Override
    public RepositoryAdmin addingService(ServiceReference<RepositoryAdmin> repositoryAdminServiceReference) {
        RepositoryAdmin repositoryAdmin = bc.getService(repositoryAdminServiceReference);
        if(!addingRepositories.getAndSet(true)) {
            // Read repositories
            Set<URI> obrRepositories = new HashSet<URI>();
            obrRepositories.add(ORBISGIS_OSGI_REPOSITORY);
            readRepositoryListFile(obrRepositories);
            RegisterSavedRepositories process = new RegisterSavedRepositories(obrRepositories,repositoryAdmin,addingRepositories);
            process.execute();
        }
        return repositoryAdmin;
    }

    @Override
    public void modifiedService(ServiceReference<RepositoryAdmin> repositoryAdminServiceReference, RepositoryAdmin repositoryAdmin) {

    }

    @Override
    public void removedService(ServiceReference<RepositoryAdmin> repositoryAdminServiceReference, RepositoryAdmin repositoryAdmin) {
        // Save repository list
        File repoListFile = bc.getDataFile(OSGI_REPOSITORY_FILENAME);
        final String LINE_SEP = System.getProperty("line.separator");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(repoListFile));
            try {
                for(Repository repository : repositoryAdmin.listRepositories()) {
                    writer.write(repository.getURL().toURI().toString()+LINE_SEP);
                }
            } finally {
                writer.close();
            }
        }catch (Exception ex) {
            logger.error("Could not save the plugins repository list",ex);
        }
    }

    /**
     * Register repositories in the BundleContext
     */
    private static class RegisterSavedRepositories extends SwingWorker<Boolean,Boolean> {
        private Set<URI> obrRepositories;
        private RepositoryAdmin repositoryAdmin;
        private final Logger logger = Logger.getLogger(RegisterSavedRepositories.class);
        private AtomicBoolean addingRepositories;

        private RegisterSavedRepositories(Set<URI> obrRepositories, RepositoryAdmin repositoryAdmin,AtomicBoolean addingRepositories) {
            this.obrRepositories = obrRepositories;
            this.repositoryAdmin = repositoryAdmin;
            this.addingRepositories = addingRepositories;
        }

        @Override
        protected Boolean doInBackground() throws Exception {        // Set additional repositories
            try {
                long startTime = System.currentTimeMillis();
                Set<URI> existing = new HashSet<URI>();
                for(Repository server : repositoryAdmin.listRepositories()) {
                    try {
                        existing.add(server.getURL().toURI());
                    } catch (Exception ex) {
                        logger.error(ex.getLocalizedMessage(),ex);
                    }
                }
                for(URI serverURI :obrRepositories) {
                    if(!existing.contains(serverURI)) {
                        try {
                            repositoryAdmin.addRepository(serverURI.toURL());
                        } catch (Exception ex) {
                            logger.error(ex.getLocalizedMessage(),ex);
                        }
                    }
                }
                logger.info("Reading repository contents in "+(System.currentTimeMillis()-startTime)+" ms");
            } finally {
                addingRepositories.set(false);
            }
            return true;
        }
    }
}
