/*
 * IPanelUI.java
 *
 * Created on 22 de febrero de 2008, 8:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.orbisgis.editorViews.toc.actions.cui.gui;

import java.awt.Component;

import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.LegendListDecorator;
import org.orbisgis.renderer.legend.Legend;

/**
 * 
 * @author zamarripa_ser
 */
public interface ILegendPanelUI {
	public Component getComponent();

	public String toString();

	public String getInfoText();

	public String getTitle();

	public String getIdentity();

	public void setIdentity(String id);

	public Legend getLegend();

	public void setDecoratorListener(LegendListDecorator dec);
}
