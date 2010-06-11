/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488: Erwan BOCHER,
 * scientific researcher, Pierre-Yves FADET, computer engineer. Previous
 * computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 * 
 * or contact directly: erwan.bocher _at_ ec-nantes.fr Pierre-Yves.Fadet _at_
 * ec-nantes.fr
 **/
package org.orbisgis.core;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.types.Constraint;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.gdms.sql.customQuery.spatial.geometry.crs.ST_Transform;
import org.orbisgis.core.layerModel.DefaultMapContext;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.Layer;
import org.orbisgis.core.layerModel.LayerCollection;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.WMSLayer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;

import fr.cts.CoordinateOperation;
import fr.cts.Identifier;
import fr.cts.IllegalCoordinateException;
import fr.cts.crs.CompoundCRS;
import fr.cts.crs.CoordinateReferenceSystem;
import fr.cts.op.CoordinateOperationFactory;
import fr.cts.op.CoordinateOperationSequence;

public class DefaultDataManager implements DataManager {

	private static final Logger logger = Logger
			.getLogger(DefaultDataManager.class);
	private DataSourceFactory dsf;
	private static CoordinateReferenceSystem crs;

	public DefaultDataManager(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	public DataSourceFactory getDSF() {
		return dsf;
	}

	public IndexManager getIndexManager() {
		return dsf.getIndexManager();
	}

	public SourceManager getSourceManager() {
		return dsf.getSourceManager();
	}

	public ILayer createLayer(String sourceName) throws LayerException {
		Source src = ((DataManager) Services.getService(DataManager.class))
				.getDSF().getSourceManager().getSource(sourceName);
		if (src != null) {
			int type = src.getType();
			if ((type & (SourceManager.RASTER | SourceManager.VECTORIAL | SourceManager.WMS)) != 0) {
				try {
					DataSource ds = ((DataManager) Services
							.getService(DataManager.class)).getDSF()
							.getDataSource(sourceName);
					return createLayer(ds);
				} catch (DriverLoadException e) {
					throw new LayerException("Cannot instantiate layer", e);
				} catch (NoSuchTableException e) {
					throw new LayerException("Cannot instantiate layer", e);
				} catch (DataSourceCreationException e) {
					throw new LayerException("Cannot instantiate layer", e);
				}
			} else {
				throw new LayerException("There is no spatial information: "
						+ type);
			}
		} else {
			throw new LayerException("There is no source "
					+ "registered with the name: " + sourceName);
		}
	}
	
/*	private void doTransformation(SpatialDataSourceDecorator sds,
			CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS){
		List<CoordinateOperation> ops = CoordinateOperationFactory
											.createCoordinateOperations((CompoundCRS)sourceCRS, (CompoundCRS)targetCRS);
		final CoordinateOperationSequence cos = new CoordinateOperationSequence(new Identifier(
				ST_Transform.class, "" + " to " + ""), ops);

		ReadOnlyDriver driver = sds.getDataSource().getDriver();
				
		Value[] values =null;
		Geometry geom;
		GeometryTransformer gt = new GeometryTransformer() {
			protected CoordinateSequence transformCoordinates(CoordinateSequence cs, Geometry geom){
				Coordinate[] cc = geom.getCoordinates();
				CoordinateSequence newcs = new CoordinateArraySequence(cc);
				for (int i = 0 ; i < cc.length ; i++) {
					Coordinate c = cc[i];
					try {	                    	
						double[] xyz = cos.transform(new double[]{c.x, c.y, c.z});
						newcs.setOrdinate(i,0,xyz[0]);
						newcs.setOrdinate(i,1,xyz[1]);
						if(xyz.length > 2)
							newcs.setOrdinate(i,2,xyz[2]);
						else
							newcs.setOrdinate(i,2,Double.NaN);
					} catch(IllegalCoordinateException ice) {ice.printStackTrace();}
				}
				return newcs;
			}
		};

		int geomIndex = sds.getSpatialFieldIndex();
		for (int i = 0; i < sds.getRowCount(); i++) {
			values = sds.getRow(i);
			geom = sds.getGeometry(i);
			values[geomIndex] = ValueFactory
			.createValue(gt.transform(geom));
			driver.addValues(values);
		}		
	}*/

	public ILayer createLayer(DataSource ds) throws LayerException {
		int type = ds.getSource().getType();
		if ((type & SourceManager.WMS) == SourceManager.WMS) {
			return new WMSLayer(ds.getName(), ds);
		} else {
			boolean hasSpatialData = true;
			if ((type & SourceManager.VECTORIAL) == SourceManager.VECTORIAL) {
				SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
						ds);
				int sfi;
				try {
					sds.open();					
					sfi = sds.getSpatialFieldIndex();
					//if(crs==null)
						crs = sds.getCRS();						
					//else {
						//doTransformation(sds,sds.getCRS(),crs);						
						//sds.setCRS(crs, "the_geom");
						
						//TODO : do CRS transformation
						
					//}
						
					try {
						sds.close();
					} catch (AlreadyClosedException e) {
						// ignore
						logger.debug("Cannot close", e);
					}
					hasSpatialData = (sfi != -1);
				} catch (DriverException e) {
					throw new LayerException("Cannot check source contents", e);
				}
			}
			if (hasSpatialData) {
				return new Layer(ds.getName(), ds);
			} else {
				throw new LayerException("The source contains no spatial info");
			}
		}
	}

	public ILayer createLayerCollection(String layerName) {
		return new LayerCollection(layerName);
	}

	public ILayer createLayer(String name, File file) throws LayerException {
		DataSourceFactory dsf = ((DataManager) Services
				.getService(DataManager.class)).getDSF();
		dsf.getSourceManager().register(name, file);
		try {
			DataSource dataSource = dsf.getDataSource(name);
			return createLayer(dataSource);
		} catch (DriverLoadException e) {
			throw new LayerException("Cannot find a suitable driver for "
					+ file.getAbsolutePath(), e);
		} catch (NoSuchTableException e) {
			throw new LayerException("bug!", e);
		} catch (DataSourceCreationException e) {
			throw new LayerException("Cannot instantiate layer", e);
		}
	}

	public ILayer createLayer(File file) throws LayerException {
		DataSourceFactory dsf = ((DataManager) Services
				.getService(DataManager.class)).getDSF();
		String name = dsf.getSourceManager().nameAndRegister(file);
		try {
			DataSource dataSource = dsf.getDataSource(name);
			return createLayer(dataSource);
		} catch (DriverLoadException e) {
			throw new LayerException("Cannot find a suitable driver for "
					+ file.getAbsolutePath(), e);
		} catch (NoSuchTableException e) {
			throw new LayerException("bug!", e);
		} catch (DataSourceCreationException e) {
			throw new LayerException("Cannot instantiate layer", e);
		}
	}

}
