/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter;

import java.util.ArrayList;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.ExpressionType;
import net.opengis.fes._2.LiteralType;
import net.opengis.fes._2.ObjectFactory;
import net.opengis.se._2_0.core.ParameterValueType;

/**
 *
 * @author maxence
 */
public abstract class Literal implements SeParameter {

	private ArrayList<LiteralListener> listeners;

	public Literal(){
		listeners = new ArrayList<LiteralListener>();
	}

	public void register(LiteralListener l){
		if (!listeners.contains(l)){
			listeners.add(l);
		}
	}

	public void fireChange(){
		for (LiteralListener l : listeners){
			l.literalChanged();
		}
	}

    @Override
    public String dependsOnFeature() {
        return "";
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
}
