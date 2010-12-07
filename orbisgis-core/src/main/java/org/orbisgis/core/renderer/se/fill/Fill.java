package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.FillType;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.DensityFillType;
import org.orbisgis.core.renderer.persistance.se.DotMapFillType;
import org.orbisgis.core.renderer.persistance.se.GraphicFillType;
import org.orbisgis.core.renderer.persistance.se.SolidFillType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;


/**
 * Describe how to fill a shape
 *
 * @todo create subclasse FillReference
 *
 * @author maxence
 */
public abstract class Fill implements SymbolizerNode {

    /**
     * Create a new fill based on the jaxbelement
     *
     * @param f XML Fill
     * @return Java Fill
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
        else{
			// Will never occurs with a valid XML style
            return new SolidFill();
        }

    }

    @Override
    public void setParent(SymbolizerNode node){
        parent = node;
    }

    @Override
    public SymbolizerNode getParent(){
        return parent;
    }

    @Override
    public Uom getUom(){
        return parent.getUom();
    }

    
    public abstract boolean dependsOnFeature();

    /**
     *
     * @param g2 draw within this graphics2d
     * @param shp fill this shape
     * @param feat feature which contains potential used attributes
     * @throws ParameterException
     * @throws IOException
     */
    public abstract void draw(Graphics2D g2, Shape shp, Feature feat, boolean selected, MapTransform mt) throws ParameterException, IOException;

    public abstract JAXBElement<? extends FillType> getJAXBElement();
    public abstract FillType getJAXBType();

    protected SymbolizerNode parent;
}
