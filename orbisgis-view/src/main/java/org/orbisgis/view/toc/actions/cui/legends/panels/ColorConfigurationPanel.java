package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.sif.ComponentUtil;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.ColorPicker;
import org.orbisgis.view.toc.actions.cui.legends.PnlUniqueSymbolSE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.util.*;
import java.util.List;

/**
 * A panel with two filled labels whose colour can be changed. It is then possible to retrieve the colour
 * of both.
 * @author Alexis Guéganno
 */
public class ColorConfigurationPanel extends JPanel {
    private static final I18n I18N = I18nFactory.getI18n(ColorConfigurationPanel.class);
    private JPanel pal;
    private JPanel grad;
    private JLabel endCol;
    private JLabel startCol;
    private JRadioButton bGrad;
    private JRadioButton bPal;
    private JComboBox schemes;

    /**
     * Builds a panel with two filled labels whose colour can be changed. It is then possible to retrieve the colour
     * of both.
     */
    public ColorConfigurationPanel(){
        super();
        JPanel intOne = new JPanel();
        GridLayout gl = new GridLayout(2,2);
        intOne.setLayout(gl);
        grad = getGradientPanel();
//        grad.setAlignmentY(.5f);
        pal = getPalettesPanel();
//        pal.setAlignmentY(.5f);
        initButtons();
        intOne.add(bGrad);
        intOne.add(grad);
        intOne.add(bPal);
        intOne.add(pal);
        this.add(intOne);
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
        lblFill.setPreferredSize(new Dimension(PnlUniqueSymbolSE.FILLED_LABEL_HEIGHT, PnlUniqueSymbolSE.FILLED_LABEL_HEIGHT));
        lblFill.setMaximumSize(new Dimension(PnlUniqueSymbolSE.FILLED_LABEL_HEIGHT, PnlUniqueSymbolSE.FILLED_LABEL_HEIGHT));
        lblFill.setOpaque(true);
        MouseListener ma = EventHandler.create(MouseListener.class, this, "chooseFillColor", "", "mouseClicked");
        lblFill.addMouseListener(ma);
        return lblFill;
    }

    private JPanel getGradientPanel(){
        JPanel ret = new JPanel();
        JPanel start = new JPanel();
        start.add(new JLabel(I18N.tr("Gradient - Start :")));
        startCol = getFilledLabel(Color.BLUE);
        start.add(startCol);
        ret.add(start);
        //The end colour
        JPanel end = new JPanel();
        end.add(new JLabel(I18N.tr("End :")));
        endCol = getFilledLabel(Color.RED);
        end.add(endCol);
        ret.add(end);
        //We add this to the global panel
        ret.setAlignmentX((float) .5);
        return ret;
    }

    private JPanel getPalettesPanel(){
        java.util.List<String> schemeNames = ColorScheme.rangeColorSchemeNames();
        schemes = new JComboBox(schemeNames.toArray(new String[schemeNames.size()]));
        schemes.setRenderer(new ColorSchemeListCellRenderer(new JList()));
        JPanel schemesPan = new JPanel();
        schemesPan.add(schemes);
        return schemesPan;
    }

    /**
     * Initialized the buttons used to configure how coloured classification must be generated.
     */
    private void  initButtons(){
        bGrad = new JRadioButton("");
        bPal = new JRadioButton("");
        ButtonGroup bg = new ButtonGroup();
        bg.add(bGrad);
        bg.add(bPal);
        ActionListener actionRefV = EventHandler.create(ActionListener.class, this, "onClickGrad");
        ActionListener actionRefC= EventHandler.create(ActionListener.class, this, "onClickPal");
        bGrad.addActionListener(actionRefV);
        bPal.addActionListener(actionRefC);
        bPal.setSelected(true);
        onClickPal();
    }

    /**
     * The user clicked on the gradient radio button. Used by EventHandler.
     */
    public void onClickGrad(){
        ComponentUtil.setFieldState(true, this.grad);
        ComponentUtil.setFieldState(false, this.pal);
    }

    /**
     * The user clicked on the palette button. Used by EventHandler.
     */
    public void onClickPal(){
        ComponentUtil.setFieldState(false,this.grad);
        ComponentUtil.setFieldState(true,this.pal);
    }

    /**
     * Retrieve the current ColorScheme.
     * @return The ColorScheme.
     */
    public ColorScheme getColorScheme(){
        if(bGrad.isSelected()){
            List<Color> cols = new ArrayList<Color>();
            cols.add(getStartColor());
            cols.add(getEndCol());
            ColorScheme ret = new ColorScheme("grad",cols);
            return ret;
        } else {
            String name = (String)schemes.getSelectedItem();
            return ColorScheme.create(name);
        }
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
     * @param e The input MouseEvent
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
