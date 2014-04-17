/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * or contact directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.workspace;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.orbisgis.core.workspace.CoreWorkspaceImpl;
import org.orbisgis.sif.components.CustomButton;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.viewapi.util.MenuCommonFunctions;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Erwan Bocher
 */
public class DatabaseSettingsPanel extends JDialog {

    private static final String DB_PROPERTIES_FILE = "db_connexions.properties";
    private static final Logger LOGGER = Logger.getLogger(DatabaseSettingsPanel.class);
    protected static final I18n I18N = I18nFactory.getI18n(DatabaseSettingsPanel.class);
    private JPanel mainPanel;
    private AtomicBoolean initialised = new AtomicBoolean(false);
    private JButton okBt;
    private JButton cancelBt;
    private Properties dbProperties = new Properties();
    private JTextField connectionName;
    private JTextField urlValue;
    private JTextField userValue;
    private JPasswordField pswValue;
    private JComboBox comboBox;

    public DatabaseSettingsPanel() {
        super();
        init();
    }

    public DatabaseSettingsPanel(Dialog owner) {
        super(owner);
        init();
    }

    public DatabaseSettingsPanel(Frame owner) {
        super(owner);
        init();
    }

    public DatabaseSettingsPanel(Window owner) {
        super(owner);
        init();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        init();
    }

    /**
     * Create the panel
     */
    private void init() {
        if (!initialised.getAndSet(true)) {
            loadDBProperties();
            Object[] dbKeys = dbProperties.keySet().toArray();
            mainPanel = new JPanel(new MigLayout());
            JLabel cbLabel = new JLabel(I18N.tr("Saved connections"));
            comboBox = new JComboBox(dbKeys);
            comboBox.addActionListener(EventHandler.create(ActionListener.class, this, "onUserSelectionChange"));
            mainPanel.add(cbLabel);
            mainPanel.add(comboBox, "span, grow");
            JLabel labelName = new JLabel(I18N.tr("Connection name"));
            connectionName = new JTextField();
            mainPanel.add(labelName);
            mainPanel.add(connectionName, "width 200!");
            CustomButton saveBt = new CustomButton(OrbisGISIcon.getIcon("save"));
            saveBt.setToolTipText(I18N.tr("Save the connection parameters"));
            saveBt.addActionListener(EventHandler.create(ActionListener.class, this, "onSave"));
            CustomButton removeBt = new CustomButton(OrbisGISIcon.getIcon("remove"));
            removeBt.setToolTipText(I18N.tr("Remove the connection parameters"));
            removeBt.addActionListener(EventHandler.create(ActionListener.class, this, "onRemove"));
            mainPanel.add(saveBt);
            mainPanel.add(removeBt,  "wrap");
            JLabel labelURL = new JLabel("JDB URL");
            urlValue = new JTextField();
            mainPanel.add(labelURL);
            mainPanel.add(urlValue, "span, grow, wrap");
            JLabel exampleURL = new JLabel(I18N.tr("Example")+ " : jdbc:h2:/tmp/testdb;DB_CLOSE_DELAY=30");
            mainPanel.add(exampleURL, "span, wrap");
            JLabel userLabel = new JLabel(I18N.tr("User name"));
            userValue = new JTextField();
            mainPanel.add(userLabel);
            mainPanel.add(userValue, "span 1, grow, wrap");
            JLabel pswLabel = new JLabel(I18N.tr("Password"));
            pswValue = new JPasswordField();
            mainPanel.add(pswLabel);
            mainPanel.add(pswValue, "span 1, grow, wrap");
            okBt = new JButton(I18N.tr("&Ok"));
            MenuCommonFunctions.setMnemonic(okBt);
            okBt.addActionListener(EventHandler.create(ActionListener.class, this, "onOk"));
            okBt.setDefaultCapable(true);
            mainPanel.add(okBt, "span 3");
            cancelBt = new JButton(I18N.tr("&Cancel"));
            MenuCommonFunctions.setMnemonic(cancelBt);
            cancelBt.addActionListener(EventHandler.create(ActionListener.class, this, "onClose"));
            cancelBt.setDefaultCapable(true);
            mainPanel.add(cancelBt, "span 3");
            getContentPane().add(mainPanel);
            setTitle(I18N.tr("Database parameters"));
            onUserSelectionChange();
            pack();
            setResizable(false);
        }
    }

