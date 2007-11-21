/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.orbisgis.tools;

import java.awt.Graphics;
import java.awt.Point;
import java.net.URL;

public interface Automaton {
    public void init(ViewContext vc, ToolManager tm) throws TransitionException, FinishedAutomatonException;
	public String[] getTransitionLabels();
	public String[] getTransitionCodes();
	public void transition(String code) throws NoSuchTransitionException, TransitionException, FinishedAutomatonException;
	public void draw(Graphics g) throws DrawingException;
    public String getMessage();
    public String getConsoleCommand();
    public String getTooltip();
    public String getName();
    /**
     * Null to use the cross cursor
     */
    public URL getMouseCursorURL();
    public boolean isEnabled(ViewContext vc, ToolManager tm);
    public boolean isVisible(ViewContext vc, ToolManager tm);
    public void toolFinished(ViewContext vc, ToolManager tm) throws NoSuchTransitionException, TransitionException, FinishedAutomatonException;
    public Point getHotSpotOffset();
}
