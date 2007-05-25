/*
 * Created on 12-oct-2004
 */
package org.gdms.sql.instruction;

import java.util.ArrayList;

import org.gdms.data.InternalDataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;

import com.hardcode.driverManager.DriverLoadException;


/**
 * Adaptador
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class TableListAdapter extends Adapter {
	private InternalDataSource[] tables;

	/**
	 * Obtiene los DataSources de la cl�usula from
	 *
	 * @return array de datasources
	 *
	 * @throws TableNotFoundException Si no se encontr� alguna tabla
	 * @throws CreationException 
	 * @throws NoSuchTableException 
	 * @throws DriverLoadException 
	 * @throws DriverException 
	 * @throws DataSourceCreationException 
	 * @throws RuntimeException
	 */
	public InternalDataSource[] getTables() throws DriverLoadException, NoSuchTableException, DataSourceCreationException {
		if (tables == null) {
			Adapter[] hijos = getChilds();
			ArrayList<InternalDataSource> ret = new ArrayList<InternalDataSource>();

			for (int i = 0; i < hijos.length; i++) {
				TableRefAdapter tRef = (TableRefAdapter) hijos[i];

				if (tRef.getAlias() == null) {
					ret.add(getInstructionContext().getDSFactory()
								.getDataSource(tRef.getName()));
				} else {
					ret.add(getInstructionContext().getDSFactory()
								.getDataSource(tRef.getName()));
				}
			}

			tables = (InternalDataSource[]) ret.toArray(new InternalDataSource[0]);
		}

		return tables;
	}
}
