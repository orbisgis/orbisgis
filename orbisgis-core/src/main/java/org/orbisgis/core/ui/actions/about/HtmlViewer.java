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
package org.orbisgis.core.ui.actions.about;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.orbisgis.core.Services;
import org.orbisgis.sif.UIPanel;

public class HtmlViewer extends JPanel implements HyperlinkListener,
		ActionListener,UIPanel {
	final JEditorPane viewer = new JEditorPane();

	public HtmlViewer(final URL url) {
		if (null == url) {
			throw new IllegalArgumentException("null url");
		} else {
			this.setLayout(new BorderLayout());
			final JScrollPane scrollPane = new JScrollPane(viewer);
			add(scrollPane, BorderLayout.CENTER);

			viewer.setEditable(false);
			viewer.addHyperlinkListener(this);
			loadPage(url);
		}
	}

	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			if (event instanceof HTMLFrameHyperlinkEvent) {
				final HTMLDocument doc = (HTMLDocument) viewer.getDocument();
				doc
						.processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent) event);
			} else
				loadPage(event.getURL());
		}
	}

	public void actionPerformed(ActionEvent event) {
	}

	public void loadPage(final URL url) {
		try {
			viewer.setPage(url);
		} catch (IOException ex) {
			Services.getErrorManager().warning("Resource is not available !", ex);
		}
	}

	public Component getComponent() {
		return this;
	}

	public URL getIconURL() {
		return null;
	}

	public String getInfoText() {
		return null;
	}

	public String initialize() {
		return null;
	}

	public String validateInput() {
		return null;
	}

	public String getTitle() {
		return "About OrbisGIS";
	}

	public String postProcess() {
		return null;
	}
}