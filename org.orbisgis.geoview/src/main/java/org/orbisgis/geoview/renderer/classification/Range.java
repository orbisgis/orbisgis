package org.orbisgis.geoview.renderer.classification;

public class Range {

	
	private int numberOfItems;
	private float partOfItems;
	private double minRange;
	private double maxRange;

	public Range(){
		
	}
	
	public Range(int numberOfItems,float partOfItems,double minRange,double maxRange){
		this.numberOfItems=numberOfItems;
		this.partOfItems=partOfItems;
		this.minRange=minRange;
		this.maxRange=maxRange;
		
	}
	
	public int getNumberOfItems(){
		return numberOfItems;
		
	}
	
		
	public float getPartOfItems(){
		return partOfItems;
				
	}
	
	public double getMinRange(){
		return minRange;
				
	}
	
	public double getMaxRange(){
		return maxRange;
	}

	public void setMaxRange(double maxRange) {
		this.maxRange = maxRange;
	}

	public void setMinRange(double minRange) {
		this.minRange = minRange;
	}

	public void setNumberOfItems(int numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	public void setPartOfItems(float partOfItems) {
		this.partOfItems = partOfItems;
	}
}
