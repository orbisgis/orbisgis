/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */
package com.vividsolutions.jump.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.util.*;
import javax.swing.*;

import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;

import com.vividsolutions.jts.util.Assert;

import java.awt.*;

/**
 * Flexible generic dialog for prompting the user to type in several values.
 */
public class MultiInputDialog extends JDialog {
	public EnableCheck createDoubleCheck(final String fieldName) {
		return new EnableCheck() {
			public String check(JComponent component) {
				try {
					Double.parseDouble(getText(fieldName).trim());
					return null;
				} catch (NumberFormatException e) {
					return "\"" + getText(fieldName).trim() + "\" "
							+ "is-an-invalid-double" + " (" + fieldName + ")";
				}
			}
		};
	}

	public EnableCheck createIntegerCheck(final String fieldName) {
		return new EnableCheck() {
			public String check(JComponent component) {
				try {
					Integer.parseInt(getText(fieldName).trim());
					return null;
				} catch (NumberFormatException e) {
					return "\"" + getText(fieldName).trim() + "\" "
							+ "is-an-invalid-integer" + " (" + fieldName + ")";
				}
			}
		};
	}

	public EnableCheck createPositiveCheck(final String fieldName) {
		return new EnableCheck() {
			public String check(JComponent component) {
				if (Double.parseDouble(getText(fieldName).trim()) > 0) {
					return null;
				}
				return "\"" + getText(fieldName).trim() + "\" " + "must-be"
						+ " > 0 (" + fieldName + ")";
			}
		};
	}

	public EnableCheck createNonNegativeCheck(final String fieldName) {
		return new EnableCheck() {
			public String check(JComponent component) {
				if (Double.parseDouble(getText(fieldName).trim()) >= 0) {
					return null;
				}
				return "\"" + getText(fieldName).trim() + "\" " + "must-be"
						+ " >= 0 (" + fieldName + ")";
			}
		};
	}

	private final static int SIDEBAR_WIDTH = 150;

	OKCancelPanel okCancelPanel = new OKCancelPanel();

	GridBagLayout gridBagLayout2 = new GridBagLayout();

	JPanel outerMainPanel = new JPanel();

	private HashMap fieldNameToComponentMap = new HashMap();

	private Map buttonGroupMap = new HashMap();

	private JComponent getComponent(String fieldName) {
		return (JComponent) fieldNameToComponentMap.get(fieldName);
	}

	public JComboBox getComboBox(String fieldName) {
		return (JComboBox) getComponent(fieldName);
	}

	public JCheckBox getCheckBox(String fieldName) {
		return (JCheckBox) getComponent(fieldName);
	}

	public JRadioButton getRadioButton(String fieldName) {
		return (JRadioButton) getComponent(fieldName);
	}

	public JComponent getLabel(String fieldName) {
		return (JComponent) fieldNameToLabelMap.get(fieldName);
	}

	private HashMap fieldNameToLabelMap = new HashMap();

	private int rowCount = 0;

	private CollectionMap fieldNameToEnableCheckListMap = new CollectionMap();

	private BorderLayout borderLayout2 = new BorderLayout();

	private JPanel imagePanel = new JPanel();

	private GridBagLayout gridBagLayout3 = new GridBagLayout();

	private JLabel imageLabel = new JLabel();

	private JPanel mainPanel = new JPanel();

	private GridBagLayout mainPanelGridBagLayout = new GridBagLayout();

	private JPanel innerMainPanel = new JPanel();

	private JPanel innerMainPanel2 = new JPanel();

	private GridBagLayout gridBagLayout5 = new GridBagLayout();

	private GridBagLayout gridBagLayout7 = new GridBagLayout();

	private GridBagLayout gridBagLayout6 = new GridBagLayout();

	private JTextArea descriptionTextArea = new JTextArea();

	private JPanel strutPanel = new JPanel();

	private JPanel currentMainPanel = innerMainPanel;

	private JPanel verticalSeparatorPanel = new JPanel();

