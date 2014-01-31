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
package org.orbisgis.view.toc.actions.cui.legend.wizard;

import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.sif.SIFWizard;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIChooser;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.beans.EventHandler;

/**
 * This class builds a SIFWizard that can be used to configure a new
 * Style for a ILayer.
 * @author Alexis Guéganno, Erwan Bocher
 */
public class LegendWizard {

    private ThematicMapWizard wp;
    private LegendUIChooser luc;
    private SIFWizard wiz;

    /**
     * Builds a SIFWizard instance that one can use to create a new
     * legend associated to {@code layer} and {@code mt}.
     * @param layer The parent layer
     * @param mt The current MapTransform
     * @return A SIFWizard that can be displayed with UIFactory.
     */
    public SIFWizard getSIFWizard(ILayer layer, MapTransform mt){
        wp = new ThematicMapWizard(layer, mt);
        luc = new LegendUIChooser(wp);
        UIPanel[] panes = new UIPanel[]{luc, wp};
        wiz = UIFactory.getWizard(panes);
        wiz.getBtnFinish().setEnabled(false);
        JButton btnNext = wiz.getBtnNext();
        ActionListener al = EventHandler.create(ActionListener.class,this, "onClickNext");
        btnNext.addActionListener(al);
        ActionListener pre = EventHandler.create(ActionListener.class,this, "onClickPrevious");
        wiz.getBtnPrevious().addActionListener(pre);
        wiz.setTitle(wp.getTitle());
        return wiz;
    }

    /**
     * Called by EventHandler when the user click on the Next button
     * of the generated SIFWizard.
     */
    public void onClickNext(){
        ILegendPanel selectedPanel = luc.getSelectedPanel();
        wp.setInnerLegend(selectedPanel);
        wiz.setTitle(selectedPanel.getLegend().getLegendTypeName());
        wiz.pack();
    }

    /**
     * Called by EventHandler when the user click on the Previous button
     * of the generated SIFWizard.
     */
    public void onClickPrevious(){
        wp.setInnerLegend(null);
        wiz.setTitle(wp.getTitle());
        wiz.pack();
    }

    /**
     * Gets a Style with a simple Rule that contains the configured Symbolizer.
     * @return The configured Style.
     */
    public Style getStyle(){
        ILegendPanel innerLegend = wp.getInnerLegend();
        Legend legend = innerLegend.getLegend();
        ILayer l = wp.getLayer();
        Style st = new Style(l, false);
        Rule r = new Rule();
        r.getCompositeSymbolizer().addSymbolizer(legend.getSymbolizer());
        st.addRule(r);
        st.setName(wp.getName());
        return st;
    }

    /**
     * Gets the Symbolizer contained in the configured Legend
     * @return The configured Symbolizer.
     */
    public Symbolizer getSymbolizer(){
        return wp.getInnerLegend().getLegend().getSymbolizer();
    }

}
