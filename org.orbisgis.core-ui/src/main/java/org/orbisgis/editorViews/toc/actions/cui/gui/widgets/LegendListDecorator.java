package org.orbisgis.editorViews.toc.actions.cui.gui.widgets;

import java.util.Random;

import org.orbisgis.renderer.legend.Legend;

public class LegendListDecorator {
	private Legend leg;
	private String id;
	
	public LegendListDecorator(Legend leg){
		if (leg.getName()==null || leg.getName()==""){
			leg.setName(leg.getLegendTypeName());
			//leg.setName(leg.getTypeName());
		}
		this.leg=leg;
		id=(new Random()).nextInt(999999999)+"";
	}
	

	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id=id;
	}
	
	public Legend getLegend(){
		return leg;
	}
	
	public String toString(){
		return leg.getName();
	}
	
	public void setLegend(Legend leg){
		this.leg=leg;
	}

	
	
}
