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
package org.orbisgis.view.toc.ext;

import org.orbisgis.view.components.actions.DefaultAction;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Action shown only with layer selection.
 * @author Nicolas Fortin
 */
public class LayerAction extends DefaultAction {
    private TocExt toc;
    private boolean singleSelection = false;
    private boolean onRealLayerOnly = false;
    private boolean onLayerWithRowSelection = false;

    /**
     * @param onLayerWithRowSelection If true this action is shown only if one of the selected layer contain a row selection.
     * @return this
     */
    public LayerAction setOnLayerWithRowSelection(boolean onLayerWithRowSelection) {
        this.onLayerWithRowSelection = onLayerWithRowSelection;
        return this;
    }

    /**
     * @param singleSelection If true this action is shown only if the user select one item,
     *                        else the number of selected items does not change anything.
     * @return this
     */
    public LayerAction setSingleSelection(boolean singleSelection) {
        this.singleSelection = singleSelection;
        return this;
    }

    /**
     * @param onRealLayerOnly If true this action is not shown if selected item is a layer collection.
     * @return
     */
    public LayerAction setOnRealLayerOnly(boolean onRealLayerOnly) {
        this.onRealLayerOnly = onRealLayerOnly;
        return this;
    }

    public LayerAction(TocExt toc,String actionId, String actionLabel, String actionToolTip, Icon icon, ActionListener actionListener, KeyStroke keyStroke) {
        super(actionId, actionLabel, actionToolTip, icon, actionListener, keyStroke);
        this.toc = toc;
    }

    @Override
    public boolean isEnabled() {
        return (!onLayerWithRowSelection || toc.hasLayerWithRowSelection()) &&
                (!singleSelection || toc.getTree().getSelectionCount()==1) &&
                (!onRealLayerOnly || !toc.hasLayerGroup()) && toc.isLayerSelection() && super.isEnabled();
    }
}
