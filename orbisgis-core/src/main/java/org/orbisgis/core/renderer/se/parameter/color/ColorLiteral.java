package org.orbisgis.core.renderer.se.parameter.color;

import java.util.Random;
import java.awt.Color;
import org.gdms.data.DataSource;

public class ColorLiteral implements ColorParameter{

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
        return color.toString();
    }

    private Color color;

    private static Random rndGenerator;
}
