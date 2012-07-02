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
import java.util.ArrayList;
import java.util.List;
import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.gdms.data.schema.Metadata;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Style;

public class Layer extends BeanLayer {        
	private DataSource dataSource;
	private RefreshSelectionEditionListener editionListener;
	private int[] selection = new int[0];

	public Layer(String name, DataSource ds) {
		super(name);
		this.dataSource = ds;
		editionListener = new RefreshSelectionEditionListener();
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
				LOGGER.error(
						I18N.tr("Cannot get the extent of the layer {0}",dataSource.getName())
								 , e);
			}
		}
		return result;
	}

    @Override
	public void close() throws LayerException {
		try {
			dataSource.removeEditionListener(editionListener);
			dataSource.close();
		} catch (AlreadyClosedException e) {
			throw new LayerException(I18N.tr("Cannot close the data source"), e);
		} catch (DriverException e) {
			throw new LayerException(I18N.tr("Cannot close the data source"),e);
		}
	}

        @Override
	public void open() throws LayerException {
		try {
			dataSource.open();
                        initStyles();
                        Style s = new Style(this, true);
                        getStyles().add(s);

			// Listen modifications to update selection
			dataSource.addEditionListener(editionListener);
		} catch (DriverException e) {
			throw new LayerException(I18N.tr("Cannot open the layer"), e);
		}
	}

	private void validateType(int sfi, int fieldType, String type)
			throws DriverException {
		Metadata metadata = dataSource.getMetadata();
		if ((metadata.getFieldType(sfi).getTypeCode() & fieldType) ==0) {
			throw new IllegalArgumentException(I18N.tr("The field is not {0}",type));
		}
	}

	private int getFieldIndexForLegend(String fieldName) throws DriverException {
		int sfi = dataSource.getFieldIndexByName(fieldName);
		if (sfi == -1) {
			throw new IllegalArgumentException(I18N.tr("The field {0} is not found",fieldName));
		}
		return sfi;
	}

    @Override
	public boolean isRaster() throws DriverException {
		return dataSource.isRaster();
	}

    @Override
	public boolean isVectorial() throws DriverException {
		return dataSource.isVectorial();
	}

    @Override
	public GeoRaster getRaster() throws DriverException {
		if (!isRaster()) {
			throw new UnsupportedOperationException(
					I18N.tr("This layer is not a raster"));
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
		throw new UnsupportedOperationException(I18N.tr("This is not a WMS layer"));
	}
        
}