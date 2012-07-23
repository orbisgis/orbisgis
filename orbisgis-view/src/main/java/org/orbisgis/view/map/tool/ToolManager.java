/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.map.tool;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceListener;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.*;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.map.TransformListener;
import org.orbisgis.core.renderer.AllowAllRenderContext;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.view.map.tool.Automaton.Code;
import org.orbisgis.view.map.tools.PanTool;
import org.orbisgis.view.map.tools.ToolUtilities;
import org.orbisgis.view.map.tools.ZoomInTool;
import org.orbisgis.view.map.tools.ZoomOutTool;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Adapter from the MapControl Behaviours to Automaton's interface. It's also
 * the EditionContext of the system.
 * 
 */
public class ToolManager implements MouseListener,MouseWheelListener,MouseMotionListener {

        public static GeometryFactory toolsGeometryFactory = new GeometryFactory();
        
        private static final I18n I18N = I18nFactory.getI18n(ToolManager.class);
        private static Logger UILOGGER = Logger.getLogger("gui."+ToolManager.class);
        private Automaton currentTool;
        private ILayer activeLayer = null;
        private MapContextListener mapContextListener;
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
        private ArrayList<Geometry> geomToDraw = new ArrayList<Geometry>();
        private ArrayList<String> textToDraw = new ArrayList<String>();
        private Component component;
        private MapTransform mapTransform;
        private ToolLayerListener layerListener = new ToolLayerListener();
        private MapContext mapContext;
        private ArrayList<ToolListener> listeners = new ArrayList<ToolListener>();
        private boolean showPopup;
        private AreaSymbolizer areaSymbolizer;
        private LineSymbolizer lineSymbolizer;
        private PointSymbolizer pointSymbolizer;

        /**
         * Creates a new EditionToolAdapter.
         *
         * @param defaultTool
         * @param mapContext
         * @param mapTransform
         * @param component
         * @throws TransitionException
         */
        public ToolManager(Automaton defaultTool, MapContext mapContext,
                MapTransform mapTransform, Component component)
                throws TransitionException {

                this.mapTransform = mapTransform;
                this.component = component;
                this.mapContext = mapContext;

                setTool(defaultTool);
                this.defaultTool = defaultTool;
                updateCursor();
                mapContextListener = new MapContextListener() {

                        @Override
                        public void layerSelectionChanged(MapContext mapContext) {
                        }

                        @Override
                        public void activeLayerChanged(ILayer previousActiveLayer, MapContext mapContext) {
                                ILayer layer = mapContext.getActiveLayer();
                                if (activeLayer == layer) {
                                        return;
                                }
                                freeResources();
                                activeLayer = layer;
                                if (activeLayer != null) {
                                        activeLayer.addLayerListener(layerListener);
                                        activeLayer.getDataSource().addEditionListener(layerListener);
                                        activeLayer.getDataSource().addDataSourceListener(layerListener);
                                }
                                try {
                                        setTool(ToolManager.this.defaultTool);
                                } catch (TransitionException e) {
                                        try {
                                                setTool(ToolManager.this.defaultTool);
                                        } catch (TransitionException e1) {
                                                throw new RuntimeException();
                                        }
                                }
                                recalculateHandlers();
                        }
                };

                this.mapContext.addMapContextListener(mapContextListener);

                mapTransform.addTransformListener(new TransformListener() {

                        @Override
                        public void extentChanged(Envelope oldExtent,
                                MapTransform mapTransform) {
                                recalculateHandlers();
                        }

                        @Override
                        public void imageSizeChanged(int oldWidth, int oldHeight,
                                MapTransform mapTransform) {
                                recalculateHandlers();
                        }
                });
                buildSymbolizers();
        }

