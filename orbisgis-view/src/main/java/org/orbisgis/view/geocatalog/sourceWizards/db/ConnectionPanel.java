/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.geocatalog.sourceWizards.db;

import java.sql.Connection;
import java.sql.SQLException;
import org.gdms.data.db.DBSource;
import org.gdms.driver.DBDriver;
import org.gdms.driver.Driver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.DBDriverFilter;
import org.gdms.source.SourceManager;
import org.orbisgis.sif.multiInputPanel.*;
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
                super(i18n.tr("Connect"));
                this.sourceManager = sourceManager;
                addInput(DBTYPE, i18n.tr("Type of database"),
                        getDriverInput());
                addInput(HOST, i18n.tr("Host"),
                        "127.0.0.1", new TextBoxType(LENGTH));
                addInput(PORT, i18n.tr("Default port"),
                        "0", new TextBoxType(LENGTH));
                addInput(DBNAME, i18n.tr("Database name"),
                        "database_name", new TextBoxType(LENGTH));
                addInput(USER, i18n.tr("User name"),
                        "postgres", new TextBoxType(LENGTH));
                addInput(PASSWORD, i18n.tr("Password"), "",
                        new PasswordType(LENGTH));

                addInput(SSL, i18n.tr("SSL"), new CheckBoxChoice(false));

                addValidation(new MIPValidation() {

                        @Override
                        public String validate(MultiInputPanel mid) {

                                //Validation
                                if (mid.getInput(DBNAME).isEmpty()) {
                                        return i18n.tr("The database name is mandatory");
                                }
                                String host = mid.getInput(HOST);
                                if (host.isEmpty()) {
                                        return i18n.tr("The host cannot be null");
                                }
                                String port = mid.getInput(PORT);

                                if (port.isEmpty()) {
                                        try {
                                                Integer portNumber = Integer.valueOf(port);
                                                if (portNumber >= 0 && portNumber <= 32767) {
                                                        return i18n.tr("The port number must be comprise between 0 and 32767");
                                                }

                                        } catch (NumberFormatException e) {
                                                return i18n.tr("Cannot format the port code into an int");
                                        }
                                }
                                return null;


                        }
                });


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

        public Connection getConnection() throws SQLException {
                DBSource dbSource = getDBSource();
                DBDriver dr = getDBDriver();

                String cs = dr.getConnectionString(dbSource.getHost(),
                        dbSource.getPort(), dbSource.isSsl(), dbSource.getDbName(), dbSource.getUser(),
                        dbSource.getPassword());
                return dr.getConnection(cs);
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
         *
         * @return
         */
        public SourceManager getSourceManager() {
                return sourceManager;
        }
}
