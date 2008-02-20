package org.orbisgis.geoview.views.outputView;



import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class OutputPanel extends JPanel {

	
	private String output;
	private JTextArea jTextArea;

	public OutputPanel(){
		this.setLayout(new BorderLayout());
		 jTextArea = new JTextArea();
		 this.add(new JScrollPane(jTextArea));
		
	}
	
	public void setOutput(String output){
		
		this.output = output;
		
		Date date = new Date( System.currentTimeMillis() );
		SimpleDateFormat sdf = new SimpleDateFormat( "dd/MM/yyyy hh:mm:ss" );		
		
		jTextArea.insert((sdf.format(date) + " : " + output), jTextArea.getCaretPosition());
		jTextArea.requestFocus();
		
	}

	protected String getOutput() {
		return output;
	}
	
	
}
