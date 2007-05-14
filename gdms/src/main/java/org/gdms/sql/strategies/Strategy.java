package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.sql.instruction.CustomAdapter;
import org.gdms.sql.instruction.SelectAdapter;
import org.gdms.sql.instruction.UnionAdapter;



/**
 * Interfaz que define las operaciones que se pueden realizar con los
 * DataSource. Las distintas implementaciones de esta interfaz ser�n las
 * encargadas del uso de los indices, del algoritmo usado para cada operaci�n,
 * ...
 */
public abstract class Strategy {
    /**
     * Realiza una select a partir de la instrucci�n que se pasa como par�metro
     *
     * @param instr Objeto con la informaci�n sobre las tablas que entran en
     *        juego en la instrucci�n, campos, expresiones condicionales, ...
     *
     * @return DataSource con el resultado de la instruccion
     *
     * @throws ExecutionException The query failed
     */
    public DataSource select(SelectAdapter instr)
        throws ExecutionException {
        throw new RuntimeException(
            "This strategy does not support select execution");
    }

    /**
     * Realiza una union a partir de la instrucci�n que se pasa como par�metro
     *
     * @param instr Objeto con la informaci�n sobre las tablas que entran en
     *        juego en la instrucci�n
     *
     * @return DataSource con el resultado de la instruccion
     *
     * @throws ExecutionException The query failed
     */
    public DataSource union(UnionAdapter instr)
        throws ExecutionException {
        throw new RuntimeException(
            "This strategy does not support union execution");
    }

    /**
     * Makes a custom query
     *
     * @param instr The instruction specifying the custom query
     *
     * @return The result DataSource
     *
     * @throws ExecutionException The query failed
     */
    public DataSource custom(CustomAdapter instr)
        throws ExecutionException {
        throw new RuntimeException(
            "This strategy does not support custom queries execution");
    }
}
