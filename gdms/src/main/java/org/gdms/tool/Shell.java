/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.tool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.engine.ParseException;
import org.gdms.sql.engine.Engine;

/**
 * Main class for command-line invocation of gdms
 * @author Antoine Gourlay
 */
public final class Shell {

        // 03/24/2011 TODO to be improved; this shell isn't terrific...

        private static DataSourceFactory dsf;
        private static final String EOL = System.getProperty("line.separator");

        private Shell() {
        }

        private static void execute(String scriptFileName) throws
                IOException, ParseException, DriverException {
                Engine.execute(readScriptFile(scriptFileName), dsf);
        }

        private static String readScriptFile(String scriptFileName) throws IOException {
                StringBuilder ret = new StringBuilder();
                BufferedReader in = null;
                try {
                        in = new BufferedReader(new FileReader(
                                scriptFileName));
                        String line;

                        while ((line = in.readLine()) != null) {
                                ret.append(line).append(EOL);
                        }

                } finally {
                        if (in != null) {
                                in.close();
                        }
                }

                return ret.toString();
        }

        /**
         * Entry point.
         * @param args
         * @throws IOException
         * @throws ParseException
         */
        public static void main(String[] args) throws IOException, ParseException, DriverException {

                if (args != null && args.length > 0) {
                        String script = args[0];

                        if (script != null) {
                                dsf = new DataSourceFactory();
                                execute(script);
                        }
                }
        }
}
