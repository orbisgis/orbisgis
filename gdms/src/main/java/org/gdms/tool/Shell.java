/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DataSourceFinalizationException;
import org.gdms.data.schema.Metadata;
import org.gdms.sql.engine.Engine;
import org.gdms.sql.engine.SQLStatement;

/**
 * Main class for command-line invocation of gdms
 *
 * @author Antoine Gourlay
 */
public final class Shell {

        /**
         * Entry point.
         *
         * @param args
         * @throws IOException  
         */
        public static void main(String[] args) throws IOException {
                System.out.println("Gdms 2.0 Console");
                System.out.println();
                Console c = System.console();
                
                if (c != null) {
                        DataSourceFactory dsf = new DataSourceFactory();
                        interactive(c.reader(), c.writer(), dsf);
                        try {
                                dsf.freeResources();
                        } catch (DataSourceFinalizationException ex) {
                                System.out.print("Error : ");
                                System.out.println(ex.getLocalizedMessage());
                        }

                } else {
                        System.out.println("No interactive console. Exiting.");
                }
        }

        /**
         * Interactive entry point.
         *
         * @param reader input
         * @param writer output
         * @param dsf DSF
         * @throws IOException if there is an error reading the input
         */
        public static void interactive(Reader reader, PrintWriter writer, DataSourceFactory dsf) throws IOException {
                BufferedReader bu = new BufferedReader(reader);

                writer.println("Interactive console");
                writer.println("Type 'help' for available commands,");
                writer.println("     'exit' to quit.");
                writer.println("SQL can be execute with the 'sql' command.");
                writer.println("Example: 'sql SELECT 42;'");

                while (true) {
                        writer.write("> ");
                        String line = bu.readLine();
                        if (line != null) {
                                if (line.startsWith("sql")) {
                                        int idx = line.indexOf(' ');
                                        if (idx == -1 || idx == line.length() - 1) {
                                                writer.println("Error with command 'sql': there"
                                                        + " must be some SQL statement after 'sql'.");
                                                continue;
                                        }
                                        String sql = line.substring(idx + 1);
                                        SQLStatement st;
                                        try {
                                                st = Engine.parse(sql, DataSourceFactory.getDefaultProperties());
                                        } catch (Exception e) {
                                                writer.format("Error: %s", e.getLocalizedMessage());
                                                writer.println();
                                                continue;
                                        }
                                        
                                        st.setDataSourceFactory(dsf);
                                        try {
                                                st.prepare();
                                        } catch (Exception e) {
                                                writer.format("Error: %s", e.getLocalizedMessage());
                                                writer.println();
                                                continue;
                                        }
                                        Metadata m = st.getResultMetadata();
                                        if (m == null) {
                                                try {
                                                        st.execute();
                                                        st.cleanUp();
                                                } catch (Exception e) {
                                                        writer.println("Error: there must be only one statement.");
                                                        continue;
                                                }
                                        } else {
                                                try {
                                                        DataSource ds = dsf.getDataSource(st, DataSourceFactory.DEFAULT, null);
                                                        ds.open();
                                                        int colCount = ds.getFieldCount();
                                                        writer.print(ds.getFieldName(0));
                                                        for (int i = 1; i < colCount; i++) {
                                                                writer.print(", ");
                                                                writer.print(ds.getFieldName(i));
                                                        }
                                                        writer.print('\n');
                                                        final long rC = ds.getRowCount();
                                                        int stop = rC > 20 ? 20 : (int) rC;
                                                        for (int i = 0; i < stop; i++) {
                                                                writer.println(Arrays.toString(ds.getRow(i)));
                                                        }
                                                        if (stop < rC) {
                                                                writer.format("and %d more rows.", rC - stop);

                                                        }
                                                        ds.close();
                                                } catch (Exception e) {
                                                        writer.format("Error: %s", e.getLocalizedMessage());
                                                        writer.println();
                                                        continue;
                                                }
                                        }
                                } else if (line.startsWith("exit")) {
                                        writer.println("Exiting.");
                                        break;
                                } else if (line.startsWith("help")) {
                                        writer.println("");
                                } else {
                                        writer.println("Unknown command!");
                                }
                        } else {
                                writer.println("Unknown command!");
                        }
                }
        }

        private Shell() {
        }
}
