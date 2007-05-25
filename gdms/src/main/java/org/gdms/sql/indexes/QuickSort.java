package org.gdms.sql.indexes;


import java.io.IOException;
import java.util.Stack;

import org.gdms.data.InternalDataSource;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;


/**
 * DOCUMENT ME!
 *
 * @author Fernando Gonz�lez Cort�s
 */
public class QuickSort {
	private FixedIndexSet ret;
	private int fieldId;
	private InternalDataSource dataSource;

	/**
	 * DOCUMENT ME!
	 *
	 * @param ini DOCUMENT ME!
	 * @param fin DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws DriverException
	 * @throws IOException
	 */
	private long partition(long ini, long fin)
		throws DriverException, IOException {
		long first = ini;
		long last = fin;

		long pivotIndex = first;
		Object pivot = dataSource.getFieldValue(ret.getIndex(pivotIndex),
				fieldId);

		long up = first;
		long down = last;

		while (up < down) {
			//Encuentra el primer valor > que el pivote
			while (compare(pivot,
						dataSource.getFieldValue(ret.getIndex(up), fieldId)) <= 0) {
				up++;

				if (up > fin) {
					break;
				}
			}

			//Encuentra el primer valor <= que el pivote
			while (compare(pivot,
						dataSource.getFieldValue(ret.getIndex(down), fieldId)) > 0) {
				down--;

				if (down < ini) {
					break;
				}
			}

			if (up < down) {
				long aux = ret.getIndex(up);
				ret.setIndex(up, ret.getIndex(down));
				ret.setIndex(down, aux);
			}
		}

		long aux = ret.getIndex(pivotIndex);
		ret.setIndex(pivotIndex, ret.getIndex(down));
		ret.setIndex(down, aux);

		return up;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param o1 DOCUMENT ME!
	 * @param o2 DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws RuntimeException DOCUMENT ME!
	 */
	private int compare(Object o1, Object o2) {
		Value v1 = (Value) o1;
		Value v2 = (Value) o2;

		try {
			if (((BooleanValue) v1.less(v2)).getValue()) {
				return -1;
			} else if (((BooleanValue) v2.less(v1)).getValue()) {
				return 1;
			} else {
				return 0;
			}
		} catch (IncompatibleTypesException e) {
			throw new RuntimeException(
				"Como incompatibles si se indexa sobre la misma columna?");
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param v DOCUMENT ME!
	 * @param fieldId DOCUMENT ME!
	 * @param low DOCUMENT ME!
	 * @param high DOCUMENT ME!
	 *
	 * @throws DriverException
	 * @throws IOException
	 */
	public void quickSort(InternalDataSource v, int fieldId, long low, long high)
		throws DriverException, IOException {
		dataSource = v;
		this.fieldId = fieldId;

		ret = IndexFactory.createFixedIndex(high - low + 1);

		for (int i = 0; i < ret.getIndexCount(); i++) {
			ret.setIndex(i, i);
		}

		Stack<Intervalo> intervalos = new Stack<Intervalo>();
		Intervalo inicial = new Intervalo(low, high);
		intervalos.push(inicial);

		while (!intervalos.empty()) {
			Intervalo i = (Intervalo) intervalos.pop();

			long pivote = partition(i.ini, i.fin);

			if (i.ini < (pivote - 1)) {
				intervalos.push(new Intervalo(i.ini, pivote - 2));
			}

			if ((pivote + 1) < i.fin) {
				intervalos.push(new Intervalo(pivote, i.fin));
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return Returns the indexes.
	 */
	public FixedIndexSet getIndexes() {
		return ret;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @author Fernando Gonz�lez Cort�s
	 */
	public class Intervalo {
		long ini;
		long fin;

		/**
		 * Crea un nuevo Intervalo.
		 *
		 * @param ini DOCUMENT ME!
		 * @param fin DOCUMENT ME!
		 */
		public Intervalo(long ini, long fin) {
			this.ini = ini;
			this.fin = fin;
		}
	}
}
