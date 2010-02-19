package org.orbisgis.core.ui.plugins.views.beanShellConsole;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.orbisgis.core.images.IconLoader;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.javaManager.autocompletion.ClassOption;
import org.orbisgis.core.javaManager.autocompletion.ConstructorOption;
import org.orbisgis.core.javaManager.autocompletion.FieldOption;
import org.orbisgis.core.javaManager.autocompletion.InlineImplementationOption;
import org.orbisgis.core.javaManager.autocompletion.MethodOption;
import org.orbisgis.core.javaManager.autocompletion.VariableOption;

public class CompletionRenderer extends JPanel implements ListCellRenderer {

	private JLabel lbl;

	public CompletionRenderer() {
		lbl = new JLabel();
		lbl.setOpaque(true);
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
		flowLayout.setHgap(0);
		flowLayout.setVgap(1);
		this.setLayout(flowLayout);
		this.add(lbl);
		this.setBackground(Color.white);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof VariableOption) {
			lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_LOCAL));
		} else if (value instanceof FieldOption) {
			lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_MEMBER));
		} else if (value instanceof ConstructorOption) {
			lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_CLASS));
		} else if (value instanceof MethodOption) {
			lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_MEMBER));
		} else if (value instanceof ClassOption) {
			ClassOption opt = (ClassOption) value;
			if (opt.isInterface()) {
				lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_INTER));
			} else {
				lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_CLASS));
			}
		} else if (value instanceof InlineImplementationOption) {
			lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_INTER));
		} else {
			lbl.setIcon(null);
		}
		lbl.setText(value.toString());

		if (isSelected) {
			lbl.setBackground(Color.lightGray);
			lbl.setForeground(Color.white);
		} else {
			lbl.setBackground(Color.white);
			lbl.setForeground(Color.black);
		}
		this.doLayout();
		return this;
	}

}
