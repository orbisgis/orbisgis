package org.orbisgis.toolboxeditor;
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

import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.functions.factory.H2GISFunctions;
import org.h2gis.functions.io.DriverManager;
import org.h2gis.utilities.SFSUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbiswps.server.WpsServerImpl;
import org.orbiswps.server.model.DataType;
import org.orbisgis.sif.docking.DockingManager;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelFactory;
import org.orbisgis.sif.docking.DockingPanelLayout;

import javax.sql.DataSource;
import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.orbisgis.toolboxeditor.ToolboxWpsClient.JdbcProperties.COLUMN_DIMENSION;


/**
 * @author Sylvain PALOMINOS
 */
public class TestOrbisGISWpsServerImpl {
    private static WpsClientImpl wpsClient;
    private static DataSource dataSource;
    private static Connection connection;

    @Before
    public void init() throws Exception {
        wpsClient = new WpsClientImpl();
        dataSource = SFSUtilities.wrapSpatialDataSource(
                H2GISDBFactory.createDataSource(TestOrbisGISWpsServerImpl.class.getSimpleName(), true));
        connection = dataSource.getConnection();
        H2GISFunctions.registerFunction(connection.createStatement(), new DriverManager(), "");
        DataManager dataManager = new DataManagerImpl(dataSource);
        wpsClient.setWpsServer(new WpsServerImpl());
        wpsClient.setDataManager(dataManager);
        wpsClient.setCoreWorkspace(new CustomCoreWorkspace());
        wpsClient.setDockingManager(new CustomDockingManager());
        wpsClient.activate();
    }

    @Test
    public void testGetTableFields() throws SQLException {
        try(Statement st = connection.createStatement()) {
            st.execute("DROP TABLE TABLE IF EXISTS");
            try {
                st.execute("CREATE TABLE TABLE (\"integers\" integer, \"geometries\" geometry)");

                List<DataType> dataTypeList = new ArrayList<>();
                List<DataType> excludedTypeList = new ArrayList<>();
                List<String> fieldList = wpsClient.getColumnList("TABLE", dataTypeList, excludedTypeList);
                Assert.assertTrue("The field list should contains the field 'geometries'.", fieldList.contains("geometries"));
                Assert.assertTrue("The field list should contains the field 'integers'.", fieldList.contains("integers"));

                dataTypeList = new ArrayList<>();
                dataTypeList.add(DataType.GEOMETRY);
                excludedTypeList = new ArrayList<>();
                fieldList = wpsClient.getColumnList("TABLE", dataTypeList, excludedTypeList);
                Assert.assertTrue("The field list should contains the field 'geometries'.", fieldList.contains("geometries"));
                Assert.assertFalse("The field list should not contains the field 'integers'.", fieldList.contains("integers"));

                dataTypeList = new ArrayList<>();
                excludedTypeList = new ArrayList<>();
                excludedTypeList.add(DataType.GEOMETRY);
                fieldList = wpsClient.getColumnList("TABLE", dataTypeList, excludedTypeList);
                Assert.assertFalse("The field list should not contains the field 'geometries'.", fieldList.contains("geometries"));
                Assert.assertTrue("The field list should contains the field 'integers'.", fieldList.contains("integers"));

                dataTypeList = new ArrayList<>();
                excludedTypeList = new ArrayList<>();
                excludedTypeList.add(DataType.GEOMETRY);
                excludedTypeList.add(DataType.INTEGER);
                fieldList = wpsClient.getColumnList("TABLE", dataTypeList, excludedTypeList);
                Assert.assertFalse("The field list should not contains the field 'geometries'.", fieldList.contains("geometries"));
                Assert.assertFalse("The field list should not contains the field 'integers'.", fieldList.contains("integers"));
            } finally {
                st.execute("DROP TABLE TABLE IF EXISTS");
            }
        }
    }

