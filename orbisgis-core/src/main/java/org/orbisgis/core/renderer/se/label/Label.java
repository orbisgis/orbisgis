/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.label;

import java.awt.Graphics2D;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.liteShape.LiteShape;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;

/**
 *
 * @author maxence
 */
public abstract class Label implements SymbolizerNode {

    @Override
    public Uom getUom() {
        throw new UnsupportedOperationException("Not supported yet.");
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

    public abstract void draw(Graphics2D g2, LiteShape shp, DataSource ds, int fid);
    
    protected SymbolizerNode parent;
    protected StyledLabel label;

}
