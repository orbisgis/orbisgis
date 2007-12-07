package org.orbisgis.geocatalog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.Timer;

import org.orbisgis.core.windows.EPWindowHelper;
import org.orbisgis.core.windows.IWindow;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.SystemListener;

public class ErrorButton extends JButton {

	private Color original;
	private Timer timer;

	private int blinks;

	public ErrorButton(String text) {
		super(text);
		PluginManager.addSystemListener(new SystemListener() {

			public void warning(String userMsg, Throwable e) {
				startBlinking();
			}

			public void statusChanged() {
			}

			public void error(String userMsg, Throwable exception) {
				startBlinking();
			}

		});
		this.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				IWindow[] wnds = EPWindowHelper
						.getWindows("org.orbisgis.core.ErrorWindow");
				IWindow wnd;
				if (wnds.length == 0) {
					wnd = EPWindowHelper
							.createWindow("org.orbisgis.core.ErrorWindow");
				} else {
					wnd = wnds[0];
				}
				wnd.showWindow();
				stopBlinking();
			}

		});

	}

	private void startBlinking() {
		timer = new Timer(500, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (ErrorButton.this.getBackground() == Color.blue) {
					ErrorButton.this.setBackground(original);
				} else {
					original = ErrorButton.this.getBackground();
					ErrorButton.this.setBackground(Color.blue);
				}
				blinks--;
				if (blinks == 0) {
					stopBlinking();
				}
			}

		});
		timer.start();
		blinks = 20;
	}

	private void stopBlinking() {
		if ((timer != null) && timer.isRunning()) {
			timer.stop();
			if (original != null) {
				setBackground(original);
			}
		}
	}
}
