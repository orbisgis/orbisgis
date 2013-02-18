/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog.sourceWizards.db;

import javax.swing.JOptionPane;
import org.orbisgis.view.geocatalog.Catalog;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Antoine Gourlay
 * @author Erwan Bocher
 */
public class DataBaseRow {
        
        private String intputSourceName;
        private String outputSourceName;
        private String schema;
        private int inputEpsgCode = -1;
        private int outputEpsgCode = -1;
        private Boolean export;
        private boolean isSpatial = false;
        private String outputSpatialField = "the_geom";
        private String inputSpatialField = "the_geom";
        private static final I18n I18N = I18nFactory.getI18n(Catalog.class);

        /**
         * Create a row object that stores all informations to export in a
         * database
         * @param intputSourceName
         * @param outputSourceName
         * @param schema
         * @param inputSpatialField
         * @param inputEpsgCode
         * @param export
         */
        public DataBaseRow(String intputSourceName, String outputSourceName, String schema, String inputSpatialField, String outputSpatialField,
                        int inputEpsgCode, int outputEpsgCode, Boolean export) {
                this.intputSourceName = intputSourceName;
                this.outputSourceName = outputSourceName;
                this.schema = schema;                
                this.inputSpatialField = inputSpatialField;
                this.outputSpatialField = outputSpatialField;
                this.inputEpsgCode = inputEpsgCode;
                this.outputEpsgCode=outputEpsgCode;                
                this.export = export;
        }

        /**
         * Specify if the input source is spatial
         * @return
         */
        public boolean isSpatial() {
                return isSpatial;
        }

        /**
         * Set if the input source is spatial
         * @param isSpatial
         */
        public void setSpatial(boolean isSpatial) {
                this.isSpatial = isSpatial;
        }

        /**
         * Return the EPSG code
         * @return
         */
        public int getInputEpsgCode() {
                return inputEpsgCode;
        }

        /**
         * Specify if the input source can be exported
         * @return
         */
        public Boolean isExport() {
                return export;
        }

       

        /**
         * Return the name of the schema
         *
         * @return
         */
        public String getSchema() {
                return schema;
        }

        /**
         * Return the name of the input source
         *
         * @return
         */
        public String getInputSourceName() {
                return intputSourceName;
        }

        /**
         * Return the value of the cell
         *
         * @param col
         * @return
         */
        public Object getValue(int col) {
                switch (col) {
                        case 0:
                                return getInputSourceName();
                        case 1:
                                return getOutputSourceName();
                        case 2:
                                return getSchema();   
                        case 3 :
                                return getInputSpatialField();
                        case 4:
                                return getOutputSpatialField();                       
                        case 5:
                                return String.valueOf(getInputEpsgCode());
                        case 6:
                                return String.valueOf(getOutputEpsgCode());
                        case 7:
                                return isExport();
                        default:
                                return null;
                }
        }

        public String getOutputSpatialField() {
                return outputSpatialField;
        }

        public void setOutputSpatialField(String outputSpatialField) {
                this.outputSpatialField = outputSpatialField;
        }

        /**
         * Set a value to the cell
         *
         * @param aValue
         * @param col
         */
        public void setValue(Object aValue, int col) {
                switch (col) {
                        case 1:
                                setOutputSourceName(String.valueOf(aValue));
                                break;
                        case 2:
                                setSchema(String.valueOf(aValue));
                                break;
                        case 4:
                                setOutputSpatialField(String.valueOf(aValue));
                                break;
                        case 6:
                                try {
                                        Integer value = Integer.valueOf(aValue.toString());
                                        setOutputEpsgCode(value);
                                } catch (NumberFormatException e) {
                                        JOptionPane.showMessageDialog(null, I18N.tr("Cannot format the EPSG code into an int.\n"
                                                + "The default code will be used."));
                                }
                                break;


                        case 7:
                                setExport(Boolean.valueOf(aValue.toString()));
                                break;
                        default:
                                break;
                }
        }

        /**
         * Change the EPSG code
         *
         * @param epsg_code
         */
        public void setInputEpsgCode(int epsg_code) {
                this.inputEpsgCode = epsg_code;
        }

        /**
         * Change the status to export the source
         *
         * @param export
         */
        public void setExport(Boolean export) {
                this.export = export;
        }

        

        /**
         * Change the schema name
         *
         * @param schema
         */
        public void setSchema(String schema) {
                this.schema = schema;
        }

        /**
         * Return all cell values as a list of objects
         *
         * @return
         */
        public Object[] getObjects() {
                return new Object[]{getInputSourceName(), getOutputSourceName(), getSchema(),
                        getInputSpatialField(), getInputEpsgCode(), isExport()};
        }

        /**
         * Change the name of the datasource
         * @param outPutsourceName 
         */
        public void setOutputSourceName(String outPutsourceName) {
                this.outputSourceName = outPutsourceName;
        }

        /**
         * Return the name of the output table
         * @return 
         */
        public String getOutputSourceName() {
                return outputSourceName;
        }

        /**
         * Return the field name of the default spatial column
         * @return 
         */
        public String getInputSpatialField() {
                return inputSpatialField;
        }
        

        /**
         * Return the output epsg code set by the user
         * @return 
         */
        public int getOutputEpsgCode() {
                return outputEpsgCode;
        }

        /**
         * Change the name of the output epsg code
         * @param outputEpsgCode 
         */
        public void setOutputEpsgCode(int outputEpsgCode) {
                this.outputEpsgCode = outputEpsgCode;
        }        
        
}
