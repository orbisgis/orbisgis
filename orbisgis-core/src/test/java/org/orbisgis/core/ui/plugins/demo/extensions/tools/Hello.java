package org.orbisgis.core.ui.plugins.demo.extensions.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.Automaton;
import org.orbisgis.core.ui.editors.map.tool.DrawingException;
import org.orbisgis.core.ui.editors.map.tool.FinishedAutomatonException;
import org.orbisgis.core.ui.editors.map.tool.NoSuchTransitionException;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;

public class Hello implements Automaton {

	@Override
	public void draw(Graphics g) throws DrawingException {
		// TODO Auto-generated method stub

	}

	@Override
	public AbstractButton getButton() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point getHotSpotOffset() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getImageIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTooltip() {
		return null;
	}

	@Override
	public String[] getTransitionCodes() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("Hello");
		return ret.toArray(new String[0]);
	}

	@Override
	public String[] getTransitionLabels() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("Hello");
		return ret.toArray(new String[0]);
	}

	@Override
	public void init(MapContext vc, ToolManager tm) throws TransitionException,
			FinishedAutomatonException {
		OutputManager om = Services.getService(OutputManager.class);
		Color color;
		color = Color.black;
		om
				.print(
						"Hello : this plugin is a tool that do nothing in map"
								+ "\n\r"
								+ "Follow Automaton rules to act on map"
								+ "\n\r"
								+ "(It's an extension plugin in lib/ext folder.)"
								+ "\n\r"
								+ "Automaton add a right click sub menu on map corresponding to this tool"
								+ "\n\r\r", color);
	}

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return false;
	}

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return false;
	}

	public void setButton(AbstractButton button) {
	}

	public void setMouseCursor(ImageIcon mouseCursor) {
	}

	public void toolFinished(MapContext vc, ToolManager tm)
			throws NoSuchTransitionException, TransitionException,
			FinishedAutomatonException {
	}

	public void transition(String code) throws NoSuchTransitionException,
			TransitionException, FinishedAutomatonException {
	}

	public void update(Observable arg0, Object arg1) {
	}

}