    @Test
    public void testGetTableList() throws SQLException {
        try(Statement st = connection.createStatement()) {
            st.execute("DROP TABLE TABLE IF EXISTS");
            try {
                st.execute("CREATE TABLE TABLEINTEGER (\"integers\" integer)");
                st.execute("CREATE TABLE TABLEGEOMETRY (\"geometries\" geometry)");
                wpsClient.onDataManagerChange();

                List<DataType> dataTypeList = new ArrayList<>();
                List<DataType> excludedTypeList = new ArrayList<>();
                List<String> tableList = wpsClient.getTableList(dataTypeList, excludedTypeList);
                Assert.assertTrue("The table list should contains the table 'TABLEGEOMETRY'.", tableList.contains("TABLEGEOMETRY"));
                Assert.assertTrue("The table list should contains the table 'TABLEINTEGER'.", tableList.contains("TABLEINTEGER"));

                dataTypeList = new ArrayList<>();
                dataTypeList.add(DataType.GEOMETRY);
                excludedTypeList = new ArrayList<>();
                tableList = wpsClient.getTableList(dataTypeList, excludedTypeList);
                Assert.assertTrue("The table list should contains the table 'TABLEGEOMETRY'.", tableList.contains("TABLEGEOMETRY"));
                Assert.assertFalse("The table list should not contains the table 'TABLEINTEGER'.", tableList.contains("TABLEINTEGER"));

                dataTypeList = new ArrayList<>();
                excludedTypeList = new ArrayList<>();
                excludedTypeList.add(DataType.GEOMETRY);
                tableList = wpsClient.getTableList(dataTypeList, excludedTypeList);
                Assert.assertFalse("The table list should not contains the table 'TABLEGEOMETRY'.", tableList.contains("TABLEGEOMETRY"));
                Assert.assertTrue("The table list should contains the table 'TABLEINTEGER'.", tableList.contains("TABLEINTEGER"));

                dataTypeList = new ArrayList<>();
                excludedTypeList = new ArrayList<>();
                excludedTypeList.add(DataType.GEOMETRY);
                excludedTypeList.add(DataType.INTEGER);
                tableList = wpsClient.getTableList(dataTypeList, excludedTypeList);
                Assert.assertFalse("The table list should not contains the table 'TABLEGEOMETRY'.", tableList.contains("TABLEGEOMETRY"));
                Assert.assertFalse("The table list should not contains the table 'TABLEINTEGER'.", tableList.contains("TABLEINTEGER"));
            } finally {
                st.execute("DROP TABLE TABLE IF EXISTS");
            }
        }
    }

    @Test
    public void testGetColumnInformation() throws SQLException {
        try(Statement st = connection.createStatement()) {
            st.execute("DROP TABLE TABLE IF EXISTS");
            try {
                st.execute("CREATE TABLE TABLE (\"integers\" integer, \"geometries\" geometry)");
                wpsClient.onDataManagerChange();

                List<Map<ToolboxWpsClient.JdbcProperties, Object>> informationList = wpsClient.getColumnInformation("TABLE");
                Assert.assertEquals("The information list .", 2, informationList.size());

                Map<ToolboxWpsClient.JdbcProperties, Object> map = informationList.get(0);
                Assert.assertEquals("The column name should be 'INTEGERS'.", "INTEGERS",
                        map.get(ToolboxWpsClient.JdbcProperties.COLUMN_NAME).toString().toUpperCase());
                Assert.assertEquals("The column type should be 'INTEGER'.", "INTEGER",
                        map.get(ToolboxWpsClient.JdbcProperties.COLUMN_TYPE).toString().toUpperCase());
                Assert.assertEquals("The column srid should be '0'.", 0, map.get(ToolboxWpsClient.JdbcProperties.COLUMN_SRID));
                Assert.assertEquals("The column dimension should be '0'.", 0, map.get(COLUMN_DIMENSION));

                map = informationList.get(1);
                Assert.assertEquals("The column name should be 'GEOMETRIES'.", "GEOMETRIES",
                        map.get(ToolboxWpsClient.JdbcProperties.COLUMN_NAME).toString().toUpperCase());
                Assert.assertEquals("The column type should be 'GEOMETRY'.", "GEOMETRY",
                        map.get(ToolboxWpsClient.JdbcProperties.COLUMN_TYPE).toString().toUpperCase());
                Assert.assertEquals("The column srid should be '0'.", 0, map.get(ToolboxWpsClient.JdbcProperties.COLUMN_SRID));
                Assert.assertEquals("The column dimension should be '0'.", 2, map.get(COLUMN_DIMENSION));
            } finally {
                st.execute("DROP TABLE TABLE IF EXISTS");
            }
        }
    }

