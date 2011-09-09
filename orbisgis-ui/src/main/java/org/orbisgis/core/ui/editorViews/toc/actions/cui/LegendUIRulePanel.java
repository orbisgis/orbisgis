/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.RadioSwitch;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.RealLiteralInput;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.TextInput;

/**
 *
 * @author maxence
 */
public class LegendUIRulePanel extends JPanel {

	private RealLiteralInput minScaleInput;
	private RealLiteralInput maxScaleInput;
	private TextInput whereInput;
	private TextInput nameInput;

	private RadioSwitch filterSwitch;

	private Rule rule;

	private ArrayList<LegendUIComponentListener> listeners;


	public LegendUIRulePanel(Rule r) {
		super(new GridLayout(0,1));
		this.rule = r;

		listeners = new ArrayList<LegendUIComponentListener>();

		updateBorder();

		minScaleInput = new RealLiteralInput("Min. scale", r.getMinScaleDenom(), 0.0, null) {
			@Override
			protected void valueChanged(Double v) {
				rule.setMinScaleDenom(v);
			}
		};

		maxScaleInput = new RealLiteralInput("Max. scale", r.getMaxScaleDenom(), 0.0, null) {
			@Override
			protected void valueChanged(Double v) {
				rule.setMaxScaleDenom(v);
			}
		};

		whereInput = new TextInput("Where", rule.getWhere(), 40, true) {
			@Override
			protected void valueChanged(String s) {
				rule.setWhere(s);
			}
		};
		whereInput.setEnabled(!rule.isFallbackRule());

		nameInput = new TextInput("Name", rule.getName(), 40, false) {
			@Override
			protected void valueChanged(String s) {
				rule.setName(s);
				updateBorder();
				fireNameChanged();
			}
		};

		String[] choices = {"Filter", "Fallback rule"};

		filterSwitch = new RadioSwitch(choices, (rule.isFallbackRule() ? 1 : 0)) {

			@Override
			protected void valueChanged(int choice) {
				System.out.println ("Value: " + choice);
				if (choice == -1)
					return;

				boolean fallback = (choice == 1);
				rule.setFallbackRule(fallback);
				whereInput.setEnabled(!fallback);
			}
		};


		this.add(nameInput, BorderLayout.PAGE_END);

		this.add(filterSwitch);
		this.add(whereInput, BorderLayout.PAGE_END);

		this.add(minScaleInput, BorderLayout.PAGE_END);
		this.add(maxScaleInput, BorderLayout.PAGE_END);
	}

	@Override
	public String toString(){
		return rule.toString();
	}

	private void updateBorder() {
		this.setBorder(BorderFactory.createTitledBorder("Rule " + rule.getName()));
	}

	public void register(LegendUIComponentListener l) {
		if (!listeners.contains(l)){
			listeners.add(l);
		}
	}

	protected void fireNameChanged(){
		for (LegendUIComponentListener l : listeners){
			l.nameChanged();
		}
	}
}