	private MapContext mapContext;

	/**
	 * @param frame
	 *            the frame on which to make this dialog modal and centred
	 */
	public MultiInputDialog(final Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		imagePanel.setVisible(false);
		descriptionTextArea.setText("");
		imageLabel.setText("");
		innerMainPanel2.setVisible(false);
		verticalSeparatorPanel.setVisible(false);
	}

	public MultiInputDialog() {
		this(null, "", true);
	}

	public void setVisible(boolean visible) {
		// Workaround for Java bug 4446522 " JTextArea.getPreferredSize()
		// incorrect when line wrapping is used": call #pack twice [Jon Aquino]
		pack();
		pack();
		super.setVisible(visible);
	}

	/**
	 * Gets the string value of a control
	 *
	 * @param fieldName
	 *            control to read
	 * @return the string value of the control
	 * @return null if the control is not in a valid state (e.g. not selected)
	 */
	public String getText(String fieldName) {
		if (fieldNameToComponentMap.get(fieldName) instanceof JTextField) {
			return ((JTextField) fieldNameToComponentMap.get(fieldName))
					.getText();
		}
		if (fieldNameToComponentMap.get(fieldName) instanceof JComboBox) {
			Object selObj = ((JComboBox) fieldNameToComponentMap.get(fieldName))
					.getSelectedItem();
			if (selObj == null)
				return null;
			return selObj.toString();
		}
		Assert.shouldNeverReachHere(fieldName);
		return null;
	}

	/**
	 * Returns selected state for checkboxes, radio buttons.
	 *
	 * @param fieldName
	 *            the name of the control to test
	 * @return the selected state of the control
	 */
	public boolean getBoolean(String fieldName) {
		AbstractButton button = (AbstractButton) fieldNameToComponentMap
				.get(fieldName);
		return button.isSelected();
	}

	public double getDouble(String fieldName) {
		return Double.parseDouble(getText(fieldName).trim());
	}

	public int getInteger(String fieldName) {
		return Integer.parseInt(getText(fieldName).trim());
	}

	public ILayer getLayer(String fieldName) {
		JComboBox comboBox = (JComboBox) fieldNameToComponentMap.get(fieldName);
		return getMapContext().getLayerModel().getLayerByName(
				(String) comboBox.getSelectedItem());
	}

	public JTextField addTextField(String fieldName, String initialValue,
			int approxWidthInChars, EnableCheck[] enableChecks,
			String toolTipText) {
		JTextField textField = new JTextField(initialValue, approxWidthInChars);
		addRow(fieldName, new JLabel(fieldName), textField, enableChecks,
				toolTipText);
		return textField;
	}

	public JComboBox addComboBox(String fieldName, Object selectedItem,
			String[] items, String toolTipText) {
		JComboBox comboBox = new JComboBox(items);
		comboBox.setSelectedItem(selectedItem);
		addRow(fieldName, new JLabel(fieldName), comboBox, null, toolTipText);
		return comboBox;
	}

	public JLabel addLabel(String text) {
		// Take advantage of #addRow's special rule for JLabels: they span all
		// the columns of the GridBagLayout. [Jon Aquino]
		JLabel lbl = new JLabel(text);
		addRow(lbl);
		return lbl;
	}

	public JButton addButton(String text) {
		// Take advantage of #addRow's special rule for JLabels: they span all
		// the columns of the GridBagLayout. [Jon Aquino]
		JButton button = new JButton(text);
		addRow(button);
		return button;
	}

	public void addRow(JComponent c) {
		addRow("DUMMY", new JLabel(""), c, null, null);
	}

	public void addSeparator() {
		JPanel separator = new JPanel();
		separator.setBackground(Color.black);
		separator.setPreferredSize(new Dimension(1, 1));
		addRow(separator);
	}

