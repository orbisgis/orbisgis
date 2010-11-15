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

public class SpatialReferenceSystem {

	private CoordinateOperationSequence coordinateOperationSequence = null;

	
	public SpatialReferenceSystem(DataSourceFactory dsf, String sourceCRS, String targetCRS) {
		GDMSProj4CRSFactory gdmsProj4CRSFactory = new GDMSProj4CRSFactory(dsf);
		init((GeodeticCRS) gdmsProj4CRSFactory.getCRSFromSRID(sourceCRS),
				(GeodeticCRS) gdmsProj4CRSFactory.getCRSFromSRID(targetCRS));
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
		return getGeometryTransformer().transform(geom);
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
