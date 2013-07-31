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
        
        public static enum ExportStatus{WAITING, OK, ERROR, UNSELECTED};
        private ExportStatus exportStatus = ExportStatus.WAITING;
        private String intputSourceName;
        private String outputSourceName;
        private String schema;
        public static final int DEFAULT_EPSG = -1;
        private  int inputEpsgCode = -1;
        private int outputEpsgCode = -1;
        private Boolean export;
        private boolean isSpatial = false;
        private String outputSpatialField = "the_geom";
        private String inputSpatialField = "the_geom";
        private static final I18n I18N = I18nFactory.getI18n(Catalog.class);        
        public static final String DEFAULT_CRS = "No crs";
        private String crsInformation  = DEFAULT_CRS;

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
         * Update the status of the row
         * @param exportStatus 
         */
        public void setExportStatus(ExportStatus exportStatus) {
                this.exportStatus = exportStatus;                
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
                                return this.exportStatus;                                 
                        case 1:
                                return getInputSourceName();
                        case 2:
                                return getOutputSourceName();
                        case 3:
                                return getSchema();   
                        case 4 :
                                return getInputSpatialField();
                        case 5:
                                return getOutputSpatialField();                       
                        case 6:
                                return getCrsInformation();
                        case 7:
                                return String.valueOf(getOutputEpsgCode());
                        case 8:
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
                        case 2:
                                setOutputSourceName(String.valueOf(aValue));
                                break;
                        case 3:
                                setSchema(String.valueOf(aValue));
                                break;
                        case 5:
                                //The crs is not null and the epsg code exists but the user wants to set a -1
                                 //Because of best pratices the user must used the default epsg code
                                if (!crsInformation.equals(DEFAULT_CRS)&& getInputEpsgCode() != -1) {
                                        JOptionPane.showMessageDialog(null, I18N.tr("-1 is not allowed .\n"
                                                + "The input code will be used."));
                                } else {
                                        setOutputSpatialField(String.valueOf(aValue));
                                }
                                break;
                        case 7:
                                try {
                                        Integer value = Integer.valueOf(aValue.toString());
                                        setOutputEpsgCode(value);
                                } catch (NumberFormatException e) {
                                        JOptionPane.showMessageDialog(null, I18N.tr("Cannot format the EPSG code into an int.\n"
                                                + "The default code will be used."));
                                }
                                break;

                        case 8:
                                Boolean isExportable = Boolean.valueOf(aValue.toString());
                                setExport(isExportable);
                                if(isExportable.booleanValue()){
                                       setExportStatus(ExportStatus.WAITING);
                                }
                                else{
                                       setExportStatus(ExportStatus.UNSELECTED); 
                                }
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
                return new Object[]{this.exportStatus, getInputSourceName(), getOutputSourceName(), getSchema(),
                        getInputSpatialField(), getOutputSpatialField(), getCrsInformation(),getOutputEpsgCode(), isExport()};
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

        /**
         * Return some informations about the input CRS
         * 
         * if the authority and the code are available return :  
         * ie  EPSG:4326 or IGNF:301111...
         * else if returns the name of the CRS 
         * ie LAMBERT2 
         * 
         * else returns "No crs"
         * 
         * @return 
         */
        public String getCrsInformation() {
                return crsInformation;
        }

        /**
         * Set informations about the CRS
         * if the authority and the code are available set :  
         * ie  EPSG:4326 or IGNF:301111...
         * else if set the name of the CRS 
         * ie LAMBERT2
         * else set "No crs"
         * 
         * @param crsInformation 
         */
        public void setCrsInformation(String crsInformation) {
                this.crsInformation = crsInformation;
        }      
        
        
        
}
