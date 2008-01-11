/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.layerModel;

import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.source.SourceManager;
import org.gdms.sql.instruction.TableNotFoundException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.persistence.LayerType;

public abstract class GdmsLayer extends BasicLayer {

	private String mainName;

	public GdmsLayer(String name,
			CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		this.mainName = name;
	}

	@Override
	public void setName(String name) throws LayerException {
		SourceManager sourceManager = OrbisgisCore.getDSF().getSourceManager();

		// Remove previous alias
		if (!mainName.equals(getName())) {
			sourceManager.removeName(getName());
		}
		if (!name.equals(mainName)) {
			super.setName(name);
			try {
				sourceManager.addName(mainName, name);
			} catch (TableNotFoundException e) {
				throw new RuntimeException("bug!", e);
			} catch (SourceAlreadyExistsException e) {
				throw new LayerException("Source already exists", e);
			}
		}
	}

	public void close() throws LayerException {
		SourceManager sourceManager = OrbisgisCore.getDSF().getSourceManager();

		// Remove alias
		if (!mainName.equals(getName())) {
			sourceManager.removeName(getName());
		}
	}

	public LayerType getStatus() {
		LayerType ret = new LayerType();
		ret.setName(getName());
		ret.setSourceName(mainName);

		return ret;
	}
}
