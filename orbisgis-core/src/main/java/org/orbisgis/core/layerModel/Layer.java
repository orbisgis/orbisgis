/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
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
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.layerModel;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.core.renderer.legend.RenderException;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.renderer.legend.carto.LegendManager;
import org.orbisgis.core.renderer.legend.carto.UniqueSymbolLegend;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolFactory;
import org.orbisgis.core.layerModel.persistence.LayerType;
import org.orbisgis.core.layerModel.persistence.Legends;
import org.orbisgis.core.layerModel.persistence.SimpleLegend;
import org.orbisgis.errorManager.ErrorManager;

import com.vividsolutions.jts.geom.Envelope;

public class Layer extends GdmsLayer {

	private SpatialDataSourceDecorator dataSource;
	private HashMap<String, LegendDecorator[]> fieldLegend = new HashMap<String, LegendDecorator[]>();
	private RefreshSelectionEditionListener editionListener;
	private int[] selection = new int[0];

	public Layer(String name, DataSource ds,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		this.dataSource = new SpatialDataSourceDecorator(ds);

		editionListener = new RefreshSelectionEditionListener();
	}

	private UniqueSymbolLegend getDefaultVectorialLegend(Type fieldType) {
		GeometryConstraint gc = (GeometryConstraint) fieldType
				.getConstraint(Constraint.GEOMETRY_TYPE);

		final Random r = new Random();
		final Color c = new Color(r.nextInt(256), r.nextInt(256), r
				.nextInt(256));

		UniqueSymbolLegend legend = LegendFactory.createUniqueSymbolLegend();
		Symbol polSym = SymbolFactory.createPolygonSymbol(Color.black, c);
		Symbol pointSym = SymbolFactory.createPointCircleSymbol(Color.black,
				Color.red, 10);
		Symbol lineSym = SymbolFactory.createLineSymbol(Color.black, 2);
		Symbol composite = SymbolFactory.createSymbolComposite(polSym,
				pointSym, lineSym);
		if (gc == null) {
			legend.setSymbol(composite);
		} else {
			switch (gc.getGeometryType()) {
			case GeometryConstraint.POINT:
			case GeometryConstraint.MULTI_POINT:
				legend.setSymbol(pointSym);
				break;
			case GeometryConstraint.LINESTRING:
			case GeometryConstraint.MULTI_LINESTRING:
				legend.setSymbol(lineSym);
				break;
			case GeometryConstraint.POLYGON:
			case GeometryConstraint.MULTI_POLYGON:
				legend.setSymbol(polSym);
				break;
			}
		}

		return legend;
	}

	public SpatialDataSourceDecorator getDataSource() {
		return dataSource;
	}

	public Envelope getEnvelope() {
		Envelope result = new Envelope();

		if (null != dataSource) {
			try {
				result = dataSource.getFullExtent();
			} catch (DriverException e) {
				Services.getErrorManager().error(
						"Cannot get the extent of the layer: "
								+ dataSource.getName(), e);
			}
		}
		return result;
	}

	public void close() throws LayerException {
		super.close();
		try {
			dataSource.removeEditionListener(editionListener);
			dataSource.close();
		} catch (AlreadyClosedException e) {
			throw new LayerException("Bug!", e);
		} catch (DriverException e) {
			throw new LayerException(e);
		}
	}

	public void open() throws LayerException {
		super.open();
		try {
			dataSource.open();
			// Create a legend for each spatial field
			Metadata metadata = dataSource.getMetadata();
			for (int i = 0; i < metadata.getFieldCount(); i++) {
				Type fieldType = metadata.getFieldType(i);
				int fieldTypeCode = fieldType.getTypeCode();
				if (fieldTypeCode == Type.GEOMETRY) {
					UniqueSymbolLegend legend = getDefaultVectorialLegend(fieldType);

					try {
						setLegend(metadata.getFieldName(i), legend);
					} catch (DriverException e) {
						// Should never reach here with UniqueSymbolLegend
					}

				} else if (fieldTypeCode == Type.RASTER) {
					GeoRaster gr = dataSource.getRaster(metadata
							.getFieldName(i), 0);
					RasterLegend rasterLegend;
					rasterLegend = new RasterLegend(gr.getDefaultColorModel(),
							1f);
					setLegend(metadata.getFieldName(i), rasterLegend);
				}
			}

			// Listen modifications to update selection
			dataSource.addEditionListener(editionListener);
		} catch (IOException e) {
			throw new LayerException("Cannot set legend", e);
		} catch (DriverException e) {
			throw new LayerException("Cannot open layer", e);
		}
	}

