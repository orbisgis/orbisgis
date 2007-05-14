package org.gdms.data.db;

import java.util.HashMap;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.driver.DriverException;
import org.gdms.sql.instruction.Adapter;
import org.gdms.sql.instruction.ColRefAdapter;
import org.gdms.sql.instruction.CustomAdapter;
import org.gdms.sql.instruction.SelectAdapter;
import org.gdms.sql.instruction.SemanticException;
import org.gdms.sql.instruction.TableRefAdapter;
import org.gdms.sql.instruction.UnionAdapter;
import org.gdms.sql.instruction.Utilities;
import org.gdms.sql.parser.SimpleNode;
import org.gdms.sql.strategies.Strategy;
import org.gdms.sql.strategies.StrategyCriterion;

import com.hardcode.driverManager.DriverLoadException;

/**
 * Strategy that delegates the execution of the select queries on the underlying
 * data base management system. The result of this delegation is a view created
 * on that system so it's necessary call DataSourceFactory.freeResources() to the
 * views to be removed
 */
public class DelegatingStrategy extends Strategy implements StrategyCriterion {

	private DataSourceFactory dsf;
	private HashMap<String, String> gdbmsNameViewName = new HashMap<String, String>();
	private boolean delegating;
	
	public DelegatingStrategy(DataSourceFactory dataSourceFactory) {
		this.dsf = dataSourceFactory;
	}

	/**
     * associates the gdbms name 'tableName' with the underlaying dbms view 
     * name 'viewName'.
     * 
	 * @param tableName gdbms name
	 * @param viewName view name
	 */
	public void registerView(String tableName, String viewName) {
		gdbmsNameViewName.put(tableName, viewName);
	}

	@Override
	public DataSource select(SelectAdapter instr) throws ExecutionException {
        try {
			DataSource[] tables = instr.getTables();
			
	        String sql = translateInstruction(instr, tables);
	
	        DBTableDataSourceAdapter table = (DBTableDataSourceAdapter) tables[0];
	
	        //Set the driver info
	        DBTableSourceDefinition dsd = table.getDataSourceDefinition();
	        DBQuerySourceDefinition qd = new DBQuerySourceDefinition(
	        		dsd.getSourceDefinition(), sql);
	
	        String dataSourceName = dsf.nameAndRegisterDataSource(qd);

            return dsf.getDataSource(dataSourceName);
        } catch (NoSuchTableException e) {
            throw new ExecutionException(e);
        } catch (DriverLoadException e) {
            throw new ExecutionException(e);
		} catch (DataSourceCreationException e) {
            throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		} catch (SemanticException e) {
			throw new ExecutionException(e);
		}
	}

	/**
	 * Translates the table references by changind the gdbms name with the
	 * underlaying database management system table name
	 *
	 * @param instr root of the adapted tree
	 * @param tables DataSources involved in the instruction
	 *
	 * @return The translated sql query
	 *
	 * @throws DriverException If driver access fails
	 * @throws SemanticException If the instruction is not semantically correct
	 */
	private String translateInstruction(Adapter instr, DataSource[] tables)
	    throws DriverException, SemanticException {
	    HashMap<String, String> instrNameDBName = new HashMap<String, String>();
	
	    translateFromTables(instr, instrNameDBName);
	    translateColRefs(instr, instrNameDBName, tables);
	
	    return Utilities.getText(instr.getEntity());
	}

	/**
	 * Translates the table references by changind the gdbms name with the
	 * underlaying database management system table name
	 *
	 * @param adapter adapter processed
	 * @param instrNameDBName hasmap with the gdbms names a s the keys and the
	 *        database name as the values.
	 * @param tables tables involved in the instruction
	 *
	 * @throws DriverException If driver access fails
	 * @throws SemanticException If the instruction is not semantically correct
	 */
	private void translateColRefs(Adapter adapter, HashMap<String, String> instrNameDBName,
	    DataSource[] tables) throws DriverException, SemanticException {
	    if (adapter instanceof ColRefAdapter) {
	        ColRefAdapter tra = (ColRefAdapter) adapter;
	        SimpleNode s = tra.getEntity();
	
	        if (s.first_token != s.last_token) {
	            String name = s.first_token.image;
	            s.first_token.image = instrNameDBName.get(name);
	        } else {
	            String tableName = guessTableName(s.first_token.image, tables);
	            s.first_token.image = instrNameDBName.get(tableName) + "." +
	                s.first_token.image;
	        }
	    } else {
	        Adapter[] hijos = adapter.getChilds();
	
	        for (int i = 0; i < hijos.length; i++) {
	            translateColRefs(hijos[i], instrNameDBName, tables);
	        }
	    }
	}

