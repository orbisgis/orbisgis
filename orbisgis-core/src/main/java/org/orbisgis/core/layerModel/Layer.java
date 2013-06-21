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
package org.orbisgis.core.layerModel;

import com.vividsolutions.jts.geom.Envelope;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.grap.model.GeoRaster;
import org.orbisgis.core.Services;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.sputilities.SFSUtilities;

import javax.sql.DataSource;

public class Layer extends BeanLayer {        
	private String dataSource;
	private RefreshSelectionEditionListener editionListener;

	public Layer(String name, String tableRef) {
		super(name);
		this.dataSource = tableRef;
		editionListener = new RefreshSelectionEditionListener();
	}

    @Override
	public String getDataSource() {
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
                        if (dataSource.isEditable()) {                                
                                try {
                                        dataSource.removeEditionListener(editionListener);
                                } catch(UnsupportedOperationException ex) {
                                        //Ignore
                                }
                        }
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
                        if (getStyles().isEmpty()) {
                                // special case: no style were ever set
                                // let's go for a default style
                                // add style in the list directly
                                // do not fire style change event
                                Style defStyle = new Style(this, true);
                                styleList.add(defStyle);
                                addStyleListener(defStyle);
                        }
			// Listen modifications to update selection
                        if (dataSource.isEditable()) {
                                try {
                                        dataSource.addEditionListener(editionListener);
                                } catch (UnsupportedOperationException ex) {
                                        LOGGER.warn(I18N.tr("The layer cannot listen to source modifications"),ex);
                                }
                        }
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

	private int getFieldIndexForLegend(String fieldName) throws LayerException {
		int sfi = dataSource.getFieldIndexByName(fieldName);
		if (sfi == -1) {
			throw new IllegalArgumentException(I18N.tr("The field {0} is not found",fieldName));
		}
		return sfi;
	}

    @Override
	public boolean isRaster() throws LayerException {
		return false;
	}

    @Override
	public boolean isVectorial() throws LayerException {
        DataSource dataSource = Services.getService(DataSource.class);
        if(dataSource==null || getDataSource()==null) {
            throw new LayerException("This layer is not opened");
        }
        try {
            Connection connection = dataSource.getConnection();
            try {
                return !SFSUtilities.getGeometryFields(connection,SFSUtilities.splitCatalogSchemaTableName(getDataSource())).isEmpty();
            } finally {
                connection.close();
            }
        } catch (SQLException ex) {
            throw new LayerException(I18N.tr("Error while fetching source MetaData"));
        }
	}

        @Override
        public boolean isSerializable() {
                return dataSource != null;
        }


    @Override
	public GeoRaster getRaster() throws LayerException {
		if (!isRaster()) {
			throw new UnsupportedOperationException(
					I18N.tr("This layer is not a raster"));
		}
		return null;
	}

	@Override
	public List<Rule> getRenderingRule() throws LayerException {
                List<Style> styles = getStyles();
                ArrayList<Rule> ret = new ArrayList<Rule>();
                for(Style s : styles){
                        if(s!=null){
                                ret.addAll(s.getRules());
                        }
                }
		return ret;
	}

	private void fireSelectionChanged() {
		for (LayerListener listener : listeners) {
			listener.selectionChanged(new SelectionEvent(this));
		}
	}

	@Override
	public void setSelection(Set<Integer> newSelection) {
		super.setSelection(newSelection);
		fireSelectionChanged();
	}

	@Override
	public boolean isStream() throws LayerException {
		return false;
	}
}