	private JTextField addNumericField(String fieldName, String initialValue,
			int approxWidthInChars, EnableCheck[] enableChecks,
			String toolTipText) {
		JTextField fld = addTextField(fieldName, initialValue,
				approxWidthInChars, enableChecks, toolTipText);
		fld.setHorizontalAlignment(JTextField.RIGHT);
		return fld;
	}

	public JTextField addIntegerField(String fieldName, int initialValue,
			int approxWidthInChars, String toolTipText) {
		return addNumericField(fieldName, String.valueOf(initialValue),
				approxWidthInChars,
				new EnableCheck[] { createIntegerCheck(fieldName) },
				toolTipText);
	}

	public JTextField addPositiveIntegerField(String fieldName,
			int initialValue, int approxWidthInChars) {
		return addNumericField(fieldName, String.valueOf(initialValue),
				approxWidthInChars, new EnableCheck[] {
						createIntegerCheck(fieldName),
						createPositiveCheck(fieldName) }, null);
	}

	public JTextField addDoubleField(String fieldName, double initialValue,
			int approxWidthInChars) {
		return addNumericField(fieldName, String.valueOf(initialValue),
				approxWidthInChars,
				new EnableCheck[] { createDoubleCheck(fieldName) }, null);
	}

	public JTextField addDoubleField(String fieldName, double initialValue,
			int approxWidthInChars, String toolTipText) {
		return addNumericField(fieldName, String.valueOf(initialValue),
				approxWidthInChars,
				new EnableCheck[] { createDoubleCheck(fieldName) }, toolTipText);
	}

	public JTextField addPositiveDoubleField(String fieldName,
			double initialValue, int approxWidthInChars) {
		return addNumericField(fieldName, String.valueOf(initialValue),
				approxWidthInChars, new EnableCheck[] {
						createDoubleCheck(fieldName),
						createPositiveCheck(fieldName) }, null);
	}

	public JTextField addNonNegativeDoubleField(String fieldName,
			double initialValue, int approxWidthInChars) {
		return addNumericField(fieldName, String.valueOf(initialValue),
				approxWidthInChars, new EnableCheck[] {
						createDoubleCheck(fieldName),
						createNonNegativeCheck(fieldName) }, null);
	}

	public JComboBox addLayerComboBox(String fieldName, ILayer initialValue,
			MapContext mapContext) {
		return addLayerComboBox(fieldName, initialValue, null, mapContext);
	}

	public JComboBox addLayerComboBox(String fieldName, ILayer initialValue,
			String toolTipText, MapContext mapContext) {
		setMapContext(mapContext);
		return addLayerComboBox(fieldName, initialValue, toolTipText,
				mapContext.getLayerModel().getLayersRecursively());
	}

	private void setMapContext(MapContext mapContext) {
		this.mapContext = mapContext;
	}

	public MapContext getMapContext() {
		return mapContext;
	}

	/*
	 * public JComboBox addEditableLayerComboBox( String fieldName, ILayer
	 * initialValue, String toolTipText, MapContext mapContext) { return
	 * addLayerComboBox( fieldName, initialValue, toolTipText,
	 * mapContext.getLayerModel().getLayersRecursively()); }
	 */
	public JComboBox addLayerComboBox(String fieldName, ILayer initialValue,
			String toolTipText, ILayer[] layers) {

		String[] layerNames = new String[layers.length];
		for (int i = 0; i < layers.length; i++) {
			layerNames[i] = layers[i].getName();
		}
		addComboBox(fieldName, initialValue.getName(), layerNames, toolTipText);
		return getComboBox(fieldName);
	}

	public JCheckBox addCheckBox(String fieldName, boolean initialValue) {
		return addCheckBox(fieldName, initialValue, null);
	}

	public JCheckBox addCheckBox(String fieldName, boolean initialValue,
			String toolTipText) {
		JCheckBox checkBox = new JCheckBox(fieldName, initialValue);
		addRow(fieldName, new JLabel(""), checkBox, null, toolTipText);
		return checkBox;
	}

