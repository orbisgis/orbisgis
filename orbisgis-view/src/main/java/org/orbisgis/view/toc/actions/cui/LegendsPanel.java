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
package org.orbisgis.view.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryTypeConstraint;
import org.gdms.data.types.Type;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.GeometryProperties;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.factory.LegendFactory;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.orbisgis.view.toc.wrapper.RuleWrapper;
import org.orbisgis.view.toc.wrapper.StyleWrapper;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This {@code Panel} contains all the needed informations to build an UI that
 * will let the user edit the legends. It is built with the following properties
 * :</p> <ul><li>Legends are displayed in the {@code LegendList}.</li> <li>An
 * inner list of available legends. It may be initialized using
 * {@code EPLegendHelper. It is used to validate a given {@code Legend}, in
 * order to determine if it can be edited or not.</li> <li>A {@code CardLayout}
 * that is used to switch fast between the {@code
 * Legend} instances stored in {@code legends}</li> <li>Two text fields : one
 * for the min scale, the other for the max scale.</li> <li>Two buttons that are
 * used to fastly set the min and/or max scales to the current one.</li> <li>A {@code MapTransform}
 * that represents the current state of the map</li> <li>A {@code Type} instance
 * (should be the type of the {@code DataSource} associated to the layer
 * associated to the legend we want to edit.</li> </ul>
 *
 * @author Alexis Guéganno, others...
 */
public class LegendsPanel extends JPanel implements UIPanel, LegendContext {

        private static final I18n I18N = I18nFactory.getI18n(LegendsPanel.class);
        private static final String NO_LEGEND_ID = "no-legend";
        private int geometryType;
        private LegendTree legendTree;
        private ILegendPanel[] availableLegends;
        private JPanel pnlContainer;
        private CardLayout cardLayout;
        private static String lastUID = "";
        private Type gc;
        private ILayer layer;
        private MapTransform mt;
        private StyleWrapper styleWrapper;

        public void init(MapTransform mt, Type gc, Style style, ILegendPanel[] availableLegends,
                ILayer layer) {
                this.mt = mt;
                this.gc = gc;
                this.layer = layer;
                if (gc == null) {
                        geometryType = GeometryProperties.ALL;
                } else {
                        switch (gc.getTypeCode()) {
                                case Type.POINT:
                                case Type.MULTIPOINT:
                                        geometryType = GeometryProperties.POINT;
                                        break;
                                case Type.LINESTRING:
                                case Type.MULTILINESTRING:
                                        geometryType = GeometryProperties.LINE;
                                        break;
                                case Type.POLYGON:
                                case Type.MULTIPOLYGON:
                                        geometryType = GeometryProperties.POLYGON;
                                        break;
                                case Type.GEOMETRYCOLLECTION:
                                case Type.GEOMETRY:
                                        geometryType = GeometryProperties.ALL;
                                        break;
                        }
                }

                this.availableLegends = Arrays.copyOf(availableLegends, availableLegends.length);
                initializeComponents();
                List<RuleWrapper> lrw = new LinkedList<RuleWrapper>();
                for (int i = 0; i < style.getRules().size(); i++) {
                        Rule r = style.getRules().get(i);
                        List<Symbolizer> sym = r.getCompositeSymbolizer().getSymbolizerList();
                        List<ILegendPanel> ll = new LinkedList<ILegendPanel>();
                        for (Symbolizer s : sym) {
                                Legend leg = LegendFactory.getLegend(s);
                                ILegendPanel ilp = getPanel(leg);
                                ilp.setId(getNewId());
                                ll.add(ilp);
                                pnlContainer.add(ilp.getComponent(), ilp.getId());
                        }
                        RuleWrapper rw = new RuleWrapper(r, ll);
                        rw.getPanel().setId(getNewId());
                        rw.getPanel().initialize(this);
                        pnlContainer.add(rw.getPanel().getComponent(), rw.getPanel().getId());
                        lrw.add(rw);
                }
                styleWrapper = new StyleWrapper(style, lrw);
                styleWrapper.getPanel().setId(getNewId());
                pnlContainer.add(styleWrapper.getPanel().getComponent(), styleWrapper.getPanel().getId());
                legendTree = new LegendTree(this);
                this.add(legendTree, BorderLayout.WEST);
                refreshLegendContainer();
        }

        /**
         * Get a new unique ID used to retrieve panels in the CardLayout.
         *
         * @return
         */
        public static String getNewId() {
                String name = "gdms" + System.currentTimeMillis();
                while (name.equals(lastUID)) {
                        name = "" + System.currentTimeMillis();
                }
                lastUID = name;
                return name;
        }

        private void initializeComponents() {
                this.setLayout(new BorderLayout());
                this.add(getLegendToolBar(), BorderLayout.NORTH);
                JPanel right = new JPanel();
                right.setLayout(new BorderLayout());
                right.add(getLegendContainer(), BorderLayout.CENTER);
                this.add(right, BorderLayout.CENTER);

        }

        private JToolBar getLegendToolBar() {
                JToolBar toolBar = new JToolBar();
                toolBar.add(new JLabel(I18N.tr("Layer :") + layer.getName()));
                toolBar.setFloatable(false);
                return toolBar;
        }

        private JPanel getLegendContainer() {
                pnlContainer = new JPanel();
                pnlContainer.setPreferredSize(new Dimension(600, 400));
                cardLayout = new CardLayout();
                pnlContainer.setLayout(cardLayout);
                pnlContainer.add(new JLabel(I18N.tr("Add or select a legend on the left")),
                        NO_LEGEND_ID);
                return pnlContainer;
        }

        private ILegendPanel getPanel(Legend legend) {
                for (ILegendPanel panel : availableLegends) {
                        if (panel.getLegend().getLegendTypeId().equals(
                                legend.getLegendTypeId())) {
                                ILegendPanel ilp = (ILegendPanel) newInstance(panel);
                                ilp.setLegend(legend);
                                return ilp;
                        }
                }

                return new NoPanel(legend);
        }

        public ILegendPanel[] getAvailableLegends() {
                return availableLegends;
        }

        @Override
        public int getGeometryType() {
                return geometryType;
        }

        @Override
        public boolean isLine() {
                return (geometryType & GeometryProperties.LINE) > 0;
        }

        @Override
        public boolean isPoint() {
                return (geometryType & GeometryProperties.POINT) > 0;
        }

        @Override
        public boolean isPolygon() {
                return (geometryType & GeometryProperties.POLYGON) > 0;
        }

        void refreshLegendContainer() {
                //We need to retrieve the currently selected legend in the tree,
                //then find its id, and finally use it to show the panel.
                ISELegendPanel selected = legendTree.getSelectedPanel();
                if (selected != null) {
                        cardLayout.show(pnlContainer, selected.getId());
                } else {
                        cardLayout.show(pnlContainer, NO_LEGEND_ID);
                }
        }

        public void legendRemoved(ISELegendPanel panel) {
                cardLayout.removeLayoutComponent(panel.getComponent());
                refreshLegendContainer();
        }

        public void legendAdded(ISELegendPanel panel) {
                //We can cast safely as we KNOW we are already dealing with a LegendPanel.
                panel.initialize(this);
                panel.setId(getNewId());
                pnlContainer.add(panel.getComponent(), panel.getId());
                refreshLegendContainer();
        }

        private ISELegendPanel newInstance(ISELegendPanel panel) {
                ISELegendPanel ret = panel.newInstance();
                ret.initialize(this);

                return ret;
        }

        public void legendRenamed(int idx, String newName) {
//		legends.get(idx).getLegend().setName(newName);
                refreshLegendContainer();
        }

        public void legendSelected() {
                refreshLegendContainer();
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public URL getIconURL() {
                return UIFactory.getDefaultIcon();
        }

        @Override
        public String getTitle() {
                return I18N.tr("Legend edition");
        }


        @Override
        public String validateInput() {
                if (!legendTree.hasLegend()) {
                        return I18N.tr("You must create almost one legend");
                }
                List<String> errors = styleWrapper.validateInput();
                StringBuilder sb = new StringBuilder();
                for (String message : errors) {
                        if (message != null && !message.isEmpty()) {
                                sb.append(message);
                                sb.append("\n");
                        }
                }
                String err = sb.toString();
                if (err != null && !err.isEmpty()) {
                        return err;
                }
                return null;
        }

        @Override
        public GeometryTypeConstraint getGeometryTypeConstraint() {
                return (GeometryTypeConstraint) gc.getConstraint(Constraint.GEOMETRY_TYPE);
        }

        @Override
        public ILayer getLayer() {
                return layer;
        }

        @Override
        public MapTransform getCurrentMapTransform() {
                return mt;
        }

        public StyleWrapper getStyleWrapper() {
                return styleWrapper;
        }

     
}