	/**
	 * Sets the legend used to draw this layer
	 *
	 * @param legends
	 * @throws DriverException
	 *             If there is some problem accessing the contents of the layer
	 */
	public void setLegend(Legend... legends) throws DriverException {
		String defaultFieldName = dataSource.getMetadata().getFieldName(
				dataSource.getSpatialFieldIndex());
		setLegend(defaultFieldName, legends);
	}

	public Legend[] getRenderingLegend() throws DriverException {
		int sfi = dataSource.getSpatialFieldIndex();
		String defaultFieldName = dataSource.getMetadata().getFieldName(sfi);
		LegendDecorator[] legendDecorators = fieldLegend.get(defaultFieldName);
		ArrayList<Legend> ret = new ArrayList<Legend>();
		for (LegendDecorator legendDecorator : legendDecorators) {
			if (legendDecorator.isValid()) {
				ret.add(legendDecorator);
			}
		}
		return ret.toArray(new Legend[0]);
	}

	public Legend[] getVectorLegend() throws DriverException {
		int sfi = dataSource.getSpatialFieldIndex();
		Metadata metadata = dataSource.getMetadata();
		if (metadata.getFieldType(sfi).getTypeCode() == Type.RASTER) {
			throw new UnsupportedOperationException("The "
					+ "field is a raster");
		}
		String defaultFieldName = metadata.getFieldName(sfi);
		return getVectorLegend(defaultFieldName);
	}

	public RasterLegend[] getRasterLegend() throws DriverException {
		int sfi = dataSource.getSpatialFieldIndex();
		Metadata metadata = dataSource.getMetadata();
		if (metadata.getFieldType(sfi).getTypeCode() == Type.GEOMETRY) {
			throw new UnsupportedOperationException("The "
					+ "field is a vector");
		}
		String defaultFieldName = metadata.getFieldName(sfi);
		return getRasterLegend(defaultFieldName);
	}

	public Legend[] getVectorLegend(String fieldName) throws DriverException {
		int sfi = getFieldIndexForLegend(fieldName);
		validateType(sfi, Type.GEOMETRY, "vector");
		LegendDecorator[] legends = fieldLegend.get(fieldName);
		Legend[] ret = new Legend[legends.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = legends[i].getLegend();
		}

		return ret;
	}

	private void validateType(int sfi, int fieldType, String type)
			throws DriverException {
		Metadata metadata = dataSource.getMetadata();
		if (metadata.getFieldType(sfi).getTypeCode() != fieldType) {
			throw new IllegalArgumentException("The " + "field is not " + type);
		}
	}

	private int getFieldIndexForLegend(String fieldName) throws DriverException {
		int sfi = dataSource.getFieldIndexByName(fieldName);
		if (sfi == -1) {
			throw new IllegalArgumentException("Field not found: " + fieldName);
		}
		return sfi;
	}

	public RasterLegend[] getRasterLegend(String fieldName)
			throws DriverException {
		int sfi = getFieldIndexForLegend(fieldName);
		validateType(sfi, Type.RASTER, "raster");
		LegendDecorator[] legends = fieldLegend.get(fieldName);
		RasterLegend[] ret = new RasterLegend[legends.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (RasterLegend) legends[i].getLegend();
		}

		return ret;
	}

	public void setLegend(String fieldName, Legend... legends)
			throws DriverException {
		if (dataSource.getFieldIndexByName(fieldName) == -1) {
			throw new IllegalArgumentException("Field not found: " + fieldName);
		} else {
			// Remove previous decorator listeners
			LegendDecorator[] oldDecorators = fieldLegend.get(fieldName);
			if (oldDecorators != null) {
				for (LegendDecorator legendDecorator : oldDecorators) {
					dataSource.removeEditionListener(legendDecorator);
				}
			}
			LegendDecorator[] decorated = decorate(fieldName, legends);
			for (LegendDecorator legendDecorator : decorated) {
				dataSource.addEditionListener(legendDecorator);
			}
			fieldLegend.put(fieldName, decorated);
			fireStyleChanged();
		}
	}

	private LegendDecorator[] decorate(String fieldName, Legend... legends) {
		LegendDecorator[] decorated = new LegendDecorator[legends.length];
		for (int i = 0; i < decorated.length; i++) {
			LegendDecorator decorator = new LegendDecorator(legends[i]);
			try {
				decorator.initialize(dataSource);
			} catch (RenderException e) {
				Services.getService(ErrorManager.class).warning(
						"Cannot initialize legend", e);
			}
			decorated[i] = decorator;
		}

		return decorated;
	}

