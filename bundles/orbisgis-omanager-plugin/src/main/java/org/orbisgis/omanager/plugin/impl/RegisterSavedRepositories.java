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
package org.orbisgis.omanager.plugin.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Register repositories in the BundleContext
 *
 * @author Nicolas Fortin
 * @author Sylvain PALOMINOS
 */
public class RegisterSavedRepositories extends SwingWorker<Boolean, Boolean> {
    private static final I18n I18N = I18nFactory.getI18n(RegisterSavedRepositories.class);

    private Set<URI> obrRepositories;
    private RepositoryAdmin repositoryAdmin;
    private final Logger logger = LoggerFactory.getLogger(RegisterSavedRepositories.class);
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
                        //Tests if the exception is because of a problem accessing to the OrbisGIS nexus.
                        if(ex.getCause() instanceof UnknownHostException &&
                                ex.getCause().getMessage().equals(serverURI.getAuthority())){
                            logger.error(I18N.tr("Unable to access to {0}. Please check your internet connexion.",
                                    serverURI.getAuthority()));
                        }
                        else {
                            logger.error(ex.getLocalizedMessage(), ex);
                        }
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
