/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import org.gdms.data.db.DBSource;

/**
 *
 * @author Cedric Le Glaunec <cedric.leglaunec@gmail.com>
 */
public class OwsContextUtils {

    /**
     * Filename that contains services' configuration
     */
    public static String SERVICES_CONFIG = "services.properties";


    /**
     * Extracts connection string's fields such as host, port, db name and table.
     * 
     * @param connectionString
     * @return 
     */
    public static DbConnectionString extractDbConnectionString(String connectionString) {
        String regex = "pgsql://(.*):(\\d+)/(.*)/(.*)";
        Scanner scanner = new Scanner(connectionString);
        scanner.findInLine(regex);

        MatchResult result = scanner.match();
        DbConnectionString db = new DbConnectionString(result.group(1), 
                Integer.parseInt(result.group(2)), result.group(3), 
                result.group(4));
        
        return db;
    }
    
    /**
     * Reads the value of the given property from the file defined in {@link OwsContextUtils#SERVICES_CONFIG}.
     * @param property A property name
     * @return A string value or null if the file or the property name does not exist
     */
    private static String readProperty(String property) {
        String value = null;
        Properties properties = new Properties();
        InputStream in = OwsContextUtils.class.getResourceAsStream(SERVICES_CONFIG);
        try {
            properties.load(in);
            value = properties.getProperty(property);
        } catch (IOException ex) {
            Logger.getLogger(OwsContextUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }
    
    /**
     * Reads the property 'orb.orbisgis.core.ui.plugins.ows.service_url_getall' 
     * from the config file ({@link OwsContextUtils#SERVICES_CONFIG})
     * @return A service's url or null if the property does not exist
     */
    public static String getServiceGetAllUrl() {
        return readProperty("orb.orbisgis.core.ui.plugins.ows.service_url_getall");
    }
    
    /**
     * Reads the property 'orb.orbisgis.core.ui.plugins.ows.service_url_getoneows' 
     * from the config file ({@link OwsContextUtils#SERVICES_CONFIG})
     * @return A service's url or null if the property does not exist
     */
    public static String getServiceGetOneOwsUrl() {
        return readProperty("orb.orbisgis.core.ui.plugins.ows.service_url_getoneows");
    }
    
    /**
     * Reads the property 'orb.orbisgis.core.ui.plugins.ows.service_url_exportows' 
     * from the config file ({@link OwsContextUtils#SERVICES_CONFIG})
     * @return A service's url or null if the property does not exist
     */
    public static String getServiceExportOwsAsUrl() {
        return readProperty("orb.orbisgis.core.ui.plugins.ows.service_url_exportows");
    }
    
    /**
     * Reads the property 'orb.orbisgis.core.ui.plugins.ows.service_url_getworkspaces' 
     * from the config file ({@link OwsContextUtils#SERVICES_CONFIG})
     * @return A service's url or null if the property does not exist
     */
    public static String getServiceGetAllOwsWorkspace() {
        return readProperty("orb.orbisgis.core.ui.plugins.ows.service_url_getworkspaces");
    }
    
    private static String generateSourceId(String table, String db, String host) {
        return table + "_" + db + "_" + host;
    }
    
    public static String generateSourceId(DbConnectionString db) {
        return generateSourceId(db.getTable(), db.getDb(), db.getHost());
    }
    
    public static String generateSourceId(DBSource db) {
        return generateSourceId(db.getTableName(), db.getDbName(), db.getHost());
    }
}
