package org.gdms.data.stream;

import java.util.List;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DriverDataSource;
import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.DeleteEditionInfo;
import org.gdms.data.edition.EditionInfo;
import org.gdms.data.edition.PhysicalRowAddress;
import org.gdms.driver.*;
import org.gdms.source.CommitListener;
import org.gdms.source.DefaultSourceManager;
import org.gdms.source.Source;
import org.orbisgis.progress.NullProgressMonitor;

/**
 * Adapter to the DataSource interface for stream drivers
 * 
 * @author Vincent Dépériers
 */
public class StreamSourceAdapter extends DriverDataSource implements Commiter, CommitListener {

    private StreamDriver driver;
    private StreamSource def;
    private static final Logger LOG = Logger.getLogger(StreamSourceAdapter.class);

    /**
     * Creates a new StreamSourceAdapter
     *
     *
     * @param src
     * @param def
     * @param driver
     */
    public StreamSourceAdapter(Source src, StreamSource def, StreamDriver driver) {
        super(src);
        this.def = def;
        this.driver = driver;
        LOG.trace("Constructor");
    }

    @Override
    public void open() throws DriverException {
        LOG.trace("Opening");
        //Il y aura des arguments pour open dont on aura la valeur grace à def :)
        driver.open();
        fireOpen(this);
        DefaultSourceManager sm = (DefaultSourceManager) getDataSourceFactory().getSourceManager();
        sm.addCommitListener(this);
    }

    @Override
    public void close() throws DriverException {
        LOG.trace("Closing");
        driver.close();
        fireCancel(this);
        DefaultSourceManager sm = (DefaultSourceManager) getDataSourceFactory().getSourceManager();
        sm.removeCommitListener(this);
    }

    @Override
    public void saveData(DataSource ds) throws DriverException {
        LOG.trace("Saving Data");
        ds.open();
        ((StreamReadWriteDriver) driver).write(ds, new NullProgressMonitor());
        ds.close();
    }

    @Override
    public Driver getDriver() {
        return driver;
    }

    @Override
    public void syncWithSource() throws DriverException {
        sync();
    }

    @Override
    public void commitDone(String name) throws DriverException {
        sync();
    }

    private void sync() throws DriverException {
        driver.close();
        //Faire ce dont on a besoin en parametre de close et open cf(DBTableDataSourceAdapter)
        driver.open();

    }

    @Override
    public boolean commit(List<PhysicalRowAddress> rowsDirections, String[] fieldNames, List<EditionInfo> schemaActions, List<EditionInfo> editionActions, List<DeleteEditionInfo> deletedPKs, DataSource modifiedSource) throws DriverException {
        LOG.trace("Commiting");
        boolean changed = ((StreamReadWriteDriver) driver).write(modifiedSource, new NullProgressMonitor());
        try {
            driver.close();
        } catch (DriverException e) {
            throw new DriverException("Cannot free resources: stream writen ...", e);
        }
        driver.open();

	fireCommit(this);
        
        return changed;
    }

    @Override
    public void isCommiting(String name, Object source) throws DriverException {
    }
}
