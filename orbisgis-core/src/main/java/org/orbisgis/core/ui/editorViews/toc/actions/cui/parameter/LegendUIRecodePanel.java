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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.real.LegendUIRealComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.color.LegendUIColorComponent;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.Recode2Real;
import org.orbisgis.core.renderer.se.parameter.string.Recode2String;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.TextInput;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.color.LegendUIColorLiteralPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.color.LegendUIMetaColorPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.real.LegendUIMetaRealPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.real.LegendUIRealLiteralPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.string.LegendUIMetaStringPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.string.LegendUIStringComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.string.LegendUIStringLiteralPanel;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

/**
 * @author maxence
 */
public abstract class LegendUIRecodePanel extends LegendUIComponent
		implements LegendUIRealComponent, LegendUIColorComponent, LegendUIStringComponent {

	private Recode recode;
	private LegendUIMetaStringPanel lookupValue;
	private LegendUIComponent fallbackPanel;
	private ArrayList<LegendUIComponent> values;
	private ArrayList<KeyInput> keys;
	private ArrayList<RmButton> rmBtns;
	private JButton addBtn;
	private LegendUIAbstractPanel mapItems;
	private LegendUIAbstractPanel header;
	private LegendUIAbstractPanel footer;

	/**
	 *
	 * @param name
	 * @param controller
	 * @param parent
	 * @param r
	 */
	public LegendUIRecodePanel(final String name, LegendUIController controller,
			LegendUIComponent parent, Recode r, boolean isNullable) {
		super(name, controller, parent, 0, isNullable);

		this.recode = r;
		this.mapItems = new LegendUIAbstractPanel(controller);
		mapItems.setLayout(new GridLayout(0, 3));
		this.header = new LegendUIAbstractPanel(controller);
		this.footer = new LegendUIAbstractPanel(controller);


		this.lookupValue = new LegendUIMetaStringPanel("Lookup value", controller, this, recode.getLookupValue(), false) {

			@Override
			public void stringChanged(StringParameter newString) {
				recode.setLookupValue(newString);
			}
		};
		this.lookupValue.init();


		if (recode.getFallbackValue() instanceof ColorLiteral) {
			fallbackPanel = new LegendUIColorLiteralPanel("Fallback color",
					controller, this, (ColorLiteral) recode.getFallbackValue(), false) {

				@Override
				protected void colorChanged(ColorLiteral color) {
					recode.setFallbackValue(color);
				}
			};
		} else if (recode.getFallbackValue() instanceof RealLiteral) {
			fallbackPanel = new LegendUIRealLiteralPanel("Fallback value",
					controller, this, (RealLiteral) recode.getFallbackValue(), false) {

				@Override
				protected void realChanged(RealLiteral real) {
					recode.setFallbackValue(real);
				}

			};
		} else if (recode.getFallbackValue() instanceof StringParameter) {
			fallbackPanel = new LegendUIStringLiteralPanel("Fallback value",
					controller, this, (StringLiteral) recode.getFallbackValue(), false) {

				@Override
				protected void stringChanged(StringLiteral string) {
					recode.setFallbackValue(string);
				}

			};
		}


		addBtn = new JButton("add");
		addBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Since it's an abstract class, we have to guess effective type
				// by checking the first class value type, which always exists.
				SeParameter classValue = recode.getFallbackValue();

				int index = -1;

				if (classValue instanceof RealParameter) {
					index = recode.addMapItem("new key", new RealLiteral(1.0));
				} else if (classValue instanceof ColorParameter) {
					index = recode.addMapItem("new key", new ColorLiteral());
				} else if (classValue instanceof StringParameter) {
					index = recode.addMapItem("new key", new StringLiteral("value"));
					//categorize.addClass(new RealLiteral(1000.0), new StringLiteral(""));
				}

				if (index >= 0) {
					keys.add(new KeyInput(index, null, recode.getMapItemKey(index), 10));
					SeParameter value = recode.getMapItemValue(index);

					if (value instanceof ColorParameter) {
						values.add(new ColorValueMetaInput(index, name,
								LegendUIRecodePanel.this.controller,
								LegendUIRecodePanel.this, (ColorParameter) value));
					} else if (value instanceof RealParameter) {
						values.add(new RealValueMetaInput(index, name,
								LegendUIRecodePanel.this.controller,
								LegendUIRecodePanel.this, (RealParameter) value));
					} else if (value instanceof StringParameter) {
						values.add(new StringValueMetaInput(index, name,
								LegendUIRecodePanel.this.controller,
								LegendUIRecodePanel.this, (StringParameter) value));
					}

					rmBtns.add(new RmButton(index));

					fireChange();
				}
			}
		});

		int i;

		values = new ArrayList<LegendUIComponent>();
		keys = new ArrayList<KeyInput>();
		rmBtns = new ArrayList<RmButton>();

		for (i = 0; i < recode.getNumMapItem(); i++) {
			keys.add(new KeyInput(i, null, recode.getMapItemKey(i), 10));
			SeParameter value = recode.getMapItemValue(i);

			if (value instanceof ColorParameter) {
				values.add(new ColorValueMetaInput(i, name, controller, this, (ColorParameter) value));
			} else if (value instanceof RealParameter) {
				values.add(new RealValueMetaInput(i, name, controller, this, (RealParameter) value));
			} else if (value instanceof StringParameter) {
				values.add(new StringValueMetaInput(i, name, controller, this, (StringParameter) value));
			}

			rmBtns.add(new RmButton(i));
		}
	}

	private void fireChange() {
		controller.structureChanged(this);
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.PENCIL;
	}

	@Override
	protected void mountComponent() {
		header.removeAll();
		header.add(lookupValue, BorderLayout.WEST);
		header.add(fallbackPanel, BorderLayout.EAST);



		int i;
		mapItems.removeAll();
		for (i = 0; i < values.size(); i++) {
			mapItems.add(keys.get(i));

			mapItems.add((LegendUIComponent)values.get(i));
			if (values.size() > 1) {
				mapItems.add(rmBtns.get(i));
			} else {
				mapItems.add(new JLabel("n/a"));
			}
		}


		footer.removeAll();
		footer.add(addBtn, BorderLayout.WEST);

		editor.add(header, BorderLayout.NORTH);
		editor.add(mapItems, BorderLayout.CENTER);
		editor.add(footer, BorderLayout.SOUTH);
	}

	@Override
	public RealParameter getRealParameter() {
		if (recode instanceof Recode2Real) {
			return (RealParameter) recode;
		} else {
			return null;
		}
	}

	@Override
	public ColorParameter getColorParameter() {
		if (recode instanceof Recode2Color) {
			return (ColorParameter) recode;
		} else {
			return null;
		}
	}

	@Override
	public StringParameter getStringParameter() {
		if (recode instanceof Recode2String) {
			return (StringParameter) recode;
		} else {
			return null;
		}
	}

	private class KeyInput extends TextInput {

		private int index;

		public KeyInput(int index, String name, String initialValue, int size) {
			super(name, initialValue, size, false);
			this.index = index;
		}

		@Override
		protected void valueChanged(String s) {
			LegendUIRecodePanel.this.recode.getMapItem(index).setKey(s);
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}

	private interface Reindexable {

		public void setIndex(int index);
	}

	private class ColorValueMetaInput extends LegendUIMetaColorPanel implements Reindexable {

		int index;

		public ColorValueMetaInput(int index, String name, LegendUIController controller, LegendUIComponent parent, ColorParameter c) {
			super(name, controller, parent, c, false);
			this.index = index;
			super.init();
		}

		@Override
		public void colorChanged(ColorParameter newColor) {
			LegendUIRecodePanel.this.recode.getMapItem(index).setValue(newColor);
		}

		@Override
		public void setIndex(int index) {
			this.index = index;
		}
	}

	private class RealValueMetaInput extends LegendUIMetaRealPanel implements Reindexable {

		int index;

		public RealValueMetaInput(int index, String name, LegendUIController controller, LegendUIComponent parent, RealParameter s) {
			super(name, controller, parent, s, false);
			this.index = index;
			super.init();
		}

		@Override
		public void realChanged(RealParameter newReal) {
			LegendUIRecodePanel.this.recode.getMapItem(index).setValue(newReal);
		}

		@Override
		public void setIndex(int index) {
			this.index = index;
		}
	}

	private class StringValueMetaInput extends LegendUIMetaStringPanel implements Reindexable {

		int index;

		public StringValueMetaInput(int index, String name, LegendUIController controller, LegendUIComponent parent, StringParameter s) {
			super(name, controller, parent, s, false);
			this.index = index;
			super.init();
		}

		@Override
		public void stringChanged(StringParameter newString) {
			LegendUIRecodePanel.this.recode.getMapItem(index).setValue(newString);
		}

		@Override
		public void setIndex(int index) {
			this.index = index;
		}
	}

	private class RmButton extends JButton {

		private int index;

		public RmButton(int i) {
			super("rm");
			this.index = i;

			this.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					recode.removeMapItem(RmButton.this.index);
					keys.remove(RmButton.this.index);
					values.remove(RmButton.this.index);
					rmBtns.remove(RmButton.this.index);


					int i;
					// When a map item is removed, have to change index of nexts map items
					for (i = index; i < keys.size(); i++) {
						keys.get(i).setIndex(i);
						((Reindexable) values.get(i)).setIndex(i);
						rmBtns.get(i).setIndex(i);
					}


					controller.structureChanged(LegendUIRecodePanel.this);
				}
			});
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}


	@Override
	public Class getEditedClass() {
		return recode.getClass();
	}
}
