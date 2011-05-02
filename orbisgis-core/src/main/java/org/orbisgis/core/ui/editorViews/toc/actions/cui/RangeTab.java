package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;

/**
 * Element of the range table
 * @author sennj
 */
class RangeTab {

    private Color color;
    private Double valueMin;
    private Double valueMax;
    private int nbElem;
    private String alias;

    /**
     * RangeTab Constructor
     * @param color the table elem color
     * @param valueMin the min value of the range
     * @param valueMax the max value of the range
     * @param alias the table elem alias
     */
    public RangeTab(Color color, Double valueMin, Double valueMax, int nbElem, String alias) {
        this.color = color;
        this.valueMin = valueMin;
        this.valueMax = valueMax;
        this.nbElem=nbElem;
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

   /**
    * getValueMax
    * @return the element max value
    */
    public Double getValueMax() {
        return valueMax;
    }

    /**
    * setValueMax
    * @param the element max value
    */
    public void setValueMax(Double valueMax) {
        this.valueMax = valueMax;
    }

    /**
    * getValueMin
    * @return the element min value
    */
    public Double getValueMin() {
        return valueMin;
    }

    /**
    * setValueMin
    * @param the element min value
    */
    public void setValueMin(Double valueMin) {
        this.valueMin = valueMin;
    }

    /**
     * getNbElem
     * @return the number of element
     */
    public int getNbElem() {
        return nbElem;
    }

    /**
     * setNbElem
     * @param nbElem the number of element
     */
    public void setNbElem(int nbElem) {
        this.nbElem = nbElem;
    }
}
