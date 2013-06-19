package org.orbisgis.view.toc.actions.cui.legends.panels;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Alexis Guéganno
 */
public class ColorSchemeListCell
        extends JPanel {

    private JLabel colorPanel1 = new JLabel();
    private JLabel colorPanel2 = new JLabel();
    private JLabel colorPanel3 = new JLabel();
    private JLabel colorPanel4 = new JLabel();
    private JLabel colorPanel5 = new JLabel();
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JLabel label = new JLabel();

    /**
     *
     * @param name
     * @param isSelected
     * @param fg
     * @param bg
     */
    public ColorSchemeListCell(String name, boolean isSelected, Color fg, Color bg) {
        try {
            Collection<Color> colors = colorScheme(name).getColors();
            Iterator i = colorScheme(name).getSubset(5).iterator();
            label.setText("(" + colors.size() + ") " +  name);
            color(colorPanel1, (Color) i.next());
            color(colorPanel2, (Color) i.next());
            color(colorPanel3, (Color) i.next());
            color(colorPanel4, (Color) i.next());
            color(colorPanel5, (Color) i.next());
            label.setForeground(fg);
            label.setBackground(bg);
            setForeground(fg);
            setBackground(bg);
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the ColorScheme whose name is {@code name}
     * @param name The name of the ColorScheme.
     * @return The ColorScheme
     */
    protected ColorScheme colorScheme(String name) {
        return ColorScheme.create(name);
    }

    /**
     * Sets the foreground and background colors of the given JLabel to {@code fillColor}.
     * @param colorPanel The JLabel we want to color.
     * @param fillColor The Color.
     */
    private void color(JLabel colorPanel, Color fillColor) {
        colorPanel.setSize(new Dimension(8,8));
        colorPanel.setOpaque(true);
        colorPanel.setBackground(fillColor);
        colorPanel.setForeground(fillColor);
    }

    /**
     * Initializes the CellRenderer.
     * @throws Exception
     */
    private void jbInit() throws Exception {
        this.setLayout(gridBagLayout1);
        label.setText("jLabel1");
        this.add(
                colorPanel1,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                        ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 0, 0));
        this.add(
                colorPanel2,
                new GridBagConstraints(
                        1,
                        0,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0),
                        0,
                        0));
        this.add(
                colorPanel3,
                new GridBagConstraints(
                        2,
                        0,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0),
                        0,
                        0));
        this.add(
                colorPanel4,
                new GridBagConstraints(
                        3,
                        0,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0),
                        0,
                        0));
        this.add(
                colorPanel5,
                new GridBagConstraints(
                        4,
                        0,
                        1,
                        1,
                        0.0,
                        0.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0),
                        0,
                        0));
        this.add(
                label,
                new GridBagConstraints(5, 0, 1, 1, 1.0, 0.0
                        ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 0, 0), 0, 0));
    }

    /**
     * Workaround for bug 4238829 in the Java bug database:
     * "JComboBox containing JPanel fails to display selected item at creation time"
     */
    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        validate();
    }

}

