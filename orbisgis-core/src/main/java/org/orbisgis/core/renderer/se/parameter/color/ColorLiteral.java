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

package org.orbisgis.core.renderer.se.parameter.color;

import java.util.Random;
import java.awt.Color;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.LiteralType;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.se.parameter.Literal;

public class ColorLiteral extends Literal implements ColorParameter{

    private Color color;
    private static Random rndGenerator;

    static {
        rndGenerator = new Random(13579);
    }

    /**
     * Create a new random color 
     */
    public ColorLiteral(){
        int r = (int)(rndGenerator.nextFloat()*255);
        int g = (int)(rndGenerator.nextFloat()*255);
        int b = (int)(rndGenerator.nextFloat()*255);
        color = new Color(r,g,b);
    }

    public ColorLiteral(Color color){
        this.color = color;
    }

    /**
     * Either "well known color" or "#aabbcc"
     * @param htmlColor
     * @todo create color from htmlColor
     */
    public ColorLiteral(String htmlColor){
        this.color = Color.decode(htmlColor.trim());
    }

    public ColorLiteral(JAXBElement<LiteralType> l) {
        this(l.getValue().getContent().get(0).toString());
    }

    @Override
    public Color getColor(SpatialDataSourceDecorator sds, long fid){
        return color;
    }

    public void setColor(Color color){
        this.color = color;
		this.fireChange();
    }

    @Override
    public String toString(){
        return "#" + String.format("%02X", color.getRed())
                + String.format("%02X", color.getGreen())
                + String.format("%02X", color.getBlue());
    }
}