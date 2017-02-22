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
package org.orbisgis.wkgui.gui;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.corejdbc.DataSourceService;
import org.orbisgis.framework.CoreWorkspaceImpl;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.sif.multiInputPanel.DirectoryComboBoxChoice;
import org.orbisgis.wkgui.icons.WKIcon;
import org.orbisgis.wkguiapi.ViewWorkspace;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.h2gis.utilities.JDBCUrlParser;

import org.orbisgis.sif.components.CustomButton;
import static org.orbisgis.wkgui.gui.DatabaseSettingsPanel.DEFAULT_MESSAGE_H2;

/**
 * GUI for Workspace selection.
 *
 * @author Nicolas Fortin
 * @author Adam Gouge
 */
@org.osgi.service.component.annotations.Component
public class WorkspaceSelectionDialog extends JPanel {

    private static final I18n I18N = I18nFactory.getI18n(WorkspaceSelectionDialog.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceSelectionDialog.class);
    private DirectoryComboBoxChoice comboBox;
    private JCheckBox defaultCheckBox;
    private CoreWorkspaceImpl selectedWorkspace;
    private JLabel errorLabel = new JLabel();
    DataSourceService dataSourceService = new DataSourceService();
    private Version bundleVersion;
    private boolean isJDBCUrlValid = true;

    public WorkspaceSelectionDialog() {
        super(new MigLayout("wrap 1"));
    }

