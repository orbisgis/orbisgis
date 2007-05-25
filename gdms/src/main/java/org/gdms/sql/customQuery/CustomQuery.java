package org.gdms.sql.customQuery;

import org.gdms.data.InternalDataSource;
import org.gdms.data.ExecutionException;
import org.gdms.sql.instruction.Expression;
import org.gdms.sql.strategies.AbstractSecondaryDataSource;



/**
 * Interface to implement by the custom queries
 *
 * @author Fernando Gonz�lez Cort�s
 */
public interface CustomQuery {
    /**
     * Executes the custom query
     *
     * @param tables tables involved in the query
     * @param values values passed to the query
     *
     * @return InternalDataSource result of the query
     *
     * @throws ExecutionException if the custom query execution fails
     */
    public AbstractSecondaryDataSource evaluate(InternalDataSource[] tables, Expression[] values)
        throws ExecutionException;

    /**
     * Gets the query name. Must ve a valid SQL identifier (i.e.: '.' is not
     * allowed)
     *
     * @return query name
     */
    public String getName();
}
