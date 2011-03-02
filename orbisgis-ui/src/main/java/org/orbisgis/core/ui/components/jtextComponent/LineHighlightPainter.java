/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *  
 *  Lead Erwan BOCHER, scientific researcher, 
 *
 *  Developer lead : Pierre-Yves FADET, computer engineer. 
 *  
 *  User support lead : Gwendall Petit, geomatic engineer. 
 * 
 * Previous computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 * 
 * or contact directly: 
 * erwan.bocher _at_ ec-nantes.fr 
 * Pierre-Yves.Fadet _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/

package org.orbisgis.core.ui.components.jtextComponent;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class LineHighlightPainter implements Highlighter.HighlightPainter {

	// paint a thick line under one line of text, from r extending rightward to
	// x2
	private void paintLine(Graphics g, Rectangle r, int x2) {
		int ytop = r.y + r.height - 3;
		g.fillRect(r.x, ytop, x2 - r.x, 3);
	}

	// paint thick lines under a block of text
	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {

		Rectangle r0 = null, r1 = null, rbounds = bounds.getBounds();
		int xmax = rbounds.x + rbounds.width; // x coordinate of right edge
		try { // convert positions to pixel coordinates
			r0 = c.modelToView(p0);
			r1 = c.modelToView(p1);
		} catch (BadLocationException ex) {
			return;
		}
		if ((r0 == null) || (r1 == null))
			return;

		g.setColor(c.getSelectionColor());

		// special case if p0 and p1 are on the same line
		if (r0.y == r1.y) {
			paintLine(g, r0, r1.x);
			return;
		}

		// first line, from p1 to end-of-line
		paintLine(g, r0, xmax);

		// all the full lines in between, if any (assumes that all lines have
		// the same height--not a good assumption with JEditorPane/JTextPane)
		r0.y += r0.height; // move r0 to next line
		r0.x = rbounds.x; // move r0 to left edge
		while (r0.y < r1.y) {
			paintLine(g, r0, xmax);
			r0.y += r0.height; // move r0 to next line
		}

		// last line, from beginning-of-line to p1
		paintLine(g, r0, r1.x);
	}
}