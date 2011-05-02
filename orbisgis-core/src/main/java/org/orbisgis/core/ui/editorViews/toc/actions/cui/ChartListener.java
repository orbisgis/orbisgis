package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 * Mouse Listener on the chart
 * @author sennj
 */
public class ChartListener implements MouseMotionListener, MouseListener {

    private ChoroplethChartPanel chartPanel;
    private ChartPanel chPanel;
    private TextTitle annot;
    private ChoroplethDatas choroDatas;
    private Point.Double pos;
    private double rangeDelta;
    private int selected;

    /***
     * ChartListener Constructor
     * @param chartPanel the chart Panel
     * @param chPanel the JFreeChart Panel
     * @param annot the chart Panel subtitle
     * @param choroDatas the datas to draw
     */
    public ChartListener(ChoroplethChartPanel chartPanel, ChartPanel chPanel, TextTitle annot, ChoroplethDatas choroDatas) {
        this.chartPanel = chartPanel;
        this.chPanel = chPanel;
        this.annot = annot;
        this.choroDatas = choroDatas;
        this.pos = new Point.Double(0, 0);
        this.rangeDelta = 5;
    }

    public void chartMouseClicked(ChartMouseEvent arg0) {
        choroDatas.setStatisticMethod(ChoroplethDatas.StatisticMethod.MANUAL, false);
    }

    @Override
    public void mouseDragged(MouseEvent arg1) {

        //Set the coord of the mouse pointer
        Point point = arg1.getPoint();
        DefaultCategoryDataset data = null;
        try {
            data = chartPanel.refreshData(choroDatas.getAliases(), choroDatas.getRange(), choroDatas.getSortedData());
        } catch (ParameterException ex) {
            Logger.getLogger(ChartListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        pos = chartPanel.convertCoords(point, chPanel, data);
        annot.setText("X = " + pos.x + " Y = " + pos.y);

        //If a range is selected and the mouse is dragged then move the range
        if (selected != -1) {
            CategoryPlot plot = (CategoryPlot) chPanel.getChart().getPlot();
            plot.clearDomainMarkers();

            Range[] ranges = choroDatas.getRange();

            double[] datas = null;
            try {
                datas = choroDatas.getSortedData();
            } catch (ParameterException ex) {
                Logger.getLogger(ChartListener.class.getName()).log(Level.SEVERE, null, ex);
            }

            if ((int) pos.x > 0 && (int) pos.x < datas.length) {
                
                int nbElemBefore = 0;
                int nbElemAfter = 0;
                for (int i = 1; i <= selected + 1; i++) {
                    if (i < selected) {
                        nbElemBefore = nbElemBefore + ranges[i - 1].getNumberOfItems();
                    }
                    nbElemAfter = nbElemAfter + ranges[i - 1].getNumberOfItems();
                }

                if ((int) pos.x > nbElemBefore && (int) pos.x < nbElemAfter) {

                    ranges[selected - 1].setMaxRange(datas[(int) pos.x ]);
                    ranges[selected].setMinRange(datas[(int) pos.x ]);

                    System.out.println("pos "+pos.x+" bef "+nbElemBefore+" aft "+nbElemAfter);

                    ranges[selected - 1].setNumberOfItems((int)(pos.x - nbElemBefore));
                    ranges[selected].setNumberOfItems((int)(nbElemAfter- pos.x+1));

                    choroDatas.setRange(ranges);
                    choroDatas.calculateColors();
                }
            }
            try {
                chartPanel.drawData(chPanel, choroDatas.getAliases(), ranges, choroDatas.getSortedData());
                chartPanel.drawAxis(chPanel.getChart(), choroDatas.getSortedData(), ranges, choroDatas.getClassesColors(), choroDatas.getAliases());
            } catch (ParameterException ex) {
                Logger.getLogger(ChartListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        chPanel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent arg1) {

        //Set the coord of the mouse pointer
        Point point = arg1.getPoint();
        DefaultCategoryDataset data = null;
        try {
            data = chartPanel.refreshData(choroDatas.getAliases(), choroDatas.getRange(), choroDatas.getSortedData());
        } catch (ParameterException ex) {
            Logger.getLogger(ChartListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        pos = chartPanel.convertCoords(point, chPanel, data);
        annot.setText("X = " + pos.x + " Y = " + pos.y);

        //If the mouse move on a range then select it
        selected = -1;
        Range[] ranges = choroDatas.getRange();
        int nbElem = 0;
        for (int i = 1; i <= ranges.length; i++) {
            nbElem += ranges[i - 1].getNumberOfItems();
            if (isNear(pos.x, nbElem)) {
                selected = i;
            }
        }
        try {
            CategoryPlot plot = (CategoryPlot) chPanel.getChart().getPlot();
            plot.clearDomainMarkers();
            chartPanel.drawAxis(chPanel.getChart(), choroDatas.getSortedData(), ranges, choroDatas.getClassesColors(), choroDatas.getAliases(), selected);
            chPanel.repaint();
        } catch (ParameterException ex) {
            Logger.getLogger(ChartListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {

        //If the mouse is pressed on a range then select it
        selected = -1;
        int nbElem = 0;
        Range[] ranges = choroDatas.getRange();

        for (int i = 1; i <= ranges.length; i++) {
            nbElem += ranges[i - 1].getNumberOfItems();
            if (isNear(pos.x, nbElem)) {
                selected = i;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        selected = -1;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * getPos
     * get the coordinate of the mouse pointer
     * @return coordinate of the mouse pointer
     */
    public Point.Double getPos() {
        return pos;
    }

    /**
     * isNear
     * Test if a an element is near another
     * @param an element
     * @param a second
     * @return true if elem is near, false other
     */
    private boolean isNear(double x, double nbElem) {
        if (x <= nbElem + rangeDelta && x >= nbElem - rangeDelta) {
            return true;
        }
        return false;
    }
}
