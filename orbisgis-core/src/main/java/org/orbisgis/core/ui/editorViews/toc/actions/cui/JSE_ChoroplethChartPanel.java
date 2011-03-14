/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import org.gdms.data.feature.Feature;
import org.gdms.data.values.Value;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;

/**
 *
 * @author sennj
 */
class JSE_ChoroplethChartPanel extends JPanel{

    private Range[] ranges;
    private Value value;


    JSE_ChoroplethChartPanel(Range[] ranges, Value value) {
        super();
        this.ranges=ranges;
        this.value=value;
    }
    
    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawLine(5, 5, 5, 100);
        
        Range range;
        for(int i =1;i<=ranges.length;i++){
        	range =ranges[i-1];
        	//g2d.drawRect(convertToStep(range.getMinRange(),width),0,convertToStep(step.getMax(),xMax,width)-convertToStep(step.getMin(),xMax,width), height);
                System.out.println("Range "+i+" min "+range.getMinRange()+" max "+range.getMaxRange()+" part "+range.getPartOfItems()+" nb "+range.getNumberOfItems());
               
        }
        System.out.println("Value "+value);
    }

}
