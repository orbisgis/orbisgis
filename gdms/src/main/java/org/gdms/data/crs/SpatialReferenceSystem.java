/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY, Adelin PIAU
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */

package org.gdms.data.crs;

import java.util.List;

import org.gdms.data.DataSourceFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;

import fr.cts.CoordinateOperation;
import fr.cts.Identifier;
import fr.cts.IllegalCoordinateException;
import fr.cts.crs.GeodeticCRS;
import fr.cts.op.CoordinateOperationFactory;
import fr.cts.op.CoordinateOperationSequence;

/**
 * 
 * Transform a geometry from crs code to another one.
 *
 */
public class SpatialReferenceSystem {

	private CoordinateOperationSequence coordinateOperationSequence = null;
        private int targetSRID;

	
	public SpatialReferenceSystem(DataSourceFactory dsf, int sourceCRS, int targetCRS) {
		GDMSProj4CRSFactory gdmsProj4CRSFactory = new GDMSProj4CRSFactory(dsf);
		init((GeodeticCRS) gdmsProj4CRSFactory.getCRSFromSRID(sourceCRS),
				(GeodeticCRS) gdmsProj4CRSFactory.getCRSFromSRID(targetCRS));
                this.targetSRID = targetCRS;
	}

	public SpatialReferenceSystem(GeodeticCRS sourceCRS, GeodeticCRS targetCRS) {

		init(sourceCRS, targetCRS);
	}

	private void init(GeodeticCRS sourceCRS, GeodeticCRS targetCRS) {
		if ((sourceCRS != null) || (targetCRS != null)) {
			List<CoordinateOperation> ops = CoordinateOperationFactory
					.createCoordinateOperations(sourceCRS, targetCRS);
			coordinateOperationSequence = new CoordinateOperationSequence(
					new Identifier(SpatialReferenceSystem.class, "From  "
							+ sourceCRS.getCode() + " to "
							+ targetCRS.getCode()), ops);
		} else {
			new RuntimeException("Source and target CRS cannot be null.");
		}

	}

	public CoordinateOperationSequence getCoordinateOperationSequence() {
		return coordinateOperationSequence;
	}

	public Geometry transform(Geometry geom) {
		Geometry g = getGeometryTransformer().transform(geom);
                g.setSRID(targetSRID);
                return g;
	}

	public GeometryTransformer getGeometryTransformer() {
		GeometryTransformer gt = null;
		if (gt == null) {
			gt = new GeometryTransformer() {
				protected CoordinateSequence transformCoordinates(
						CoordinateSequence cs, Geometry geom) {
					Coordinate[] cc = geom.getCoordinates();
					CoordinateSequence newcs = new CoordinateArraySequence(cc);
					for (int i = 0; i < cc.length; i++) {
						Coordinate c = cc[i];
						try {
							double[] xyz = coordinateOperationSequence
									.transform(new double[] { c.x, c.y, c.z });
							newcs.setOrdinate(i, 0, xyz[0]);
							newcs.setOrdinate(i, 1, xyz[1]);
							if (xyz.length > 2)
								newcs.setOrdinate(i, 2, xyz[2]);
							else
								newcs.setOrdinate(i, 2, Double.NaN);
						} catch (IllegalCoordinateException ice) {
							ice.printStackTrace();
						}
					}
					return newcs;
				}
			};
		}

		return gt;
	}
}
