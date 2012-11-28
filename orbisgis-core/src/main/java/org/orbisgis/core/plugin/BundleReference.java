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
package org.orbisgis.core.plugin;

import java.io.InputStream;

/**
 * Built-in bundle reference. Reference to a bundle stored as a Jar ressource.
 * Used to install minimal bundle of OrbisGIS.
 * @author Nicolas Fortin
 */
public class BundleReference {

        private String artifactId;
        private String bundleUri;
        private InputStream bundleJarContent;
        private boolean autoStart = false;

        public BundleReference(String artifactId) {
                this.artifactId = artifactId;
        }

        public BundleReference(String artifactId, String bundleUri) {
                this.artifactId = artifactId;
                this.bundleUri = bundleUri;
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
         * @return Bundle, uri, used on refresh bundle
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
        public String getResourceUrl() {
                return artifactId + ".jar";
        }

        /**
         * @return Artifact name
         */
        public String getArtifactId() {
                return artifactId;
        }

        /**
         * Set the input stream to read the jar content.
         * @param bundleJarContent 
         */
        public void setBundleJarContent(InputStream bundleJarContent) {
                this.bundleJarContent = bundleJarContent;
        }
        /**
         * This input stream read the Bundle Jar File
         * @return 
         */
        public InputStream getBundleJarContent() {
                return bundleJarContent;
        }
}
