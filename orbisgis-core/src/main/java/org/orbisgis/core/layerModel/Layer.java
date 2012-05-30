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
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.layerModel;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.Color;
import java.io.IOException;
import java.util.*;
import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryTypeConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.layerModel.persistence.LayerType;
import org.orbisgis.core.layerModel.persistence.Legends;
import org.orbisgis.core.layerModel.persistence.SimpleLegend;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.core.renderer.legend.RenderException;
import org.orbisgis.core.renderer.legend.WMSLegend;
import org.orbisgis.core.renderer.legend.carto.LegendFactory;
import org.orbisgis.core.renderer.legend.carto.LegendManager;
import org.orbisgis.core.renderer.legend.carto.UniqueSymbolLegend;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.renderer.symbol.SymbolFactory;
import org.orbisgis.utils.I18N;

public class Layer extends GdmsLayer {

	private DataSource dataSource;
	private HashMap<String, LegendDecorator[]> fieldLegend = new HashMap<String, LegendDecorator[]>();
	private RefreshSelectionEditionListener editionListener;
	private int[] selection = new int[0];

	public Layer(String name, DataSource ds) {
		super(name);
		this.dataSource = ds;
		editionListener = new RefreshSelectionEditionListener();
	}

	private UniqueSymbolLegend getDefaultVectorialLegend(Type fieldType) {
		GeometryTypeConstraint gc = (GeometryTypeConstraint) fieldType
				.getConstraint(Constraint.GEOMETRY_TYPE);

		final Random r = new Random();
		final Color cFill = new Color(r.nextInt(256), r.nextInt(256), r
				.nextInt(256), 255 - 40);
		final Color cOutline = cFill.darker();

		UniqueSymbolLegend legend = LegendFactory.createUniqueSymbolLegend();
		Symbol polSym = SymbolFactory.createPolygonSymbol(cOutline, cFill);
		Symbol pointSym = SymbolFactory.createPointSquareSymbol(Color.black,
				Color.red, 5);
		Symbol lineSym = SymbolFactory.createLineSymbol(cOutline, 1);
		Symbol composite = SymbolFactory.createSymbolComposite(polSym,
				pointSym, lineSym);
		if (gc == null) {
			legend.setSymbol(composite);
		} else {
			switch (gc.getGeometryType()) {
			case GeometryTypeConstraint.POINT:
			case GeometryTypeConstraint.MULTI_POINT:
				legend.setSymbol(pointSym);
				break;
			case GeometryTypeConstraint.LINESTRING:
			case GeometryTypeConstraint.MULTI_LINESTRING:
				legend.setSymbol(lineSym);
				break;
			case GeometryTypeConstraint.POLYGON:
			case GeometryTypeConstraint.MULTI_POLYGON:
				legend.setSymbol(polSym);
				break;
			case GeometryTypeConstraint.GEOMETRY_COLLECTION:
				legend.setSymbol(composite);
				break;
			}
		}

		return legend;
	}

    @Override
	public DataSource getDataSource() {
		return dataSource;
	}

    @Override
	public Envelope getEnvelope() {
		Envelope result = new Envelope();

		if (null != dataSource) {
			try {
				result = dataSource.getFullExtent();
			} catch (DriverException e) {
				Services.getErrorManager().error(
						I18N.getString("org.orbisgis.layerModel.layer.cannotGetTheExtentOfLayer")
								+ dataSource.getName(), e);
			}
		}
		return result;
	}

