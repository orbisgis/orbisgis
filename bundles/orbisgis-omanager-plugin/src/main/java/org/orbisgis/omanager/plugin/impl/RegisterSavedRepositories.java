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
package org.orbisgis.omanager.plugin.impl;

import org.apache.log4j.Logger;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;

import javax.swing.*;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Register repositories in the BundleContext
 *
 * @author Nicolas Fortin
 */
public class RegisterSavedRepositories extends SwingWorker<Boolean, Boolean>

{
    private Set<URI> obrRepositories;
    private RepositoryAdmin repositoryAdmin;
    private final Logger logger = Logger.getLogger(RegisterSavedRepositories.class);
    private AtomicBoolean addingRepositories;

    /**
     * Register repositories in the BundleContext     *
     * @param obrRepositories URI to register
     * @param repositoryAdmin Service
     * @param addingRepositories Thread lock
     */
    public RegisterSavedRepositories(Set<URI> obrRepositories, RepositoryAdmin repositoryAdmin, AtomicBoolean addingRepositories) {
        this.obrRepositories = obrRepositories;
        this.repositoryAdmin = repositoryAdmin;
        this.addingRepositories = addingRepositories;
    }

    @Override
    protected Boolean doInBackground() throws Exception {        // Set additional repositories
        try {
            long startTime = System.currentTimeMillis();
            Set<URI> existing = new HashSet<URI>();
            for (Repository server : repositoryAdmin.listRepositories()) {
                try {
                    existing.add(server.getURL().toURI());
                } catch (Exception ex) {
                    logger.error(ex.getLocalizedMessage(), ex);
                }
            }
            for (URI serverURI : obrRepositories) {
                if (!existing.contains(serverURI)) {
                    try {
                        repositoryAdmin.addRepository(serverURI.toURL());
                    } catch (Exception ex) {
                        logger.error(ex.getLocalizedMessage(), ex);
                    }
                }
            }
            logger.info("Reading repository contents in " + (System.currentTimeMillis() - startTime) + " ms");
        } finally {
            addingRepositories.set(false);
        }
        return true;
    }
}