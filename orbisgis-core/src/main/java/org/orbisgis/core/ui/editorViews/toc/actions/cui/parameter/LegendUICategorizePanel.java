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
package org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter;

import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.string.LegendUIStringComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.real.LegendUIRealLiteralPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.real.LegendUIMetaRealPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.real.LegendUIRealComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.color.LegendUIColorLiteralPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.color.LegendUIMetaColorPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.color.LegendUIColorComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.classification.RangeMethod;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.CategorizeListener;
import org.orbisgis.core.renderer.se.parameter.LiteralListener;
import org.orbisgis.core.renderer.se.parameter.PropertyName;
import org.orbisgis.core.renderer.se.parameter.PropertyNameListener;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.Categorize2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIAbstractMetaPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.RadioSwitch;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.string.LegendUIStringLiteralPanel;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

/**
 * @todo BUG !!!!
 * @author maxence
 */
public abstract class LegendUICategorizePanel extends LegendUIComponent
		implements LegendUIRealComponent, LegendUIColorComponent,
		LegendUIStringComponent,
		CategorizeListener, PropertyNameListener, LiteralListener {

	private LegendUIMetaRealPanel lookupPanel;
	protected Categorize categorize;
	private LegendUIComponent fallbackPanel;
	private RadioSwitch thresholdsSwitch;
	private JButton add;
	private ArrayList<LegendUIAbstractMetaPanel> values;
	private ArrayList<MetaRealThreshold> thresholds;

	private LegendUIAbstractPanel left;
	private LegendUIAbstractPanel right;
	private LegendUIAbstractPanel header;
	private LegendUIAbstractPanel content;
	private LegendUIAbstractPanel footer;

	private LegendUIAbstractPanel classes;

	public LegendUICategorizePanel(String name, LegendUIController controller, LegendUIComponent parent, Categorize c, boolean isNullable) {
		super(name, controller, parent, 0, isNullable);

		left = new LegendUIAbstractPanel(controller);
		right = new LegendUIAbstractPanel(controller);

		header = new LegendUIAbstractPanel(controller);
		footer = new LegendUIAbstractPanel(controller);

		//toolbar = new LegendUIAbstractPanel(controller);

		content = new LegendUIAbstractPanel(controller);
		classes = new LegendUIAbstractPanel(controller);

		right.setLayout(new GridLayout(0,1));
		classes.setLayout(new GridLayout(0,3));

		this.categorize = c;

		this.lookupPanel = new LookupValuePanel(controller, this, c.getLookupValue());

		if (categorize.getFallbackValue() instanceof ColorLiteral) {
			fallbackPanel = new LegendUIColorLiteralPanel("Fallback color",
					controller, this, (ColorLiteral) categorize.getFallbackValue(), false) {

				@Override
				protected void colorChanged(ColorLiteral color) {
					categorize.setFallbackValue(color);
				}

			};
		} else if (categorize.getFallbackValue() instanceof RealLiteral) {
			fallbackPanel = new LegendUIRealLiteralPanel("Fallback value",
					controller, this, (RealLiteral) categorize.getFallbackValue(), false) {
				@Override
				protected void realChanged(RealLiteral real) {
					categorize.setFallbackValue(real);
				}
			};
		} else if (categorize.getFallbackValue() instanceof StringParameter) {
			fallbackPanel = new LegendUIStringLiteralPanel("Fallback value",
					controller, this, (StringLiteral) categorize.getFallbackValue(), false) {

				@Override
				protected void stringChanged(StringLiteral string) {
					categorize.setFallbackValue(string);
				}

			};
		}

		String[] options = {"Pre.", "Suc."};
		thresholdsSwitch = new RadioSwitch(options, (categorize.areThresholdsPreceding() ? 0 : 1)) {

			@Override
			protected void valueChanged(int choice) {
				if (choice == 0) {
					categorize.setThresholdsPreceding();
				} else {
					categorize.setThresholdsSucceeding();
				}
			}
		};



		add = new JButton("add");
		add.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Since it's an abstract class, we have to guess effective type
				// by checking the first class value type, which always exists.
				SeParameter classValue = categorize.getClassValue(0);
				if (classValue instanceof RealParameter) {
					categorize.addClass(new RealLiteral(1000.0), new RealLiteral(10.0));
				} else if (classValue instanceof ColorParameter) {
					categorize.addClass(new RealLiteral(1000.0), new ColorLiteral());
				} else if (classValue instanceof StringParameter) {
					categorize.addClass(new RealLiteral(1000.0), new StringLiteral(""));
				}

				//fireChange();

			}
		});
		values = new ArrayList<LegendUIAbstractMetaPanel>();
		thresholds = new ArrayList<MetaRealThreshold>();

		// classes
		for (int i = 0; i < categorize.getNumClasses(); i++) {
			SeParameter classValue = categorize.getClassValue(i);

			System.out.println("CLASS VALUE IS " + classValue + " for class " + i);
			if (classValue instanceof RealParameter) {
				values.add(new MetaRealValue(controller, this, (RealParameter) classValue, i));
			} else if (classValue instanceof ColorParameter) {
				values.add(new MetaColor(controller, this, (ColorParameter) classValue, i));
			}

			if (i < categorize.getNumClasses() - 1) {
				thresholds.add(new MetaRealThreshold(controller, this, categorize.getThresholdValue(i), i));
			}
		}
		literalChanged();


		categorize.register(this);
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.PALETTE;
	}

	private void fireChange() {
		controller.structureChanged(this);
	}

	@Override
	protected void mountComponent() {
		header.removeAll();
		header.add(lookupPanel, BorderLayout.WEST);
		header.add(thresholdsSwitch, BorderLayout.CENTER);
		header.add(fallbackPanel, BorderLayout.EAST);


		left.removeAll();
		left.add(header, BorderLayout.NORTH);

		classes.removeAll();

		for (int i = 0; i < values.size(); i++) {
			classes.add(values.get(i));

			if (i < thresholds.size()) {
				classes.add(thresholds.get(i));
			} else {
				classes.add(new JLabel("n/a"));
			}

			if (values.size() > 1) {
				JButton rm = new JButton(OrbisGISIcon.REMOVE);
				rm.setMargin(new Insets(0, 0, 0, 0));

				rm.addActionListener(new RmListener(i));
				classes.add(rm, BorderLayout.EAST);
			} else {
				classes.add(new JLabel("n/a"));
			}
		}

		content.removeAll();
		content.add(classes, BorderLayout.CENTER);
		content.add(add, BorderLayout.SOUTH);
		left.add(content, BorderLayout.CENTER);
		left.add(footer, BorderLayout.SOUTH);

		editor.add(left, BorderLayout.WEST);
		editor.add(right, BorderLayout.EAST);
	}

	@Override
	public StringParameter getStringParameter() {
		if (this.categorize instanceof StringParameter) {
			return (StringParameter) categorize;
		} else {
			return null;
		}
	}

	@Override
	public RealParameter getRealParameter() {
		if (this.categorize instanceof RealParameter) {
			return (RealParameter) categorize;
		} else {
			return null;
		}
	}

	@Override
	public ColorParameter getColorParameter() {
		if (this.categorize instanceof ColorParameter) {
			return (ColorParameter) categorize;
		} else {
			return null;
		}
	}

	@Override
	public void classRemoved(int i) {
		values.remove(i);

		int ti = i - 1;

		if (ti<0)
			ti += 1;

		thresholds.remove(ti);

		for (;ti<thresholds.size();ti++){
			thresholds.get(ti).setIndex(ti);
		}

		literalChanged();


	}

	@Override
	public void classAdded(int i) {
		SeParameter classValue = categorize.getClassValue(i);
		if (classValue instanceof RealParameter) {
			values.add(i, new MetaRealValue(controller, this, (RealParameter) categorize.getClassValue(i), i));
		} else if (classValue instanceof ColorParameter) {
			values.add(i, new MetaColor(controller, this, (ColorParameter) categorize.getClassValue(i), i));
			literalChanged();
		}

		System.out.println ("Add threshold: " + (i-1));
		thresholds.add(i - 1, new MetaRealThreshold(controller, this, categorize.getThresholdValue(i - 1), i));
	}

	@Override
	public void classMoved(int i, int j) {
		System.out.println("Moving ! " + i + " to " + j);
		if (i < thresholds.size()) {
			MetaRealThreshold c = thresholds.remove(i);
			thresholds.add(j, c);
		}
		LegendUIAbstractMetaPanel c = values.remove(i);
		values.add(j, c);
	}

	/*
	 * Called when one of the color has changed (only for literal colors)
	 */
	@Override
	public final void literalChanged(){
		right.removeAll();
		System.out.println ("LiteralChanged");
		for (LegendUIAbstractMetaPanel v : values){
			LegendUIComponent comp = v.getCurrentComponent();
			if (comp instanceof LegendUIColorLiteralPanel){
				right.add(new JLabel("C:" + ((LegendUIColorLiteralPanel)comp).getColorParameter().toString()));
				System.out.println ("Color: " + ((LegendUIColorLiteralPanel)comp).getColorParameter());
			}
		}
	}

	/**
	 * Called when lookup value is a property name and when a new value is selected
	 * @todo Build an actual histogram !
	 * @param p
	 */
	@Override
	public void propertyNameChanged(PropertyName p) {
		footer.removeAll();
		JLabel text;

		SpatialDataSourceDecorator sds = controller.getEditedFeatureTypeStyle().getLayer().getDataSource();

		try {
			RangeMethod rangesHelper = new RangeMethod(sds, (RealAttribute) p, 4);

			rangesHelper.disecMean();
			Range[] ranges = rangesHelper.getRanges();


			text = new JLabel("(Histogram) ["
				+ ranges[0].getMinRange() + ";"
				+ ranges[1].getMinRange() + ";"
				+ ranges[2].getMinRange() + ";"
				+ ranges[3].getMinRange() + ";"
				+ ranges[3].getMaxRange() + "]");


		} catch (Exception ex) {
			text = new JLabel("Please select a valid field");
			text.setForeground(Color.red);
		}
		footer.add(text);
	}

	private class MetaRealValue extends LegendUIMetaRealPanel {

		private int i;

		public MetaRealValue(LegendUIController controller, LegendUIComponent parent, RealParameter value, int i) {
			super("Value", controller, parent, value, false);
			this.i = i;
			init();
		}

		@Override
		public void realChanged(RealParameter newReal) {
			((Categorize2Real)categorize).setClassValue(i, newReal);
		}

		private void setIndex(int i) {
			this.i = i;
		}
	}

	private class MetaRealThreshold extends LegendUIMetaRealPanel {

		private int i;

		public MetaRealThreshold(LegendUIController controller, LegendUIComponent parent, RealParameter value, int i) {
			super("Threshold", controller, parent, value, false);
			System.out.println("Threshold: " + value);
			this.i = i;
			init();
		}

		@Override
		public void realChanged(RealParameter newReal) {
			categorize.setThresholdValue(i, newReal);
			fireChange();
		}

		private void setIndex(int i) {
			this.i = i;
		}
	}

	private class MetaColor extends LegendUIMetaColorPanel {

		private int i;

		public MetaColor(LegendUIController controller, LegendUIComponent parent, ColorParameter value, int i) {
			super("color value", controller, parent, value, false);
			System.out.println("NEw meta color => i is " + i);
			this.i = i;
			init();
		}

		@Override
		public void colorChanged(ColorParameter newColor) {
			System.out.println("Fire change class value :" + i + " (" + newColor);

			if (newColor instanceof ColorLiteral){
				((ColorLiteral)newColor).register((LiteralListener) parent);
			}

			categorize.setClassValue(i, newColor);
		}

		private void setIndex(int i) {
			this.i = i;
		}
	}

	private class RmListener implements ActionListener {

		private int i;

		public RmListener(int i) {
			this.i = i;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (categorize.removeClass(i)) {
				fireChange();
			}
		}

		private void setIndex(int i) {
			this.i = i;
		}
	}

	private class LookupValuePanel extends LegendUIMetaRealPanel {

		public LookupValuePanel(LegendUIController controller, LegendUICategorizePanel parent, RealParameter r) {
			super("Lookup value", controller, parent, r, false);
			init();
		}

		@Override
		public void realChanged(RealParameter newReal) {
			if (newReal instanceof RealAttribute){
				RealAttribute r = (RealAttribute) newReal;
				r.register((PropertyNameListener) parent);
				((LegendUICategorizePanel)parent).propertyNameChanged(r);
			}
			categorize.setLookupValue(newReal);
		}
	}


	@Override
	public Class getEditedClass() {
		return categorize.getClass();
	}
}
