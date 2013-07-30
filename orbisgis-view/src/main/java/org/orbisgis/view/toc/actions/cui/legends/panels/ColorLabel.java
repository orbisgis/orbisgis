/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.ColorPicker;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 29/07/13
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class ColorLabel extends JPanel {

    /**
     * Width used for the rectangles that displays the color parameters of the symbols.
     */
    public final static int FILLED_LABEL_WIDTH = 55;
    /**
     * Height used for the rectangles that displays the color parameters of the symbols.
     */
    public final static int FILLED_LABEL_HEIGHT = 15;

    private CanvasSE preview;
    private ConstantSolidFill fill;

    public ColorLabel(ConstantSolidFill legend, CanvasSE preview) {
        super();
        this.preview = preview;
        this.fill = legend;
        add(getColorField(legend));
    }

    /**
     * Get the fill.
     *
     * @return The fill
     */
    public ConstantSolidFill getFill() {
        return fill;
    }

    /**
     * Get a {@code JPanel} that contains a {@code JLabel}. If the {@code
     * JLabel} is clicked, a dialog is open to let the user choose a color.
     * This {@code JLabel} is linked to the given {@code USParameter}.
     *
     * @param fill
     * @return
     */
    public JLabel getColorField(final ConstantSolidFill fill) {
        JLabel lblFill = getFilledLabel(fill.getColor());
        lblFill.addPropertyChangeListener(
                "background",
                EventHandler.create(
                        PropertyChangeListener.class, fill, "color", "newValue"));
        lblFill.addPropertyChangeListener(
                "background",
                EventHandler.create(
                        PropertyChangeListener.class, preview, "imageChanged"));
        return lblFill;
    }

    /**
     * Get a JLabel of dimensions {@link org.orbisgis.view.toc.actions.cui.legends.PnlUniqueSymbolSE#FILLED_LABEL_WIDTH} and {@link org.orbisgis.view.toc.actions.cui.legends.PnlUniqueSymbolSE#FILLED_LABEL_HEIGHT}
     * opaque and with a background of Color {@code c}.
     *
     * @param c The background color of the label we want.
     * @return the label with c as a background colour.
     */
    public JLabel getFilledLabel(Color c) {
        JLabel lblFill = new JLabel();
        lblFill.setBackground(c);
        lblFill.setBorder(BorderFactory.createLineBorder(Color.black));
        lblFill.setPreferredSize(new Dimension(FILLED_LABEL_WIDTH, FILLED_LABEL_HEIGHT));
        lblFill.setMaximumSize(new Dimension(FILLED_LABEL_WIDTH, FILLED_LABEL_HEIGHT));
        lblFill.setOpaque(true);
        lblFill.setHorizontalAlignment(JLabel.LEFT);
        lblFill.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chooseFillColor(e);
            }
        });
        return lblFill;
    }

    /**
     * This method will let the user choose a color that will be set as the
     * background of the source of the event.
     *
     * @param e The input event.
     */
    public void chooseFillColor(MouseEvent e) {
        Component source = (Component) e.getSource();
        if (source.isEnabled()) {
            JLabel lab = (JLabel) source;
            ColorPicker picker = new ColorPicker(lab.getBackground());
            if (UIFactory.showDialog(picker, false, true)) {
                Color color = picker.getColor();
                source.setBackground(color);
            }
        }
    }
}
