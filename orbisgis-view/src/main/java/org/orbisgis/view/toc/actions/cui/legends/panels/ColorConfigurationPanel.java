package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.ColorPicker;
import org.orbisgis.view.toc.actions.cui.legends.PnlUniqueSymbolSE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;

/**
 * A panel with two filled labels whose colour can be changed. It is then possible to retrieve the colour
 * of both.
 * @author Alexis Guéganno
 */
public class ColorConfigurationPanel extends JPanel {
    private static final I18n I18N = I18nFactory.getI18n(ColorConfigurationPanel.class);
    private JLabel endCol;
    private JLabel startCol;

    /**
     * Builds a panel with two filled labels whose colour can be changed. It is then possible to retrieve the colour
     * of both.
     */
    public ColorConfigurationPanel(){
        super();
        BoxLayout classLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(classLayout);
        //The start colour
        JPanel start = new JPanel();
        start.add(new JLabel(I18N.tr("Start colour :")));
        startCol = getFilledLabel(Color.BLUE);
        start.add(startCol);
        start.setAlignmentX((float).5);
        this.add(start);
        //The end colour
        JPanel end = new JPanel();
        end.add(new JLabel(I18N.tr("End colour :")));
        endCol = getFilledLabel(Color.RED);
        end.setAlignmentX((float).5);
        end.add(endCol);
        this.add(end);
        //We add this to the global panel
        this.setAlignmentX((float).5);
    }

    /**
     * Get a JLabel of dimensions {@link PnlUniqueSymbolSE#FILLED_LABEL_WIDTH} and {@link PnlUniqueSymbolSE#FILLED_LABEL_HEIGHT}
     * opaque and with a background of Color {@code c}.
     * @param c The background color of the label we want.
     * @return the label with c as a background colour.
     */
    public JLabel getFilledLabel(Color c){
        JLabel lblFill = new JLabel();
        lblFill.setBackground(c);
        lblFill.setBorder(BorderFactory.createLineBorder(Color.black));
        lblFill.setPreferredSize(new Dimension(PnlUniqueSymbolSE.FILLED_LABEL_WIDTH, PnlUniqueSymbolSE.FILLED_LABEL_HEIGHT));
        lblFill.setMaximumSize(new Dimension(PnlUniqueSymbolSE.FILLED_LABEL_WIDTH, PnlUniqueSymbolSE.FILLED_LABEL_HEIGHT));
        lblFill.setOpaque(true);
        MouseListener ma = EventHandler.create(MouseListener.class, this, "chooseFillColor", "", "mouseClicked");
        lblFill.addMouseListener(ma);
        return lblFill;
    }

    /**
     * Gets the start Color for the classification generation
     * @return The start Color
     */
    public Color getStartColor(){
        return startCol.getBackground();
    }

    /**
     * Gets the end Color for the classification generation
     * @return The end Color
     */
    public Color getEndCol() {
        return endCol.getBackground();
    }

    /**
     * This method will let the user choose a color that will be set as the
     * background of the source of the event.
     * @param e
     */
    public void chooseFillColor(MouseEvent e) {
        Component source = (Component)e.getSource();
        if(source.isEnabled()){
            JLabel lab = (JLabel) source;
            ColorPicker picker = new ColorPicker(lab.getBackground());
            if (UIFactory.showDialog(picker, false, true)) {
                Color color = picker.getColor();
                source.setBackground(color);
            }
        }
    }
}