    /**
     * Click on the close button
     */
    public void onClose() {
        setVisible(false);
    }

    /**
     * Click on the Ok button
     */
    public void onOk() {
        checkParameters();
        saveProperties();
        setVisible(false);
    }

    /**
     * Check if the parameters are well filled.
     */
    private boolean checkParameters() {
        boolean isParametersOk =true;
        if (connectionName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, I18N.tr("Please specify a connexion name."));
            isParametersOk=false;
        } else if (urlValue.getText().isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, I18N.tr("The URL of the database cannot be null."));
            isParametersOk=false;
        } else if (userValue.getText().isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, I18N.tr("The user name cannot be null."));
            isParametersOk=false;
        } else if (pswValue.getPassword().length == 0) {
            JOptionPane.showMessageDialog(rootPane, I18N.tr("The password cannot be null."));
            isParametersOk=false;
        }
        return isParametersOk;

    }

    /**
     * Click on the Ok button
     */
    public void onSave() {
        if (checkParameters()) {
            String nameValue = connectionName.getText();
            if (!dbProperties.containsKey(nameValue)) {
            dbProperties.setProperty(nameValue,  urlValue.getText() + "|" + userValue.getText());
            comboBox.addItem(nameValue);
            comboBox.setSelectedItem(nameValue);
            saveProperties();
            onUserSelectionChange();
            }
        }
    }

    /**
     * Click on the Ok button
     */
    public void onRemove() {
        String valueConnection = connectionName.getText();
        if(dbProperties.containsKey(valueConnection)){
            dbProperties.remove(valueConnection);
            comboBox.removeItem(valueConnection);
            saveProperties();
            onUserSelectionChange();
        }
    }

    /**
     * Load the connection properties file.
     */
    private void loadDBProperties() {
        try {
            File propertiesFile = new File(new CoreWorkspaceImpl().getApplicationFolder() + File.separator + DB_PROPERTIES_FILE);
            if (propertiesFile.exists()) {
                dbProperties.load(new FileInputStream(propertiesFile));
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Save the connection properties file
     */
    public void saveProperties() {
        try {
            dbProperties.store(new FileOutputStream(new CoreWorkspaceImpl().getApplicationFolder() + File.separator + DB_PROPERTIES_FILE),
                    I18N.tr("Saved with the OrbisGIS database panel"));
        } catch (IOException ex) {
            LOGGER.error(ex);
        }

    }

    /**
     * Change the populate the components
     */
    public void onUserSelectionChange() {
        boolean isCmbEmpty = comboBox.getItemCount() == 0;
        if (!isCmbEmpty) {
            String value = comboBox.getSelectedItem().toString();
            String data = dbProperties.getProperty(value);
            connectionName.setText(value);
            StringTokenizer st = new StringTokenizer(data, "|");
            urlValue.setText(st.nextToken());
            userValue.setText(st.nextToken());
        }
    }
    
    /**
     * @return Password field
     */
    public String getPassword() {
        return new String(pswValue.getPassword());
    }
    /**
     * @return URI field
     */
    public String getJdbcURI() {
        return urlValue.getText();
    }

    /**
     * @return User field
     */
    public String getUser() {
        return userValue.getText();
    }

    /**
     * Set a new JDBC URL.
     * 
     * @param jdbcConnectionReference 
     */
    public void setURL(String jdbcConnectionReference) {
        urlValue.setText(jdbcConnectionReference);
    }

    /**
     * Set a new database user name.
     *
     * @param dataBaseUser
     */
    public void setUser(String dataBaseUser) {
        userValue.setText(dataBaseUser);
    }

    /**
     * Set a new password.
     * 
     * @param dataBasePassword
     */
    public void setPassword(String dataBasePassword) {
        pswValue.setText(dataBasePassword);
    }
}
