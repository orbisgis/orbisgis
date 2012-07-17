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

import org.apache.log4j.Logger;
import org.gdms.driver.DataSet;
import org.orbisgis.view.geocatalog.Catalog;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author ebocher
 */
public class DataBaseRow {

        private String intputSourceName;
        private String outputsourceName;
        private String schema;
        private String pk = "gid";
        private int epsg_code;
        private Boolean export;
        private boolean isSpatial = false;
        private String spatialField = "the_geom";
        private String inputSpatialField;
        private static final Logger LOGGER = Logger.getLogger(Catalog.class);
        protected final static I18n I18N = I18nFactory.getI18n(Catalog.class);

        /*
         * Create a row object that stores all informations to export in a
         * database
         */
        public DataBaseRow(String intputSourceName, String outputSourceName, String schema, String pk, String spatialField, int epsg_code, Boolean export) {
                this.intputSourceName = intputSourceName;
                this.outputsourceName = outputSourceName;
                this.inputSpatialField = spatialField;
                this.schema = schema;
                this.pk = pk;
                this.spatialField = spatialField;
                this.epsg_code = epsg_code;
                this.export = export;
        }

        /*
         * Specify if the input source is spatial
         */
        public boolean isIsSpatial() {
                return isSpatial;
        }

        /*
         * Set if the input source is spatial
         */
        public void setIsSpatial(boolean isSpatial) {
                this.isSpatial = isSpatial;
        }

        /*
         * Return the spatial field
         */
        public String getSpatialField() {
                return spatialField;
        }

        /**
         * Return the EPSG code
         *
         * @return
         */
        public int getEpsg_code() {
                return epsg_code;
        }

        /**
         * Specify if the input source can be exported
         *
         * @return
         */
        public Boolean isExport() {
                return export;
        }

        /**
         * Return the list of all primary key
         *
         * @return
         */
        public String getPK() {
                return pk;
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
                                return getOutPutsourceName();
                        case 2:
                                return getSchema();
                        case 3:
                                return getPK();
                        case 4:
                                return getSpatialField();
                        case 5:
                                return String.valueOf(getEpsg_code());
                        case 6:
                                return isExport();
                        default:
                                return null;
                }
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
                                setOutPutsourceName(String.valueOf(aValue));
                                break;
                        case 2:
                                setSchema(String.valueOf(aValue));
                                break;
                        case 3:
                                setPk(String.valueOf(aValue));
                                break;
                        case 4:
                                setSpatialField(String.valueOf(aValue));
                                break;
                        case 5:
                                try {
                                        Integer value = Integer.valueOf(aValue.toString());
                                        setEpsg_code(value);
                                } catch (NumberFormatException e) {
                                        LOGGER.error(I18N.tr("Cannot format the EPSG code into an int. The default code will be used."), e);
                                }
                                break;


                        case 6:
                                setExport(Boolean.valueOf(aValue.toString()));
                                break;
                        default:
                                break;
                }
        }

        /**
         * Change the input source name
         *
         * @param sourceName
         */
        private void setInputSourceName(String sourceName) {
                this.intputSourceName = sourceName;
        }

        /**
         * Change the EPSG code
         *
         * @param epsg_code
         */
        public void setEpsg_code(int epsg_code) {
                this.epsg_code = epsg_code;
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
         * Change the name of the primary key
         *
         * @param pk
         */
        public void setPk(String pk) {
                this.pk = pk;
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
         * Set a new spatial field
         *
         * @param spatialField
         */
        public void setSpatialField(String spatialField) {
                this.spatialField = spatialField;
        }

        /**
         * Return all cell values as a list of objects
         *
         * @return
         */
        public Object[] getObjects() {
                return new Object[]{getInputSourceName(), getOutPutsourceName(), getSchema(), getPK(), getSpatialField(), getEpsg_code(), isExport()};
        }

        public void setOutPutsourceName(String outPutsourceName) {
                this.outputsourceName = outPutsourceName;
        }

        public String getOutPutsourceName() {
                return outputsourceName;
        }

        public String getInputSpatialField() {
                return inputSpatialField;
        }

        public void setInputSpatialField(String inputSpatialField) {
                this.inputSpatialField = inputSpatialField;
        }

        public String toSQL() {
                StringBuilder sb = new StringBuilder();
                sb.append("SELECT ");
                sb.append("* EXCEPT(+").append(getPK()).append(") ").append(getPK());
                if (getEpsg_code() != -1) {
                        sb.append(" , ST_Transform(").append(getInputSpatialField()).append(", ").
                                append(getEpsg_code()).append(") as ").append(getSpatialField());
                }
                sb.append(" FROM ");
                sb.append(getInputSourceName());
                sb.append(";");
                return sb.toString();
        }
}
