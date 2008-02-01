package org.orbisgis.geoview.renderer.classification;

import java.util.Arrays;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;

public class ProportionnalMethod {

	
	
	
	private DataSource ds;
	private String fieldName;
	private int rowCount;
	public double maxValue;
	
	/**Used by rootsquare propoprtionnal method 
	 * @todo: must customizable by user in the GUI
	 * 
	 */
	
	public int facteurRacine = 2;
	
	//The surface reference must be greater or equals than 10.
	public static int surfRef = 3000;

	public ProportionnalMethod(DataSource ds, String fieldName){
		
		this.ds=ds;
		this.fieldName=fieldName;
		try {
			rowCount =  (int)ds.getRowCount();
		} catch (DriverException e) {
			e.printStackTrace();
		}
	}
	
	
	public void build(int surfRef){
		
		if (surfRef>= 10){
			this.surfRef = surfRef;
		}
		else {
			
		}
		
		double[] valeurs = getValueSorted();
		
		
		maxValue = valeurs[valeurs.length-1];
		 
		int i = 0;
        int minIndex = 0;
        while (valeurs[minIndex] == Double.MIN_VALUE) {
        	minIndex++;
        }
        
        double legMinValeur = valeurs[minIndex];
        double legMedValeur = valeurs[(int) (valeurs.length / 2)];
        double legMaxValeur = valeurs[valeurs.length - 1];
        int legMinIndex = valeurs.length + 1;
        int legMedIndex = valeurs.length + 1;
        int legMaxIndex = valeurs.length + 1;
        
        int fieldIndex;
		try {
			fieldIndex = ds.getFieldIndexByName(fieldName);
			
			double value;
	        for (i = 0; i < valeurs.length; i++) {
	        	
	        	 value = ds.getFieldValue(i, fieldIndex).getAsDouble();	
	        	 
	        	 if (value == valeurs[minIndex]){
	        		 legMinIndex = i;
	        	 }
	        	 else if (value == valeurs[(int) (valeurs.length / 2)] ) {
	        		 legMedIndex = i;
				}
	        	 else if (value == valeurs[valeurs.length - 1]) {
	        		 legMaxIndex = i;
				}
	        }
	        	        
	        
		} catch (DriverException e) {
			e.printStackTrace();
		}
    	
		
		
	}
	
	
	public double[] getValueSorted(){
		double[] values = new double[rowCount];

		try {
			int fieldIndex = ds.getFieldIndexByName(fieldName);

			for (int i = 0; i < values.length; i++) {

				values[i] = ds.getFieldValue(i, fieldIndex).getAsDouble();

			}

			Arrays.sort(values);


		} catch (DriverException e) {

			e.printStackTrace();
		}
		return values;


	}
	
}
