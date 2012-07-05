package org.orbisgis.view.toc.actions.cui.choropleth.gui;

import java.awt.Color;

/**
 * Element of the range table
 * @author sennj
 */
class RangeTab {

    /** The table element color*/
    private Color color;
    /** The table element alias label*/
    private String alias;

    /**
     * RangeTab Constructor
     * @param color the table element color
     * @param alias the table element alias
     */
    public RangeTab(Color color, String alias) {
        this.color = color;
        this.alias = alias;
    }

    /**
     * Get the alias
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Set the alias
     * @param alias the element alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Get the color
     * @return the element color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the color
     * @param color the element color
     */
    public void setColor(Color color) {
        this.color = color;
    }
}