	/**
	 * Translates the table references by changind the gdbms name with the
	 * underlaying database management system table name
	 *
	 * @param adapter adapter processed
	 * @param instrNameDBName hasmap with the gdbms names a s the keys and the
	 *        database name as the values.
	 */
	private void translateFromTables(Adapter adapter, HashMap<String, String> instrNameDBName) {
	    if (adapter instanceof TableRefAdapter) {
	        TableRefAdapter tra = (TableRefAdapter) adapter;
	        SimpleNode s = tra.getEntity();
	
	        if (s.first_token == s.last_token) {
	            String alias = "gdbms" + System.currentTimeMillis();
	            String name = s.first_token.image;
	            s.first_token.image = gdbmsNameViewName.get(name) + " " + alias;
	            instrNameDBName.put(name, alias);
	        } else {
	            String alias = s.last_token.image;
	            String name = s.first_token.image;
	            s.first_token.image = gdbmsNameViewName.get(name).toString();
	            instrNameDBName.put(alias, alias);
	        }
	    } else {
	        Adapter[] hijos = adapter.getChilds();
	
	        for (int i = 0; i < hijos.length; i++) {
	            translateFromTables(hijos[i], instrNameDBName);
	        }
	    }
	}

	/**
	 * Gets the name of the table where the field is in
	 *
	 * @param fieldName field whose table wants to be guessed
	 * @param tables tables involved in the search
	 *
	 * @return table name
	 *
	 * @throws DriverException If driver access fails
	 * @throws SemanticException If the instruction is not semantically correct
	 */
	private String guessTableName(String fieldName, DataSource[] tables)
	    throws DriverException, SemanticException {
	    int tableIndex = -1;
	
	    for (int i = 0; i < tables.length; i++) {
	        tables[i].beginTrans();
	
	        if (tables[i].getFieldIndexByName(fieldName) != -1) {
	            if (tableIndex != -1) {
	                throw new SemanticException("ambiguous column reference: " +
	                    fieldName);
	            } else {
	                tableIndex = i;
	            }
	        }
	
	        tables[i].rollBackTrans();
	    }
	
	    if (tableIndex == -1) {
	        throw new SemanticException("Field not found: " + fieldName);
	    }
	
	    return tables[tableIndex].getName();
	}

	/**
	 * Devuelve true si todas las tablas provienen del mismo data base
	 * management system
	 *
	 * @param tables Array de tablas
	 *
	 * @return boolean
	 */
	private boolean sameDBMS(DataSource[] tables) {
	    if (!(tables[0] instanceof DBTableDataSourceAdapter)) {
	        return false;
	    }
	
	    String dbms = ((DBTableDataSourceAdapter) tables[0]).getDBMS();
	
	    for (int i = 1; i < tables.length; i++) {
	        if (!(tables[i] instanceof DBTableDataSourceAdapter)) {
	            return false;
	        }
	
	        if (!dbms.equals(((DBTableDataSourceAdapter) tables[1]).getDBMS())) {
	            return false;
	        }
	    }
	
	    return true;
	}

	public Strategy getStrategy(SelectAdapter instr) {
	    DataSource[] tables;
		try {
			tables = instr.getTables();

			if (sameDBMS(tables) && delegating) {
		    	return this;
		    } else {
		    	return null;
		    }
		} catch (DriverLoadException e) {
		} catch (NoSuchTableException e) {
		} catch (DataSourceCreationException e) {
		}
	
		return null;
	}

	public Strategy getStrategy(UnionAdapter instr) {
		return null;
	}

	public Strategy getStrategy(CustomAdapter instr) {
		return null;
	}

	public boolean isDelegating() {
		return delegating;
	}

	public void setDelegating(boolean delegating) {
		this.delegating = delegating;
	}


}
