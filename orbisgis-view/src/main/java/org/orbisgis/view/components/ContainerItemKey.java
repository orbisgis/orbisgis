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
package org.orbisgis.view.components;

/**
 * @brief Generic list item that store a key with the value shown
 */
public class ContainerItemKey {
    private String label; //I18N label
    private String key;   //Internal name of the item
    /**
     * Constructor
     * @param label The I18N label of the Item, shown in the GUI
     * @param key The internal name of this item, retrieved by listeners for processing
     */
    public ContainerItemKey(String key, String label) {
        this.label = label;
        this.key = key;
    }
    /**
     * 
     * @return The internal name of this item
     */
    public String getKey() {
        return key;
    }
    /**
     * Set the internal name of this item
     * @param key Internal name of this item
     */
    public void setKey(String key) {
        this.key = key;
    }
    /**
     * Get the I18N GUI label
     * @return the I18N GUI label
     */
    public String getLabel() {
        return label;
    }
    /**
     * Set the I18N GUI label
     * @param label the I18N GUI label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * The GUI representation of the item
     * @return String GUI representation of the item
     */
    @Override
    public String toString() {
        return label;
    }

    /**
     * The equal is done on the KEY internal name.
     * @param obj Other item of the list
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContainerItemKey other = (ContainerItemKey) obj;
        if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key)) {
            return false;
        }
        return true;
    }

    /**
     * Compute hashcode for the item
     * @return Hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.key != null ? this.key.hashCode() : 0);
        return hash;
    }
}
