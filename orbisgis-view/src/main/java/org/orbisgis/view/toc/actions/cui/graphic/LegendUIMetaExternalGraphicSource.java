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
package org.orbisgis.view.toc.actions.cui.graphic;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import org.orbisgis.core.renderer.se.common.VariableOnlineResource;
import org.orbisgis.core.renderer.se.graphic.ExternalGraphic;
import org.orbisgis.core.renderer.se.graphic.ExternalGraphicSource;
import org.orbisgis.view.toc.actions.cui.LegendUIAbstractMetaPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 *
 * @author Maxence Laurent
 */
public class LegendUIMetaExternalGraphicSource extends LegendUIAbstractMetaPanel {

    private LegendUIComponent comp;
    private ExternalGraphic extG;
    private final Class[] classes = {VariableOnlineResource.class};

    public LegendUIMetaExternalGraphicSource(LegendUIController ctrl, LegendUIComponent parent, ExternalGraphic ext) {
        super(null, ctrl, parent, 0, false);
        this.extG = ext;

        comp = null;
        ExternalGraphicSource source = extG.getSource();
        if (source instanceof VariableOnlineResource){
            comp = getCompForClass(VariableOnlineResource.class);
        }
    }

    @Override
    protected final LegendUIComponent getCompForClass(Class newClass) {
        if (newClass == VariableOnlineResource.class) {
            return new LegendUIVariableOnlineResourcePanel(controller, this, (VariableOnlineResource) extG.getSource());
        }
        return null;
    }

    @Override
    public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette");
    }

    @Override
    public void init() {
        init(classes, comp);
    }

    @Override
    protected void switchTo(LegendUIComponent newActiveComp) {
        LegendUIExternalSourceComponent src = (LegendUIExternalSourceComponent) newActiveComp;
        try {
            extG.setSource(src.getSource());
        } catch (IOException ex) {
            Logger.getLogger(LegendUIMetaExternalGraphicSource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Class getEditedClass() {
        if (extG.getSource() instanceof VariableOnlineResource){
            return VariableOnlineResource.class;
        } else {
            return null;
        }
    }
}
