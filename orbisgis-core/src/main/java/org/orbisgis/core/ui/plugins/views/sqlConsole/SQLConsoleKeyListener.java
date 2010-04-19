/**
 *
 */
package org.orbisgis.core.ui.plugins.views.sqlConsole;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.text.JTextComponent;

import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;

public class SQLConsoleKeyListener extends KeyAdapter {

	private JTextComponent txt;

	public SQLConsoleKeyListener(JTextComponent txt) {
		this.txt = txt;
	}

	@Override
	public void keyPressed(KeyEvent e) {

		String originalText = txt.getText();
		if ((e.getKeyCode() == KeyEvent.VK_ALT) && e.isControlDown()) {
			BackgroundManager bm = (BackgroundManager) Services
					.getService(BackgroundManager.class);
			bm.backgroundOperation(new ExecuteScriptProcess(originalText));

		} else if ((e.getKeyCode() == KeyEvent.VK_S) && e.isControlDown()
				&& e.isShiftDown()) {
			try {
				final SaveFilePanel outfilePanel = new SaveFilePanel(
						"org.orbisgis.core.ui.views.sqlConsoleOutFile",
						"Save script");
				outfilePanel.addFilter("sql", "SQL script (*.sql)");

				if (UIFactory.showDialog(outfilePanel)) {
					final BufferedWriter out = new BufferedWriter(
							new FileWriter(outfilePanel.getSelectedFile()));
					out.write(originalText);
					out.close();
				}
			} catch (IOException e1) {
				Services.getErrorManager().error(
						"Cannot save code completion test case", e1);
			}
		}
	}

}