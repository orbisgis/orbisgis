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

import net.miginfocom.swing.MigLayout;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.LegendStructure;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.xnap.commons.i18n.I18n;

import javax.swing.*;

/**
 * Root class for unique symbol and proportional panels.
 *
 * @author Adam Gouge
 */
public abstract class AbsPanel extends JPanel {

    protected LegendStructure legend;
    protected CanvasSE preview;

    public static final String OPACITY = I18n.marktr("Opacity");
    public static final String WIDTH = I18n.marktr("Width");
    public static final String HEIGHT = I18n.marktr("Height");
    public static final String SYMBOL = I18n.marktr("Symbol");
    public static final String DASH_ARRAY = I18n.marktr("Dash array");
    public static final String NUMERIC_FIELD = I18n.marktr("<html><b>Numeric field</b></html>");
    public static final String NONSPATIAL_FIELD = I18n.marktr("<html><b>Nonspatial field</b></html>");
    public static final String LINE_WIDTH_UNIT = I18n.marktr("Line width unit");
    public static final String SYMBOL_SIZE_UNIT = I18n.marktr("Symbol size unit");
    public static final String PLACE_SYMBOL_ON = I18n.marktr(
            "<html><p style=\"text-align:right\">Place symbol<br>on</p></html>");
    /**
     * Width of the second column in pixels.
     */
    private static final int SECOND_COL_WIDTH = 95;
    /**
     * Constraints for ComboBoxes for sizing consistency.
     */
    public static final String COMBO_BOX_CONSTRAINTS =
            "width " + SECOND_COL_WIDTH + "!";
    /**
     * MigLayout constraints for sizing consistency.
     */
    public static final String COLUMN_CONSTRAINTS =
            "[align r, 110::][align c, "
                    + SECOND_COL_WIDTH + ":" + SECOND_COL_WIDTH + ":]";
    /**
     * Fixed width for panels that need it.
     */
    public static final int FIXED_WIDTH = 210;

    /**
     * Constructor
     *
     * @param legend  Legend
     * @param preview Preview
     * @param title   Title
     */
    public AbsPanel(LegendStructure legend,
                    CanvasSE preview,
                    String title) {
        super(new MigLayout("wrap 2", COLUMN_CONSTRAINTS));
        setBorder(BorderFactory.createTitledBorder(title));
        this.legend = legend;
        this.preview = preview;
        if (legend != null && preview == null) {
            initPreview();
        }
    }

    /**
     * Gets the legend.
     *
     * @return The legend.
     */
    protected LegendStructure getLegend() {
        return legend;
    }

    /**
     * Rebuild the {@code CanvasSE} instance used to display a preview of
     * the current symbol.
     */
    protected void initPreview() {
        preview = new CanvasSE(((Legend) legend).getSymbolizer());
        preview.imageChanged();
    }

    /**
     * Initialize the components. Must be called at the end of the
     * constructor, just before {@link #addComponents()}.
     */
    protected abstract void init();

    /**
     * Add the components to the UI. Must be the last line in the constructor,
     * just after {@link #init()}.
     */
    protected abstract void addComponents();
}
