package org.orbisgis.core.renderer.se.label;


import java.awt.image.BufferedImage;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;

public class StyledLabel implements SymbolizerNode {

    public StyledLabel(){
        labelText = new StringLiteral("Label");
        setStroke(new PenStroke());
        setFill(new SolidFill());
    }


    @Override
    public Uom getUom() {
        return parent.getUom();
    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        parent = node;
    }

    public Fill getFill() {
        return fill;
    }

    public void setFill(Fill fill) {
        this.fill = fill;
        fill.setParent(this);
    }

    public Halo getHalo() {
        return halo;
    }

    public void setHalo(Halo halo) {
        this.halo = halo;
        halo.setParent(this);
    }

    public StringParameter getLabelText() {
        return labelText;
    }

    public void setLabelText(StringParameter labelText) {
        this.labelText = labelText;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
        stroke.setParent(this);
    }

    // TODO implements
    public BufferedImage getImage(DataSource ds, int fid){
        // HALO, FILL, STROKE
        return null;
    }


    private SymbolizerNode parent;
    private StringParameter labelText;
    //private Font font;
    private Stroke stroke;
    private Fill fill;
    private Halo halo;

}
