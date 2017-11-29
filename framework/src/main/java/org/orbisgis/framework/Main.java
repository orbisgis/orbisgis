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
package org.orbisgis.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.felix.framework.Logger;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.Version;
import org.osgi.framework.launch.Framework;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Entry point of User Interface.
 */
final class Main {
    private static final I18n I18N = I18nFactory.getI18n(Main.class);
    private static boolean debugMode = false;
    /** if true Remove configurations file and cache */
    private static boolean noFailMode = false;
    private static final int BUNDLE_STABILITY_TIMEOUT = 3000;
    private static final int SAFE_MODE_COUNTDOWN_DELETE = 5000;
    private static final Logger LOGGER = new Logger();
    private static Version version;
    private static final String OBR_REPOSITORY_URL = "obr.repository.url";
    private static final String OBR_REPOSITORY_SNAPSHOT_URL = "obr.repository.snapshot.url";

    //Minimum supported java version
    public static final char MIN_JAVA_VERSION = '7';
    public static final char MAX_JAVA_VERSION = '8';

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
            if (argument.contentEquals("--debug")) {
                debugMode = true;
            } else if(argument.contentEquals("--nofailmode")) {
                noFailMode = true;
            }
        }
    }

    /**
     * Entry point of User Interface
     */
    public static void main(String[] args) {
        // Deactivate JOOQ Ascii Art printing
        Properties properties = System.getProperties();
        properties.setProperty("org.jooq.no-logo","True");
        // debug try { Thread.sleep(5000); } catch (Exception ex) {}
        long deploymentTime = 0;
        BundleTools bundleTools = new BundleTools(LOGGER);
        parseCommandLine(args);
        //Check if the java version is greater than 1.6+
        if (!isVersion(MIN_JAVA_VERSION, MAX_JAVA_VERSION)) {
            JOptionPane.showMessageDialog(null, I18N.tr("OrbisGIS needs at least a java 1.7+"));
        } else {
            // Fetch application version
            version = new Version(1, 0, 0);
            try (InputStream fs = Main.class.getResourceAsStream("version.txt")) {
                String versionTxt = IOUtils.readLines(fs).get(0);
                version = new Version(versionTxt.replace("-", "."));
            } catch (IOException ex) {
                LOGGER.log(Logger.LOG_ERROR, ex.getLocalizedMessage(), ex);
            }
            // Create CoreWorkspace instance
            CoreWorkspaceImpl coreWorkspace = new CoreWorkspaceImpl(version.getMajor(), version.getMinor(),
                    version.getMicro(), version.getQualifier(), LOGGER);
            // Create Lock file if not exists
            File lockFile = new File(coreWorkspace.getApplicationFolder(), "instance.lock");
            if(!lockFile.exists()) {
                try {
                    new File(coreWorkspace.getApplicationFolder()).mkdirs();
                    if(!lockFile.createNewFile()) {
                        LOGGER.log(Logger.LOG_ERROR, "Cannot create lock file !\n"+lockFile.getAbsolutePath());
                        return;
                    }
                } catch (IOException ex) {
                    LOGGER.log(Logger.LOG_ERROR, "Application cache folder is not accessible !\n"+lockFile.getAbsolutePath(), ex);
                    return;
                }
            }
            try(FileOutputStream fileOutputStream = new FileOutputStream(lockFile);
                    FileLock lock = fileOutputStream.getChannel().tryLock()) {
                if(lock == null) {
                    LOGGER.log(Logger.LOG_ERROR, "Only a single instance of OrbisGIS can be run, please close other instance");
                    return;
                }
                // Fetch cache folder
                File felixBundleCache = new File(coreWorkspace.getPluginCache());
                // If safe mode delete cache
                if (noFailMode && felixBundleCache.isDirectory()) {
                    System.err.println("Safe mode engaged, clear the following folder in 5 seconds..\n" + felixBundleCache);
                    try {
                        Thread.sleep(SAFE_MODE_COUNTDOWN_DELETE);
                        FileUtils.deleteDirectory(felixBundleCache);
                    } catch (InterruptedException ex) {
                        return;
                    } catch (IOException ex) {
                        LOGGER.log(Logger.LOG_ERROR, ex.getLocalizedMessage(), ex);
                    }
                }
                // Delete snapshot fragments bundles
                long beginDeleteFragments = System.currentTimeMillis();
                bundleTools.deleteFragmentInCache(felixBundleCache);
                deploymentTime += System.currentTimeMillis() - beginDeleteFragments;
                LOGGER.log(Logger.LOG_INFO, I18N.tr("Waiting for bundle stability, deployment of built-in bundles done in" + " {0} s", deploymentTime / 1000.0));
                // Start main of felix framework
                try {
                    String[] felixArgs = new String[]{"-b", BundleTools.BUNDLE_DIRECTORY, felixBundleCache.getAbsolutePath()};
                    LOGGER.log(Logger.LOG_INFO, "Start Apache Felix:\n" + Arrays.toString(felixArgs));
                    startFelix(BundleTools.BUNDLE_DIRECTORY, felixBundleCache.getAbsolutePath());
                } catch (Exception ex) {
                    LOGGER.log(Logger.LOG_ERROR, ex.getLocalizedMessage(), ex);
                }
            } catch (OverlappingFileLockException ex) {
                LOGGER.log(Logger.LOG_ERROR, "Only a single instance of OrbisGIS can be run, please close other instance");
            } catch (IOException ex) {
                LOGGER.log(Logger.LOG_ERROR, "Application cache folder is not accessible !\n"+lockFile.getAbsolutePath(), ex);
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

        //If OrbisGIS is in a SNAPSHOT version, uses the release and snapshot orb.
        //Else only uses the release one.
        if(configProps.containsKey(OBR_REPOSITORY_URL) && configProps.containsKey(OBR_REPOSITORY_SNAPSHOT_URL)) {
            if (version.getQualifier().equals("SNAPSHOT")) {
                configProps.remove(OBR_REPOSITORY_URL);
                configProps.put(OBR_REPOSITORY_URL, configProps.get(OBR_REPOSITORY_SNAPSHOT_URL));
            }
            configProps.remove(OBR_REPOSITORY_SNAPSHOT_URL);
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
            PluginHost pluginHost = new PluginHost(new File(configProps.get(Constants.FRAMEWORK_STORAGE)), LOGGER);
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
        } catch (Exception ex) {
            LOGGER.log(Logger.LOG_ERROR, "Could not create framework: ", ex);
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
    private static boolean isVersion(char minJavaVersion, char maxJavaVersion) {
        String version = System.getProperty("java.version");
        char javaVersion=0;
        if (version.charAt(0) == '1') {
            javaVersion = version.charAt(2);
        }
        else{
            javaVersion = version.charAt(0);
        }
        if (javaVersion >= minJavaVersion && javaVersion <= maxJavaVersion) {
            return true;
        }
        return false;
    }
}
