/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.view.toc.actions.cui.legend;

import java.awt.Component;
import org.orbisgis.view.toc.actions.cui.LegendContext;

/**
 *
 * @author alexis
 */
public interface ISELegendPanel {

	/**
	 * This function will return the Component of the object (normally a
	 * JPanel).
	 * 
	 * @return Component
	 */
	Component getComponent();

	/**
	 * Initialize the legend. This method is called just after the legend
	 * creation.
	 *
	 * @param lc
	 *            LegendContext is useful to get some information about the
	 *            layer in edition.
	 */
	void initialize(LegendContext lc);

	/**
	 * Creates a new empty instance of this panel.
	 *
	 * @return
	 */
	ISELegendPanel newInstance();

        /**
         * Gets the identifier of this panel.
         * @return
         */
        String getId();

        /**
         * Associates an identifier to this panel. Particularly useful if we
         * want to put this panel in a {@code CardLayout}.
         * @param newId
         */
        void setId(String newId);

	/**
	 * @return {@code null} if the status of the edited legend is ok. An error message
	 *         if the legend cannot be created
	 */
	String validateInput();

}
