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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.h2gis.utilities.TableLocation;
import org.h2gis.utilities.URIUtility;
import org.orbisgis.core.stream.GeoStream;
import org.orbisgis.core.stream.SimpleWMSDriver;
import org.orbisgis.core.stream.WMSStreamSource;
import org.orbisgis.coreapi.api.DataManager;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Style;
import org.h2gis.utilities.SFSUtilities;

public class Layer extends BeanLayer {
    // When dataURI is not specified, this layer use the tableReference instead of external URI
    private static final String JDBC_REFERENCE_SCHEME = "WORKSPACE";

	private String tableReference = "";
    private URI dataURI;
    private DataManager dataManager;
    private Envelope envelope;
    private GeoStream stream;

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
            try {
                Map<String,String> query = URIUtility.getQueryKeyValuePairs(URI.create(dataURI.getSchemeSpecificPart()));
                tableReference = new TableLocation(query.get("catalog"),query.get("schema"),query.get("table")).toString();
            } catch (UnsupportedEncodingException ex) {
                LOGGER.trace(ex.getLocalizedMessage(), ex);
            }
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
            TableLocation table = TableLocation.parse(tableReference);
            try(Connection connection = dataManager.getDataSource().getConnection()) {
                // Look at table remarks if there is a file reference
                DatabaseMetaData meta = connection.getMetaData();
                try(ResultSet tablesRs = meta.getTables(table.getCatalog(),table.getSchema(),table.getTable(),null)) {
                    if(tablesRs.next()) {
                        String remarks = tablesRs.getString("REMARKS");
                        if(remarks!= null && remarks.toLowerCase().startsWith("file:")) {
                            try {
                                // The table is extracted from a file
                                URI fileUri =  URI.create(remarks);
                                if(new File(fileUri).exists()) {
                                    return fileUri;
                                }
                            } catch (Exception ex) {
                                //Ignore, not an URI
                            }
                        }
                    }
                }
                // Extract table location on database
                URI databaseUri = URI.create(meta.getURL());
                String query = String.format("catalog=%s&schema=%s&table=%s",table.getCatalog(),table.getSchema(), table.getTable());
                return URI.create(databaseUri.toString()+"?"+query);
            } catch (SQLException|IllegalArgumentException ex) {
                LOGGER.warn(I18N.tr("Unable to create URI from Layer.Please fix the layer named {0}", getName()));
                return null;
            }
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
        // TODO reset envelope when the Table is updated
        if(envelope == null) {
            try {
                if(isStream()) {
                    return stream.getEnvelope();
                } else {
                    try(Connection connection = dataManager.getDataSource().getConnection()) {
                        envelope = SFSUtilities.getTableEnvelope(connection, TableLocation.parse(tableReference),"");
                    } catch (SQLException ex) {
                        LOGGER.error(I18N.tr("Cannot compute layer envelope"),ex);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(I18N.tr("Cannot compute layer envelope"), ex);
                return null;
            }
        }
		return envelope;
	}

    @Override
	public void close() throws LayerException {

	}

    @Override
	public void open() throws LayerException {
        if(tableReference.isEmpty()) {
            if("http".equalsIgnoreCase(dataURI.getScheme())) {
                SimpleWMSDriver driver = new SimpleWMSDriver();
                try {
                    driver.open(new WMSStreamSource(dataURI));
                } catch (IOException ex) {
                    throw new LayerException(ex);
                }
                stream = driver;
            } else {
                try {
                    tableReference =  dataManager.registerDataSource(dataURI);
                } catch (Exception ex) {
                    LOGGER.warn(I18N.tr("Unable to load the data source uri {0}.", dataURI), ex);
                }
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
        if(getTableReference().isEmpty()) {
            return false;
        }
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            try {
                return !SFSUtilities.getGeometryFields(connection,TableLocation.parse(getTableReference())).isEmpty();
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
                ArrayList<Rule> ret = new ArrayList<>();
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
		return stream != null;
	}

    @Override
    public GeoStream getStream() throws LayerException {
            return stream;
    }
}
