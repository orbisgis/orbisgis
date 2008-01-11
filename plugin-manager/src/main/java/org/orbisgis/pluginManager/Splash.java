package org.orbisgis.pluginManager;

//Upadates: 2004.04.02, 2004.01.09

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A splash screen to show while the main program is loading. A typical use is:
 * 
 * <pre>
 * public static void main(String[] args) {
 * 	Splash s = new Splash(delay1);
 * 	new MainProgram();
 * 	s.dispose(delay2);
 * }
 * </pre>
 * 
 * The first line creates a Splash that will appear until another frame hides it
 * (MainProgram), but at least during "delay1" milliseconds.<br>
 * To distroy the Splash you can either call "s.dispose()" or
 * "s.dispose(delay2)", that will actually show the Splash for "delay2"
 * milliseconds and only then hide it.<br>
 * The picture to show must be in a file called "splash.png".
 */
public class Splash extends JFrame {

	/**
	 * Creates a Splash that will appear until another frame hides it, but at
	 * least during "delay" milliseconds.
	 * 
	 * @param delay
	 *            the delay in milliseconds
	 */
	public Splash() {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		Image image = new ImageIcon(getClass().getResource("splashball.png"))
				.getImage();
		p.add(new SplashPicture(image), BorderLayout.CENTER);
		p.add(new JLabel(getVersion()), BorderLayout.SOUTH);
		p.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		getContentPane().add(p);
		setSize(500, 169);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setVisible(true);
	}

	public static String getVersion() {
		return "Version 1-0b2 - IRSTV CNRS-FR-2488";
	}

	/**
	 * This class loads and shows a picture, that can be either in the same jar
	 * file than the program or not. If the picture is smaller than the
	 * available space, it will be centered. If the picture is bigger than the
	 * available space, a zoom will be applied in order to fit exactly the
	 * space.
	 */
	class SplashPicture extends JPanel {
		Image img;

		public SplashPicture(Image image) {
			img = image;
			repaint();
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (img == null)
				return;
			int w = img.getWidth(this);
			int h = img.getHeight(this);
			boolean zoom = (w > getWidth() || h > getHeight());
			if (zoom)
				g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
			else
				g.drawImage(img, (getWidth() - w) / 2, (getHeight() - h) / 2,
						this);
		}
	}
}