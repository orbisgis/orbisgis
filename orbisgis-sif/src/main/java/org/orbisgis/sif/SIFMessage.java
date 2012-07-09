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

package org.orbisgis.sif;

/**
 *
 * @author ebocher
 */


public class SIFMessage {
        public static int ERROR = 3;
        public static int WARNING = 2;
        public static int OK = 1;
        private final String message;
        private final int messageType;
        
        /*
         * Create a SIFMessage to the corresponding status
         */
        public SIFMessage(String message, int messageType){
                this.message =message;
                this.messageType = messageType;
        }
        
        /**
         * Create a default SIFMessage that corresponds to OK.
         */
        public SIFMessage(){
                this.message =null;
                this.messageType = OK;
        }

        /**
         * Get the message
         * @return 
         */
        public String getMessage() {
                return message;
        }
        
        /**
         * Get the message type.
         * 3 types are allowed :
         * 1 = OK
         * 2 = Warning
         * 3 = Error
         * SIF message type is used to lock or not the OK - CANCEL buttons in a SIFPanel.
         * @return 
         */
        public int getMessageType() {
                return messageType;
        }
}
