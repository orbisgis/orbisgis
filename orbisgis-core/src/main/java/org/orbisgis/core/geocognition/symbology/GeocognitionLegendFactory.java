package org.orbisgis.core.geocognition.symbology;

import java.util.HashSet;

import org.orbisgis.core.Services;
import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.carto.LegendManager;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.GeocognitionExtensionElement;

public class GeocognitionLegendFactory implements GeocognitionElementFactory {

	@Override
	public String getJAXBContextPath() {
		HashSet<String> contexts = new HashSet<String>();
		Legend[] availableLegends = getAvailableLegends();
		for (Legend legend : availableLegends) {
			String context = legend.getJAXBContext();
			if (context != null) {
				contexts.add(context);
			}
		}

		String ret = "";
		String separator = "";
		for (String context : contexts) {
			ret += separator + context;
			separator = ":";
		}

		if (ret.length() == 0) {
			return null;
		} else {
			return ret;
		}
	}

	private Legend[] getAvailableLegends() {
		LegendManager lm = (LegendManager) Services
				.getService(LegendManager.class);
		Legend[] availableLegends = lm.getAvailableLegends();
		return availableLegends;
	}

	@Override
	public GeocognitionExtensionElement createElementFromXML(Object xmlObject,
			String contentTypeId) throws PersistenceException {
		Legend[] availableLegends = getAvailableLegends();
		for (Legend legend : availableLegends) {
			if (legend.getLegendTypeId().equals(contentTypeId)) {
				Legend newInstance = legend.newInstance();
				newInstance.setJAXBObject(xmlObject);
				return new GeocognitionLegend(newInstance, this);
			}
		}

		throw new PersistenceException("Unrecognized legend: " + contentTypeId);
	}

	@Override
	public GeocognitionExtensionElement createGeocognitionElement(Object object) {
		return new GeocognitionLegend((Legend) object, this);
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof Legend;
	}

	@Override
	public boolean acceptContentTypeId(String typeId) {
		Legend[] availableLegends = getAvailableLegends();
		for (Legend legend : availableLegends) {
			if (legend.getLegendTypeId().equals(typeId)) {
				return true;
			}
		}

		return false;
	}

}
