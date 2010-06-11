package org.orbisgis.core.renderer.se.parameter.color;

import java.util.Random;
import java.awt.Color;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.ogc.LiteralType;
import org.orbisgis.core.renderer.se.parameter.Literal;

public class ColorLiteral extends Literal implements ColorParameter{

    /**
     * Create a new random color 
     */
    public ColorLiteral(){
        if (rndGenerator == null){
            rndGenerator = new Random(13579);
        }
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
        this.color = Color.decode(htmlColor);
    }

    public ColorLiteral(JAXBElement<LiteralType> l) {
        this(l.getValue().getContent().get(0).toString());
    }

    @Override
    public boolean dependsOnFeature(){
        return false;
    }

    @Override
    public Color getColor(Feature feat){
        return color;
    }

    public void setColor(Color color){
        this.color = color;
    }

    @Override
    public String toString(){
        return "#" + String.format("%02X", color.getRed())
                + String.format("%02X", color.getGreen())
                + String.format("%02X", color.getBlue());
    }

    private Color color;

    private static Random rndGenerator;
}
