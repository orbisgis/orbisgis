/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

/**
 * This value object class stands for a workspace in the data repository.
 * @author cleglaun
 */
public class OwsWorkspace {
    private final String name;

    public OwsWorkspace(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the workspace.
     * @return 
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OwsWorkspace other = (OwsWorkspace) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    /**
     * This custom toString() avoids me to use a custom renderer for the combobox
     * in {@link OwsImportPanel} and {@link OwsExportPanel}.
     * @return 
     */
    @Override
    public String toString() {
        return name;
    }
}
