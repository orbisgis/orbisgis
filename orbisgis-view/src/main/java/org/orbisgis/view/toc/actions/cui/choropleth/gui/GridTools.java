package org.orbisgis.view.toc.actions.cui.choropleth.gui;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

/**
 * Grid maker tools
 */
public class GridTools {

    /** The inital x value*/
    private static int initX = 1;
    /** The inital y value*/
    private static int initY = 1;
    /** The inital x padding value*/
    private static int paddingX = 5;
    /** The inital y padding value*/
    private static int paddingY = 5;

    public static void generateGrid(JPanel panel, int rows, int cols) {
        SpringLayout layout = (SpringLayout) panel.getLayout();

        Spring xPadSp = Spring.constant(paddingX);
        Spring yPadSp = Spring.constant(paddingY);
        Spring initXSp = Spring.constant(initX);
        Spring initYSp = Spring.constant(initY);
        int max = rows * cols;

        Component comp = panel.getComponent(0);
        Spring maxWSp = layout.getConstraints(comp).getWidth();
        Spring maxHSp = layout.getConstraints(comp).getHeight();
        Component panComp;
        for (int i = 1; i < max; i++) {
            panComp = panel.getComponent(i);
            SpringLayout.Constraints cons = layout.getConstraints(panComp);
            maxWSp = Spring.max(maxWSp, cons.getWidth());
            maxHSp = Spring.max(maxHSp, cons.getHeight());
        }

        for (int i = 0; i < max; i++) {
            panComp = panel.getComponent(i);
            SpringLayout.Constraints cons = layout.getConstraints(panComp);
            cons.setWidth(maxWSp);
            cons.setHeight(maxHSp);
        }

        SpringLayout.Constraints lastCons = null;
        SpringLayout.Constraints lastRowCons = null;
        for (int i = 0; i < max; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(panel.getComponent(i));
            if (i % cols == 0) {
                lastRowCons = lastCons;
                cons.setX(initXSp);
            } else {
                cons.setX(Spring.sum(lastCons.getConstraint(SpringLayout.EAST), xPadSp));
            }
            if (i / cols == 0) {
                cons.setY(initYSp);
            } else {
                cons.setY(Spring.sum(lastRowCons.getConstraint(SpringLayout.SOUTH), yPadSp));
            }
            lastCons = cons;
        }

        SpringLayout.Constraints pCons = layout.getConstraints(panel);
        pCons.setConstraint(SpringLayout.SOUTH, Spring.sum(Spring.constant(paddingX),
                lastCons.getConstraint(SpringLayout.SOUTH)));
        pCons.setConstraint(SpringLayout.EAST, Spring.sum(Spring.constant(paddingY),
                lastCons.getConstraint(SpringLayout.EAST)));
    }
}
