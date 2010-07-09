/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se;

import javax.swing.JPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.EditFeatureTypeStylePanel;

/**
 *
 * @author maxence
 */
public interface PanelableNode {
	public abstract JPanel getEditionPanel(EditFeatureTypeStylePanel ftsPanel);
}