    /**
     * @param dataSourceFactory DataSourceFactory instance
     * @param serviceProperties Must contain DataSourceFactory.OSGI_JDBC_DRIVER_NAME entry.
     */
    @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC, policyOption =
            ReferencePolicyOption.GREEDY)
    public void addDataSourceFactory(DataSourceFactory dataSourceFactory, Map<String,String> serviceProperties) {
        dataSourceService.addDataSourceFactory(dataSourceFactory, serviceProperties);
    }

    /**
     * @param dataSourceFactory DataSourceFactory instance
     * @param serviceProperties Must contain DataSourceFactory.OSGI_JDBC_DRIVER_NAME entry.
     */
    public void removeDataSourceFactory(DataSourceFactory dataSourceFactory, Map<String,String> serviceProperties) {
        dataSourceService.removeDataSourceFactory(dataSourceFactory, serviceProperties);
    }

    @Activate
    public void activate(BundleContext bc) throws BundleException {
        bundleVersion = bc.getBundle().getVersion();
        new RegisterViewWorkspaceJob(this, bc).execute();
    }

    public ViewWorkspaceImpl askWorkspaceFolder(Window parentComponent, ProgressMonitor pm) {
        CoreWorkspaceImpl coreWorkspace = new CoreWorkspaceImpl(bundleVersion.getMajor(), bundleVersion.getMinor(),
                bundleVersion.getMicro(), bundleVersion.getQualifier(), new org.apache.felix.framework.Logger());

        String errorMessage = "";
        try {
            do {
                if (WorkspaceSelectionDialog.showWorkspaceFolderSelection(parentComponent, coreWorkspace, errorMessage)) {
                    /////////////////////
                    // Check connection
                    dataSourceService.setCoreWorkspace(coreWorkspace);
                    try {
                        dataSourceService.activate();
                        pm.setTaskName(I18N.tr("Connecting to the database.."));
                        try (Connection connection = dataSourceService.getConnection()) {
                            DatabaseMetaData meta = connection.getMetaData();
                            LOGGER.info(I18N.tr("Data source available {0} version {1}", meta.getDriverName(), meta.getDriverVersion()));
                            return new ViewWorkspaceImpl(coreWorkspace);
                        }
                    } catch (SQLException ex) {
                        errorMessage = ex.getLocalizedMessage();
                    }
                } else {
                    // User cancel, stop OrbisGIS
                    return null;
                }
            } while (true);
        } finally {
            pm.endTask();
        }
    }

    /**
     * Init the workspace panel
     * 
     * @param coreWorkspace
     * @param errorMessage 
     */
    private void init(CoreWorkspaceImpl coreWorkspace, String errorMessage) {
        selectedWorkspace = new CoreWorkspaceImpl(coreWorkspace.getVersionMajor(), coreWorkspace.getVersionMinor(),
                coreWorkspace.getVersionRevision(), coreWorkspace.getVersionQualifier(), new org.apache.felix
                .framework.Logger());
        // Get the list of known workspaces
        List<File> knownWorkspaces = coreWorkspace.readKnownWorkspacesPath();

        // Remove the currently loaded workspace from the list
        String currentWorkspacePath = coreWorkspace.getWorkspaceFolder();
        if (currentWorkspacePath != null && !currentWorkspacePath.isEmpty()) {
            knownWorkspaces.remove(new File(currentWorkspacePath));
        }
        // Initialize components
        JLabel chooseLabel = new JLabel(I18N.tr("Choose the workspace folder"));
        String defaultFontName = chooseLabel.getFont().getName();
        Font largeFont = new Font(defaultFontName, Font.BOLD, 16);
        Font smallFont = new Font(defaultFontName, Font.PLAIN, 10);
        chooseLabel.setFont(largeFont);
        JLabel subChooseLabel = new JLabel(
                "<html><body><p style='width: 200px;'>" +
                        I18N.tr("Choose a previous OrbisGIS workspace or create a new one")
                        + "</p></body></html>");
        subChooseLabel.setFont(smallFont);
        comboBox = new DirectoryComboBoxChoice(knownWorkspaces);
        if (!knownWorkspaces.isEmpty()) {
            // Select the default workspace on the combo box
            comboBox.setValue(knownWorkspaces.get(0).getAbsolutePath());
        }
        ActionListener selectionDone = EventHandler.create(ActionListener.class, this, "onWorkspaceFolderChange");
        comboBox.getComboBox().addActionListener(selectionDone);
        defaultCheckBox = new JCheckBox(I18N.tr("Set as default?"));
        JLabel subCheckBox = new JLabel("<html><body><p style='width: 200px;'>" +
                I18N.tr("Setting this workspace as default will allow you to " +
                        "skip this dialog next time") + "</p></body></html>");
        JButton deleteButton = new CustomButton(WKIcon.getIcon("remove"));
        deleteButton.addActionListener(EventHandler.create(ActionListener.class, this, "onDeleteWorkspaceEntry"));
        subCheckBox.setFont(smallFont);
        // Add components
        errorLabel.setForeground(Color.RED.darker());
        add(errorLabel);
        add(chooseLabel);
        add(subChooseLabel);
        add(comboBox.getComponent(), "split 2");
        add(deleteButton, "gapleft 0");
        add(Box.createGlue());
        add(defaultCheckBox);
        add(subCheckBox);
        CustomButton customDataBase = new CustomButton(WKIcon.getIcon("database"));
        customDataBase.setText(I18N.tr("Customize your database"));
        customDataBase.setToolTipText(I18N.tr("Click to customize your database."));
        customDataBase.addActionListener(
                EventHandler.create(ActionListener.class, this, "onOpenDBPanel"));
        add(customDataBase);            
        errorLabel.setText(errorMessage);  
        onWorkspaceFolderChange();         
    }
    
    
    
    
    /**
     * Check the JDBC url 
     * if invalid return false and the default JDBC connection reference
     * if valid return true 
     */
    public void checkJDBCUrl() {
        String jdbc_url = selectedWorkspace.getJDBCConnectionReference();
        if (!jdbc_url.isEmpty()) {
            try {
                Properties jdbcProperties = JDBCUrlParser.parse(jdbc_url);
                selectedWorkspace.setDatabaseName(jdbcProperties.getProperty(DataSourceFactory.JDBC_DATABASE_NAME));
                errorLabel.setText("");
                isJDBCUrlValid = true;
            } catch (IllegalArgumentException ex) {
                selectedWorkspace.setJDBCConnectionReference("");
                isJDBCUrlValid = false;
                errorLabel.setText(I18N.tr("The database parameters are invalid."));
            }
        }
        else{
            errorLabel.setText("The database parameters are invalid.");
        }
        
    }

    /**
     * Return true if the JDBC url is valid
     * @return 
     */
    public boolean isJDBCUrlValid() {
        return isJDBCUrlValid;
    }    
    

    /**
     * User click on delete button.
     */
    public void onDeleteWorkspaceEntry() {
        JComboBox combo = comboBox.getComboBox();
        if(combo.getItemCount() != 0 && combo.getSelectedIndex() != -1) {
            combo.removeItemAt(combo.getSelectedIndex());
        }
    }
   
    /**
     * The user click on add open button
     */
    public void onOpenDBPanel() {
        Window window = SwingUtilities.getWindowAncestor(this);
        DatabaseSettingsPanel databaseSettingsPanel = new DatabaseSettingsPanel(window, selectedWorkspace);
        databaseSettingsPanel.setConnectionName(new File(selectedWorkspace.getWorkspaceFolder()).getName());
        String jdbc_url = selectedWorkspace.getJDBCConnectionReference();
        Properties dbProperties = JDBCUrlParser.parse(jdbc_url);
        databaseSettingsPanel.setDBName(dbProperties.getProperty(DataSourceFactory.JDBC_DATABASE_NAME));
        String dbTypeName = DatabaseSettingsPanel.parseDbType(jdbc_url);
        if (dbTypeName.equalsIgnoreCase("h2")) {
            String netProt = dbProperties.getProperty(DataSourceFactory.JDBC_NETWORK_PROTOCOL);
            if (netProt != null) {
                databaseSettingsPanel.setDBType(DatabaseSettingsPanel.DB_TYPES.H2GIS_SERVER);
                databaseSettingsPanel.setHost(dbProperties.getProperty(DataSourceFactory.JDBC_SERVER_NAME));
                String portNum = dbProperties.getProperty(DataSourceFactory.JDBC_PORT_NUMBER);
                databaseSettingsPanel.setPort(portNum != null ? portNum : DatabaseSettingsPanel.DEFAULT_H2_PORT);
            } else {
                databaseSettingsPanel.setDBType(DatabaseSettingsPanel.DB_TYPES.H2GIS_EMBEDDED);
                databaseSettingsPanel.setPort(DEFAULT_MESSAGE_H2);
                databaseSettingsPanel.setHost(DEFAULT_MESSAGE_H2);
            }
        } else if (dbTypeName.equalsIgnoreCase("postgresql")) {
            databaseSettingsPanel.setDBType(DatabaseSettingsPanel.DB_TYPES.POSTGIS);
            databaseSettingsPanel.setHost(dbProperties.getProperty(DataSourceFactory.JDBC_SERVER_NAME));
            databaseSettingsPanel.setPort(dbProperties.getProperty(DataSourceFactory.JDBC_PORT_NUMBER));
        }

        databaseSettingsPanel.setUser(selectedWorkspace.getDataBaseUser());
        databaseSettingsPanel.setHasPassword(selectedWorkspace.isRequirePassword());
        databaseSettingsPanel.setAlwaysOnTop(true);
        databaseSettingsPanel.setModal(true);
        databaseSettingsPanel.setLocationRelativeTo(window);
        databaseSettingsPanel.setVisible(true);
        if (!databaseSettingsPanel.isCanceled()) {
            // Read selected attributes
            selectedWorkspace.setDataBaseUser(databaseSettingsPanel.getUser());
            selectedWorkspace.setRequirePassword(databaseSettingsPanel.hasPassword());
            selectedWorkspace.setJDBCConnectionReference(databaseSettingsPanel.getJdbcURI());
            selectedWorkspace.setDatabaseName(databaseSettingsPanel.getDatabaseName());
            isJDBCUrlValid=true;
            errorLabel.setText("");
        }
    }   
    
    

    /**
     * @return The workspace selection combo box
     */
    public DirectoryComboBoxChoice getComboBox() {
        return comboBox;
    }

    /**
     * @return The default workspace check box
     */
    public JCheckBox getDefaultCheckBox() {
        return defaultCheckBox;
    }    

    /**
     * Shows a dialog to choose the workspace folder
     *
     * @param parent        Parent component
     * @param coreWorkspace Core workspace
     * @param errorMessage
     *
     * @return True if the user validate workspace change
     */
    public static boolean showWorkspaceFolderSelection(Window parent,
                                                    CoreWorkspaceImpl coreWorkspace, String errorMessage) {
        String oldWorkspace = coreWorkspace.getWorkspaceFolder();
        // Initialize a panel to contain the dialog
        WorkspaceSelectionDialog panel = new WorkspaceSelectionDialog();
        panel.init(coreWorkspace, errorMessage);
        // Show the dialog and get the user's choice.
        int userChoice = JOptionPane.showConfirmDialog(parent,
                panel,
                I18N.tr("Workspace Manager"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                WKIcon.getIcon("sidebar"));

        // If the user clicked OK, then update the workspace.
        if (userChoice == JOptionPane.OK_OPTION) {
            String chosenWorkspacePath = panel.getComboBox().getValue();
            if (!ViewWorkspaceImpl.isWorkspaceValid(new File(chosenWorkspacePath), coreWorkspace.getVersionMajor())) {
                LOGGER.error(I18N.tr("The workspace folder version is invalid " +
                        "(!=OrbisGIS {0}), or the folder is not empty",
                        coreWorkspace.getVersionMajor()));
                return false;
            }
            if(!panel.isJDBCUrlValid()){
                LOGGER.error(I18N.tr("The database parameters are invalid."));
                return false;
            }
            try {
                if(panel.selectedWorkspace.isRequirePassword()) {
                    //The user must input the password
                    JPanel passwordPanel = new JPanel(new BorderLayout());
                    JPasswordField pass = new JPasswordField(10);
                    JLabel message = new JLabel(I18N.tr("<html>Database : {0}<br>User : {1}</html>",
                            panel.selectedWorkspace.getDatabaseName(),
                            panel.selectedWorkspace.getDataBaseUser()));
                    passwordPanel.add(pass, BorderLayout.CENTER);
                    passwordPanel.add(message, BorderLayout.NORTH);
                    if(JOptionPane.showConfirmDialog(panel, passwordPanel,
                            I18N.tr("Enter a password for the database"), JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,WKIcon.getIcon("database")) == JOptionPane.OK_OPTION) {
                        coreWorkspace.setDataBasePassword(new String(pass.getPassword()));
                    } else {
                        return false;
                    }
                }
                updateWorkspace(coreWorkspace, panel.getComboBox(), oldWorkspace, panel);
            } catch (IOException ex) {
                LOGGER.error(I18N.tr("Problem updating the workspace. ")
                        + ex.getLocalizedMessage(), ex);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * The user select another workspace folder.Update the JDBC uri
     */
    public void onWorkspaceFolderChange() {        
        // Check if workspace folder is valid
        if(ViewWorkspaceImpl.isWorkspaceValid(new File(comboBox.getValue()), selectedWorkspace.getVersionMajor())) {
            selectedWorkspace.setWorkspaceFolder(comboBox.getValue());
            errorLabel.setText("");
        } else {
            errorLabel.setText(I18N.tr("The selected folder is not a valid workspace"));
        }     
        checkJDBCUrl();
    }

    /**
     * Called when the user change/set the workspace folder of OrbisGIS.
     * @param coreWorkspace       Core workspace
     * @param comboBox            ComboBox with possible workspace directories
     * @param oldWorkspacePath    Path of the previous workspace
     * @param wkDialog            Workspace panel
     * @throws IOException during certain workspace operations.
     */
    private static void updateWorkspace(CoreWorkspaceImpl coreWorkspace,
                                        DirectoryComboBoxChoice comboBox,
                                        String oldWorkspacePath,WorkspaceSelectionDialog wkDialog) throws IOException {
        // Set as default workspace if necessary
        if (wkDialog.getDefaultCheckBox().isSelected()) {
            coreWorkspace.setDefaultWorkspace(new File(wkDialog.getComboBox().getValue()));
        } else {
            coreWorkspace.setDefaultWorkspace(null);
        }
        // Save the workspace list, including the previous one
        List<File> workspaces = comboBox.getValues();
        File wkFile = new File(wkDialog.getComboBox().getValue());
        workspaces.remove(wkFile);            
        if (oldWorkspacePath != null && !oldWorkspacePath.isEmpty()) {
            workspaces.add(new File(oldWorkspacePath));
        }
        workspaces.add(0, wkFile);
        coreWorkspace.writeKnownWorkspaces(workspaces);
        // Initialize the workspace if empty or new
       
        File[] files = wkFile.listFiles();
        if (!wkFile.exists() || (files != null && files.length == 0)) {
            ViewWorkspaceImpl.initWorkspaceFolder(wkFile, coreWorkspace.getVersionMajor(),
                    coreWorkspace.getVersionMinor(), coreWorkspace.getVersionRevision(),
                    coreWorkspace.getVersionQualifier());
        }
        // Write chosen jdbc attributes
        wkDialog.selectedWorkspace.writeUriFile();
        // Do this at the end because there is trigger on property change
        coreWorkspace.setWorkspaceFolder(wkDialog.getComboBox().getValue());
    }

   

    public static class RegisterViewWorkspaceJob extends SwingWorker {
        private WorkspaceSelectionDialog workspaceSelectionDialog;
        private BundleContext bundleContext;

        public RegisterViewWorkspaceJob(WorkspaceSelectionDialog workspaceSelectionDialog, BundleContext
                bundleContext) {
            this.workspaceSelectionDialog = workspaceSelectionDialog;
            this.bundleContext = bundleContext;
        }

        @Override
        protected Object doInBackground() throws Exception {
            LoadingFrame loadingFrame = new LoadingFrame();
            try {
                loadingFrame.setVisible(true);
                ProgressMonitor progressMonitor = loadingFrame.getProgressMonitor().startTask(2);
                ViewWorkspaceImpl viewWorkspace = workspaceSelectionDialog.askWorkspaceFolder(loadingFrame, progressMonitor);
                if (viewWorkspace != null) {
                    progressMonitor.setTaskName(I18N.tr("Loading OrbisGIS.."));
                    bundleContext.registerService(CoreWorkspace.class, viewWorkspace.getCoreWorkspace(), null);
                    bundleContext.registerService(ViewWorkspace.class, viewWorkspace, null);
                    progressMonitor.endTask();
                } else {
                    bundleContext.getBundle(0).stop();
                }
                return null;
            } finally {
                loadingFrame.setVisible(false);
                loadingFrame.dispose();
            }
        }
    }
}
