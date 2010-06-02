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
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.persistance.se.CompositeSymbolizerType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.SymbolizerType;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * This is the entry point
 *
 * @author maxence
 */
public class CompositeSymbolizer {

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


    public CompositeSymbolizer(JAXBElement<? extends SymbolizerType> st) {
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

    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid) throws ParameterException, IOException, DriverException{
        for (Symbolizer s : this.symbolizers){
            if (s instanceof VectorSymbolizer){
                ((VectorSymbolizer)s).draw(g2, sds, fid);
            }
        }
    }

    public CompositeSymbolizer(){
        symbolizers = new ArrayList<Symbolizer>();
    }

    public void addSymbolizer(Symbolizer s){
        symbolizers.add(s);
    }

    private ArrayList<Symbolizer> symbolizers;
}
