package org.sif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class MsgPanel extends JPanel  {

	private CRFlowLayout layout = new CRFlowLayout();
	private JLabel msg = new JLabel();
	private JLabel title;
	private ImageIcon image;

	/**
	 * This is the default constructor
	 */
	public MsgPanel(URL image) {
		if (image != null) {
			URL url = image;
			this.image = new ImageIcon(url);
		}
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private  void initialize() {
		JPanel central = new JPanel() {

			@Override
			protected void paintComponent(Graphics g) {
				int width = getWidth();
				int height = getHeight();

				double ax = width / 255.0;
				double lastX = 0;
				for (int i = 0; i < 255; i++) {
					g.setColor(new Color(255, 255, 255 - i));
					g.fillRect((int) lastX, 0, (int) (lastX + ax), height);
					lastX = lastX + ax;
				}
			}

		};
		central.setLayout(layout);
		central.setBackground(Color.white);
		central.setPreferredSize(new Dimension(200, 50));

		title = new JLabel();
		title.setFont(Font.decode("Arial-BOLD-14"));
		central.add(title);
        central.add(new CarriageReturn());
		msg.setHorizontalTextPosition(SwingConstants.CENTER);
		msg.setFont(Font.decode("Arial-13"));
		central.add(msg);
		layout.setAlignment(CRFlowLayout.LEFT);

		this.setLayout(new BorderLayout());
		this.add(new JLabel(image), BorderLayout.WEST);
        this.add(central, BorderLayout.CENTER);
	}

	/* (non-Javadoc)
	 * @see org.prueba.IMsgPanel#setText(java.lang.String)
	 */
	public void setText(String text){
		msg.setText(text);
	}

	/* (non-Javadoc)
	 * @see org.prueba.IMsgPanel#setTitle(java.lang.String)
	 */
	public void setTitle(String text){
		title.setText(text);
	}

	public int getImageHeight() {
		return image.getImage().getHeight(null);
	}

}
