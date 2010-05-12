/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.label;

import java.awt.Graphics2D;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.LabelType;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.liteShape.LiteShape;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 */
public abstract class Label implements SymbolizerNode {

    @Override
    public Uom getUom() {
        if (uom != null)
            return uom;
        else
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

    public StyledLabel getLabel() {
        return label;
    }

    public void setLabel(StyledLabel label) {
        this.label = label;
        label.setParent(this);
    }
    
    public abstract void draw(Graphics2D g2, LiteShape shp, DataSource ds, long fid) throws ParameterException, IOException;
    
    public abstract JAXBElement<? extends LabelType> getJAXBInstance();

    protected SymbolizerNode parent;

    protected Uom uom;
    protected StyledLabel label;
    protected HorizontalAlignment hAlign;
    protected VerticalAlignment vAlign;
}
