package org.orbisgis.core.resourceTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

public class Folder extends BasicResource {

	private final Icon emptyIcon = new ImageIcon(getClass().getResource(
			"empty_folder.png"));

	private final Icon openIcon = new ImageIcon(getClass().getResource(
			"open_folder.png"));

	public Folder(String name) {
		super(name);
	}

	public Icon getIcon(boolean isExpanded) {
		Icon icon = emptyIcon;
		if (getChildCount() != 0) {
			if (!isExpanded) {
				icon = openIcon;
			}

		}

		return (icon);
	}

	public JMenuItem[] getPopupActions() {
		JMenuItem[] items = new JMenuItem[1];

		JMenuItem menuItem = new JMenuItem("Sample item");
		menuItem.setIcon(emptyIcon);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Sample Action");
			}

		});

		items[0] = menuItem;

		return items;
	}

}
