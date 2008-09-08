package org.orbisgis.views.europeGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import org.orbisgis.editors.map.MapControl;

public class EuropePanel extends JPanel {
	// Toolbar button names
	public static String SELECTION_TOOL_NAME = "Select";
	public static String ZOOM_IN_TOOL_NAME = "Zoom In";
	public static String ZOOM_OUT_TOOL_NAME = "Zoom Out";
	public static String PAN_TOOL_NAME = "Move Map";
	public static String FULL_EXTENT_TOOL_NAME = "Show All Europe";

	// Toolbar icons
	private static final String IMAGE_RESOURCE = "../../images/";
	private static final Icon SELECTION_BUTTON = new ImageIcon(EuropeGame.class
			.getResource(IMAGE_RESOURCE + "select.png"));
	private static final Icon PAN_BUTTON = new ImageIcon(EuropeGame.class
			.getResource(IMAGE_RESOURCE + "pan.png"));
	private static final Icon ZOOM_IN_BUTTON = new ImageIcon(EuropeGame.class
			.getResource(IMAGE_RESOURCE + "zoom_in.png"));
	private static final Icon ZOOM_OUT_BUTTON = new ImageIcon(EuropeGame.class
			.getResource(IMAGE_RESOURCE + "zoom_out.png"));
	private static final Icon FULL_EXTENT_BUTTON = new ImageIcon(
			EuropeGame.class.getResource(IMAGE_RESOURCE + "world.png"));

	// Interface
	private JLabel label;
	private JToggleButton zoomInButton, zoomOutButton, fullExtentButton,
			selectionButton, panButton;

	// Reference to the container
	private EuropeGame europeGame;

	/**
	 * Creates a new EuropePanel
	 * 
	 * @param eg
	 *            The class container of this panel
	 */
	public EuropePanel(EuropeGame eg) {
		europeGame = eg;

		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.CENTER);
		JPanel south = new JPanel();
		south.add(label);
		south.setBorder(BorderFactory.createEtchedBorder());

		zoomInButton = createButton(ZOOM_IN_BUTTON, ZOOM_IN_TOOL_NAME);
		zoomOutButton = createButton(ZOOM_OUT_BUTTON, ZOOM_OUT_TOOL_NAME);
		panButton = createButton(PAN_BUTTON, PAN_TOOL_NAME);
		fullExtentButton = createButton(FULL_EXTENT_BUTTON,
				FULL_EXTENT_TOOL_NAME);
		selectionButton = createButton(SELECTION_BUTTON, SELECTION_TOOL_NAME);
		selectionButton.setSelected(true);

		JPanel north = new JPanel();
		north.add(selectionButton);
		north.add(panButton);
		north.add(fullExtentButton);
		north.add(zoomInButton);
		north.add(zoomOutButton);
		north.setBorder(BorderFactory.createEtchedBorder());

		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setVgap(10);
		borderLayout.setHgap(10);
		setLayout(borderLayout);
		add(south, BorderLayout.SOUTH);
		add(north, BorderLayout.NORTH);
		setBackground(Color.white);
	}

	/**
	 * Sets the model of this panel
	 * 
	 * @param mapControl
	 *            the map control to show in the center of the panel
	 * @param country
	 *            the country to find
	 */
	public void setModel(MapControl mapControl, String country) {
		mapControl.getInsets().bottom = 20;
		add(mapControl, BorderLayout.CENTER);
		label.setText("Where is " + country + "?");
	}

	/**
	 * Updates the country to find in the bottom label
	 * 
	 * @param country
	 *            the country to find
	 */
	public void updateCountry(String country) {
		label.setText("Where is " + country + "?");
	}

	/**
	 * Creates a new button with the given icon and tooltip
	 * 
	 * @param icon
	 *            the icon of the button
	 * @param tooltip
	 *            the tooltip of the button
	 * @return the JToggleButton created
	 */
	private JToggleButton createButton(Icon icon, String tooltip) {
		final JToggleButton button = new JToggleButton(icon);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonClicked(button);
			}
		});

		button.setToolTipText(tooltip);
		Dimension size = button.getPreferredSize();
		button.setPreferredSize(new Dimension(size.height, size.height));
		button
				.setBorder(BorderFactory
						.createEtchedBorder(EtchedBorder.LOWERED));

		return button;
	}

	/**
	 * Updates the toolbar and activates the tool in the EuropeGame
	 * 
	 * @param clicked
	 *            the clicked button
	 */
	private void buttonClicked(JToggleButton clicked) {
		if (clicked == fullExtentButton) {
			selectionButton.setSelected(false);
			zoomInButton.setSelected(false);
			zoomOutButton.setSelected(false);
			panButton.setSelected(false);

			if (europeGame.isActiveTool(SELECTION_TOOL_NAME)) {
				selectionButton.setSelected(true);
			} else if (europeGame.isActiveTool(ZOOM_IN_TOOL_NAME)) {
				zoomInButton.setSelected(true);
			} else if (europeGame.isActiveTool(ZOOM_OUT_TOOL_NAME)) {
				zoomOutButton.setSelected(true);
			} else if (europeGame.isActiveTool(PAN_TOOL_NAME)) {
				panButton.setSelected(true);
			}

			europeGame.fullExtent();
		} else {
			if (!clicked.isSelected()) {
				selectionButton.setSelected(true);
				zoomInButton.setSelected(false);
				zoomOutButton.setSelected(false);
				panButton.setSelected(false);
			} else {
				if (clicked == selectionButton) {
					europeGame.activateTool(SELECTION_TOOL_NAME);
				} else if (clicked == panButton) {
					europeGame.activateTool(PAN_TOOL_NAME);
				} else if (clicked == zoomInButton) {
					europeGame.activateTool(ZOOM_IN_TOOL_NAME);
				} else if (clicked == zoomOutButton) {
					europeGame.activateTool(ZOOM_OUT_TOOL_NAME);
				}

				selectionButton.setSelected(clicked == selectionButton);
				zoomInButton.setSelected(clicked == zoomInButton);
				zoomOutButton.setSelected(clicked == zoomOutButton);
				panButton.setSelected(clicked == panButton);
			}
		}
		fullExtentButton.setSelected(false);
		requestFocus();
	}
}
