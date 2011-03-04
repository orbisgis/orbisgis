/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */


package org.orbisgis.core.renderer.se;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;

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

    /*
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, boolean selected, MapTransform mt) throws ParameterException, IOException, DriverException{
        for (Symbolizer s : this.symbolizers){
            if (s instanceof VectorSymbolizer){
                ((VectorSymbolizer)s).draw(g2, sds, fid, selected, mt, null, null);
            }
        }
    }*/

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