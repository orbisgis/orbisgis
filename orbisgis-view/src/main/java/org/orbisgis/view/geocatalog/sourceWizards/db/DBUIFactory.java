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

import org.gdms.driver.Driver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.DBDriverFilter;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.sif.multiInputPanel.*;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Erwan Bocher
 */
public class DBUIFactory {

        protected static final I18n I18N = I18nFactory.getI18n(TableExportPanel.class);
        static String DBTYPE = "dbtype";
        static String HOST = "host";
        static String PORT = "port";
        static String DBNAME = "dbname";
        static String USER = "user";
        static String SSL = "ssl";
        static String CONNAME = "conname";

        /**
         * Create a connection panel to add a new database
         *
         * @return
         */
        public static MultiInputPanel getConnectionPanel() {
                int LENGTH = 20;

                MultiInputPanel connectionMP = new MultiInputPanel(I18N.tr("Connection parameters"));

                connectionMP.addInput(CONNAME, I18N.tr("Connexion name"), I18N.tr("MyConnexion"),
                        new TextBoxType(LENGTH));

                connectionMP.addInput(DBTYPE, I18N.tr("Type of database"),
                        getDriverInput());
                connectionMP.addInput(HOST, I18N.tr("Host"),
                        "127.0.0.1", new TextBoxType(LENGTH));
                connectionMP.addInput(PORT, I18N.tr("Default port"),
                        "0", new TextBoxType(LENGTH));
                connectionMP.addInput(DBNAME, I18N.tr("Database name"),
                        "database_name", new TextBoxType(LENGTH));
                connectionMP.addInput(USER, I18N.tr("User name"),
                        "postgres", new TextBoxType(LENGTH));

                connectionMP.addInput(SSL, I18N.tr("SSL"), new CheckBoxChoice(false));

                connectionMP.addValidation(new MIPValidation() {

                        @Override
                        public String validate(MultiInputPanel mid) {

                                if (mid.getInput(CONNAME).isEmpty()) {
                                        return I18N.tr("Please specify a connexion name");
                                }

                                if (mid.getInput(DBNAME).isEmpty()) {
                                        return I18N.tr("The database name is mandatory");
                                }
                                String host = mid.getInput(HOST);
                                if (host.isEmpty()) {
                                        return I18N.tr("The host cannot be null");
                                }
                                String port = mid.getInput(PORT);

                                if (port.isEmpty()) {
                                        try {
                                                Integer portNumber = Integer.valueOf(port);
                                                if (portNumber >= 0 && portNumber <= 32767) {
                                                        return I18N.tr("The port number must be comprise between 0 and 32767");
                                                }

                                        } catch (NumberFormatException e) {
                                                return I18N.tr("Cannot format the port code into an int");
                                        }
                                }
                                return null;


                        }
                });

                return connectionMP;
        }

        /**
         * Create a connection panel to edit the database connection parameters
         *
         * @return
         */
        public static MultiInputPanel getEditConnectionPanel(String connexionName, String property) {
                int LENGTH = 20;
                String[] connectionParams = property.split(",");
                MultiInputPanel connectionMP = new MultiInputPanel(I18N.tr("Connection parameters"));

                connectionMP.addInput(CONNAME, I18N.tr("Connexion name"), connexionName,
                        new TextBoxType(LENGTH));

                connectionMP.addInput(DBTYPE, I18N.tr("Type of database"), connectionParams[0],
                        getDriverInput());
                connectionMP.addInput(HOST, I18N.tr("Host"),
                        connectionParams[1], new TextBoxType(LENGTH));
                connectionMP.addInput(PORT, I18N.tr("Default port"),
                        connectionParams[2], new TextBoxType(LENGTH));
                connectionMP.addInput(DBNAME, I18N.tr("Database name"),
                        connectionParams[4], new TextBoxType(LENGTH));
                connectionMP.addInput(USER, I18N.tr("User name"),
                        connectionParams[5], new TextBoxType(LENGTH));


                connectionMP.addInput(SSL, I18N.tr("SSL"), new CheckBoxChoice(Boolean.valueOf(connectionParams[3])));

                connectionMP.addValidation(new MIPValidation() {

                        @Override
                        public String validate(MultiInputPanel mid) {

                                if (mid.getInput(CONNAME).isEmpty()) {
                                        return I18N.tr("Please specify a connexion name");
                                }

                                if (mid.getInput(DBNAME).isEmpty()) {
                                        return I18N.tr("The database name is mandatory");
                                }
                                String host = mid.getInput(HOST);
                                if (host.isEmpty()) {
                                        return I18N.tr("The host cannot be null");
                                }
                                String port = mid.getInput(PORT);

                                if (port.isEmpty()) {
                                        try {
                                                Integer portNumber = Integer.valueOf(port);
                                                if (portNumber >= 0 && portNumber <= 32767) {
                                                        return I18N.tr("The port number must be comprise between 0 and 32767");
                                                }

                                        } catch (NumberFormatException e) {
                                                return I18N.tr("Cannot format the port code into an int");
                                        }
                                }
                                return null;


                        }
                });

                return connectionMP;
        }

        /**
         * Populate a combobox with all supported drivers
         * @return 
         */
        public static InputType getDriverInput() {
                DataManager dm = (DataManager) Services.getService(DataManager.class);
                DriverManager driverManager = dm.getSourceManager().getDriverManager();

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
}
