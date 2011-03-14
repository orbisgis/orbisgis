package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;


public class JSE_Steps {

	private float min;
	private float max;
	private Color color;
	private String alias;

	public JSE_Steps(float min,float max,Color color,String alias){
		this.min=min;
		this.max=max;
		this.color=color;
		this.alias=alias;
	}

	public float getMin() {
		return min;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}
