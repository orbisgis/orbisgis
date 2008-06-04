package org.orbisgis.processing.editorViews.toc.actions.nodata;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.sif.SQLUIPanel;
import org.sif.multiInputPanel.DoubleType;
import org.sif.multiInputPanel.InputType;

public class NullableDoubleType implements InputType,  ActionListener{

	private JPanel panel = new JPanel();
	private JCheckBox jCheckBox = new JCheckBox();
	private JTextField jTextField = new JTextField();
	
	
	
	public NullableDoubleType(String text, int columns){
		jCheckBox.setSelected(false);
		jCheckBox.addActionListener(this);
		jTextField.setColumns(columns);
		jTextField.setEnabled(false);
		panel.add(jCheckBox);
		panel.add(jTextField);
		
	}
	
	public Component getComponent() {
		
		return panel;
	}

	public int getType() {
		
		return SQLUIPanel.DOUBLE;
	}

	public String getValue() {
		
		if (jCheckBox.isEnabled()){
			return jTextField.getText();
		}
		return null;
	}

	public boolean isPersistent() {
		return true;
	}

	public void setValue(String value) {
		jCheckBox.setSelected(value!=null);
		jTextField.setText(value);
		
	}
	
	public void actionPerformed(ActionEvent e) {
	    if ("disable".equals(e.getActionCommand())) {
	    	jTextField.setEnabled(false);
	       
	    } else {
	    	jTextField.setEnabled(true);
	     
	    }
	}


}
