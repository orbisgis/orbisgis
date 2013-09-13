package org.orbisgis.view.toc.actions.cui.legends.wizard;

import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Style;
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
 * @author Alexis Guéganno
 */
public class LegendWizard {

    private WizardPanel wp;
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
        wp = new WizardPanel(layer, mt);
        luc = new LegendUIChooser(wp);
        UIPanel[] panes = new UIPanel[]{luc, wp};
        wiz = UIFactory.getWizard(panes);
        JButton btnNext = wiz.getBtnNext();
        ActionListener al = EventHandler.create(ActionListener.class,this, "onClickNext");
        btnNext.addActionListener(al);
        ActionListener pre = EventHandler.create(ActionListener.class,this, "onClickPrevious");
        wiz.getBtnPrevious().addActionListener(pre);
        return wiz;
    }

    /**
     * Called by EventHandler when the user click on the Next button
     * of the generated SIFWizard.
     */
    public void onClickNext(){
        ILegendPanel selectedPanel = luc.getSelectedPanel();
        wp.setInnerLegend(selectedPanel);
        wiz.pack();
    }

    /**
     * Called by EventHandler when the user click on the Previous button
     * of the generated SIFWizard.
     */
    public void onClickPrevious(){
        wp.setInnerLegend(null);
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

}
