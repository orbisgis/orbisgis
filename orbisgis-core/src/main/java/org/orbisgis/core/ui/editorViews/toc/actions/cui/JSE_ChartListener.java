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

public class JSE_ChartListener implements MouseMotionListener, MouseListener {

    JSE_ChoroplethChartPanel chartPanel;
    private ChartPanel chPanel;
    private TextTitle annot;
    private JSE_ChoroplethDatas ChoroDatas;

    private Point.Double pos;
    private double rangeDelta;
    private int selected;

    public JSE_ChartListener(JSE_ChoroplethChartPanel chartPanel,ChartPanel chPanel, TextTitle annot, JSE_ChoroplethDatas ChoroDatas) {
        this.chartPanel=chartPanel;
        this.chPanel = chPanel;
        this.annot = annot;
        this.ChoroDatas = ChoroDatas;
        this.pos = new Point.Double(0, 0);
        this.rangeDelta = 1;
    }

    public void chartMouseClicked(ChartMouseEvent arg0) {
    }

    @Override
    public void mouseDragged(MouseEvent arg1) {
        Point point = arg1.getPoint();

        DefaultCategoryDataset data = null;
        try {
            data = chartPanel.refreshData(ChoroDatas.getRange(), ChoroDatas.getSortedData());
        } catch (ParameterException ex) {
            Logger.getLogger(JSE_ChartListener.class.getName()).log(Level.SEVERE, null, ex);
        }

        pos = JSE_ChoroplethChartPanel.convertCoords(point, chPanel, data);

        annot.setText("X = " + pos.x + " Y = " + pos.y);

        if(selected!=-1)
        {
            CategoryPlot plot = (CategoryPlot) chPanel.getChart().getPlot();
            plot.clearDomainMarkers();

            Range[] ranges = ChoroDatas.getRange();

            double range=0;
            try {
                range = ChoroDatas.getSortedData()[(int) pos.x];
            } catch (ParameterException ex) {
                Logger.getLogger(JSE_ChartListener.class.getName()).log(Level.SEVERE, null, ex);
            }

            ranges[selected-1].setMaxRange(range);
            ranges[selected].setMinRange(range);

            ChoroDatas.setRange(ranges);

            try {
                JSE_ChoroplethChartPanel.drawData(chPanel, ranges, ChoroDatas.getSortedData());
                JSE_ChoroplethChartPanel.drawAxis(chPanel.getChart(), ChoroDatas.getSortedData(), ranges, ChoroDatas.getClassesColors());
            } catch (ParameterException ex) {
                Logger.getLogger(JSE_ChartListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        chPanel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent arg1) {
        Point point = arg1.getPoint();

        DefaultCategoryDataset data = null;
        try {
            data = chartPanel.refreshData(ChoroDatas.getRange(), ChoroDatas.getSortedData());
        } catch (ParameterException ex) {
            Logger.getLogger(JSE_ChartListener.class.getName()).log(Level.SEVERE, null, ex);
        }

        pos = JSE_ChoroplethChartPanel.convertCoords(point, chPanel, data);

        annot.setText("X = " + pos.x + " Y = " + pos.y);

        chPanel.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {

        selected=-1;
        Range[] ranges = ChoroDatas.getRange();
        for (int i = 1; i <= ranges.length; i++) {
            double range=0;
            try {
                range = ChoroDatas.getSortedData()[(int) pos.x];
            } catch (ParameterException ex) {
                Logger.getLogger(JSE_ChartListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (isNear(range, ranges[i-1].getMaxRange())) {
                selected = i;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        selected=-1;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public Point.Double getPos() {
        return pos;
    }

    private boolean isNear(double x, double delta) {
        if (x <= delta + rangeDelta && x >= delta - rangeDelta) {
            return true;
        }
        return false;
    }
}
