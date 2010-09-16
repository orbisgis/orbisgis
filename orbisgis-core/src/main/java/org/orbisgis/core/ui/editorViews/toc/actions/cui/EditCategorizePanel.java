/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */



package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.RadioSwitch;

/**
 *
 * @author maxence
 */
public class EditCategorizePanel extends JPanel {

	private Categorize categorize;

	private JPanel fallbackPanel;
	private JPanel lookupPanel;
	private RadioSwitch thresholdsSwitch;

	private JPanel classes;

	public EditCategorizePanel(Categorize c){
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(BorderFactory.createTitledBorder("Classification"));

		this.categorize = c;

		lookupPanel = new MetaRealParameterPanel(categorize.getLookupValue()) {
			@Override
			public void realChanged(RealParameter newReal) {
				categorize.setLookupValue(newReal);
			}
		};
		this.add(lookupPanel);


		fallbackPanel = categorize.getFallbackValue().getEditionPanel(null);
		this.add(fallbackPanel);

		String[] options = {"Pre.", "Suc."};

		thresholdsSwitch = new RadioSwitch(options, (categorize.areThresholdsPreceding() ? 0 : 1)) {
			@Override
			protected void valueChanged(int choice) {
				if (choice == 0){
					categorize.setThresholdsPreceding();
				}else{
					categorize.setThresholdsSucceeding();
				}
			}
		};

		this.add(thresholdsSwitch);

		classes = new JPanel();
		classes.setLayout(new BoxLayout(classes, BoxLayout.Y_AXIS));

		this.add(classes);
		updateClassList();

	}


	private void updateClassList(){
		classes.removeAll();
		for (int i = 0;i< categorize.getNumClasses(); i++){
			SeParameter cv = categorize.getClassValue(i);
			JPanel panel = null;
			if (cv instanceof RealParameter){
				panel = new MetaRealParameterPanelImpl((RealParameter) cv, i);
			}else if (cv instanceof StringParameter){
				//panel = new MetaRealParameterPanelImpl((RealParameter) cv, i);
				panel = null;
			} else if (cv instanceof ColorParameter){
				panel = new MetaColorParameterPanelImpl((ColorParameter) cv, i);
			}

			if (panel != null){
				classes.add(panel);
			}

			if (i < categorize.getNumClasses() -1){
				JPanel tPanel = new MetaRealParameterPanelImpl(categorize.getThresholdValue(i), i);
				classes.add(tPanel);
			}
		}
		classes.revalidate();
	}

	public StringParameter getStringParameter(){
		if (categorize instanceof StringParameter){
			return (StringParameter) categorize;
		}else{
			return null;
		}
	}

	public RealParameter getRealParameter(){
		if (categorize instanceof RealParameter){
			return (RealParameter) categorize;
		}else{
			return null;
		}
	}

	public ColorParameter getColorParameter(){
		if (categorize instanceof ColorParameter){
			return (ColorParameter) categorize;
		}else{
			return null;
		}
	}

	private class MetaRealParameterPanelImpl extends MetaRealParameterPanel{

		public int i;

		public MetaRealParameterPanelImpl(RealParameter r, int i){
			super(r);
			this.i = i;
		}

		@Override
		public void realChanged(RealParameter newReal) {
			categorize.setClassValue(i, newReal);
		}
	}

	private class MetaColorParameterPanelImpl extends MetaColorParameterPanel{

		public int i;

		public MetaColorParameterPanelImpl(ColorParameter r, int i){
			super(r);
			this.i = i;
		}

		@Override
		public void colorChanged(ColorParameter newColor) {
			categorize.setClassValue(i, newColor);
		}
	}

}
