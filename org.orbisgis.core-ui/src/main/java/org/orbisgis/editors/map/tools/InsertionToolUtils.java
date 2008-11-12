package org.orbisgis.editors.map.tools;

import java.text.ParseException;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.editors.map.tool.TransitionException;
import org.orbisgis.ui.sif.AskValidValue;
import org.sif.UIFactory;

public class InsertionToolUtils {

	/**
	 * Ask the user to input initial values for the non null fields
	 * 
	 * @param sds
	 * @param row
	 * @return
	 * @throws DriverException
	 * @throws TransitionException
	 */
	public static Value[] populateNotNullFields(SpatialDataSourceDecorator sds,
			Value[] row) throws DriverException, TransitionException {
		Value[] ret = new Value[row.length];
		for (int i = 0; i < sds.getFieldCount(); i++) {
			Type type = sds.getFieldType(i);
			if (type.getBooleanConstraint(Constraint.NOT_NULL)
					&& !type.getBooleanConstraint(Constraint.AUTO_INCREMENT)) {
				AskValidValue av = new AskValidValue(sds, i);
				if (UIFactory.showDialog(av)) {
					try {
						ret[i] = av.getUserValue();
					} catch (ParseException e) {
						throw new TransitionException("bug!");
					}
				} else {
					throw new TransitionException("Insertion cancelled");
				}
			} else {
				ret[i] = row[i];
			}
		}

		return ret;
	}

}
