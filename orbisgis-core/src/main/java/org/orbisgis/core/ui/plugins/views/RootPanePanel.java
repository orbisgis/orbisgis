package org.orbisgis.core.ui.plugins.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;

public class RootPanePanel extends JPanel implements RootPaneContainer, ContainerListener {
	 
	private JRootPane rootPane;
	
	public RootPanePanel () {
		this (new BorderLayout());
		rootPane.setOpaque(true);
	}
	
	public RootPanePanel (LayoutManager layout) {
		rootPane = new JRootPane ();
		rootPane.setOpaque(true);		
		rootPane.getContentPane().setLayout(layout);
		super.add (rootPane);
	}
	
	public Container getContentPane() {
		return rootPane.getContentPane();
	}
 
	public Component getGlassPane() {
		return rootPane.getGlassPane();
	}
 
	public JLayeredPane getLayeredPane() {
		return rootPane.getLayeredPane();
	}
 
	public void setContentPane(Container arg0) {		
		rootPane.setContentPane(arg0);
	}
 
	public void setGlassPane(Component arg0) {
		rootPane.setGlassPane(arg0);
	}
 
	public void setLayeredPane(JLayeredPane arg0) {
		rootPane.setLayeredPane(arg0);
	}
 
	@Override
	protected void addImpl(Component comp, Object constraints, int index) 
    {     
		if (comp == rootPane) {
			super.addImpl(comp, constraints, index);
		}
		else {
			getContentPane().add(comp, constraints, index);
		}
    }
	
	@Override
	public Component add(Component comp, int index) {
		return rootPane.getContentPane().add(comp, index);
	}
 
	@Override
	public void add(Component comp, Object constraints, int index) {
		rootPane.getContentPane().add(comp, constraints, index);
	}
 
	@Override
	public void add(Component comp, Object constraints) {
		rootPane.getContentPane().add(comp, constraints);
	}
 
	@Override
	public Component add(Component comp) {
		return rootPane.getContentPane().add(comp);
	}
 
	@Override
	public Component add(String name, Component comp) {
		return rootPane.getContentPane().add(name, comp);
	}
	
	public JRootPane getRootPane() {
		return rootPane;
	}
	


	@Override
	public void componentAdded(ContainerEvent e) {
		System.out.println("Component added");
		
	}

	@Override
	public void componentRemoved(ContainerEvent e) {
		// TODO Auto-generated method stub
		
	}
}
