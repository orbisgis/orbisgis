/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.geocognition.symbology;

import java.util.HashSet;

import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.GeocognitionExtensionElement;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.carto.LegendManager;

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
