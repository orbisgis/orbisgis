package org.gdms.data;

import org.gdms.driver.DriverException;

public class OCCounterDecorator extends AbstractDataSourceDecorator {

	private OpenCloseCounter ocCounter;

	public OCCounterDecorator(DataSource internalDataSource) {
		super(internalDataSource);
		ocCounter = new OpenCloseCounter(this.getName());
	}

	@Override
	public void cancel() throws DriverException, AlreadyClosedException {
		if (ocCounter.stop()) {
			try {
				getDataSource().cancel();
			} catch (DriverException e) {
				ocCounter.start();
				throw e;
			}
		}
	}

	@Override
	public void commit() throws DriverException, FreeingResourcesException, NonEditableDataSourceException {
		if (ocCounter.nextStopCloses()) {
			try {
				getDataSource().commit();
				ocCounter.stop();
			} catch (DriverException e) {
				throw e;
			}
		}
	}

	@Override
	public void open() throws DriverException {
		if (ocCounter.start()) {
			getDataSource().open();
		}
	}

	@Override
	public boolean isOpen() {
		return ocCounter.isOpen();
	}

}
