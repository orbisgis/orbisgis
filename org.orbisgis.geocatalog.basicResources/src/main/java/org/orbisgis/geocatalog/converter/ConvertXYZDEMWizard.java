package org.orbisgis.geocatalog.converter;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.sif.DynamicUIPanel;
import org.sif.SIFDialog;
import org.sif.SIFWizard;
import org.sif.UIFactory;
import org.sif.UIPanel;



public class ConvertXYZDEMWizard {


	
	private FilePanel outfilePanel;	
	private UIXYZDEMPanel uixyzPanel;
	
	private static Map<String, String>  inFormat = new HashMap<String, String>();
	static {
		inFormat.put("xyz", "XYZ DEM (*.xyz)");
		
	}
	
	private static Map<String, String>  outFormat = new HashMap<String, String>();
	static {
		outFormat.put("tif", "TIF with TFW format (*.tif)");
		outFormat.put("png", "PNG with PGW format (*.png)");
		outFormat.put("asc", "Esri ascii grid format (*.asc)");
		
	}
	
	
	
	public UIPanel[] getWizardPanels() {
		
		
		 uixyzPanel = new UIXYZDEMPanel();
		
		outfilePanel = new FilePanel(outFormat);
		
		
		return new UIPanel[] {uixyzPanel, outfilePanel };
	}
	
	
	protected File[] getSelectedInFiles() {
		return outfilePanel.fileChooser.getSelectedFiles();
	}
	
	
	protected File[] getSelectedOutFiles() {
		return outfilePanel.fileChooser.getSelectedFiles();
	}
	
	
	

	public static void main(String[] args) {
		
		//UIFactory.showDialog(new ConvertFileWizard().getWizardPanels());
		
		SIFWizard sifDialog = UIFactory.getWizard(new ConvertXYZDEMWizard().getWizardPanels());
		sifDialog.pack();
		sifDialog.setVisible(true);
		
	}
}
