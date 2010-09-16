/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter;

import javax.swing.JPanel;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.ogc.LiteralType;
import org.orbisgis.core.renderer.persistance.ogc.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.EditFeatureTypeStylePanel;

/**
 *
 * @author maxence
 */
public abstract class Literal implements SeParameter {

    @Override
    public boolean dependsOnFeature() {
        return false;
    }


    @Override
    public ParameterValueType getJAXBParameterValueType()
    {
        ParameterValueType p = new ParameterValueType();
        p.getContent().add(this.toString());
        return p;
    }

    @Override
    public JAXBElement<? extends ExpressionType> getJAXBExpressionType() {
        LiteralType l = new LiteralType();
        l.getContent().add(this.toString());
        ObjectFactory of = new ObjectFactory();
        return of.createLiteral(l);
    }


	@Override
	public JPanel getEditionPanel(EditFeatureTypeStylePanel ftsPanel){
		throw new UnsupportedOperationException("Not yet implemented ("+ this.getClass() + " )");
	}
}
