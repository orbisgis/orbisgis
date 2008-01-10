package org.orbisgis.geoview.views.memoryIndicator;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ViewPanel extends JPanel {

	public ViewPanel() {
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				ViewPanel.this.repaint();
			}

		}, 3000, 3000);

	}

	@Override
	protected void paintComponent(Graphics g) {
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		freeMemory = freeMemory / 1024;
		allocatedMemory = allocatedMemory / 1024;
		maxMemory = maxMemory / 1024;
		long totalFreeMemory = freeMemory + (maxMemory - allocatedMemory);

		int pos = (int) ((maxMemory - totalFreeMemory) * getHeight() / maxMemory);
		g.setColor(new Color(32, 128, 32));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(new Color(230, 0, 0));
		g.fillRect(0, getHeight() - pos, getWidth(), pos);

		g.setColor(Color.yellow);
		g.drawString(maxMemory/1024 + "Mb", 10, 20);
		g.drawString("0Mb", 10, getHeight() - 20);
	}

	public static void main(String[] args) {
		JFrame frm = new JFrame();
		frm.getContentPane().add(new ViewPanel());
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setSize(100, 200);
		frm.setVisible(true);
	}

}
