/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.sif;

import java.awt.Component;
import java.net.URL;

/**
 * Interface that provides the necessary information to the SIF framework to
 * show dialogs and perform some validation
 * 
 * @author Fernando Gonzalez Cortes
 */
public interface UIPanel {

	/**
	 * Gets the icon of the UIPanel. If this method returns null, the default
	 * icon in {@link UIFactory} is used
	 * 
	 * @return
	 */
	URL getIconURL();

	/**
	 * Gets the title to show in the dialog
	 * 
	 * @return
	 */
	String getTitle();

	/**
	 * Performs any initialization. This method is called before the dialog is
	 * shown. In a wizard context, all previous steps have been accomplished
	 * 
	 * @return An error description if something goes wrong or null if
	 *         everything is ok
	 */
	String initialize();

	/**
	 * When the user accepts the dialog or wizard this method is called to make
	 * a last validation before closing the dialog or going to next step in the
	 * wizard. If the user cancels this method is not called
	 * 
	 * @return An error description if the validation fails or null if
	 *         everything is ok
	 */
	String postProcess();

	/**
	 * A method invoked regularly to validate the contents of the interface
	 * 
	 * @return An error description if the validation fails or null if
	 *         everything is ok
	 */
	String validateInput();

	/**
	 * Gets the swing component to show in the dialog
	 * 
	 * @return
	 */
	Component getComponent();

	/**
	 * Gets a text to be shown as tool tip for the dialog. Typically this will
	 * be the purpose of the dialog or some similar information
	 * 
	 * @return
	 */
	String getInfoText();

}
