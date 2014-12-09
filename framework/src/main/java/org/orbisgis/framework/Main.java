/**
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Stack;
import javax.swing.JOptionPane;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Entry point of User Interface.
 */
final class Main 
{
    private static final I18n I18N = I18nFactory.getI18n(Main.class);
    private static boolean DEBUG_MODE=false;
    private static final int BUNDLE_STABILITY_TIMEOUT = 3000;
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    //Minimum supported java version
    public static final char MIN_JAVA_VERSION = '7';
    
    /**
     * Utility class
     */
    private Main() {

    }

    private static void parseCommandLine(String[] args) {
        //Read parameters
        Stack<String> sargs=new Stack<String>();
        for(String arg : args) {
            sargs.insertElementAt(arg, 0);
        }
        while(!sargs.empty()) {
            String argument=sargs.pop();
            if(argument.contentEquals("-debug")) {
                DEBUG_MODE=true;
            }
        }
    }

    /**
    * Entry point of User Interface
    */
    public static void main( String[] args )
    {
        long deploymentTime = 0;
        parseCommandLine(args);
            //Check if the java version is greater than 1.6+
            if (!isVersion(MIN_JAVA_VERSION)) {
                    JOptionPane.showMessageDialog(null, I18N.tr("OrbisGIS needs at least a java 1.7+"));
            } else {
                    // Fetch application version
                    URL mainJar = ClassLoader.getSystemClassLoader().getResource(".");
                    Version version = new Version(1,0,0);
                    if(mainJar != null) {
                        try {
                            BundleReference bundleReference = BundleTools.parseJarManifest(new File(mainJar.getPath()), null);
                            version = bundleReference.getVersion();
                        } catch (IOException ex) {
                            // Ignore
                        }
                    }
                    // Create CoreWorkspace instance
                    CoreWorkspaceImpl coreWorkspace = new CoreWorkspaceImpl(version.getMajor(), version.getMinor(),
                            version.getMicro(), version.getQualifier());
                    // Fetch cache folder
                    File felixBundleCache = new File(coreWorkspace.getPluginFolder());
                    // Delete snapshot fragments bundles
                    long beginDeleteFragments = System.currentTimeMillis();
                    BundleTools.deleteFragmentInCache(felixBundleCache);
                    deploymentTime += System.currentTimeMillis() - beginDeleteFragments;
                    PluginHost pluginHost = new PluginHost();
                    pluginHost.init();
                    // Install built-in bundles
                    long beginInstallBundles = System.currentTimeMillis();
                    // If needed TODO load preferences from external configuration file
                    BundleTools.installBundles(pluginHost.getHostBundleContext(), new BundleReference[0]);
                    deploymentTime += System.currentTimeMillis() - beginInstallBundles;
                    // Start bundles
                    pluginHost.start();
                    LOGGER.info(I18N.tr("Waiting for bundle stability, deployment of built-in bundles done in {0} s", deploymentTime / 1000.0));
                    if(!pluginHost.waitForBundlesStableState(BUNDLE_STABILITY_TIMEOUT)) {
                        LOGGER.warn("Could not resolve bundle in the specified stability timeout");
                    }
                    pluginHost.
            }
    }

        /**
         * Utility method to check if the java machine is supported.
         *
         * @param minJavaVersion
         * @return
         */
        private static boolean isVersion(char minJavaVersion) {
                String version = System.getProperty("java.version");
                char minor = version.charAt(2);
                if (minor >= minJavaVersion ) {
                        return true;
                }
                return false;
        }
}
