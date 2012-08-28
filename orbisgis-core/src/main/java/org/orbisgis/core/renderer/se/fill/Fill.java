/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.io.IOException;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.FillType;
import net.opengis.se._2_0.core.GraphicFillType;
import net.opengis.se._2_0.core.HatchedFillType;
import net.opengis.se._2_0.core.SolidFillType;
import net.opengis.se._2_0.thematic.DensityFillType;
import net.opengis.se._2_0.thematic.DotMapFillType;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;


/**
 * Describe how to fill a shape
 *
 * @todo create subclasse FillReference
 *
 * @author Maxence Laurent
 */
public abstract class Fill implements UomNode {

    protected UomNode parent;
    private Uom uom;
    /**
     * Create a new fill based on the jaxbelement
     *
     * @param f XML Fill
     * @return Java SE Fill
     */
    public static Fill createFromJAXBElement(JAXBElement<? extends FillType> f) throws InvalidStyle{
        if (f.getDeclaredType() == SolidFillType.class){
            return new SolidFill((JAXBElement<SolidFillType>)f);
        }
        else if (f.getDeclaredType() == GraphicFillType.class){
            return new GraphicFill((JAXBElement<GraphicFillType>)f);
        }
        else if (f.getDeclaredType() == DensityFillType.class){
            return new DensityFill((JAXBElement<DensityFillType>)f);
        }
        else if (f.getDeclaredType() == DotMapFillType.class){
            return new DotMapFill((JAXBElement<DotMapFillType>)f);
        }
        else if (f.getDeclaredType() == HatchedFillType.class){
            return new HatchedFill((JAXBElement<HatchedFillType>) f);
        }
        else{
            throw new InvalidStyle("This stroke is not supported: " + f.getDeclaredType());
        }

    }

    @Override
    public void setParent(SymbolizerNode node){
        parent = (UomNode)node;
    }

    @Override
    public void update() {
            parent.update();
    }
    
    @Override
    public void setUom(Uom u){
            uom = u;
    }

    @Override
    public Uom getOwnUom(){
            return uom;
    }

    @Override
    public Uom getUom(){
        return uom == null ? parent.getUom() : uom;
    }

    @Override
    public SymbolizerNode getParent(){
        return parent;
    }

    /**
     *
     * Fill the shape according to this SE Fill
     *
     * @param g2 draw within this graphics2d
     * @param shp fill this shape
     * @param feat feature which contains potential used attributes
     * @throws ParameterException
     * @throws IOException
     */
    public abstract void draw(Graphics2D g2, Map<String,Value> map, Shape shp,
            boolean selected, MapTransform mt) throws ParameterException, IOException;


    /**
     * Return a Paint that correspond to the SE Fill type.
     * If the fill type cannot be converted into a Painter, null is returned
     *
     * @param fid current feature id
     * @param sds data source
     * @param selected is the feature selected ?
     * @param mt the map transform
     * @return the paint that correspond to the SE Fill or null if inconvertible (e.g hatched fill, dot map fill, etc)
     *
     * @throws ParameterException
     */
	public abstract Paint getPaint(Map<String,Value> map, boolean selected, MapTransform mt) throws ParameterException, IOException;


    /**
     * Serialise to JAXBElement
     * @return
     */
    public abstract JAXBElement<? extends FillType> getJAXBElement();

    /**
     * Serialise to FillType
     * @return
     */
    public abstract FillType getJAXBType();


}
