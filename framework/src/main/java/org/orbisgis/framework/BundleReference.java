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
package org.orbisgis.framework;

import org.osgi.framework.Version;

/**
 * Built-in bundle reference. Reference to a bundle stored as a Jar ressource.
 * Used to install minimal bundle of OrbisGIS.
 * @author Nicolas Fortin
 */
public class BundleReference {

        private String artifactId;
        private String bundleUri;
        private boolean autoStart = true;
        private boolean autoInstall = true;
        private Version version;

        /**
         * Constructor
         * @param artifactId Bundle symbolic name (Identifier of a Bundle is ArtifactId and Version)
         */
        public BundleReference(String artifactId) {
                this.artifactId = artifactId;
        }

        /**
         * Complete bundle identifier constructor
         * @param artifactId Bundle symbolic name (Identifier of a Bundle is ArtifactId and Version)
         * @param version Bundle version
         */
        public BundleReference(String artifactId, Version version) {
            this.artifactId = artifactId;
            this.version = version;
        }

        /**
         *
         * @param artifactId Bundle symbolic name (Identifier of a Bundle is ArtifactId and Version)
         * @param bundleUri Representation of the URI, the validation of the URI is done later.
         */
        public BundleReference(String artifactId, String bundleUri) {
                this.artifactId = artifactId;
                this.bundleUri = bundleUri;
        }

        /**
         * True if this bundle will be automatically installed at startup.
         * @return
         */
        public boolean isAutoInstall() {
            return autoInstall;
        }

        /**
         * @param autoInstall True to automatically install this bundle at startup.
         * @return this
         */
        public BundleReference setAutoInstall(boolean autoInstall) {
            this.autoInstall = autoInstall;
            return this;
        }

        /**
         * If true, this bundle will be started automatically
         * @return 
         */
        public boolean isAutoStart() {
                return autoStart;
        }
        
        /**
         * If set to true, this bundle will be started automatically
         * @param autoStart
         * @return this
         */
        public BundleReference setAutoStart(boolean autoStart) {
                this.autoStart = autoStart;
                return this;
        }

        
        /**
         * A URI String can be set to download the Jar on update package.
         * @return Uri as String, or ArtifactId if not set. 
         */
        public String getBundleUri() {
                if (bundleUri == null) {
                        return artifactId;
                } else {
                        return bundleUri;
                }
        }

        /**
         * @return Resource name in this package
         */
        public String getResourcePath() {
                return artifactId + ".jar";
        }

        /**
         * @return Artifact name
         */
        public String getArtifactId() {
                return artifactId;
        }

        /**
         * The bundle symbolic name
         * @return
         */
        public String getSymbolicName() {
            return artifactId;
        }

        /**
         * Get the bundle Version
         * @return Version instance or null
         */
        public Version getVersion() {
            return version;
        }
}