	public boolean isRaster() throws DriverException {
		return dataSource.isDefaultRaster();
	}

	public boolean isVectorial() throws DriverException {
		return dataSource.isDefaultVectorial();
	}

	public GeoRaster getRaster() throws DriverException {
		if (!isRaster()) {
			throw new UnsupportedOperationException(
					"This layer is not a raster layer");
		}
		return getDataSource().getRaster(0);
	}

	private class RefreshSelectionEditionListener implements EditionListener {

		public void multipleModification(MultipleEditionEvent e) {
			EditionEvent[] events = e.getEvents();
			int[] selection = getSelection();
			for (int i = 0; i < events.length; i++) {
				int[] newSel = getNewSelection(events[i].getRowIndex(),
						selection);
				selection = newSel;
			}
			setSelection(selection);
		}

		public void singleModification(EditionEvent e) {
			if (e.getType() == EditionEvent.DELETE) {
				int[] selection = getSelection();
				int[] newSel = getNewSelection(e.getRowIndex(), selection);
				setSelection(newSel);
			} else if (e.getType() == EditionEvent.RESYNC) {
				setSelection(new int[0]);
			}

		}

		private int[] getNewSelection(long rowIndex, int[] selection) {
			int[] newSelection = new int[selection.length];
			int newSelectionIndex = 0;
			for (int i = 0; i < selection.length; i++) {
				if (selection[i] != rowIndex) {
					newSelection[newSelectionIndex] = selection[i];
					newSelectionIndex++;
				}
			}

			if (newSelectionIndex < selection.length) {
				selection = new int[newSelectionIndex];
				System.arraycopy(newSelection, 0, selection, 0,
						newSelectionIndex);
			}
			return selection;
		}
	}

	public LayerType saveLayer() {
		LayerType ret = new LayerType();
		ret.setName(getName());
		ret.setSourceName(getMainName());
		ret.setVisible(isVisible());

		Iterator<String> it = fieldLegend.keySet().iterator();
		while (it.hasNext()) {
			String fieldName = it.next();
			Legends legends = new Legends();
			legends.setFieldName(fieldName);
			LegendDecorator[] legendDecorators = fieldLegend.get(fieldName);
			for (int i = 0; i < legendDecorators.length; i++) {
				LegendDecorator legendDecorator = legendDecorators[i];
				Legend legend = legendDecorator.getLegend();
				String legendTypeId = legend.getLegendTypeId();
				Object legendJAXBObject = legend.getJAXBObject();
				SimpleLegend simpleLegend = new SimpleLegend();
				simpleLegend.setLegendId(legendTypeId);
				simpleLegend.setAny(legendJAXBObject);
				legends.getSimpleLegend().add(simpleLegend);
			}
			ret.getLegends().add(legends);
		}

		return ret;
	}

	public void restoreLayer(LayerType lyr) throws LayerException {
		LayerType layer = (LayerType) lyr;
		this.setName(layer.getName());
		this.setVisible(layer.isVisible());

		List<Legends> legendsCollection = layer.getLegends();
		for (Legends legends : legendsCollection) {
			String fieldName = legends.getFieldName();
			List<SimpleLegend> legendCollection = legends.getSimpleLegend();
			ArrayList<Legend> fieldLegends = new ArrayList<Legend>();
			LegendManager lm = (LegendManager) Services
					.getService(LegendManager.class);
			for (SimpleLegend simpleLegend : legendCollection) {
				String legendId = simpleLegend.getLegendId();

				Legend legend = lm.getNewLegend(legendId);
				if (legend == null) {
					throw new LayerException("Unsupported legend: " + legendId);
				}
				try {
					legend.setJAXBObject(simpleLegend.getAny());
				} catch (Exception e) {
					Services.getErrorManager().error(
							"Cannot recover legend. Legend is lost", e);
				}
				fieldLegends.add(legend);
			}
			try {
				setLegend(fieldName, fieldLegends.toArray(new Legend[0]));
			} catch (DriverException e) {
				throw new LayerException("Cannot restore legends", e);
			}
		}
	}

	private void fireSelectionChanged() {
		for (LayerListener listener : listeners) {
			listener.selectionChanged(new SelectionEvent());
		}
	}

	@Override
	public int[] getSelection() {
		return selection;
	}

	@Override
	public void setSelection(int[] newSelection) {
		this.selection = newSelection;
		fireSelectionChanged();
	}

	@Override
	public boolean isWMS() {
		return false;
	}

	@Override
	public WMSConnection getWMSConnection()
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException("This is not a WMS layer");
	}
}