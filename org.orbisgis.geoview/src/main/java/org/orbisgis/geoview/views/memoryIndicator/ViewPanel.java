package org.orbisgis.geoview.views.memoryIndicator;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ViewPanel extends JPanel {
	public ViewPanel() {
		final Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				ViewPanel.this.repaint();
			}
		}, 500, 500);
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Runtime runtime = Runtime.getRuntime();
		final long maxMemory = runtime.maxMemory() / 1024;
		final long allocatedMemory = runtime.totalMemory() / 1024;
		final long freeMemory = runtime.freeMemory() / 1024;
		final long totalFreeMemory = freeMemory + (maxMemory - allocatedMemory);
		final long memoryUsed = maxMemory - totalFreeMemory;

		final int pos = (int) (memoryUsed * getHeight() / maxMemory);
		g.setColor(new Color(32, 128, 32));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(new Color(230, 0, 0));
		g.fillRect(0, getHeight() - pos, getWidth(), pos);

		g.setColor(Color.yellow);
		g.drawString(maxMemory / 1024 + "MB", 10, 20);
		g.drawString(memoryUsed / 1024 + "MB", 10, getHeight() - 20);
	}

	public static void main(String[] args) {
		final JFrame frm = new JFrame();
		frm.getContentPane().add(new ViewPanel());
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setSize(100, 200);
		frm.setVisible(true);
	}
}