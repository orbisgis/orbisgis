package org.gdms.data.edition;

public interface EditionListener {
    /**
     * One change has been done in the DataSource
     * 
     * @param e
     */
    public void singleModification(EditionEvent e);
    
    /**
     * Zero or more changes has been done in the DataSource 
     * 
     * @param e
     */
    public void multipleModification(MultipleEditionEvent e);
}
