package org.gdms.sql.strategies;

import org.gdms.sql.instruction.CreateAdapter;
import org.gdms.sql.instruction.CustomAdapter;
import org.gdms.sql.instruction.SelectAdapter;
import org.gdms.sql.instruction.UnionAdapter;

public interface StrategyCriterion {

	/**
	 * Obtiene la estrategia m�s adecuada en funci�n de la instrucci�n a
	 * ejecutar y de las condiciones actuales del sistema
	 *
	 * @param instr
	 *            Instrucci�n que se desea ejecutar
	 *
	 * @return estrategia capaz de ejecutar la instrucci�n
	 */
	public Strategy getStrategy(SelectAdapter instr);

	/**
	 * Obtiene la estrategia �ptima para ejecutar la instrucci�n de union que se
	 * pasa como par�metro
	 *
	 * @param instr
	 *            instrucci�n que se quiere ejecutar
	 *
	 * @return
	 */
	public Strategy getStrategy(UnionAdapter instr);

	/**
	 * Gets the only strategy to execute custom queries
	 *
	 * @param instr
	 *            root node of the custom query to execute
	 *
	 * @return Strategy
	 */
	public Strategy getStrategy(CustomAdapter instr);

	/**
	 * Gets the strategy to execute custom queries
	 *
	 * @param instr
	 * @return
	 */
	public Strategy getStrategy(CreateAdapter instr);

}
