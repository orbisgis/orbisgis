package org.orbisgis.view.toc.actions.cui.choropleth.gui;

import java.awt.Color;

/**
 * Element of the range table
 * @author sennj
 */
class RangeTab {

    private Color color;
    private String alias;

    /**
     * RangeTab Constructor
     * @param color the table element color
     * @param valueMin the minimal value of the range
     * @param valueMax the max value of the range
     * @param delta the delta value of the range
     * @param alias the table element alias
     */
    public RangeTab(Color color, String alias) {
        this.color = color;
        this.alias = alias;
    }

    /**
     * getAlias
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * setAlias
     * @param alias the element alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * getColor
     * @return the element color
     */
    public Color getColor() {
        return color;
    }

    /**
     * setColor
     * @param color the element color
     */
    public void setColor(Color color) {
        this.color = color;
    }
}
