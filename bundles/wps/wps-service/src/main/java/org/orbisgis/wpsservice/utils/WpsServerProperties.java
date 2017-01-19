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
 * Copyright (C) 2015-2016 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.wpsservice.utils;

import org.orbisgis.frameworkapi.CoreWorkspace;
import org.osgi.service.component.annotations.Reference;

import java.util.List;
import java.util.Properties;

/**
 * Properties of the wps server.
 *
 * @author Sylvain PALOMINOS
 */
public class WpsServerProperties {

    /** CoreWorkspace of OrbisGIS */
    private CoreWorkspace coreWorkspace;

    private Properties properties;

    @Reference
    public void setCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = coreWorkspace;
    }
    public void unsetCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = null;
    }

    public WpsServerProperties(){

    }

    public static final String SERVICE = "SERVICE";
    public static final String SERVER_VERSION = "SERVER_VERSION";
    public static final String ACCEPTED_VERSIONS = "ACCEPTED_VERSIONS";
    public static final String ACCEPTED_LANGUAGES = "ACCEPTED_LANGUAGES";
    public static final String ACCEPTED_FORMATS = "ACCEPTED_FORMATS";
    public static final String SECTIONS = "SECTIONS";
    public static final String SERVICE_TYPE = "SERVICE_TYPE";
    public static final String SERVICE_TYPE_VERSION = "SERVICE_TYPE_VERSION";
    public static final String TITLE = "TITLE";
    public static final String ABSTRACT = "ABSTRACT";
    public static final String KEYWORDS = "KEYWORDS";
    public static final String FEES = "FEES";
    public static final String ACCESS_CONSTRAINTS = "ACCESS_CONSTRAINTS";
    public static final String PROVIDER_NAME = "PROVIDER_NAME";
    public static final String PROVIDER_SITE = "PROVIDER_SITE";
    public static final String SERVICE_CONTACT = "SERVICE_CONTACT";

    public boolean match(String property, Object o){
        switch(property){
            case SERVICE:
            case SERVER_VERSION:
                return o.equals(properties.getProperty(property));
            case ACCEPTED_VERSIONS:
            case ACCEPTED_LANGUAGES:
            case ACCEPTED_FORMATS:
                String[] splitProperty = property.split(",");
                if(o instanceof List){

                }
                break;
            case SECTIONS:
                break;
            case SERVICE_TYPE:
                break;
            case SERVICE_TYPE_VERSION:
                break;
            case TITLE:
                break;
            case ABSTRACT:
                break;
            case KEYWORDS:
                break;
            case FEES:
                break;
            case ACCESS_CONSTRAINTS:
                break;
            case PROVIDER_NAME:
                break;
            case PROVIDER_SITE:
                break;
            case SERVICE_CONTACT:
                break;
        }
        return true;
    }
}
