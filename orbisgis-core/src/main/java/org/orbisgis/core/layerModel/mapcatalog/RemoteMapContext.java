/*
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
package org.orbisgis.core.layerModel.mapcatalog;

import java.util.Date;
import org.orbisgis.core.renderer.se.common.Description;

/**
 *
 * @author Nicolas Fortin
 */
public class RemoteMapContext {
        private int id = 0;
        private Description description = new Description();
        private ConnectionProperties cParams;
        private Date date;

        /**
         * Constructor
         * @param cParams Connection parameters
         */
        public RemoteMapContext(ConnectionProperties cParams) {
                this.cParams = cParams;
        }

        @Override
        public boolean equals(Object obj) {
                if(!(obj instanceof RemoteMapContext)) {
                        return false;
                }                
                final RemoteMapContext other = (RemoteMapContext) obj;
                if (this.id != other.id) {
                        return false;
                }
                if (this.description != other.description && (this.description == null || !this.description.equals(other.description))) {
                        return false;
                }
                if (this.cParams != other.cParams && (this.cParams == null || !this.cParams.equals(other.cParams))) {
                        return false;
                }
                if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
                        return false;
                }
                return true;
        }

        @Override
        public int hashCode() {
                int hash = 7;
                hash = 89 * hash + this.id;
                hash = 89 * hash + (this.description != null ? this.description.hashCode() : 0);
                hash = 89 * hash + (this.cParams != null ? this.cParams.hashCode() : 0);
                hash = 89 * hash + (this.date != null ? this.date.hashCode() : 0);
                return hash;
        }

        
        /**
         * 
         * @param description 
         */
        public void setDescription(Description description) {
                this.description = description;
        }

        /**
         * Get remote map context Id
         * @param id 
         */
        public void setId(int id) {
                this.id = id;
        }

        /**
         * 
         * @return Date of the document
         */
        public Date getDate() {
                return date;
        }

        /**
         * Set the date
         * @param date Date of the document
         */
        public void setDate(Date date) {
                this.date = date;
        }
        
        /**
         * Request Map Description, the description is buffered.
         * @return Map description
         */
        public Description getDescription() {
                return description;
        }
        
        /**
         * Connect to the server and request the map content.
         * This call may take a long time to execute.
         * @return
         * @throws IOException The request fail 
         */
        //public InputStream getMapContent() throws IOException {
                
        //}
}
