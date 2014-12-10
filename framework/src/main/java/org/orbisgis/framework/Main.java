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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.jar.Manifest;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.Version;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Entry point of User Interface.
 */
final class Main {
    private static final I18n I18N = I18nFactory.getI18n(Main.class);
    private static boolean DEBUG_MODE = false;
    private static final int BUNDLE_STABILITY_TIMEOUT = 3000;
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    //Minimum supported java version
    public static final char MIN_JAVA_VERSION = '7';

    protected static Framework m_fwk = null;
    /**
     * Utility class
     */
    private Main() {

    }

    private static void parseCommandLine(String[] args) {
        //Read parameters
        Stack<String> sargs = new Stack<String>();
        for (String arg : args) {
            sargs.insertElementAt(arg, 0);
        }
        while (!sargs.empty()) {
            String argument = sargs.pop();
            if (argument.contentEquals("-debug")) {
                DEBUG_MODE = true;
            }
        }
    }

    /**
     * Entry point of User Interface
     */
    public static void main(String[] args) {
        long deploymentTime = 0;
        parseCommandLine(args);
        //Check if the java version is greater than 1.6+
        if (!isVersion(MIN_JAVA_VERSION)) {
            JOptionPane.showMessageDialog(null, I18N.tr("OrbisGIS needs at least a java 1.7+"));
        } else {
            // Fetch application version
            Version version = new Version(1, 0, 0);
            try (InputStream fs = Main.class.getResourceAsStream("version.txt")) {
                String versionTxt = IOUtils.readLines(fs).get(0);
                version = new Version(versionTxt.replace("-", "."));
            } catch (IOException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
            // Create CoreWorkspace instance
            CoreWorkspaceImpl coreWorkspace = new CoreWorkspaceImpl(version.getMajor(), version.getMinor(),
                    version.getMicro(), version.getQualifier());
            // Fetch cache folder
            File felixBundleCache = new File(coreWorkspace.getPluginCache());
            // Delete snapshot fragments bundles
            long beginDeleteFragments = System.currentTimeMillis();
            BundleTools.deleteFragmentInCache(felixBundleCache);
            deploymentTime += System.currentTimeMillis() - beginDeleteFragments;
            LOGGER.info(I18N.tr("Waiting for bundle stability, deployment of built-in bundles done in {0} s",
                    deploymentTime / 1000.0));
            // Start main of felix framework
            try {
                String[] felixArgs = new String[]{"-b", BundleTools.BUNDLE_DIRECTORY,
                        felixBundleCache.getAbsolutePath()};
                LOGGER.info("Start Apache Felix:\n" + Arrays.toString(felixArgs));
                startFelix(BundleTools.BUNDLE_DIRECTORY, felixBundleCache.getAbsolutePath());
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }
    }


    public static void startFelix(String bundleDir, String cacheDir) {
        // Load system properties.
        org.apache.felix.main.Main.loadSystemProperties();

        // Read configuration properties.
        Map<String, String> configProps = org.apache.felix.main.Main.loadConfigProperties();
        // If no configuration properties were found, then create
        // an empty properties object.
        if (configProps == null)
        {
            System.err.println("No " + org.apache.felix.main.Main.CONFIG_PROPERTIES_FILE_VALUE + " found.");
            configProps = new HashMap<>();
        }

        // Copy framework properties from the system properties.
        org.apache.felix.main.Main.copySystemProperties(configProps);

        // If there is a passed in bundle auto-deploy directory, then
        // that overwrites anything in the config file.
        if (bundleDir != null)
        {
            configProps.put(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY, bundleDir);
        }

        // If there is a passed in bundle cache directory, then
        // that overwrites anything in the config file.
        if (cacheDir != null)
        {
            configProps.put(Constants.FRAMEWORK_STORAGE, cacheDir);
        }

        // If enabled, register a shutdown hook to make sure the framework is
        // cleanly shutdown when the VM exits.
        String enableHook = configProps.get(org.apache.felix.main.Main.SHUTDOWN_HOOK_PROP);
        if ((enableHook == null) || !enableHook.equalsIgnoreCase("false"))
        {
            Runtime.getRuntime().addShutdownHook(new Thread("Felix Shutdown Hook") {
                public void run()
                {
                    try
                    {
                        if (m_fwk != null)
                        {
                            m_fwk.stop();
                            m_fwk.waitForStop(0);
                        }
                    }
                    catch (Exception ex)
                    {
                        System.err.println("Error stopping framework: " + ex);
                    }
                }
            });
        }

        try
        {
            // Create an instance of the framework.
            PluginHost pluginHost = new PluginHost(new File(configProps.get(Constants.FRAMEWORK_STORAGE)));
            pluginHost.init(configProps);
            m_fwk =  pluginHost.getFramework();
            // Use the system bundle context to process the auto-deploy
            // and auto-install/auto-start properties.
            AutoProcessor.process(configProps, m_fwk.getBundleContext());
            FrameworkEvent event;
            do
            {
                // Start the framework.
                m_fwk.start();
                // Wait for framework to stop to exit the VM.
                event = m_fwk.waitForStop(0);
            }
            // If the framework was updated, then restart it.
            while (event.getType() == FrameworkEvent.STOPPED_UPDATE);
            // Otherwise, exit.
            System.exit(0);
        }
        catch (Exception ex)
        {
            System.err.println("Could not create framework: " + ex);
            ex.printStackTrace();
            System.exit(0);
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
        if (minor >= minJavaVersion) {
            return true;
        }
        return false;
    }
}
