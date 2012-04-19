/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend;

/**
 * {@code LegendStructure} instances that are associated to numeric values have to
 * realizes this interface rather than {@code LegendStructure}. Concretely, they will
 * match the deepest elements of the legend (the values of the fields, and the
 * functions that are applied on it).
 * @author alexis
 */
public interface NumericLegend extends LegendStructure {

}
