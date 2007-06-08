/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;

import com.hardcode.driverManager.DriverLoadException;

/**
 * Adaptador
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class TableListAdapter extends Adapter {
	private DataSource[] tables;

	/**
	 * Obtiene los DataSources de la cl�usula from
	 * 
	 * @return array de datasources
	 * 
	 * @throws TableNotFoundException
	 *             Si no se encontr� alguna tabla
	 * @throws CreationException
	 * @throws NoSuchTableException
	 * @throws DriverLoadException
	 * @throws DriverException
	 * @throws DataSourceCreationException
	 * @throws RuntimeException
	 */
	public DataSource[] getTables() throws DriverLoadException,
			NoSuchTableException, DataSourceCreationException {
		if (tables == null) {
			Adapter[] hijos = getChilds();
			ArrayList<DataSource> ret = new ArrayList<DataSource>();

			for (int i = 0; i < hijos.length; i++) {
				TableRefAdapter tRef = (TableRefAdapter) hijos[i];

				if (tRef.getAlias() == null) {
					ret.add(getInstructionContext().getDSFactory()
							.getDataSource(tRef.getName(),
									DataSourceFactory.NORMAL));
				} else {
					ret.add(getInstructionContext().getDSFactory()
							.getDataSource(tRef.getName(),
									DataSourceFactory.NORMAL));
				}
			}

			tables = (DataSource[]) ret.toArray(new DataSource[0]);
		}

		return tables;
	}
}