    private class CustomDockingManager implements DockingManager{
        @Override public void addDockingPanel(DockingPanel frame) {}
        @Override public void removeDockingPanel(String dockId) {}
        @Override public String addToolbarItem(Action action) {return null;}
        @Override public boolean removeToolbarItem(Action action) {return false;}
        @Override public JFrame getOwner() {return null;}
        @Override public JMenu getLookAndFeelMenu() {return null;}
        @Override public JMenu getCloseableDockableMenu() {return null;}
        @Override public void saveLayout() {}
        @Override public void showPreferenceDialog() {}
        @Override public void registerPanelFactory(String factoryName, DockingPanelFactory factory) {}
        @Override public void unregisterPanelFactory(String factoryName) {}
        @Override public void dispose() {}
        @Override public List<DockingPanel> getPanels() {return null;}
        @Override public void setDockingLayoutPersistanceFilePath(String dockingStateFilePath) {}
        @Override public void show(String factoryId, DockingPanelLayout panelLayout) {}
    }

    private class CustomCoreWorkspace implements CoreWorkspace {

        File applicationFolder = null;

        @Override public String getApplicationFolder() {
            if(applicationFolder == null) {
                try {
                    //Creates a directory in the temporary folder
                    applicationFolder = File.createTempFile("temp", Long.toString(System.nanoTime()));
                    if(!(applicationFolder.delete()))
                    {
                        return null;
                    }

                    if(!(applicationFolder.mkdir()))
                    {
                        return null;
                    }
                } catch (IOException e) {
                    return null;
                }
            }
            return applicationFolder.getAbsolutePath();
        }

        //Methods not used in the tests
        @Override public String getJDBCConnectionReference() {return null;}
        @Override public String getDataBaseUriFilePath() {return null;}
        @Override public String getDataBaseUser() {return null;}
        @Override public String getDataBasePassword() {return null;}
        @Override public void setDataBaseUser(String user) {}
        @Override public void setDataBasePassword(String password) {}
        @Override public boolean isRequirePassword() {return false;}
        @Override public void setRequirePassword(boolean requirePassword) {}
        @Override public String getPluginCache() {return null;}
        @Override public String getLogFile() {return null;}
        @Override public String getLogPath() {return null;}
        @Override public List<File> readKnownWorkspacesPath() {return null;}
        @Override public File readDefaultWorkspacePath() {return null;}
        @Override public String getTempFolder() {return null;}
        @Override public String getPluginFolder() {return null;}
        @Override public String getSourceFolder() {return null;}
        @Override public String getWorkspaceFolder() {return null;}
        @Override public void addPropertyChangeListener(PropertyChangeListener listener) {}
        @Override public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {}
        @Override public void removePropertyChangeListener(PropertyChangeListener listener) {}
        @Override public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {}
        @Override public int getVersionMajor() {return 0;}
        @Override public int getVersionMinor() {return 0;}
        @Override public int getVersionRevision() {return 0;}
        @Override public String getVersionQualifier() {return null;}
        @Override public String getDatabaseName() {return null;}
        @Override public void setDatabaseName(String databaseName) {}
    }
}

