package org.orbisgis.views.geocognition.actions;

import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.Services;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.geocognition.Geocognition;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.sql.GeocognitionBuiltInFunction;
import org.orbisgis.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.views.geocognition.action.IGeocognitionAction;

public class RegisterBuiltInFunction implements IGeocognitionAction {

	@Override
	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		if (GeocognitionFunctionFactory.BUILT_IN_FUNCTION_ID.equals(element
				.getTypeId())) {
			String registered = element.getProperties().get(
					GeocognitionBuiltInFunction.REGISTERED);
			if ((registered != null)
					&& registered
							.equals(GeocognitionBuiltInFunction.IS_NOT_REGISTERED)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return selectionCount > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Geocognition geocognition, GeocognitionElement element) {
		if (GeocognitionFunctionFactory.BUILT_IN_FUNCTION_ID.equals(element
				.getTypeId())) {
			Class<? extends Function> fnc = (Class<? extends Function>) element
					.getObject();
			try {
				FunctionManager.remove(fnc.newInstance().getName());
				FunctionManager.addFunction(fnc);
			} catch (InstantiationException e) {
				Services.getService(ErrorManager.class).error("Bug!", e);
			} catch (IllegalAccessException e) {
				Services.getService(ErrorManager.class).error("Bug!", e);
			}
		}
	}
}
