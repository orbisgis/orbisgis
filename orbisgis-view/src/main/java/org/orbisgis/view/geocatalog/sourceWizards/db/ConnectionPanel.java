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
 *
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
package org.orbisgis.view.geocatalog.sourceWizards.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.data.db.DBSource;
import org.gdms.driver.DBDriver;
import org.gdms.driver.Driver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.DBDriverFilter;
import org.gdms.source.SourceManager;
import org.orbisgis.sif.multiInputPanel.CheckBoxChoice;
import org.orbisgis.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.sif.multiInputPanel.InputType;
import org.orbisgis.sif.multiInputPanel.IntType;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.PasswordType;
import org.orbisgis.sif.multiInputPanel.StringType;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class ConnectionPanel extends MultiInputPanel {
        protected final static I18n i18n = I18nFactory.getI18n(ConnectionPanel.class);
        private final static int LENGTH = 20;
        public static final String DBTYPE = "dbtype";
        public static final String HOST = "host";
        public static final String PORT = "port";
        public static final String DBNAME = "dbname";
        public static final String USER = "user";
        public static final String PASSWORD = "pass";
        public static final String SSL = "ssl";
        private final SourceManager sourceManager;

        public ConnectionPanel(SourceManager sourceManager) {
                super("orbisgis.view.geocatalog.sourceWizards.db.FirstUIPanel", i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.connect"));
                this.sourceManager = sourceManager;
                setInfoText(i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.connectionParameters"));
                addInput(DBTYPE, i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.dbType"),
                        getDriverInput());
                addValidationExpression(DBTYPE + " is not null", i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.dbTypeChooser"));
                addInput(HOST, i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.hostName"),
                        "127.0.0.1", new StringType(LENGTH));
                addValidationExpression(HOST + " is not null", i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.hostNameChooser"));
                addInput(PORT, i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.portNumberDefault"),
                        "0", new IntType(LENGTH));

                addValidationExpression("(" + PORT + " >= 0) and (" + PORT
                        + " <= 32767)", i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.portNumber"));
                addInput(DBNAME, i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.dbName"),
                        "database_name", new StringType(LENGTH));
                addValidationExpression(DBNAME + " is not null", i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.dbNameMandatory"));
                addInput(USER, i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.userName"),
                        "postgres", new StringType(LENGTH));
                addInput(PASSWORD, i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.password"), "",
                        new PasswordType(LENGTH));

                addInput(SSL, i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.ssl"), new CheckBoxChoice(false));
        }

        private InputType getDriverInput() {
                DriverManager driverManager = sourceManager.getDriverManager();

                Driver[] filtered = driverManager.getDrivers(new DBDriverFilter());

                String[] ids = new String[filtered.length];
                String[] texts = new String[filtered.length];
                for (int i = 0; i < texts.length; i++) {
                        Driver rod = filtered[i];
                        ids[i] = rod.getDriverId();
                        texts[i] = rod.getTypeDescription();
                }
                ComboBoxChoice combo = new ComboBoxChoice(ids, texts);
                return combo;
        }

        @Override
        public String postProcess() {
                try {
                        Connection connection = getConnection();
                        connection.close();
                        return null;
                } catch (SQLException e) {
                        return i18n.tr("orbisgis.view.geocatalog.sourceWizards.db.cannotConnect") + ": " + e.getMessage();
                }
        }

        public Connection getConnection() throws SQLException {
                DBSource dbSource = getDBSource();
                Connection connection = getDBDriver().getConnection(dbSource.getHost(),
                        dbSource.getPort(), dbSource.isSsl(), dbSource.getDbName(), dbSource.getUser(),
                        dbSource.getPassword());
                return connection;
        }

        public DBDriver getDBDriver() {
                DriverManager driverManager = sourceManager.getDriverManager();
                String dbType = getInput(DBTYPE);
                DBDriver dbDriver = (DBDriver) driverManager.getDriver(dbType);
                return dbDriver;
        }

        @Override
        public String validateInput() {
                return null;
        }

        public DBSource getDBSource() {
                String host = getInput(HOST);
                int port = Integer.parseInt(getInput(PORT));
                String dbName = getInput(DBNAME);
                String user = getInput(USER);
                String password = getInput(PASSWORD);
                DBDriver dbDriver = getDBDriver();
                if ((port == 0) || (getInput(PORT).trim().length() == 0)) {
                        port = dbDriver.getDefaultPort();
                }
                boolean ssl = false;
                if (getInput(SSL).equals("true")) {
                        ssl = true;
                }

                return new DBSource(host, port, dbName, user, password, getDBDriver().getPrefixes()[0], ssl);
        }

        /**
         * Return the sourceManager
         * @return 
         */
        public SourceManager getSourceManager() {
                return sourceManager;
        }
}