    @Override
	public void close() throws LayerException {
		super.close();
		try {
			dataSource.removeEditionListener(editionListener);
			dataSource.close();
		} catch (AlreadyClosedException e) {
			throw new LayerException(I18N.getString("org.orbisgis.layerModel.layer.bug"), e);
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
				if ((fieldTypeCode & Type.GEOMETRY) !=0) {
					UniqueSymbolLegend legend = getDefaultVectorialLegend(fieldType);

					try {
						setLegend(metadata.getFieldName(i), legend);
					} catch (DriverException e) {
						// Should never reach here with UniqueSymbolLegend
					}

				} else if (fieldTypeCode == Type.RASTER) {
					GeoRaster gr = dataSource.getRaster(0);
					RasterLegend rasterLegend;
					rasterLegend = new RasterLegend(gr.getDefaultColorModel(),
							1f);
					setLegend(metadata.getFieldName(i), rasterLegend);
				}
			}

			// Listen modifications to update selection
			dataSource.addEditionListener(editionListener);
		} catch (IOException e) {
			throw new LayerException(I18N.getString("org.orbisgis.layerModel.layer.cannotSetLegend"), e);
		} catch (DriverException e) {
			throw new LayerException(I18N.getString("org.orbisgis.layerModel.layer.cannotOpenLayer"), e);
		}
	}

	private void validateType(int sfi, int fieldType, String type)
			throws DriverException {
		Metadata metadata = dataSource.getMetadata();
		if ((metadata.getFieldType(sfi).getTypeCode() & fieldType) ==0) {
			throw new IllegalArgumentException(I18N.getString("org.orbisgis.layerModel.layer.the") + I18N.getString("org.orbisgis.layerModel.layer.fieldIsNot") + type);
		}
	}

	private int getFieldIndexForLegend(String fieldName) throws DriverException {
		int sfi = dataSource.getFieldIndexByName(fieldName);
		if (sfi == -1) {
			throw new IllegalArgumentException(I18N.getString("org.orbisgis.layerModel.layer.fieldIsNotFound") + fieldName);
		}
		return sfi;
	}

	public RasterLegend[] getRasterLegend(String fieldName)
			throws DriverException {
		int sfi = getFieldIndexForLegend(fieldName);
		validateType(sfi, Type.RASTER, I18N.getString("org.orbisgis.layerModel.layer.raster"));
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
			throw new IllegalArgumentException(I18N.getString("org.orbisgis.layerModel.layer.fieldIsNotFound") + fieldName);
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
						I18N.getString("org.orbisgis.layerModel.layer.cannotInitializeLegend"), e);
			}
			decorated[i] = decorator;
		}

		return decorated;
	}

	public boolean isRaster() throws DriverException {
		return dataSource.isRaster();
	}

	public boolean isVectorial() throws DriverException {
		return dataSource.isVectorial();
	}

	public GeoRaster getRaster() throws DriverException {
		if (!isRaster()) {
			throw new UnsupportedOperationException(
					I18N.getString("org.orbisgis.layerModel.layer.isNotARasterLayer"));
		}
		return getDataSource().getRaster(0);
	}

	@Override
	public List<Rule> getRenderingRule() throws DriverException {
                List<Style> styles = getStyles();
                ArrayList<Rule> ret = new ArrayList<Rule>();
                for(Style s : styles){
                        if(s!=null){
                                ret.addAll(s.getRules());
                        }
                }
		return ret;
	}

	private class RefreshSelectionEditionListener implements EditionListener {

        @Override
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

        @Override
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

    @Override
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

    @Override
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
					throw new LayerException(I18N.getString("org.orbisgis.layerModel.layer.unsupportedLegend") + legendId);
				}
				try {
					legend.setJAXBObject(simpleLegend.getAny());
				} catch (Exception e) {
					Services.getErrorManager().error(
							I18N.getString("org.orbisgis.layerModel.layer.cannotRecoverLegendLost"), e);
				}
				fieldLegends.add(legend);
			}
			try {
				setLegend(fieldName, fieldLegends
						.toArray(new Legend[fieldLegends.size()]));
			} catch (DriverException e) {
				throw new LayerException(I18N.getString("org.orbisgis.layerModel.layer.cannotRestoreLegends"), e);
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
		throw new UnsupportedOperationException(I18N.getString("org.orbisgis.layerModel.layer.notAWmsLayer"));
	}
        
}