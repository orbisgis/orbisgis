package org.gdms.driver;

import org.orbisgis.progress.ProgressMonitor;

/**
 * Interface to be implement by the Stream drivers to get RW capabilities
 * 
 * @author Vincent Dépériers
 */
public interface StreamReadWriteDriver extends StreamDriver {

    /**
     * Writes the content in the DataSet to the specified file
     *
     * @param dataSource DataSource with the contents
     * @param pm
     * @return
     * @throws DriverException
     */
    //Faire comment dans MemoryDataSetDriver.write
    boolean write(DataSet dataSource, ProgressMonitor pm) throws DriverException;
}
