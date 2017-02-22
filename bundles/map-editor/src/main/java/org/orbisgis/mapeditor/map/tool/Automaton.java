/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.mapeditor.map.tool;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Observer;
import javax.swing.ImageIcon;
import org.orbisgis.coremap.layerModel.MapContext;

public interface Automaton extends Observer {

        public static enum Status{
                CANCEL,
                DONE,
                LINE,
                MAKE_MOVE,
                MOUSE_DOWN,
                MOUSE_RELEASED,
                MOVEMENT,
                ONE_POINT,
                ONE_POINT_LEFT,
                POINT,
                POINT_WITH_SELECTION,
                RECTANGLE_DONE,
                SELECTION,
                STANDBY,
                THREE_POINTS,
                TWO_POINTS;
        }
        public static enum Code {
                EMPTY,
                ESC,
                FINISHED,
                INIT,
                IN_HANDLER,
                L,
                NO_SELECTION,
                OUT_HANDLER,
                P,
                POINT,
                PRESS,
                RELEASE,
                SELECTION,
                TERMINATE;
        }

    /**
     * Initialisation of the tool, done when the tool is set on the ToolManager {@link ToolManager#setTool(Automaton)}
     * @param vc
     * @param tm
     * @throws TransitionException
     * @throws FinishedAutomatonException
     */
	public void init(MapContext vc, ToolManager tm) throws TransitionException,
			FinishedAutomatonException;

    /**
     * Get the label of Automaton Popup menu
     * @return The translated message
     */
	public String[] getTransitionLabels();

    /**
     * @return The code that correspond to Popup menu
     */
	public Code[] getTransitionCodes();

	public void transition(Code code) throws NoSuchTransitionException,
			TransitionException, FinishedAutomatonException;

	public void draw(Graphics g) throws DrawingException;

        /**
         * The translated message of the ToolTip
         * @return 
         */
	public String getTooltip();
        /**
         * Short translate name of the Tool
         * @return 
         */
	public String getName();

        /**
         * Automaton Icon representation
         * @return 
         */
	public ImageIcon getImageIcon();
        
	/**
	* Null to use the cross cursor
        * @return ImageIcon
	*/
        public ImageIcon getCursor();

	public boolean isEnabled(MapContext vc, ToolManager tm);

	public boolean isVisible(MapContext vc, ToolManager tm);

	public void toolFinished(MapContext vc, ToolManager tm)
			throws NoSuchTransitionException, TransitionException,
			FinishedAutomatonException;

	public Point getHotSpotOffset();

    /**
     * Help the user by displaying the next action waiting by the automaton.
     * @return Translated message
     */
    String getMessage();

}
