/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;

/**
 *
 * @author sennj
 */
class JSE_RangeTab {

    private Color color;
    private Double valueMin;
    private Double valueMax;
    private String alias;

    public JSE_RangeTab(Color color, Double valueMin, Double valueMax, String alias) {
        this.color = color;
        this.valueMin = valueMin;
        this.valueMax = valueMax;
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Double getValueMax() {
        return valueMax;
    }

    public void setValueMax(Double valueMax) {
        this.valueMax = valueMax;
    }

    public Double getValueMin() {
        return valueMin;
    }

    public void setValueMin(Double valueMin) {
        this.valueMin = valueMin;
    }
}
