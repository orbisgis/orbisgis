/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;

import org.gdms.data.feature.Feature;
import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;

import org.orbisgis.core.renderer.persistance.se.CompositeSymbolizerType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.SymbolizerType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * This is the entry point
 *
 * @author maxence
 */
public final class CompositeSymbolizer implements SymbolizerNode {

    public JAXBElement<? extends SymbolizerType> getJAXBElement(){
        if (symbolizers.size() == 1){
            return symbolizers.get(0).getJAXBElement();
        }
        else if (symbolizers.size() > 1){
            ObjectFactory of = new ObjectFactory();
            CompositeSymbolizerType cs = of.createCompositeSymbolizerType();
            List<JAXBElement<? extends SymbolizerType>> sList = cs.getSymbolizer();
            for (Symbolizer s : symbolizers){
                sList.add(s.getJAXBElement());
            }

            return of.createCompositeSymbolizer(cs);
        }
        else{
            return null;
        }
    }


    public CompositeSymbolizer(JAXBElement<? extends SymbolizerType> st) throws InvalidStyle {
        symbolizers = new ArrayList<Symbolizer>();
        
        if (st.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.CompositeSymbolizerType.class){
            JAXBElement<CompositeSymbolizerType> jcs = (JAXBElement<CompositeSymbolizerType>)st;

            for (JAXBElement<? extends SymbolizerType> s : jcs.getValue().getSymbolizer()){
                if (s.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.CompositeSymbolizerType.class){
                    // If the sub-symbolizer is another collection : inline all
                    CompositeSymbolizer cs2 = new CompositeSymbolizer(s);
                    for (Symbolizer s2 : cs2.symbolizers){
                        this.addSymbolizer(s2);
                    }
                }
                else{
                    Symbolizer symb = Symbolizer.createSymbolizerFromJAXBElement(s);
                    this.addSymbolizer(symb);
                }
            }
        }
        else{
            this.addSymbolizer(Symbolizer.createSymbolizerFromJAXBElement(st));
        }
    }

    
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException, DriverException{
        for (Symbolizer s : this.symbolizers){
            if (s instanceof VectorSymbolizer){
                ((VectorSymbolizer)s).draw(g2, sds, fid, selected, mt);
            }
        }
    }

    public CompositeSymbolizer(){
        symbolizers = new ArrayList<Symbolizer>();
    }

    public ArrayList<Symbolizer> getSymbolizerList(){
        return this.symbolizers;
    }

    public void addSymbolizer(Symbolizer s){
        symbolizers.add(s);
        s.setParent(this);
		if (s.getLevel() < 0){
			s.setLevel(symbolizers.size());
		}
    }

	/**
	 * @deprecated
	 */
	public void moveSymbolizerDown(Symbolizer s){
		int index = symbolizers.indexOf(s);
		if (index > -1 && index < symbolizers.size()-1){
			symbolizers.remove(index);
			symbolizers.add(index+1, s);
		}
	}


	/**
	 * @deprecated
	 */
	public void moveSymbolizerUp(Symbolizer s){
		int index = symbolizers.indexOf(s);
		if (index > 0){
			symbolizers.remove(index);
			symbolizers.add(index-1, s);
		}
	}

	public void removeSymbolizer(Symbolizer s){
		symbolizers.remove(s);
	}

    private ArrayList<Symbolizer> symbolizers;

    @Override
    public Uom getUom() {
        return null;
    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode rule) {
        this.parent = rule;
    }

    SymbolizerNode parent;
}