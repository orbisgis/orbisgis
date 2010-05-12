package org.orbisgis.core.renderer.se.parameter.color;

import java.util.Random;
import java.awt.Color;
import org.gdms.data.DataSource;
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

    @Override
    public boolean dependsOnFeature(){
        return false;
    }

    @Override
    public Color getColor(DataSource ds, long fid){
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
