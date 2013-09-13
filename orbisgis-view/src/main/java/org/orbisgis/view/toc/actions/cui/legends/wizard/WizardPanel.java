package org.orbisgis.view.toc.actions.cui.legends.wizard;

import org.apache.log4j.Logger;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.JPanel;
import java.awt.Component;
import java.net.URL;

/**
 * This UIPanel intends to host a ILegendPanel instance that will
 * be added to a layer
 * @author Alexis Guéganno
 */
public class WizardPanel implements UIPanel, LegendContext {

    private static final Logger LOGGER = Logger.getLogger(WizardPanel.class);
    private static final I18n I18N = I18nFactory.getI18n(WizardPanel.class);
    private int geometryType = SimpleGeometryType.ALL;
    private ILegendPanel inner;
    private JPanel jp;
    private ILayer layer;
    private MapTransform mt;

    /**
     * Builds the WizardPanel. The objects it will create will be linked to {@code l}.
     * @param l The parent layer.
     * @param m Needed for LegendContext implementation.
     */
    public WizardPanel(ILayer l, MapTransform m){
        layer = l;
        try {
            Type type = layer.getDataSource().getMetadata().getFieldType(
                    layer.getDataSource().getSpatialFieldIndex());
            this.geometryType = (type == null)
                    ? SimpleGeometryType.ALL
                    : SimpleGeometryType.getSimpleType(type);
            mt = m;

        } catch (DriverException e) {
            LOGGER.error("Error while reading the data source");
        }

    }

    @Override
    public URL getIconURL() {
        return UIFactory.getDefaultIcon();
    }

    @Override
    public String getTitle() {
        return I18N.tr("Wizard");
    }

    @Override
    public String validateInput() {
        return null;
    }

    @Override
    public Component getComponent() {
        if(jp == null){
            jp = new JPanel();
            if(inner != null){
                jp.add(inner.getComponent());
            }
        }
        return jp;
    }

    /**
     * Replace the inner legend with the given one. If the input is null,
     * the child component is simply removed.
     * @param ilp The new ILegendPanel.
     */
    public void setInnerLegend(ILegendPanel ilp){
        if(jp==null){
            jp = new JPanel();
        }
        if(inner != null){
            Component comp = inner.getComponent();
            if(jp.isAncestorOf(comp)){
                jp.remove(comp);
            }
        }
        inner = ilp;
        if(ilp !=null){
            jp.add(inner.getComponent());
        }
    }

    /**
     *Retrieve the inner ILegendPanel.
     */
    public ILegendPanel getInnerLegend(){
        return inner;
    }

    @Override
    public int getGeometryType() {
        return geometryType;
    }

    @Override
    public boolean isPoint() {
        return (geometryType & SimpleGeometryType.POINT) > 0;
    }

    @Override
    public boolean isLine() {
        return (geometryType & SimpleGeometryType.LINE) > 0;
    }

    @Override
    public boolean isPolygon() {
        return (geometryType & SimpleGeometryType.POLYGON) > 0;
    }

    @Override
    public ILayer getLayer() {
        return layer;
    }

    @Override
    public MapTransform getCurrentMapTransform() {
        return mt;
    }
}
