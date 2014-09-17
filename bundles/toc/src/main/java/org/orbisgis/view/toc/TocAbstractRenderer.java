/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.toc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JTree;

import org.apache.log4j.Logger;
import org.h2gis.utilities.GeometryTypeCodes;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.view.components.renderers.TreeLaFRenderer;
import org.orbisgis.view.toc.icons.TocIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public abstract class TocAbstractRenderer extends TreeLaFRenderer {
    protected static final I18n I18N = I18nFactory.getI18n(TocAbstractRenderer.class);
    private static final Logger LOGGER = Logger.getLogger(TocAbstractRenderer.class);

    public TocAbstractRenderer(JTree tree) {
        super(tree);
    }

    public static ImageIcon getLayerIcon(ILayer layer) throws SQLException,
            IOException {
        try {
            if (layer.acceptsChilds()) {
                return TocIcon.getIcon("layers");
            } else {
                if (layer.isStream()) {
                    return TocIcon.getIcon("wms_layer");
                } else {
                    try (Connection connection = layer.getDataManager().getDataSource().getConnection()) {
                        // Create a legend for each spatial field
                        int type = SFSUtilities.getGeometryType(connection, TableLocation.parse(layer.getTableReference()), "");
                        if (type >= 0) {
                            switch (type) {
                                case GeometryTypeCodes.GEOMETRY:
                                case GeometryTypeCodes.GEOMCOLLECTION:
                                    return TocIcon.getIcon("layermixe");
                                case GeometryTypeCodes.POINT:
                                case GeometryTypeCodes.MULTIPOINT:
                                    return TocIcon.getIcon("layerpoint");
                                case GeometryTypeCodes.LINESTRING:
                                case GeometryTypeCodes.MULTILINESTRING:
                                    return TocIcon.getIcon("layerline");
                                case GeometryTypeCodes.POLYGON:
                                case GeometryTypeCodes.MULTIPOLYGON:
                                    return TocIcon.getIcon("layerpolygon");
                                default:
                                    throw new RuntimeException(I18N.tr("Unable to find appropriate icon for typeCode {0}", type));
                            }

                        } else {
                            return TocIcon.getIcon("remove");
                            // TODO Raster
                            /*
                            if (layer.getRaster().getType() == ImagePlus.COLOR_RGB) {
                                return TocIcon.getIcon("layerrgb");
                            } else {
                                return TocIcon.getIcon("raster");
                            }
                            */
                        }
                    }
                }
            }
        } catch (Exception ex) {
            // Error while reading datasource, may be a thread race condition or the table does not exists
            LOGGER.trace(I18N.tr("Error while drawing the Toc tree"));
            return TocIcon.getIcon("remove");
        }
    }

}
