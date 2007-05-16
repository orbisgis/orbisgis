package org.gdms.sql.instruction;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;

import com.hardcode.driverManager.DriverLoadException;


/**
 * Adaptador de la instrucci�n UNION
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class UnionAdapter extends Adapter {
    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private DataSource getTable(int table) throws NoSuchTableException, DataSourceCreationException, ExecutionException, DriverLoadException {
        Adapter hijo = getChilds()[table];

        if (hijo instanceof TableRefAdapter) {
            String name = Utilities.getText(hijo.getEntity());

            return getTableByName(name);
        } else if (hijo instanceof SelectAdapter) {
            return getTableBySelect((SelectAdapter) hijo);
        } else {
            throw new IllegalStateException("Cannot create the DataSource");
        }
    }

    /**
     * @throws ExecutionException 
     * @throws DataSourceCreationException
     * @see org.gdms.sql.instruction.UnionInstruction#getFirstTable()
     */
    public DataSource getFirstTable() throws NoSuchTableException, DataSourceCreationException, ExecutionException, DriverLoadException {
        return getTable(0);
    }

    /**
     * Obtiene el data source a partir de una select
     *
     * @param select
     *
     * @return
     */
    private DataSource getTableBySelect(SelectAdapter select) throws DriverLoadException, NoSuchTableException, ExecutionException {
        return getInstructionContext().getDSFactory().getDataSource(select);
    }

    /**
     * Obtiene un data source por el nombre
     *
     * @param name
     *
     * @return
     *
     * @throws TableNotFoundException Si nop hay ninguna tabla con el nombre
     *         'name'
     * @throws CreationException 
     * @throws NoSuchTableException 
     * @throws DriverLoadException 
     * @throws DriverException 
     * @throws DataSourceCreationException 
     * @throws RuntimeException
     */
    private DataSource getTableByName(String name)
        throws DriverLoadException, NoSuchTableException, DataSourceCreationException {
        String[] tabla = name.split(" ");

        if (tabla.length == 1) {
            return getInstructionContext().getDSFactory()
                       .getDataSource(name);
        } else {
            return getInstructionContext().getDSFactory()
                       .getDataSource(tabla[0], tabla[1]);
        }
    }

    /**
     * @throws ExecutionException 
     * @throws DataSourceCreationException 
     * @see org.gdms.sql.instruction.UnionInstruction#getSecondTable()
     */
    public DataSource getSecondTable() throws DriverLoadException, NoSuchTableException, DataSourceCreationException, ExecutionException {
        return getTable(1);
    }
}
