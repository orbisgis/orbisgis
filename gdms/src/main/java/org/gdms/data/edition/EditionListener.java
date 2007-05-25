package org.gdms.data.edition;

public interface EditionListener {
    /**
     * One change has been done in the InternalDataSource
     * 
     * @param e
     */
    public void singleModification(EditionEvent e);
    
    /**
     * Zero or more changes has been done in the InternalDataSource 
     * 
     * @param e
     */
    public void multipleModification(MultipleEditionEvent e);
}
