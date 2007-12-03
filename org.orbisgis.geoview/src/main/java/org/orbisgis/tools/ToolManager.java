/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.orbisgis.tools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;
import org.orbisgis.geoview.renderer.liteShape.LiteShape;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Adapter from the MapControl Behaviours to Automaton's interface. It's also
 * the EditionContext of the system.
 *
 * @author Fernando Gonzlez Corts
 */
public class ToolManager extends MouseAdapter implements MouseMotionListener,
		ToolManagerListener {

	public static final String TERMINATE = "t";

	public static final String RELEASE = "release";

	public static final String PRESS = "press";

	public static final String POINT = "point";

	public static GeometryFactory toolsGeometryFactory = new GeometryFactory();

	private static Logger logger = Logger
			.getLogger(ToolManager.class.getName());

	private Automaton currentTool;

	private int active = -1;

	private double[] values = new double[0];

	private int uiTolerance = 6;

	private boolean selectionImageDirty = true;

	private Image selectionImage;

	private Point adjustedPoint = null;

	private Point2D worldAdjustedPoint = null;

	private int lastMouseX;

	private int lastMouseY;

	private ArrayList<Handler> currentHandlers = new ArrayList<Handler>();

	private JPopupMenu toolPopUp;

	private int mouseModifiers;

	private Automaton defaultTool;

	private static final Color HANDLER_COLOR = Color.BLUE;

	private ViewContext vc;

	private ArrayList<Geometry> geomToDraw = new ArrayList<Geometry>();

	/**
	 * Creates a new EditionToolAdapter.
	 *
	 * @param vp
	 * @param ef
	 */
	public ToolManager(Automaton defaultTool, final ViewContext ec) {
		this.vc = ec;
		currentTool = defaultTool;
		this.defaultTool = defaultTool;
		updateCursor();

		ec.setToolManagerListener(this);
		ec.setToolManager(this);
	}

	public void selectionChanged() {
		try {
			recalculateHandlers();
		} catch (EditionContextException e) {
			vc.error(e);
		}
	}

	public void extentChanged() {
		try {
			recalculateHandlers();
		} catch (EditionContextException e) {
			vc.error(e);
		}
	}

	public void dataChanged() {
		try {
			recalculateHandlers();
		} catch (EditionContextException e) {
			vc.error(e);
		}
	}

	public void activeThemeChanged() {
		try {
			recalculateHandlers();
		} catch (EditionContextException e) {
			vc.error(e);
		}
	}

	public void mouseMoved(MouseEvent e) {
		lastMouseX = e.getPoint().x;
		lastMouseY = e.getPoint().y;
		vc.repaint();

		setAdjustedHandler();
	}

	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (e.getClickCount() == 2) {
				leftClickTransition(e, TERMINATE);
			} else {
				leftClickTransition(e, POINT);
			}
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			if (!vc.thereIsActiveTheme()) {
				return;
			}
			/*
			 * TODO will disable this when we implement edition
			 * toolPopUp.show(ec.getComponent(), e.getPoint().x,
			 * e.getPoint().y);
			 */
		}

	}

	private void leftClickTransition(MouseEvent e, String transitionCode) {
		try {
			Point2D p = e.getPoint();
			if (worldAdjustedPoint != null) {
				ToolManager.this.setValues(new double[] {
						worldAdjustedPoint.getX(), worldAdjustedPoint.getY() });
			} else {
				Point2D mapPoint = vc
						.toMapPoint((int) p.getX(), (int) p.getY());
				ToolManager.this.setValues(new double[] { mapPoint.getX(),
						mapPoint.getY() });
			}
			mouseModifiers = e.getModifiersEx();
			transition(transitionCode);
			vc.stateChanged();
		} catch (NoSuchTransitionException e1) {
			vc.error(e1);
		} catch (TransitionException e1) {
			vc.toolError(e1);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			leftClickTransition(e, PRESS);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			leftClickTransition(e, RELEASE);
		}
	}

	/**
	 * Tests and sets the adjusted point of the mouse cursor. It tests the
	 * handlers
	 */
	private void setAdjustedHandler() {
		adjustedPoint = null;
		worldAdjustedPoint = null;

		for (int i = 0; i < currentHandlers.size(); i++) {
			Point2D p = vc.fromMapPoint(currentHandlers.get(i).getPoint());
			if (p.distance(lastMouseX, lastMouseY) < uiTolerance) {
				adjustedPoint = new Point((int) p.getX(), (int) p.getY());
				worldAdjustedPoint = currentHandlers.get(i).getPoint();
				break;
			}
		}
	}

	public void paintEdition(Graphics g) {

		if (vc.thereIsActiveTheme()) {
			if (vc.isActiveThemeVisible()) {
				if (selectionImageDirty) {

					selectionImage = new BufferedImage(vc.getImageWidth(), vc
							.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = (Graphics2D) selectionImage.getGraphics();

					for (Handler handler : currentHandlers) {
						handler.draw(g2, HANDLER_COLOR, this, vc);
					}

					selectionImageDirty = false;
				}

				g.drawImage(selectionImage, 0, 0, null);

				String error = null;
				geomToDraw.clear();
				try {
					currentTool.draw(g);
				} catch (Exception e) {
					error = e.getMessage();
				}
				for (int i = 0; i < geomToDraw.size(); i++) {
					((Graphics2D) g).draw(new LiteShape(geomToDraw.get(i), vc
							.getTransformation(), false));
				}

				if (error != null) {
					drawTextWithWhiteBackGround((Graphics2D) g, error,
							new Point2D.Double(lastMouseX, lastMouseY));
				}

			}
		}
	}

	private void drawTextWithWhiteBackGround(Graphics2D g2, String text,
			Point2D p) {
		TextLayout tl = new TextLayout(text, g2.getFont(), g2
				.getFontRenderContext());
		g2.setColor(Color.WHITE);
		Rectangle2D textBounds = tl.getBounds();
		g2.fill(new Rectangle2D.Double(textBounds.getX() + p.getX(), textBounds
				.getY()
				+ p.getY(), textBounds.getWidth(), textBounds.getHeight()));
		g2.setColor(Color.BLACK);
		tl.draw(g2, (float) p.getX(), (float) p.getY());
	}

	/**
	 * Draws the cursor at the mouse cursor position or at the adjusted point if
	 * any
	 *
	 * @param g
	 */
	private void drawCursor(Graphics g) {
		int x, y;
		if (adjustedPoint == null) {
			x = lastMouseX;
			y = lastMouseY;
		} else {
			x = (int) adjustedPoint.getX();
			y = (int) adjustedPoint.getY();
		}

		x = 0;
		y = 0;

		g.setColor(Color.BLACK);

		g.drawRect(x - uiTolerance / 2, y - uiTolerance / 2, uiTolerance,
				uiTolerance);

		g.drawLine(x, y - 2 * uiTolerance, x, y + 2 * uiTolerance);

		g.drawLine(x - 2 * uiTolerance, y, x + 2 * uiTolerance, y);
	}

	private void updateCursor() {
		Cursor c = null;
		URL cursor = getTool().getMouseCursorURL();
		if (cursor == null) {
			BufferedImage image = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDefaultConfiguration().createCompatibleImage(32, 32,
							Transparency.BITMASK);
			Graphics2D g = image.createGraphics();
			g.setTransform(AffineTransform.getTranslateInstance(16, 16));
			drawCursor(g);
			Cursor crossCursor = Toolkit
					.getDefaultToolkit()
					.createCustomCursor(image, new Point(16, 16), "crossCursor"); //$NON-NLS-1$

			c = crossCursor;
		} else {
			Dimension size = Toolkit.getDefaultToolkit().getBestCursorSize(32,
					32);
			BufferedImage bi = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDefaultConfiguration().createCompatibleImage(
							size.width, size.height, Transparency.BITMASK);
			Image image = new ImageIcon(cursor).getImage();
			int xOffset = (size.width - image.getWidth(null)) / 2;
			int yOffset = (size.height - image.getHeight(null)) / 2;
			bi.createGraphics().drawImage(image, xOffset, yOffset, null);

			Point hotSpot = getTool().getHotSpotOffset();
			hotSpot = new Point(hotSpot.x + xOffset, hotSpot.y + yOffset);
			c = Toolkit.getDefaultToolkit().createCustomCursor(
					bi, hotSpot, ""); //$NON-NLS-1$
		}

		vc.setCursor(c);
	}

	/**
	 * @see org.estouro.ui.ViewContext#getValues()
	 */
	public double[] getValues() {
		return values;
	}

	/**
	 * @see org.estouro.ui.ViewContext#setValues(double[])
	 */
	public void setValues(double[] values) {
		this.values = values;
	}

	/**
	 * @see org.estouro.ui.ViewContext#getTolerance()
	 */
	public double getTolerance() {
		return uiTolerance / vc.getTransformation().getScaleX();
	}

	/**
	 * @see org.estouro.ui.ViewContext#setUITolerance(int)
	 */
	public void setUITolerance(int tolerance) {
		logger.info("setting uiTolerance: " + tolerance); //$NON-NLS-1$
		uiTolerance = tolerance;
	}

	/**
	 * @see org.estouro.ui.ViewContext#transition(java.lang.String)
	 */
	public void transition(String code) throws NoSuchTransitionException,
			TransitionException {
		if (!vc.thereIsActiveTheme()) {
			throw new TransitionException("No hay ningn tema activo");
		} else if (!currentTool.isEnabled(vc, this)
				&& (!currentTool.getClass().equals(defaultTool))) {
			throw new TransitionException("The current tool is not enabled");
		} else {
			try {
				currentTool.transition(code);
				configureMenu();
				vc.repaint();
			} catch (FinishedAutomatonException e) {
				setTool(defaultTool);
			} catch (NoSuchTransitionException e) {
				/*
				 * Withou this line, this exception will be catch by the "catch
				 * (throwable)" below
				 */
				throw e;
			} catch (TransitionException e) {
				/*
				 * Withou this line, this exception will be catch by the "catch
				 * (throwable)" below
				 */
				throw e;
			} catch (Throwable e) {
				/*
				 * leave it in a stable status
				 */
				setTool(defaultTool);
				throw new RuntimeException(e);
			}
		}
	}

	private void configureMenu() {
		String[] labels = currentTool.getTransitionLabels();
		String[] codes = currentTool.getTransitionCodes();
		toolPopUp = new JPopupMenu();
		for (int i = 0; i < codes.length; i++) {
			JMenuItem item = new JMenuItem(labels[i]);
			item.setActionCommand(codes[i]);
			item.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					try {
						transition(e.getActionCommand());
					} catch (NoSuchTransitionException e1) {
						vc.error(e1);
					} catch (TransitionException e1) {
						vc.toolError(e1);
					}
				}

			});
			toolPopUp.add(item);
		}

		if (vc.atLeastNGeometriesSelected(1) && vc.isActiveThemeWritable()) {
			JMenuItem delete = new JMenuItem("Eliminar seleccin");
			delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vc.removeSelected();
				}
			});
			toolPopUp.addSeparator();
			toolPopUp.add(delete);
		}
	}

	/**
	 * @throws TransitionException
	 * @throws FinishedAutomatonException
	 * @see org.estouro.ui.ViewContext#setEditionTool(org.ag.Automaton)
	 */
	public void setTool(Automaton tool) throws TransitionException {
		logger.info("seting tool " + tool.getClass().getName()); //$NON-NLS-1$
		try {
			if ((currentTool != null) && (vc.thereIsActiveTheme())) {
				try {
					currentTool.toolFinished(vc, this);
				} catch (NoSuchTransitionException e) {
					// no way
				} catch (TransitionException e) {
					// be quiet
				}
			}
			currentTool = tool;
			currentTool.init(vc, this);
			configureMenu();
			vc.toolChanged();
			vc.stateChanged();
		} catch (FinishedAutomatonException e1) {
			setTool(defaultTool);
		}

		updateCursor();
	}

	public Point2D getLastRealMousePosition() {
		if (worldAdjustedPoint != null) {
			return worldAdjustedPoint;
		} else {
			return vc.toMapPoint(lastMouseX, lastMouseY);
		}

	}

	/**
	 * @see org.estouro.ui.ViewContext#getLastMouseX()
	 */
	public int getLastMouseX() {
		if (adjustedPoint != null) {
			return adjustedPoint.x;
		} else {
			return lastMouseX;
		}
	}

	/**
	 * @see org.estouro.ui.ViewContext#getLastMouseY()
	 */
	public int getLastMouseY() {
		if (adjustedPoint != null) {
			return adjustedPoint.y;
		} else {
			return lastMouseY;
		}
	}

	/**
	 * @see org.estouro.ui.ViewContext#getUITolerance()
	 */
	public int getUITolerance() {
		return uiTolerance;
	}

	/**
	 * @see org.estouro.ui.ViewContext#getCurrentHandlers()
	 */
	public ArrayList<Handler> getCurrentHandlers() {
		return currentHandlers;
	}

	private void recalculateHandlers() throws EditionContextException {
		if (!vc.isActiveThemeVisible()) {
			return;
		}

		currentHandlers.clear();
		selectionImageDirty = true;
		if (!vc.atLeastNGeometriesSelected(1)) {
			return;
		}

		Geometry[] selectedGeometries = vc.getSelectedGeometries();
		for (int i = 0; i < selectedGeometries.length; i++) {

			Primitive p = new Primitive(selectedGeometries[i]);
			Handler[] handlers = p.getHandlers();
			for (int j = 0; j < handlers.length; j++) {
				currentHandlers.add(handlers[j]);
			}
		}
	}

	/**
	 * @see org.estouro.ui.ViewContext#getMouseModifiers()
	 */
	public int getMouseModifiers() {
		return mouseModifiers;
	}

	/**
	 * @see org.estouro.ui.ViewContext#setMouseModifiers(int)
	 */
	public void setMouseModifiers(int modifiers) {
		mouseModifiers = modifiers;
	}

	/**
	 * @see org.estouro.ui.ViewContext#getEditionTool()
	 */
	public Automaton getTool() {
		return currentTool;
	}

	public void setActiveTheme(int index) throws EditionContextException {
		if (index == active)
			return;

		if (active != -1) {
			/*
			 * the editing tool is set twice because the first one will cause a
			 * termination event on the tool and that event must be dealt
			 * without changing the theme
			 */
			try {
				setTool(defaultTool);
			} catch (TransitionException e2) {
				// ignore it
			}
		}

		active = index;

		// Initialize the current tool before anything can fail
		try {
			setTool(defaultTool);
		} catch (TransitionException e) {
			try {
				setTool(defaultTool);
			} catch (TransitionException e1) {
				/*
				 * SelectionTool does not fails at initialization
				 */
				throw new RuntimeException();
			}
		}

		recalculateHandlers();
	}

	public void addGeomToDraw(Geometry geom) {
		this.geomToDraw.add(geom);
	}

}
