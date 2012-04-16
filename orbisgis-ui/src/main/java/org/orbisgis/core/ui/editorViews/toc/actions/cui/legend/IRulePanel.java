/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legend;

import java.awt.Component;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendContext;

/**
 *
 * @author alexis
 */
public interface IRulePanel {

	/**
	 * This function will return the Component of the object (normally a
	 * JPanel).
	 * 
	 * @return Component
	 */
	public Component getComponent();

	/**
	 * Initialize the legend. This method is called just after the legend
	 * creation.
	 *
	 * @param lc
	 *            LegendContext is useful to get some information about the
	 *            layer in edition.
	 */
	public void initialize(LegendContext lc);

	/**
	 * Creates a new empty instance of this panel.
	 *
	 * @return
	 */
	public IRulePanel newInstance();
}
