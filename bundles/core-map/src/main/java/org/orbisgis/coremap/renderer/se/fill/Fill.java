/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.coremap.renderer.se.fill;

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
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.UomNode;
import org.orbisgis.coremap.renderer.se.common.Uom;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;


/**
 * Describe how to fill a shape
 *
 * @todo create subclasse FillReference
 *
 * @author Maxence Laurent
 */
public abstract class Fill extends AbstractSymbolizerNode implements UomNode {

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
    public void setUom(Uom u){
            uom = u;
    }

    @Override
    public Uom getOwnUom(){
            return uom;
    }

    @Override
    public Uom getUom(){
        return uom == null ? ((UomNode)getParent()).getUom() : uom;
    }

    /**
     *
     * Fill the shape according to this SE Fill
     *
     * @param g2 draw within this graphics2d
     * @param shp fill this shape
     * @throws ParameterException
     * @throws IOException
     */
    public abstract void draw(Graphics2D g2, Map<String,Object> map, Shape shp,
            boolean selected, MapTransform mt) throws ParameterException, IOException;


    /**
     * Return a Paint that correspond to the SE Fill type.
     * If the fill type cannot be converted into a Painter, null is returned
     *
     * @param selected is the feature selected ?
     * @param mt the map transform
     * @return the paint that correspond to the SE Fill or null if inconvertible (e.g hatched fill, dot map fill, etc)
     *
     * @throws ParameterException
     */
	public abstract Paint getPaint(Map<String,Object> map, boolean selected, MapTransform mt) throws ParameterException, IOException;


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
