package org.orbisgis.core.ui.views.geocognition.actions;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.sql.GeocognitionBuiltInCustomQuery;
import org.orbisgis.core.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.core.ui.views.geocognition.action.IGeocognitionAction;
import org.orbisgis.errorManager.ErrorManager;

public class UnRegisterBuiltInCustomQuery implements IGeocognitionAction {

	@Override
	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		if (GeocognitionCustomQueryFactory.BUILT_IN_QUERY_ID.equals(element
				.getTypeId())) {
			String registered = element.getProperties().get(
					GeocognitionBuiltInCustomQuery.REGISTERED);
			if ((registered != null)
					&& registered
							.equals(GeocognitionBuiltInCustomQuery.IS_REGISTERED)) {
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
		if (GeocognitionCustomQueryFactory.BUILT_IN_QUERY_ID.equals(element
				.getTypeId())) {
			Class<? extends CustomQuery> fnc = (Class<? extends CustomQuery>) element
					.getObject();
			try {
				QueryManager.remove(fnc.newInstance().getName());
			} catch (InstantiationException e) {
				Services.getService(ErrorManager.class).error("Bug!", e);
			} catch (IllegalAccessException e) {
				Services.getService(ErrorManager.class).error("Bug!", e);
			}
		}
	}

}
