/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.plugins;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import org.gdms.data.DataSourceFactory;

/**
 * PlugIn manager for Gdms.
 * @author Antoine Gourlay
 * @since 2.0
 */
public class PlugInManager {

        private Logger log = Logger.getLogger(PlugInManager.class);
        private File plugInDirectory;
        private DataSourceFactory dsf;
        private Set<PlugInManagerListener> listeners = new HashSet<PlugInManagerListener>();
        private Set<GdmsPlugIn> plugins = new HashSet<GdmsPlugIn>();

        /**
         * Creates a new instance of the plug-in manager.
         * @param plugInDirectory the directory that contains the plug-in jar files.
         * @param dsf the DataSourceFactory
         */
        public PlugInManager(File plugInDirectory, DataSourceFactory dsf) {
                this.plugInDirectory = plugInDirectory;
                this.dsf = dsf;
        }

        private void loadPlugins() {
                Set<File> files = new HashSet<File>();
                findFilesRecursively(plugInDirectory, files);
                ClassLoader classLoader = new URLClassLoader(toURLs(files));

                for (File f : files) {
                        if (f.getName().endsWith(".jar")) {
                                try {
                                        ZipFile zipFile = new ZipFile(f);
                                        for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
                                                ZipEntry entry = e.nextElement();

                                                // Filter by filename; otherwise we'll be loadingversionLabel all the classes,
                                                // which takes significantly longer
                                                if (!(entry.getName().endsWith("PlugIn.class") || entry.getName().endsWith("Plugin.class"))) {
                                                        continue;
                                                }
                                                if (entry.isDirectory()) {
                                                        continue;
                                                }
                                                if (!entry.getName().endsWith(".class")) {
                                                        continue;
                                                }
                                                if (entry.getName().indexOf('$') != -1) {
                                                        // I assume it's not necessary to load inner classes explicitly.
                                                        continue;
                                                }

                                                String className = entry.getName();
                                                className = className.substring(0, className.length() - 6);
                                                className = className.replace("/", ".");
                                                
                                                Class candidate;

                                                if (!firePluginLoading(className)) {
                                                        continue;
                                                }

                                                try {
                                                        candidate = classLoader.loadClass(className);
                                                } catch (ClassNotFoundException ex) {
                                                        continue;
                                                } catch (Throwable t) {
                                                        log.error("Throwable encountered loading " + className, t);
                                                        continue;
                                                }

                                                if (GdmsPlugIn.class.isAssignableFrom(candidate)) {
                                                        GdmsPlugIn pl = null;
                                                        try {
                                                                pl = (GdmsPlugIn) candidate.newInstance();
                                                        } catch (InstantiationException ex) {
                                                                log.error("Error encountered loading " + className, ex);
                                                        } catch (IllegalAccessException ex) {
                                                                log.error("Error encountered loading " + className, ex);
                                                        }

                                                        plugins.add(pl);
                                                }
                                        }
                                } catch (ZipException ex) {
                                        // forget about the file
                                        log.warn("Failed to read file '" + f.getPath() + " as a .jar.", ex);
                                } catch (IOException ex) {
                                        // forget about the file
                                        log.warn("Failed to read file '" + f.getPath() + '.', ex);
                                }
                        }
                }
        }

        private URL[] toURLs(Set<File> files) {
                URL[] urls = new URL[files.size()];
                int i = 0;
                for (File f : files) {
                        try {
                                urls[i] = new URL("jar:file:" + f.getPath() + "!/");
                        } catch (MalformedURLException e) {
                                // should never happen
                                throw new IllegalStateException(e);
                        }
                        i++;
                }

                return urls;
        }

        private void findFilesRecursively(File directory, Set<File> files) {

                if (!directory.isDirectory()) {
                        return;
                }


                File[] f = directory.listFiles();
                for (int i = 0; i < f.length; i++) {
                        File file = f[i];
                        if (file.isDirectory()) {
                                findFilesRecursively(file, files);
                        }
                        if (!file.isFile()) {
                                continue;
                        }
                        files.add(file);
                }

        }

        /**
         * Loads the plugins.
         * 
         * This goes through all .jar files in the plug-in directory, looking for classes whose name
         * ends in "PlugIn" or "Plugin" and that inherit from {@link GdmsPlugIn}.
         * It then loads them all.
         */
        public void load() {
                loadPlugins();
                for (GdmsPlugIn p : plugins) {
                        p.load(dsf);
                        firePluginLoaded(p);
                }
        }

        /**
         * Unloads all loaded plug-ins.
         */
        public void unload() {
                for (GdmsPlugIn p : plugins) {
                        firePluginUnloading(p);
                        p.unload();
                }
                plugins.clear();
        }

        /**
         * Gets all loaded plug-ins.
         * @return a read-only set of plug-ins.
         */
        public Set<GdmsPlugIn> getPlugIns() {
                return Collections.unmodifiableSet(plugins);
        }

        private boolean firePluginLoading(String name) {
                for (PlugInManagerListener l : listeners) {
                        if (!l.pluginLoading(name)) {
                                return false;
                        }
                }
                return true;
        }

        private void firePluginLoaded(GdmsPlugIn p) {
                for (PlugInManagerListener l : listeners) {
                        l.pluginLoaded(p);
                }
        }

        private void firePluginUnloading(GdmsPlugIn p) {
                for (PlugInManagerListener l : listeners) {
                        l.pluginUnloading(p);
                }
        }

        /**
         * Register a listener on the events of this class.
         * @param l a listener
         */
        public void registerListener(PlugInManagerListener l) {
                listeners.add(l);
        }

        /**
         * Unregisters a listener.
         * @param p a previously registered listener
         * @return true if the listener was actually removed.
         */
        public boolean unregisterListener(PlugInManagerListener p) {
                return listeners.remove(p);
        }
}
