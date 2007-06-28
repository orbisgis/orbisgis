package org.gdms.sql.strategies;

import org.gdms.sql.instruction.CreateAdapter;
import org.gdms.sql.instruction.CustomAdapter;
import org.gdms.sql.instruction.SelectAdapter;
import org.gdms.sql.instruction.SemanticException;
import org.gdms.sql.instruction.UnionAdapter;

/**
 * Manejador de las distintas estrategias disponibles para ejecutar las
 * instrucciones
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class StrategyManager {

	private StrategyCriterion[] criteria;

	public StrategyManager(StrategyCriterion... dbdsFactory) {
		criteria = dbdsFactory;
	}

	/**
	 * Obtiene la estrategia m�s adecuada en funci�n de la instrucci�n a
	 * ejecutar y de las condiciones actuales del sistema
	 *
	 * @param instr
	 *            Instrucci�n que se desea ejecutar
	 *
	 * @return estrategia capaz de ejecutar la instrucci�n
	 * @throws SemanticException
	 */
	public Strategy getStrategy(SelectAdapter instr) {
		for (StrategyCriterion c : criteria) {
			Strategy s = c.getStrategy(instr);
			if (s != null)
				return s;
		}

		return new FirstStrategy();
	}

	/**
	 * Obtiene la estrategia �ptima para ejecutar la instrucci�n de union que se
	 * pasa como par�metro
	 *
	 * @param instr
	 *            instrucci�n que se quiere ejecutar
	 *
	 * @return
	 * @throws SemanticException
	 */
	public Strategy getStrategy(UnionAdapter instr) {
		for (StrategyCriterion c : criteria) {
			Strategy s = c.getStrategy(instr);
			if (s != null)
				return s;
		}

		return new FirstStrategy();
	}

	/**
	 * Gets the only strategy to execute custom queries
	 *
	 * @param instr
	 *            root node of the custom query to execute
	 *
	 * @return Strategy
	 * @throws SemanticException
	 */
	public Strategy getStrategy(CustomAdapter instr) {
		for (StrategyCriterion c : criteria) {
			Strategy s = c.getStrategy(instr);
			if (s != null)
				return s;
		}

		return new FirstStrategy();
	}

	public Strategy getStrategy(CreateAdapter instr) {
		for (StrategyCriterion c : criteria) {
			Strategy s = c.getStrategy(instr);
			if (s != null)
				return s;
		}

		return new FirstStrategy();
	}

}
