package org.gdms.data;

import java.util.ArrayList;

public class DataSourceListenerSupport {

	private ArrayList<DataSourceListener> listeners = new ArrayList<DataSourceListener>();

	public void addDataSourceListener(DataSourceListener listener) {
		listeners.add(listener);
	}

	public void removeDataSourceListener(DataSourceListener listener) {
		listeners.remove(listener);
	}

	public void fireOpen(DataSource ds) {
		for (DataSourceListener listener : listeners) {
			listener.open(ds);
		}
	}

	public void fireCancel(DataSource ds) {
		for (DataSourceListener listener : listeners) {
			listener.cancel(ds);
		}
	}

	public void fireCommit(DataSource ds) {
		for (DataSourceListener listener : listeners) {
			listener.commit(ds);
		}
	}

}
