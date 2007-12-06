package org.orbisgis.geocatalog.converter;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class XYZDEMPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final String EOL = System.getProperty("line.separator"); // @jve:decl-index=0:
	

	private JEditorPane jTextArea = null;

	private JLabel jLabel = null;

	private JTextField pixelSizeField = null;


	public XYZDEMPanel() {

		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(484, 280);
		
		jLabel = new JLabel();
		jLabel.setBounds(new Rectangle(142, 190, 120, 26));
		jLabel.setText("Pixel size :");

		this.setLayout(null);
		this.add(getJTextArea(), null);
		this.add(jLabel, null);
		this.add(getPixelSizeField(), null);
		
	}

	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JEditorPane getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JEditorPane();
			jTextArea.setBounds(new Rectangle(7, 9, 473, 152));
			jTextArea.setEditable(false);
			// jTextArea.setLineWrap(true);
			jTextArea.setBackground(Color.lightGray);
			jTextArea
					.setText("This plugin imports X,Y,Z coordinates of (usually irregularly distributed) points from the first 3 columns of a plain text file "
							+ "and interpolates a Digital Elevation "
							+ EOL
							+ "Model (DEM) image or Digital Terrain Model (DTM) image."
							+ " The given points are projected to the X,Y-plane and are meshed by a  2D-Delaunay triangulation. For each image pixel position, a signed 32-bit floating-point pixel value Z=Z(X,Y) is calculated by linear interpolation within the corresponding triangle. Pixel positions outside"
							+ " the convex hull get a user chosen background value. The detection of occluded surface areas is not supported. It is recommended to use the TIFF file format for 32-bit images.");
		}
		return jTextArea;
	}

	/**
	 * This method initializes jTextField1
	 * 
	 * @return javax.swing.JTextField
	 */
	JTextField getPixelSizeField() {
		if (pixelSizeField == null) {
			pixelSizeField = new JTextField();
			pixelSizeField.setBounds(new Rectangle(276, 190, 50, 26));

		}
		return pixelSizeField;
	}
	

	
	public float getPixelSize() {

		return new Float(pixelSizeField.getText());
	}

}
