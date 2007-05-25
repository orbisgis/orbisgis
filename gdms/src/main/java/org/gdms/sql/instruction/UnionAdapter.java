package org.gdms.sql.instruction;

import org.gdms.data.InternalDataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.SimpleNode;
import org.gdms.sql.parser.Token;

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
    private InternalDataSource getTable(int table) throws NoSuchTableException, DataSourceCreationException, ExecutionException, DriverLoadException {
        Adapter hijo = getChilds()[table];

        if (hijo instanceof TableRefAdapter) {
            String name = Utilities.getText(hijo.getEntity());

            return getTableByName(name);
        } else if (hijo instanceof SelectAdapter) {
            return getTableBySelect((SelectAdapter) hijo);
        } else {
            throw new IllegalStateException("Cannot create the InternalDataSource");
        }
    }

    /**
     * @throws ExecutionException
     * @throws DataSourceCreationException
     * @see org.gdms.sql.instruction.UnionInstruction#getFirstTable()
     */
    public InternalDataSource getFirstTable() throws NoSuchTableException, DataSourceCreationException, ExecutionException, DriverLoadException {
        return getTable(0);
    }

    /**
     * Obtiene el data source a partir de una select
     *
     * @param select
     *
     * @return
     */
    private InternalDataSource getTableBySelect(SelectAdapter select) throws DriverLoadException, NoSuchTableException, ExecutionException {
    	SimpleNode node = select.getEntity();
    	Token t = node.first_token;
    	StringBuilder sql = new StringBuilder("");
    	while (t != node.last_token) {
    		sql.append(t.image).append(" ");
    		t = t.next;
    	}
    	sql.append(t.image).append(" ");

        return getInstructionContext().getDSFactory().executeSQL(sql.toString());
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
    private InternalDataSource getTableByName(String name)
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
    public InternalDataSource getSecondTable() throws DriverLoadException, NoSuchTableException, DataSourceCreationException, ExecutionException {
        return getTable(1);
    }
}
