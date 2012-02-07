/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orb.orbisgis.core.ui.plugins.ows;

/**
 * Intended to contain meta information related to an ows context file
 * @author cleglaun
 */
public class OwsFileBasic {
    private final int id;
    private final String owsTitle;
    private final String owsAbstract;

    public OwsFileBasic(int id, String owsTitle, String owsAbstract) {
        this.id = id;
        this.owsTitle = owsTitle;
        this.owsAbstract = owsAbstract;
    }

    public int getId() {
        return id;
    }

    public String getOwsAbstract() {
        return owsAbstract;
    }

    public String getOwsTitle() {
        return owsTitle;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OwsFileBasic other = (OwsFileBasic) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public String toString() {
        return "OwsFileBasic{" + "id=" + id + ", owsTitle=" + owsTitle + ", owsAbstract=" + owsAbstract + '}';
    }
}
