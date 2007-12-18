package org.orbisgis.geocatalog.tools.about;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.orbisgis.pluginManager.PluginManager;

public class HtmlViewer extends JFrame implements HyperlinkListener,
		ActionListener {
	final JEditorPane viewer = new JEditorPane();

	public HtmlViewer(final URL url) {
		if (null == url) {
			throw new RuntimeException("BUG");
		} else {
			final JScrollPane scrollPane = new JScrollPane(viewer);
			getContentPane().add(scrollPane, BorderLayout.CENTER);

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
			PluginManager.warning("Resource is not available !", ex);
		}
	}
}