	public JRadioButton addRadioButton(String fieldName,
			String buttonGroupName, boolean initialValue, String toolTipText) {
		JRadioButton radioButton = new JRadioButton(fieldName, initialValue);
		addRow(fieldName, new JLabel(""), radioButton, null, toolTipText);

		// add to button group, if specified (and create one if it doesn't
		// exist)
		if (buttonGroupName != null) {
			ButtonGroup group = (ButtonGroup) buttonGroupMap
					.get(buttonGroupName);
			if (group == null) {
				group = new ButtonGroup();
				buttonGroupMap.put(buttonGroupName, group);
			}
			group.add(radioButton);
		}

		return radioButton;
	}

	public void setSideBarImage(Icon icon) {
		// Add imageLabel only if #setSideBarImage is called. Otherwise the
		// margin
		// above the description will be too tall. [Jon Aquino]
		imagePanel.add(imageLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 10, 0, 10), 0, 0));
		imagePanel.setVisible(true);
		imageLabel.setIcon(icon);
	}

	public void setSideBarDescription(String description) {
		imagePanel.setVisible(true);
		descriptionTextArea.setText(description);
	}

	public boolean wasOKPressed() {
		return okCancelPanel.wasOKPressed();
	}

	// Experience suggests that one should avoid using weights when using the
	// GridBagLayout. I find that nonzero weights can cause layout bugs that are
	// hard to track down. [Jon Aquino]
	void jbInit() throws Exception {
		verticalSeparatorPanel.setBackground(Color.black);
		imageLabel.setText("images-goes-here");
		descriptionTextArea.setOpaque(false);
		okCancelPanel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okCancelPanel_actionPerformed(e);
			}
		});
		// LDB: set the default button for Enter to the OK for all
		this.getRootPane().setDefaultButton(okCancelPanel.getButton("OK"));

		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				this_componentShown(e);
			}
		});
		outerMainPanel.setLayout(gridBagLayout6);
		outerMainPanel.setAlignmentX((float) 0.7);
		this.setResizable(true);
		this.getContentPane().setLayout(borderLayout2);
		imagePanel.setBorder(BorderFactory.createEtchedBorder());
		imagePanel.setLayout(gridBagLayout3);
		mainPanel.setLayout(mainPanelGridBagLayout);
		innerMainPanel.setLayout(gridBagLayout5);
		innerMainPanel2.setLayout(gridBagLayout7);
		descriptionTextArea.setEnabled(false);
		descriptionTextArea.setEditable(false);
		descriptionTextArea.setText("description-goes-here");
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setWrapStyleWord(true);
		strutPanel.setMaximumSize(new Dimension(SIDEBAR_WIDTH, 1));
		strutPanel.setMinimumSize(new Dimension(SIDEBAR_WIDTH, 1));
		strutPanel.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 1));
		verticalSeparatorPanel.setPreferredSize(new Dimension(1, 1));
		this.getContentPane().add(okCancelPanel, BorderLayout.SOUTH);
		this.getContentPane().add(outerMainPanel, BorderLayout.CENTER);
		imagePanel.add(descriptionTextArea, new GridBagConstraints(0, 1, 1, 1,
				0.0, 1.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
		imagePanel.add(strutPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		outerMainPanel.add(mainPanel, new GridBagConstraints(2, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		mainPanel.add(innerMainPanel,
				new GridBagConstraints(1, 0, 1, 2, 1.0, 1.0,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10,
								10), 0, 0));
		mainPanel.add(innerMainPanel2,
				new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10,
								10), 0, 0));
		mainPanel.add(verticalSeparatorPanel, new GridBagConstraints(2, 0, 1,
				1, 0.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
		outerMainPanel.add(imagePanel, new GridBagConstraints(1, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		descriptionTextArea.setFont(imageLabel.getFont());
		descriptionTextArea.setDisabledTextColor(imageLabel.getForeground());
	}

	/**
	 * If the dialog contains a single tabbed panel, it looks better to have a 0
	 * inset.
	 */
	public void setInset(int inset) {
		setInset(inset, innerMainPanel);
		setInset(inset, innerMainPanel2);
	}

	private void setInset(int inset, JComponent component) {
		GridBagLayout layout = (GridBagLayout) component.getParent()
				.getLayout();
		GridBagConstraints constraints = layout.getConstraints(component);
		constraints.insets = new Insets(inset, inset, inset, inset);
		layout.setConstraints(component, constraints);
	}

	void okCancelPanel_actionPerformed(ActionEvent e) {
		if (!okCancelPanel.wasOKPressed() || isInputValid()) {
			setVisible(false);
			return;
		}
		reportValidationError(firstValidationErrorMessage());
	}

	void this_componentShown(ComponentEvent e) {
		okCancelPanel.setOKPressed(false);
	}

	private boolean isInputValid() {
		return firstValidationErrorMessage() == null;
	}

	private void reportValidationError(String errorMessage) {
		JOptionPane.showMessageDialog(this, errorMessage, "JUMP",
				JOptionPane.ERROR_MESSAGE);
	}

	private String firstValidationErrorMessage() {
		for (Iterator i = fieldNameToEnableCheckListMap.keySet().iterator(); i
				.hasNext();) {
			String fieldName = (String) i.next();
			for (Iterator j = fieldNameToEnableCheckListMap.getItems(fieldName)
					.iterator(); j.hasNext();) {
				EnableCheck enableCheck = (EnableCheck) j.next();
				String message = enableCheck.check(null);
				if (message != null) {
					return message;
				}
			}
		}
		return null;
	}

	/**
	 * This method can be called once only.
	 */
	public void startNewColumn() {
		if (innerMainPanel2.isVisible()) {
			Assert
					.shouldNeverReachHere("#startNewColumn can be called once only");
		}
		currentMainPanel = innerMainPanel2;
		innerMainPanel2.setVisible(true);
		verticalSeparatorPanel.setVisible(true);
	}

	public void addRow(String fieldName, JComponent label,
			JComponent component, EnableCheck[] enableChecks, String toolTipText) {
		if (toolTipText != null) {
			label.setToolTipText(toolTipText);
			component.setToolTipText(toolTipText);
		}
		fieldNameToLabelMap.put(fieldName, label);
		fieldNameToComponentMap.put(fieldName, component);
		if (enableChecks != null) {
			addEnableChecks(fieldName, Arrays.asList(enableChecks));
		}
		int componentX;
		int componentWidth;
		int labelX;
		int labelWidth;
		if (component instanceof JCheckBox || component instanceof JRadioButton
				|| component instanceof JLabel || component instanceof JPanel) {
			componentX = 1;
			componentWidth = 3;
			labelX = 4;
			labelWidth = 1;
		} else {
			labelX = 1;
			labelWidth = 1;
			componentX = 2;
			componentWidth = 1;
		}
		currentMainPanel.add(label, new GridBagConstraints(labelX, rowCount,
				labelWidth, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 5, 10), 0, 0));
		// HORIZONTAL especially needed by separator. [Jon Aquino]
		currentMainPanel
				.add(
						component,
						new GridBagConstraints(
								componentX,
								rowCount,
								componentWidth,
								1,
								0,
								0.0,
								GridBagConstraints.WEST,
								component instanceof JPanel ? GridBagConstraints.HORIZONTAL
										: GridBagConstraints.NONE, new Insets(
										0, 0, 5, 0), 0, 0));
		rowCount++;
	}

	public void addEnableChecks(String fieldName, Collection enableChecks) {
		fieldNameToEnableCheckListMap.addItems(fieldName, enableChecks);
	}

	public void indentLabel(String comboBoxFieldName) {
		getLabel(comboBoxFieldName).setBorder(
				BorderFactory.createMatteBorder(0, (int) new JCheckBox()
						.getPreferredSize().getWidth(), 0, 0, getLabel(
						comboBoxFieldName).getBackground()));
	}
}
