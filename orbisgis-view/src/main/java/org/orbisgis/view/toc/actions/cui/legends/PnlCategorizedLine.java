package org.orbisgis.view.toc.actions.cui.legends;

import org.apache.log4j.Logger;
import org.orbisgis.core.renderer.se.CompositeSymbolizer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.categorize.AbstractCategorizedLegend;
import org.orbisgis.legend.thematic.categorize.CategorizedLine;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.model.KeyEditorCategorizedLine;
import org.orbisgis.view.toc.actions.cui.legends.model.ParametersEditorCategorizedLine;
import org.orbisgis.view.toc.actions.cui.legends.model.TableModelCatLine;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.util.Map;
import java.util.Set;

/**
 * "Interval classification - Line" UI.
 *
 * @author Alexis Guéganno
 */
public class PnlCategorizedLine extends PnlAbstractCategorized<LineParameters>{
    public static final Logger LOGGER = Logger.getLogger(PnlCategorizedLine.class);
    private static final I18n I18N = I18nFactory.getI18n(PnlCategorizedLine.class);

    /**
     * This methods is called by EventHandler when the user clicks on the fall back's preview. It opens an UI that lets
     * the user edit the parameters of the fall back configuration and that apply it if the user clicks OK.
     * @param me The MouseEvent that caused the call to this method.
     */
    public void onEditFallback(MouseEvent me){
        getLegend().setFallbackParameters(editCanvas(fallbackPreview));
    }

    /**
     * Builds a SIF dialog used to edit the given LineParameters.
     * @param cse The canvas we want to edit
     * @return The LineParameters that must be used at the end of the edition.
     */
    private LineParameters editCanvas(CanvasSE cse){
        LineParameters lps = getLegend().getFallbackParameters();
        UniqueSymbolLine usl = new UniqueSymbolLine(lps);
        usl.setStrokeUom(getLegend().getStrokeUom());
        PnlUniqueLineSE pls = new PnlUniqueLineSE(false);
        pls.setLegend(usl);
        if(UIFactory.showDialog(new UIPanel[]{pls}, true, true)){
            usl = (UniqueSymbolLine) pls.getLegend();
            LineParameters nlp = usl.getLineParameters();
            cse.setSymbol(usl.getSymbolizer());
            return nlp;
        } else {
            return lps;
        }
    }

    @Override
    public void initPreview() {
        System.out.println("    Called from initPreview CL");
        fallbackPreview = new CanvasSE(getFallbackSymbolizer());
        MouseListener l = EventHandler.create(MouseListener.class, this, "onEditFallback", "", "mouseClicked");
        fallbackPreview.addMouseListener(l);
    }

    @Override
    public LineParameters getColouredParameters(LineParameters lp, Color newCol){
        return new LineParameters(newCol, lp.getLineOpacity(), lp.getLineWidth(), lp.getLineDash());
    }

    @Override
    public CategorizedLine getEmptyAnalysis() {
        return new CategorizedLine();
    }

    @Override
    public AbstractTableModel getTableModel() {
        return new TableModelCatLine((AbstractCategorizedLegend<LineParameters>)getLegend());
    }

    @Override
    public TableCellEditor getPreviewCellEditor() {
        return new ParametersEditorCategorizedLine();
    }

    @Override
    public TableCellEditor getKeyCellEditor() {
        return new KeyEditorCategorizedLine();
    }

    @Override
    public void setLegend(Legend legend) {
        if (legend instanceof CategorizedLine) {
            if(getLegend() != null){
                Rule rule = getLegend().getSymbolizer().getRule();
                if(rule != null){
                    CompositeSymbolizer compositeSymbolizer = rule.getCompositeSymbolizer();
                    int i = compositeSymbolizer.getSymbolizerList().indexOf(this.getLegend().getSymbolizer());
                    compositeSymbolizer.setSymbolizer(i, legend.getSymbolizer());
                }
            }
            setLegendImpl((CategorizedLine)legend);
            this.initializeLegendFields();
        } else {
            throw new IllegalArgumentException(I18N.tr("You must use recognized RecodedLine instances in"
                    + "this panel."));
        }
    }

    @Override
    public void setGeometryType(int type) {
    }

    @Override
    public boolean acceptsGeometryType(int geometryType) {
        return geometryType == SimpleGeometryType.LINE ||
                geometryType == SimpleGeometryType.POLYGON||
                geometryType == SimpleGeometryType.ALL;
    }

    @Override
    public Legend copyLegend() {
        CategorizedLine cl = (CategorizedLine) getLegend();
        Set<Map.Entry<Double,LineParameters>> entries = cl.entrySet();
        CategorizedLine ret = new CategorizedLine();
        for(Map.Entry<Double,LineParameters> en : entries){
            ret.put(en.getKey(),en.getValue());
        }
        ret.setStrokeUom(cl.getStrokeUom());
        ret.setFallbackParameters(cl.getFallbackParameters());
        ret.setLookupFieldName(cl.getLookupFieldName());
        return ret;
    }

    @Override
    public String validateInput() {
        return "";
    }
}
