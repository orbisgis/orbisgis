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
package org.orbisgis.view.toc.actions;

import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.tocapi.TocExt;
import org.orbisgis.view.toc.TocTreeNodeStyle;
import org.orbisgis.view.toc.TocTreeSelectionIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action shown on style items
 * @author Nicolas Fortin
 */
public class StyleAction extends DefaultAction {
    protected TocExt toc;
    private boolean onSingleStyleSelection = false;
    private boolean onVectorSourceOnly = true;
    private static final Logger LOGGER = LoggerFactory.getLogger(StyleAction.class);
    /**
     * Show only on selected style
     * @param toc
     * @param actionId
     * @param actionLabel
     * @param actionToolTip
     * @param icon
     * @param actionListener
     * @param keyStroke
     */
    public StyleAction(TocExt toc, String actionId, String actionLabel, String actionToolTip, Icon icon, ActionListener actionListener, KeyStroke keyStroke) {
        super(actionId, actionLabel, actionToolTip, icon, actionListener, keyStroke);
        this.toc = toc;
    }

    /**
     * @param onSingleStyleSelection If true, if there is more than one selected style then this action is not shown
     * @return this
     */
    public StyleAction setOnSingleStyleSelection(boolean onSingleStyleSelection) {
        this.onSingleStyleSelection = onSingleStyleSelection;
        return this;
    }

    /**
     * @param onVectorSourceOnly If one or more non vectorial style layer source is selected then this action is not shown
     * @return this
     */
    public StyleAction setOnVectorSourceOnly(boolean onVectorSourceOnly) {
        this.onVectorSourceOnly = onVectorSourceOnly;
        return this;
    }

    @Override
    public boolean isEnabled() {
        TocTreeSelectionIterable<TocTreeNodeStyle> styleIterator =
                new TocTreeSelectionIterable<>(toc.getTree().getSelectionPaths(),TocTreeNodeStyle.class);
        int styleSelectionCount = 0;
        boolean hasNonVectorSource = false;
        for(TocTreeNodeStyle styleNode : styleIterator) {
            styleSelectionCount++;
            ILayer layer = styleNode.getStyle().getLayer();
            if(onVectorSourceOnly && !hasNonVectorSource) {
                try(Connection connection = layer.getDataManager().getDataSource().getConnection()) {
                    TableLocation tableLocation = TableLocation.parse(layer.getTableReference());
                    List<String> geomFields = SFSUtilities.getGeometryFields(connection, tableLocation);
                    hasNonVectorSource = geomFields.isEmpty();
                } catch (SQLException ex) {
                    LOGGER.error(ex.getLocalizedMessage(), ex);
                }
            }
        }
        return (!onSingleStyleSelection || styleSelectionCount==1) &&
                (!onVectorSourceOnly || !hasNonVectorSource) && styleSelectionCount>=1 && super.isEnabled();
    }
}
