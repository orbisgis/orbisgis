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
package org.orbisgis.core.renderer.se.stroke;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.*;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * Style description for linear features (Area or Line)
 *
 * @author Maxence Laurent, Alexis Guéganno.
 */
public abstract class Stroke extends AbstractSymbolizerNode implements UomNode {

    private Uom uom;
    private boolean linearRapport;
    private boolean offsetRapport;

    /**
     * Instanciate a new default {@code Stroke}, with linear and offset rapports
     * set to false.
     */
    protected Stroke(){
        linearRapport = false;
        offsetRapport = false;
    }

    /**
     * Instanciate a new {@code Stroke}, using the JAXB {@code StrokeType} given
     * in argument.
     * @param s
     */
    protected Stroke (StrokeType s){
        this();

        if (s.getExtension() != null) {
            for (ExtensionParameterType param : s.getExtension().getExtensionParameter()) {

                /* Only to handle old styles... */
                if (param.getName().equalsIgnoreCase("linearRapport")) {
                    linearRapport = param.getContent().equalsIgnoreCase("on");
                } else if (param.getName().equalsIgnoreCase("offsetRapport")) {
                    offsetRapport = param.getContent().equalsIgnoreCase("on");
                }
            }
        }

        if (s.isLinearRapport() != null){
            linearRapport = s.isLinearRapport();
        }
    }

    /**
     * Create a new stroke based on the jaxbelement
     *
     * @param s XML Stroke
     * @return Java Stroke
     */
    public static Stroke createFromJAXBElement(JAXBElement<? extends StrokeType> s) throws InvalidStyle{
        if (s.getDeclaredType() == PenStrokeType.class){
            return new PenStroke((JAXBElement<PenStrokeType>)s);
        } else if (s.getDeclaredType() == GraphicStrokeType.class){
            return new GraphicStroke((JAXBElement<GraphicStrokeType>)s);
        }else if (s.getDeclaredType() == CompoundStrokeType.class){
			return new CompoundStroke((JAXBElement<CompoundStrokeType>)s);
        }else if (s.getDeclaredType() == TextStrokeType.class){
			return new TextStroke((JAXBElement<TextStrokeType>)s);
		}
        //As s.getDeclaredType() os a StrokeType, we are supposed to never throw this Exception...
        throw new InvalidStyle("Trying to create a Stroke from an invalid input");
    }

    /**
     * When delineating closed shapes (i.e. a ring), indicate, whether or not,
     * the length of stroke elements shall be scaled in order to make the pattern
     * appear a integral number of time. This will make the junction more aesthetical
     *
     * @return <cdoe>true</code> if the stroke elements' length shall be scaled,
     * <code>false</code> otherwise.
     */
    public boolean isLengthRapport() {
        return linearRapport;
    }

    /**
     * Determines if we want to use an length rapport or not.
     * @param lengthRapport
     */
    public void setLengthRapport(boolean lengthRapport) {
        this.linearRapport = lengthRapport;
    }

    /**
     * When delineating a line with a perpendicular offset, indicate whether or not
     * stroke element shall following the initial line (rapport=true) or should only
     * be based on the offseted line (rapport=false);
     *
     * @return true if offseted element shall follow initial line
     */
    public boolean isOffsetRapport() {
        return offsetRapport;
    }

    /**
     * Determines if we want to use an offset rapport or not.
     * @param offsetRapport
     */
    public void setOffsetRapport(boolean offsetRapport) {
        this.offsetRapport = offsetRapport;
    }

    /**
     * Apply the present Stroke to the geometry stored in sds, at index fid, in
     * graphics g2.
     * @param g2 draw within this graphics2d
     * @param map
     * @param shp stroke this shape (note this is note a JTS Geometry, because
     *        stroke can be used to delineate graphics (such as MarkGraphic,
     *        PieChart or AxisChart)
     * @param selected emphasis or not the stroke (e.g invert colours)
     * @param mt the well known mapTransform 
     * @param  offset perpendicular offset to apply
     * @throws ParameterException
     * @throws IOException
     */
    public abstract void draw(Graphics2D g2, Map<String,Object> map, Shape shp,
            boolean selected, MapTransform mt, double offset) throws ParameterException, IOException;

    /**
     * Get a JAXB representation of this {@code Label}
     * @return
     * A {@code JAXBElement} that contains a {@code LabelType} specialization.
     */
    public abstract JAXBElement<? extends StrokeType> getJAXBElement();

    /**
     * Fill the {@code LabelType} given in argument with this {@code Label}'s
     * properties.
     */
    protected final void setJAXBProperties(StrokeType s) {

        ObjectFactory of = new ObjectFactory();
        ExtensionType exts = of.createExtensionType();

        s.setLinearRapport(this.isLengthRapport());
        
        /*ExtensionParameterType linRap = of.createExtensionParameterType();
        linRap.setName("linearRapport");
        if (this.linearRapport){
            linRap.setContent("on");
        } else {
            linRap.setContent("off");
        }
        exts.getExtensionParameter().add(linRap);
         */

        ExtensionParameterType offRap = of.createExtensionParameterType();
        offRap.setName("offsetRapport");
        if (this.offsetRapport){
            offRap.setContent("on");
        } else {
            offRap.setContent("off");
        }
        exts.getExtensionParameter().add(offRap);


        s.setExtension(exts);
    }

    /**
     * Returns the stroke pattern natural length, in pixel unit
     * @param map
     * @param shp
     * @param mt
     * @return
     * @throws ParameterException
     * @throws IOException
     */
    public abstract Double getNaturalLength(Map<String,Object> map,
            Shape shp, MapTransform mt) throws ParameterException, IOException;

    /**
     * same as getNaturalLength, but in some case (i.e. PenStroke) the natural length
     * to use in not the same as returned by the latter : Especially for PenStroke : 
     * To compute a tile for hatching, we need to know the length of the pen stroke
     * (i.e. only when the stroke is dashed...), but to embed such a stroke in a compound, 
     * the natural length shall be +Inf
     * 
     * @param map
     * @param shp
     * @param mt
     * @return
     * @throws ParameterException
     * @throws IOException 
     */
    public Double getNaturalLengthForCompound(Map<String,Object> map,
            Shape shp, MapTransform mt) throws ParameterException, IOException {
        return getNaturalLength(map, shp, mt);
    }

    @Override
    public Uom getUom() {
        if (uom != null) {
            return uom;
        } else if(getParent() instanceof UomNode){
            return ((UomNode)getParent()).getUom();
        } else {
                return Uom.PX;
        }
    }

    @Override
    public void setUom(Uom u) {
        uom = u;
    }

    @Override
    public Uom getOwnUom() {
        return uom;
    }
    
}
