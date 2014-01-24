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

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.orbisgis.coreapi.api.DataManager;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Style;
import org.h2gis.utilities.SFSUtilities;

public class Layer extends BeanLayer {
    // When dataURI is not specified, this layer use the tableReference instead of external URI
    private static final String JDBC_REFERENCE_SCHEME = "WORKSPACE";

	private String tableReference;
    private URI dataURI;
    private DataManager dataManager;

	public Layer(String name, String tableReference,DataManager dataManager) {
		super(name);
		this.tableReference = tableReference;
        this.dataManager = dataManager;
	}

    public Layer(String name, URI dataURI,DataManager dataManager) {
        super(name);
        this.dataURI = dataURI;
        this.dataManager = dataManager;
        if(JDBC_REFERENCE_SCHEME.equalsIgnoreCase(dataURI.getScheme())) {
            String path =  dataURI.getPath(); // ex: /myschema.mytable
            tableReference = path.substring(1);
        } else {
            tableReference = "";
        }
    }

    @Override
    public DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public URI getDataUri() {
        if(dataURI!=null) {
            return dataURI;
        } else {
            return URI.create(JDBC_REFERENCE_SCHEME+":/"+tableReference);
        }
    }

    @Override
    public void setDataUri(URI uri) {
        URI oldUri = this.dataURI;
        this.dataURI = uri;
        propertyChangeSupport.firePropertyChange(PROP_SOURCE_URI, oldUri, uri);
    }

    @Override
	public String getTableReference() {
		return tableReference;
	}

    @Override
	public Envelope getEnvelope() {
		Envelope result = new Envelope();
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            try {
                return SFSUtilities.getTableEnvelope(connection, SFSUtilities.splitCatalogSchemaTableName(tableReference),"");
            } finally {
                connection.close();
            }
        } catch (SQLException ex) {
            LOGGER.error(I18N.tr("Cannot compute layer envelope"),ex);
        }
		return result;
	}

    @Override
	public void close() throws LayerException {

	}

        @Override
	public void open() throws LayerException {
        if(tableReference.isEmpty()) {
            try {
                tableReference =  dataManager.registerDataSource(dataURI);
            } catch (Exception ex) {
                LOGGER.warn(I18N.tr("Unable to load the data source uri {0}.", dataURI), ex);
            }
        } else if(dataURI == null) {
            // Check if the table exists
            try {
                if(!dataManager.isTableExists(tableReference)) {
                    LOGGER.warn(I18N.tr("Specified table '{0}' does not exists, and no source URI is given", tableReference));
                }
            } catch (SQLException ex) {
                LOGGER.warn("Error while fetching table list",ex);
            }
        }
        if (getStyles().isEmpty()) {
            // special case: no style were ever set
            // let's go for a default style
            // add style in the list directly
            // do not fire style change event
            Style defStyle = new Style(this, true);
            styleList.add(defStyle);
            addStyleListener(defStyle);
        }
	}

    @Override
	public boolean isRaster() throws LayerException {
		return false;
	}

    @Override
	public boolean isVectorial() throws LayerException {
        if(getTableReference()==null) {
            throw new LayerException("This layer is not opened");
        }
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            try {
                return !SFSUtilities.getGeometryFields(connection,SFSUtilities.splitCatalogSchemaTableName(getTableReference())).isEmpty();
            } finally {
                connection.close();
            }
        } catch (SQLException ex) {
            throw new LayerException(I18N.tr("Error while fetching source MetaData"));
        }
	}

        @Override
        public boolean isSerializable() {
                return tableReference != null;
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