        public void freeResources() {
                if (activeLayer != null) {
                        activeLayer.removeLayerListener(layerListener);
                        activeLayer.getDataSource().removeEditionListener(layerListener);
                        activeLayer.getDataSource().removeDataSourceListener(layerListener);
                        try {
                                setTool(ToolManager.this.defaultTool);
                        } catch (TransitionException e2) {
                                // ignore it
                        }
                }
                this.mapContext.removeMapContextListener(mapContextListener);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
                lastMouseX = e.getPoint().x;
                lastMouseY = e.getPoint().y;

                // hack to go around bad Swing drawing when the MapControl
                // is empty AND when the new sqlConsole plugin is loaded
                // (very weird !)
                // TODO : change this one day
                if (mapContext.getLayerModel().getLayerCount() != 0) {
                        component.repaint();
                }

                setAdjustedHandler();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                        if (e.getClickCount() == 2) {
                                leftClickTransition(e, Code.TERMINATE);
                        } else {
                                leftClickTransition(e, Code.POINT);
                        }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                        if (showPopup) {
                                toolPopUp.show(component, e.getPoint().x, e.getPoint().y);
                        }
                }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                Automaton oldTool = getTool();
                try {
                        if (notches < 0) {
                                setTool(new ZoomInTool());
                        } else {
                                setTool(new ZoomOutTool());
                        }
                        leftClickTransition(e, Code.PRESS);
                        leftClickTransition(e, Code.RELEASE);
                        leftClickTransition(e, Code.POINT);
                        setTool(oldTool);
                } catch (TransitionException e1) {
                        UILOGGER.error(I18N.tr("Cannot set the automaton"), e1);
                }
        }

        private void leftClickTransition(MouseEvent e, Code transitionCode) {
                try {
                        Point2D p = e.getPoint();
                        if (worldAdjustedPoint != null) {
                                ToolManager.this.setValues(new double[]{
                                                worldAdjustedPoint.getX(), worldAdjustedPoint.getY()});
                        } else {
                                Point2D mapPoint = mapTransform.toMapPoint((int) p.getX(),
                                        (int) p.getY());
                                ToolManager.this.setValues(new double[]{mapPoint.getX(),
                                                mapPoint.getY()});
                        }
                        mouseModifiers = e.getModifiersEx();
                        transition(transitionCode);
                        fireStateChanged();
                } catch (NoSuchTransitionException e1) {
                        // ignore
                } catch (TransitionException e1) {
                        fireToolError(e1);
                }
        }

        private void fireStateChanged() {
                for (ToolListener listener : listeners) {
                        listener.stateChanged(this);
                }
        }

        private void fireCurrentToolChanged(Automaton last) {
                for (ToolListener listener : listeners) {
                        listener.currentToolChanged(last, this);
                }
        }

        private void fireToolError(TransitionException e) {
                for (ToolListener listener : listeners) {
                        listener.transitionException(this, e);
                }
        }

        @Override
        public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                        leftClickTransition(e, Code.PRESS);
                } else if (e.getButton() == MouseEvent.BUTTON2) {
                        try {
                                setTool(new PanTool());
                                leftClickTransition(e, Code.PRESS);
                        } catch (TransitionException e1) {
                                UILOGGER.error(I18N.tr("Cannot set the automaton"), e1);
                        }
                }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1
                        || e.getButton() == MouseEvent.BUTTON2) {
                        leftClickTransition(e, Code.RELEASE);
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
                        Point2D p = mapTransform.fromMapPoint(currentHandlers.get(i).getPoint());
                        if (p.distance(lastMouseX, lastMouseY) < uiTolerance) {
                                adjustedPoint = new Point((int) p.getX(), (int) p.getY());
                                worldAdjustedPoint = currentHandlers.get(i).getPoint();
                                UILOGGER.info(worldAdjustedPoint);
                                break;
                        }
                }
        }

        public void paintEdition(Graphics g) {
                try {
                        if (selectionImageDirty) {
                                selectionImage = new BufferedImage(mapTransform.getWidth(),
                                        mapTransform.getHeight(), BufferedImage.TYPE_INT_ARGB);
                                Graphics2D g2 = (Graphics2D) selectionImage.getGraphics();

                                for (Handler handler : currentHandlers) {
                                        handler.draw(g2, HANDLER_COLOR, this, mapTransform);
                                }
                                selectionImageDirty = false;
                        }
                        g.drawImage(selectionImage, 0, 0, null);
                        String error = null;
                        geomToDraw.clear();
                        textToDraw.clear();
                        try {
                                currentTool.draw(g);
                        } catch (Exception e) {
                                error = e.getMessage();
                        }
                        Graphics2D g2 = (Graphics2D) g;
                        for (int i = 0; i < geomToDraw.size(); i++) {
                                Geometry geometry = geomToDraw.get(i);
                                BufferedImage bi = new BufferedImage(mapTransform.getWidth(),
                                        mapTransform.getHeight(), BufferedImage.TYPE_INT_ARGB);
                                Graphics2D graphics = bi.createGraphics();
                                drawFeature(graphics, geometry, mapTransform, new AllowAllRenderContext());
                                g2.drawImage(bi, 0, 0, null);
                        }
                        if (adjustedPoint != null) {
                                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND,
                                        BasicStroke.JOIN_ROUND, 1.0f,
                                                new float[] { 5f, 3f, 3f, 3f }, 0));
                                g2.setColor(Color.red);
                                g2.drawArc(adjustedPoint.x - uiTolerance, adjustedPoint.y
                                        - uiTolerance, 2 * uiTolerance, 2 * uiTolerance, 0, 360);
                        }

                        if (error != null) {
                                drawTextWithWhiteBackGround(g2, error, new Point2D.Double(
                                        lastMouseX, lastMouseY));
                        } else {
                                Font f = g2.getFont();
                                g2.setFont(f.deriveFont(Font.BOLD, 16));
                                g2.setColor(Color.black);
                                int height = lastMouseY + 3 * uiTolerance;
                                for (String text : textToDraw) {
                                        g2.drawString(text, lastMouseX + uiTolerance, height);
                                        height += g2.getFontMetrics().getStringBounds(text, g2).getHeight();
                                }
                                g2.setFont(f);
                        }
                } catch(ParameterException pe) {
                        UILOGGER.error(I18N.tr("Error while drawing the feature, ")+ pe.getMessage());
                } catch(IOException ie) {
                        UILOGGER.error(I18N.tr("Error while accessing data, ")+ ie.getMessage());
                } catch(DriverException de){
                        UILOGGER.error(I18N.tr("Error while accessing data, ")+ de.getMessage());
                }
        }

        private void drawTextWithWhiteBackGround(Graphics2D g2, String text,
                Point2D p) {
                TextLayout tl = new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
                g2.setColor(Color.WHITE);
                Rectangle2D textBounds = tl.getBounds();
                g2.fill(new Rectangle2D.Double(textBounds.getX() + p.getX(), textBounds.getY()
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
                x = 0;
                y = 0;

                g.setColor(Color.BLACK);

                g.drawRect(x - uiTolerance / 2, y - uiTolerance / 2, uiTolerance,
                        uiTolerance);

                g.drawLine(x, y - 2 * uiTolerance, x, y + 2 * uiTolerance);

                g.drawLine(x - 2 * uiTolerance, y, x + 2 * uiTolerance, y);
        }

        private void updateCursor() {
                Cursor c;
                ImageIcon cursor = getTool().getCursor();
                if (cursor == null) {
                        BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(32, 32,
                                Transparency.BITMASK);
                        Graphics2D g = image.createGraphics();
                        g.setTransform(AffineTransform.getTranslateInstance(16, 16));
                        drawCursor(g);
                        Cursor crossCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(16, 16), "crossCursor");

                        c = crossCursor;
                } else {
                        Dimension size = Toolkit.getDefaultToolkit().getBestCursorSize(32,
                                32);
                        BufferedImage bi = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(
                                size.width, size.height, Transparency.BITMASK);
                        Image image = cursor.getImage();
                        int xOffset = (size.width - image.getWidth(null)) / 2;
                        int yOffset = (size.height - image.getHeight(null)) / 2;
                        bi.createGraphics().drawImage(image, xOffset, yOffset, null);

                        Point hotSpot = getTool().getHotSpotOffset();
                        hotSpot = new Point(hotSpot.x + xOffset, hotSpot.y + yOffset);
                        c = Toolkit.getDefaultToolkit().createCustomCursor(bi, hotSpot, "");
                }

                component.setCursor(c);
        }

        /**
         * @see org.orbisgis.plugins.core.layerModel.persistence.estouro.ui.MapContext#getValues()
         */
        public double[] getValues() {
                return values;
        }

        /**
         * @see org.orbisgis.plugins.core.layerModel.persistence.estouro.ui.MapContext#setValues(double[])
         */
        public void setValues(double[] values) {
                this.values = values;
        }

        /**
         * @see org.orbisgis.plugins.core.layerModel.persistence.estouro.ui.MapContext#getTolerance()
         */
        public double getTolerance() {
                return uiTolerance / mapTransform.getAffineTransform().getScaleX();
        }

        /**
         * @see org.orbisgis.plugins.core.layerModel.persistence.estouro.ui.MapContext#setUITolerance(int)
         */
        public void setUITolerance(int tolerance) {
                UILOGGER.info("setting uiTolerance: " + tolerance);
                uiTolerance = tolerance;
        }

        /**
         * @see org.orbisgis.plugins.core.layerModel.persistence.estouro.ui.MapContext#transition(java.lang.String)
         */
        public void transition(Code code) throws NoSuchTransitionException,
                TransitionException {
                if (!currentTool.isEnabled(mapContext, this)
                        && (!currentTool.getClass().equals(defaultTool))) {
                        /*
                         * Services.getService(ErrorManager.class).error( "The current tool
                         * is not enabled");
                         */
                        // throw new TransitionException("The current tool is not enabled");
                } else {
                        try {
                                currentTool.transition(code);
                                configureMenu();
                                component.repaint();
                        } catch (FinishedAutomatonException e) {
                                setTool(currentTool);
                        } catch (NoSuchTransitionException e) {
                                /*
                                 * Without this line, this exception will be catch by the "catch
                                 * (throwable)" below
                                 */
                                throw e;
                        } catch (TransitionException e) {
                                /*
                                 * Without this line, this exception will be catch by the "catch
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
                if (ToolUtilities.isRestrictedPopup(currentTool)) {
                        showPopup = false;
                } else {
                        showPopup = true;
                        String[] labels = currentTool.getTransitionLabels();
                        Code[] codes = currentTool.getTransitionCodes();
                        toolPopUp = new JPopupMenu();
                        for (int i = 0; i < codes.length; i++) {
                                JMenuItem item = new JMenuItem(labels[i]);
                                item.setActionCommand(codes[i].toString().toUpperCase());
                                item.addActionListener(new ActionListener() {

                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                                try {
                                                        transition(Code.valueOf(e.getActionCommand().toUpperCase()));
                                                        component.repaint();
                                                } catch (NoSuchTransitionException e1) {
                                                        UILOGGER.error(
                                                                I18N.tr("Error in the tool."), e1);
                                                } catch (TransitionException e1) {
                                                        fireToolError(e1);
                                                }
                                        }
                                });
                                toolPopUp.add(item);
                        }
                        // TODO (pyf): ajouter des plugins dans la popup
//                        WorkbenchContext wbContext = Services.getService(WorkbenchContext.class);
//                        JComponent[] menus = wbContext.getWorkbench().getFrame().getMenuMapTreePopup().getJMenus();
//                        for (JComponent menu : menus) {
//                                toolPopUp.add(menu);
//                        }

                }
        }

        /**
         * @throws TransitionException
         * @throws FinishedAutomatonException
         * @see org.orbisgis.plugins.core.layerModel.persistence.estouro.ui.MapContext#setEditionTool(org.orbisgis.plugins.core.ui.editors.map.tool.ag.Automaton)
         */
        public final void setTool(Automaton tool) throws TransitionException {
                Automaton lastTool = currentTool;
                try {
                        if ((currentTool != null) && (activeLayer != null)) {
                                try {
                                        currentTool.toolFinished(mapContext, this);
                                } catch (NoSuchTransitionException e) {
                                        // no way
                                } catch (TransitionException e) {
                                        // be quiet
                                }
                        }
                        currentTool = tool;
                        currentTool.init(mapContext, this);
                        configureMenu();
                        fireCurrentToolChanged(lastTool);
                        fireStateChanged();
                } catch (FinishedAutomatonException e1) {
                        setTool(defaultTool);
                }
                updateCursor();
        }

        /**
         * Last mouse position in world coordinate
         * TODO : Use directly JTS object ?
         * @return
         */
        public Point2D getLastRealMousePosition() {
                if (worldAdjustedPoint != null) {
                        return worldAdjustedPoint;
                } else {
                        return mapTransform.toMapPoint(lastMouseX, lastMouseY);
                }

        }

        /**
         * @see org.orbisgis.plugins.core.layerModel.persistence.estouro.ui.MapContext#getLastMouseX()
         */
        public int getLastMouseX() {
                if (adjustedPoint != null) {
                        return adjustedPoint.x;
                } else {
                        return lastMouseX;
                }
        }

        /**
         * @see org.orbisgis.plugins.core.layerModel.persistence.estouro.ui.MapContext#getLastMouseY()
         */
        public int getLastMouseY() {
                if (adjustedPoint != null) {
                        return adjustedPoint.y;
                } else {
                        return lastMouseY;
                }
        }

        /**
         * @see org.orbisgis.plugins.core.layerModel.persistence.estouro.ui.MapContext#getUITolerance()
         */
        public int getUITolerance() {
                return uiTolerance;
        }

        /**
         * @see org.orbisgis.plugins.core.layerModel.persistence.estouro.ui.MapContext#getCurrentHandlers()
         */
        public ArrayList<Handler> getCurrentHandlers() {
                return currentHandlers;
        }

        private void recalculateHandlers() {

                clearHandlers();

                if ((activeLayer == null) || (!activeLayer.isVisible())
                        || (activeLayer.getSelection().length == 0)) {
                        return;
                }

                DataSource sds = activeLayer.getDataSource();
                int[] selection = activeLayer.getSelection();
                try {
                        for (int selectedRow : selection) {
                                Primitive p;
                                Geometry geometry = sds.getGeometry(selectedRow);
                                if (geometry != null) {
                                        p = new Primitive(geometry, selectedRow);
                                        Handler[] handlers = p.getHandlers();
                                        currentHandlers.addAll(Arrays.asList(handlers));
                                }
                        }
                } catch (DriverException e) {
                        UILOGGER.warn(
                                I18N.tr("Cannot recalculate the handlers"), e);
                }
        }

        private void clearHandlers() {
                currentHandlers.clear();
                selectionImageDirty = true;
        }

        /**
         * @see org.orbisgis.plugins.core.layerModel.persistence.estouro.ui.MapContext#getMouseModifiers()
         */
        public int getMouseModifiers() {
                return mouseModifiers;
        }

        /**
         * @see org.orbisgis.plugins.core.layerModel.persistence.estouro.ui.MapContext#setMouseModifiers(int)
         */
        public void setMouseModifiers(int modifiers) {
                mouseModifiers = modifiers;
        }

        /**
         * @see org.orbisgis.plugins.core.layerModel.persistence.estouro.ui.MapContext#getEditionTool()
         */
        public Automaton getTool() {
                return currentTool;
        }

        public void addGeomToDraw(Geometry geom) {
                this.geomToDraw.add(geom);
        }

        public void addTextToDraw(String text) {
                this.textToDraw.add(text);
        }

        public void checkToolStatus() throws TransitionException {
                if (!currentTool.isEnabled(mapContext, this)
                        && !currentTool.getClass().equals(defaultTool.getClass())) {
                        setTool(defaultTool);
                }
        }

        public void addToolListener(ToolListener listener) {
                listeners.add(listener);
        }

        public void removeToolListener(ToolListener listener) {
                listeners.remove(listener);
        }

        public GeometryFactory getToolsFactory() {
                return toolsGeometryFactory;
        }

        public void mouseEntered(MouseEvent me) {
        }

        public void mouseExited(MouseEvent me) {
        }

        private class ToolLayerListener extends LayerListenerAdapter implements
                LayerListener, EditionListener, DataSourceListener {

                @Override
                public void selectionChanged(SelectionEvent e) {
                        recalculateHandlers();
                }

                @Override
                public void multipleModification(MultipleEditionEvent e) {
                        recalculateHandlers();
                }

                @Override
                public void singleModification(EditionEvent e) {
                        recalculateHandlers();
                }

                @Override
                public void cancel(DataSource ds) {
                        clearHandlers();
                }

                @Override
                public void commit(DataSource ds) {
                }

                @Override
                public void open(DataSource ds) {
                        recalculateHandlers();
                }
        }

        public MapTransform getMapTransform() {
                return mapTransform;
        }

        /**
         * We'll use three dedicated tymbols to draw the selected features. We
         * build them here... and that's quite long, as you'll see. Not that it
         * takes much time to the CPU, but it's hard to access the part of the
         * symbolizers we want to customize.
         */
        private void buildSymbolizers(){
                //Let's retrieve the values we want to set to our color to the
                //selected items
                int alpha = 255;
                int r = Color.YELLOW.getRed();
                int g = Color.YELLOW.getGreen();
                int b = Color.YELLOW.getBlue();
                Color col = new Color(r,g,b,alpha);
                //The point symbolizer first...
                pointSymbolizer = new PointSymbolizer();
                //From here, I just want to retrieve the color of the line and
                //the color of the fill of the mark graphic. Let's go...
                MarkGraphic mg = (MarkGraphic) pointSymbolizer.getGraphicCollection().getGraphic(0);
                ((SolidFill)mg.getFill()).setColor(new ColorLiteral(col));
                ((SolidFill)((PenStroke)mg.getStroke()).getFill()).setColor(new ColorLiteral(col));
                //Next, the line symbolizer
                lineSymbolizer = new LineSymbolizer();
                PenStroke ps = (PenStroke)lineSymbolizer.getStroke();
                ((SolidFill)(ps).getFill()).setColor(new ColorLiteral(col));
                ps.setWidth(new RealLiteral(0.1));
                //And finally, the AreaSymbolizer...
                areaSymbolizer = new AreaSymbolizer();
                PenStroke psa = (PenStroke)areaSymbolizer.getStroke();
                ((SolidFill)(psa).getFill()).setColor(new ColorLiteral(col));
                psa.setWidth(new RealLiteral(0.1));
                ((SolidFill)areaSymbolizer.getFill()).setColor(new ColorLiteral(Color.YELLOW));
        }

        /**
         * Draw selected features using the dedicated symbolizers.
         * @param graphics
         * @param geometry
         * @param mapTransform
         * @param allowAllRenderContext
         * @throws IOException
         * @throws DriverException
         * @throws ParameterException
         */
        private void drawFeature(Graphics2D graphics, Geometry geometry,
                        MapTransform mapTransform, AllowAllRenderContext allowAllRenderContext)
                        throws IOException, DriverException, ParameterException {
                if(geometry instanceof com.vividsolutions.jts.geom.Point ||
                        geometry instanceof  MultiPoint){
                        pointSymbolizer.draw(graphics, null, -1, false, mapTransform, geometry, allowAllRenderContext);
                } else if(geometry instanceof LineString || geometry instanceof MultiLineString){
                        lineSymbolizer.draw(graphics, null, -1, false, mapTransform, geometry, allowAllRenderContext);
                } else if(geometry instanceof Polygon || geometry instanceof MultiPolygon){
                        areaSymbolizer.draw(graphics, null, -1, false, mapTransform, geometry, allowAllRenderContext);
                } else {
                        //We are dealing with a geoemtry collection
                        GeometryCollection gc = (GeometryCollection) geometry;
                        int num = gc.getNumGeometries();
                        for(int i=0; i<num; i++){
                                Geometry geom = gc.getGeometryN(i);
                                drawFeature(graphics, geom, mapTransform, allowAllRenderContext);
                        }
                }
        }
}
