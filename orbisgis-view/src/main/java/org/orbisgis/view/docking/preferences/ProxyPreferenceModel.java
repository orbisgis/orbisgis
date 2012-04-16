/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *I
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */ 
package org.orbisgis.view.docking.preferences;

import bibliothek.extension.gui.dock.preference.DefaultPreference;
import bibliothek.extension.gui.dock.preference.DefaultPreferenceModel;
import bibliothek.extension.gui.dock.preference.Preference;
import bibliothek.extension.gui.dock.preference.PreferenceListener;
import bibliothek.extension.gui.dock.preference.preferences.DockPropertyPreference;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;
import bibliothek.util.Path;
import java.beans.EventHandler;
import java.util.Properties;
import org.orbisgis.view.translation.I18N;
import org.orbisgis.view.docking.preferences.editors.UserInformationEditor;
/**
 *
 */
public class ProxyPreferenceModel extends DefaultPreferenceModel {
    
    // JAVA proxy properties constant
    // @see http://docs.oracle.com/javase/1.4.2/docs/guide/net/properties.html
    private static final String SYSTEM_SOCKS_PROXY_PORT = "socksProxyPort";
    private static final String SYSTEM_SOCKS_PROXY_HOST = "socksProxyHost";
    private static final String SYSTEM_FTP_PROXY_PORT = "ftp.proxyPort";
    private static final String SYSTEM_FTP_PROXY_HOST = "ftp.proxyHost";
    private static final String SYSTEM_HTTP_PROXY_PORT = "http.proxyPort";
    private static final String SYSTEM_HTTP_PROXY_HOST = "http.proxyHost";
    
    //Key String
    private static final String USE_PROXY_KEY = "web.proxy.useproxy";
    private static final String PROXY_URL_KEY = "web.proxy.proxyurl";
    private static final String PROXY_PORT_KEY = "web.proxy.proxyport";
    private static final String PROXY_LABEL_KEY = "web.proxy.proxylabel";
    
    private String DEFAULT_PORT_VALUE = "8080";
    private DefaultPreference<String> proxyInfo;
    private String oldProxyPort = DEFAULT_PORT_VALUE;
    private DockPropertyPreference<Boolean> useProxy;
    private DockPropertyPreference<String> proxyUrl;
    private DockPropertyPreference<String> proxyPort;
    
    private boolean skipEvent = false; //Skip event while update values
    //USE PROXY Key
    public static final PropertyKey<Boolean> USE_PROXY = 
        new PropertyKey<Boolean>( USE_PROXY_KEY,
        		new ConstantPropertyFactory<Boolean>( false ), true );
    //Proxy URL Key
    public static final PropertyKey<String> PROXY_URL = 
        new PropertyKey<String>( PROXY_URL_KEY,
        		new ConstantPropertyFactory<String>(""), true );
    //Proxy Port
    public static final PropertyKey<String> PROXY_PORT = 
        new PropertyKey<String>( PROXY_PORT_KEY,
        		new ConstantPropertyFactory<String>("8080"), true );    
    public ProxyPreferenceModel(DockController controller) {
        super(controller);
        //Message Label
        proxyInfo = new UnsavedPreference<String>("",UserInformationEditor.TYPE_USER_INFO, new Path(PROXY_LABEL_KEY));
        proxyInfo.setDefaultValue("");
        proxyInfo.setLabel("");
        this.add(proxyInfo);
        //Use Proxy Check Box
        useProxy = new DockPropertyPreference<Boolean>(controller.getProperties(),USE_PROXY, Path.TYPE_BOOLEAN_PATH, new Path(USE_PROXY_KEY));
        useProxy.setLabel(I18N.tr("orbisgis.preferencies.proxy.useproxylabel"));
        useProxy.setDefaultValue(Boolean.FALSE);
        this.add(useProxy);
        //Proxy Url
        proxyUrl = new DockPropertyPreference<String>(controller.getProperties(),PROXY_URL, Path.TYPE_STRING_PATH, new Path(PROXY_URL_KEY));
        proxyUrl.setLabel(I18N.tr("orbisgis.preferencies.proxy.proxyUrlLabel"));
        this.add(proxyUrl);
        //Proxy Port
        proxyPort = new DockPropertyPreference<String>(controller.getProperties(),PROXY_PORT, Path.TYPE_STRING_PATH, new Path(PROXY_PORT_KEY));
        proxyPort.setLabel(I18N.tr("orbisgis.preferencies.proxy.proxyPortLabel"));
        proxyPort.setDefaultValue(DEFAULT_PORT_VALUE);
        this.add(proxyPort);
        
    }
    
    
    //Apply values
    @Override
    public void write() {
        super.write();
        if(useProxy.getValue()!=null) {
            updateSystemSettings(useProxy.getValue());
        }
    }
    /**
     * Init listeners
     * @return 
     */
    public ProxyPreferenceModel initListeners() {
        //useProxy.addPreferenceListener(EventHandler.create(PreferenceListener.class, this,"onChangeUseProxy",""));        
        proxyPort.addPreferenceListener(EventHandler.create(PreferenceListener.class, this,"onUserSetProxyPort",""));        
        return this;
    }
    
    
    private void updateSystemSettings(boolean activate) {
        Properties systemSettings = System.getProperties();
        String hostValue = proxyUrl.getValue();
        String portValue = proxyPort.getValue();
        //System properties is the 
        if(activate) {
            systemSettings.put(SYSTEM_HTTP_PROXY_HOST, hostValue);
            systemSettings.put(SYSTEM_HTTP_PROXY_PORT, portValue);
            systemSettings.put(SYSTEM_FTP_PROXY_HOST, hostValue);
            systemSettings.put(SYSTEM_FTP_PROXY_PORT, portValue);
            systemSettings.put(SYSTEM_SOCKS_PROXY_HOST, hostValue);
            systemSettings.put(SYSTEM_SOCKS_PROXY_PORT, portValue);        
        }else{
            systemSettings.remove(SYSTEM_HTTP_PROXY_HOST);
            systemSettings.remove(SYSTEM_HTTP_PROXY_PORT);
            systemSettings.remove(SYSTEM_FTP_PROXY_HOST);
            systemSettings.remove(SYSTEM_FTP_PROXY_PORT);
            systemSettings.remove(SYSTEM_SOCKS_PROXY_HOST);
            systemSettings.remove(SYSTEM_SOCKS_PROXY_PORT);
        }
    }
    /**
     * User update the port value, verify that the port is correct
     * @param preference Updated preference, the port input
     */
    public void onUserSetProxyPort(Preference<String> preference) {
        if(skipEvent) {
            return;
        }
        try {
            int p = Integer.parseInt(preference.getValue());
            if (p < 0 || p > 65535) {
                skipEvent = true;
                proxyInfo.setValue(I18N.tr("orbisgis.preferencies.proxy.invalidPortNumber"));
                preference.setValue(oldProxyPort);
            }
        } catch (NumberFormatException e) {
            skipEvent = true;
            proxyInfo.setValue(I18N.tr("orbisgis.preferencies.proxy.invalidPortNumber"));
            preference.setValue(oldProxyPort);
            return;
        }
        finally { 
            skipEvent = false;
        }
        proxyInfo.setValue("");
        oldProxyPort = preference.getValue();
    }
    
    /**
     * Update the system properties
     * @param preference 
     */
    public void onChangeUseProxy(Preference<Boolean> preference) {
        if(preference.getValue()) {
            //Enable proxy properties

        } else {
            //Disable proxy properties

        }
    }
}
