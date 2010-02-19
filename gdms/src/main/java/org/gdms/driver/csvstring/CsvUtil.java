/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/*
 * net/balusc/util/CsvUtil.java
 *
 * Copyright (C) 2007 BalusC
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.gdms.driver.csvstring;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Useful CSV utilities.
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2006/06/parse-csv-upload.html
 */
public class CsvUtil {

    // Init ---------------------------------------------------------------------------------------

    // Defaults.
    private static final char DEFAULT_CSV_SEPARATOR = ',';
    private static final String DEFAULT_LINE_SEPARATOR = "\r\n"; // CRLF.

    private CsvUtil() {
        // Utility class, hide the constructor.
    }

    // Parsers ------------------------------------------------------------------------------------

    /**
     * CSV content parser. Convert an InputStream with the CSV contents to a two-dimensional List
     * of Strings representing the rows and columns of the CSV. Each CSV record is expected to be
     * separated by the default CSV field separator, a comma.
     * @param csvInput The InputStream with the CSV contents.
     * @return A two-dimensional List of Strings representing the rows and columns of the CSV.
     */
    public static List<List<String>> parseCsv(InputStream csvInput) {
        return parseCsv(csvInput, DEFAULT_CSV_SEPARATOR);
    }

    /**
     * CSV content parser. Convert an InputStream with the CSV contents to a two-dimensional List
     * of Strings representing the rows and columns of the CSV. Each CSV record is expected to be
     * separated by the specified CSV field separator.
     * @param csvInput The InputStream with the CSV contents.
     * @param csvSeparator The CSV field separator to be used.
     * @return A two-dimensional List of Strings representing the rows and columns of the CSV.
     */
    public static List<List<String>> parseCsv(InputStream csvInput, char csvSeparator) {

        // Prepare.
        BufferedReader csvReader = new BufferedReader(new InputStreamReader(csvInput));
        List<List<String>> csvList = new ArrayList<List<String>>();
        String csvRecord;

        // Process records.
        try {
            while ((csvRecord = csvReader.readLine()) != null) {
                csvList.add(parseCsvRecord(csvRecord, csvSeparator));
            }
        } catch (IOException e) {
            // This exception should never occur however as this should already be covered by the
            // source which feeds the InputStream to this method.
            throw new RuntimeException("Reading CSV failed.", e);
        }

        return csvList;
    }

    /**
     * CSV record parser. Convert a CSV record to a List of Strings representing the fields of each
     * CSV record. The CSV record is expected to be separated by the specified CSV field separator.
     * @param record The CSV record.
     * @param csvSeparator The CSV field separator to be used.
     * @return A List of Strings representing the fields of each CSV record.
     */
    private static List<String> parseCsvRecord(String record, char csvSeparator) {

        // Prepare.
        boolean quoted = false;
        StringBuilder csvBuilder = new StringBuilder();
        List<String> fields = new ArrayList<String>();

        // Process fields.
        for (int i = 0; i < record.length(); i++) {
            char c = record.charAt(i);
            csvBuilder.append(c);

            if (c == '"') {
                quoted = !quoted; // Detect nested quotes.
            }

            if ((!quoted && c == csvSeparator) // The separator.
                || i + 1 == record.length()) // End of record.
            {
                String field = csvBuilder.toString(); // Obtain the field.
                field = field.replaceAll(csvSeparator + "$", ""); // Trim ending semicolon
                field = field.replaceAll("^\"|\"$", ""); // Trim surrounding quotes.
                field = field.replaceAll("\"\"", "\\\""); // Re-escape quotes.
                fields.add(field.trim()); // Add field to List.
                csvBuilder = new StringBuilder(); // Reset.
            }
        }

        return fields;
    }

    // Formatters --------------------------------------------------------------------------------

    /**
     * CSV content formatter. Convert a two-dimensional List of Objects to a CSV in an InputStream.
     * Each CSV record will be separated by the default CSV field separator, a comma.
     * @param csvList A two-dimensional List of Objects representing the rows and columns of the
     * CSV.
     * @return The InputStream containing the CSV contents (actually a ByteArrayInputStream).
     */
    public static <T extends Object> InputStream formatCsv(List<List<T>> csvList) {
        return formatCsv(csvList, DEFAULT_CSV_SEPARATOR);
    }

    /**
     * CSV content formatter. Convert a two-dimensional List of Objects to a CSV in an InputStream.
     * Each CSV record will be separated by the specified CSV field separator.
     * @param csvList A two-dimensional List of Objects representing the rows and columns of the
     * CSV.
     * @param csvSeparator The CSV field separator to be used.
     * @return The InputStream containing the CSV contents (actually a ByteArrayInputStream).
     */
    public static <T extends Object> InputStream formatCsv(List<List<T>> csvList, char csvSeparator) {

        // Prepare.
        StringBuilder csvContent = new StringBuilder();

        // Process records.
        for (List<T> csvRecord : csvList) {
            if (csvRecord != null) {
                csvContent.append(formatCsvRecord(csvRecord, csvSeparator));
            }

            // Add default line separator.
            csvContent.append(DEFAULT_LINE_SEPARATOR);
        }

        return new ByteArrayInputStream(csvContent.toString().getBytes());
    }

    /**
     * CSV record formatter. Convert a List of Objects representing the fields of a CSV record to a
     * String representing each CSV record. The CSV record will be separated by the specified CSV
     * field separator.
     * @param csvRecord A List of Objects representing the fields of a CSV reecord.
     * @param csvSeparator The CSV field separator to be used.
     * @return A String representing a CSV record.
     */
    private static <T extends Object> String formatCsvRecord(List<T> csvRecord, char csvSeparator) {

        // Prepare.
        StringBuilder fields = new StringBuilder();
        String separator = String.valueOf(csvSeparator);

        // Process fields.
        for (Iterator<T> iter = csvRecord.iterator(); iter.hasNext();) {
            T object = iter.next();

            if (object != null) {
                String field = object.toString();

                if (field.contains("\"")) {
                    field = field.replaceAll("\"", "\"\""); // Escape quotes.
                }

                if (field.contains(separator) || field.contains("\"")) {
                    field = "\"" + field + "\""; // Surround with quotes.
                }

                fields.append(field);
            }

            if (iter.hasNext()) {
                fields.append(separator); // Add field separator.
            }
        }

        return fields.toString();
    }

}