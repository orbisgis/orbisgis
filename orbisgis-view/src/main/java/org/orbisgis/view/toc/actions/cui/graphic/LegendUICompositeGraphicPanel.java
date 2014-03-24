/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.toc.actions.cui.graphic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import org.orbisgis.core.renderer.se.graphic.AxisChart;
import org.orbisgis.core.renderer.se.graphic.ExternalGraphic;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.graphic.PieChart;
import org.orbisgis.core.renderer.se.graphic.PointTextGraphic;
import org.orbisgis.view.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;

/**
 *
 * @author Maxence Laurent
 */
public class LegendUICompositeGraphicPanel extends LegendUIComponent {

    private GraphicCollection gc;
    private LegendUIAbstractPanel left; // menu
    private LegendUIAbstractPanel right; // editor
    private LegendUIAbstractPanel tools;

    private final JList list;
    private DefaultListModel model;
    private JButton btnUp;
    private JButton btnDown;
    private JButton btnAdd;
    private JButton btnRm;
    private static String[] availableGraphic = {"Mark", "External", "Text", "Pie chart", "Axis chart"};
    private ArrayList<LegendUIComponent> graphics;
    private int currentGraphic;

    private LegendUIComponent getCompForGraphic(Graphic graphic) {
        if (graphic instanceof MarkGraphic) {
            return new LegendUIMarkGraphicPanel(controller, this, (MarkGraphic) graphic);
        } else if (graphic instanceof ExternalGraphic) {
            return new LegendUIExternalGraphicPanel(controller, this, (ExternalGraphic) graphic);
        } else if (graphic instanceof PieChart) {
            return new LegendUIPieChartPanel(controller, this, (PieChart) graphic);
        } else if (graphic instanceof AxisChart) {
            return new LegendUIAxisChartPanel(controller, this, (AxisChart) graphic);
        } else if (graphic instanceof PointTextGraphic){
            return new LegendUIPointTextGraphicPanel(controller, this, (PointTextGraphic)graphic);
        }

        return null;
    }

    public LegendUICompositeGraphicPanel(LegendUIController ctrl, LegendUIComponent parent, GraphicCollection graphicCollection) {
        super("Graphic collection", ctrl, parent, 0, false);
        this.gc = graphicCollection;

        left = new LegendUIAbstractPanel(controller);
        right = new LegendUIAbstractPanel(controller);
        tools = new LegendUIAbstractPanel(controller);

        graphics = new ArrayList<LegendUIComponent>();

        model = new DefaultListModel();

        currentGraphic = -1;

        list = new JList(model);
        list.setCellRenderer(new CellRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnRm = new JButton(OrbisGISIcon.getIcon("remove"));
        btnAdd = new JButton(OrbisGISIcon.getIcon("add"));
        btnDown = new JButton(OrbisGISIcon.getIcon("go-down"));
        btnUp = new JButton(OrbisGISIcon.getIcon("go-up"));

        int i;
        for (i = 0; i < gc.getNumGraphics(); i++) {
            Graphic graphic = gc.getGraphic(i);
            LegendUIComponent gPanel = getCompForGraphic(graphic);
            graphics.add(gPanel);
            model.addElement(graphic);
        }


        btnRm.setMargin(new Insets(0, 0, 0, 0));
        btnAdd.setMargin(new Insets(0, 0, 0, 0));
        btnUp.setMargin(new Insets(0, 0, 0, 0));
        btnDown.setMargin(new Insets(0, 0, 0, 0));

        btnUp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int i = list.getSelectedIndex();
                if (gc.moveGraphicUp(i)) {
                    // reflect the new order in the UI
                    DefaultListModel model = (DefaultListModel) list.getModel();
                    Object g = model.remove(i);
                    model.add(i - 1, g);
                    list.setSelectedIndex(i - 1);

                    LegendUIComponent remove = graphics.remove(i);
                    graphics.add(i - 1, remove);
                    currentGraphic = i - 1;
                }
            }
        });

        btnDown.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int i = list.getSelectedIndex();
                if (gc.moveGraphicDown(i)) {
                    // reflect the new order in the UI
                    DefaultListModel model = (DefaultListModel) list.getModel();
                    Object g = model.remove(i);
                    model.add(i + 1, g);
                    list.setSelectedIndex(i + 1);

                    LegendUIComponent remove = graphics.remove(i);
                    graphics.add(i + 1, remove);

                    //displayGraphic(i+1);
                    currentGraphic = i + 1;
                }
            }
        });

        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Graphic g = null;

                String choice = (String) JOptionPane.showInputDialog(null,
                        "Choose a new graphic type", "Choose a new graphic type",
                        JOptionPane.PLAIN_MESSAGE, null,
                        availableGraphic, availableGraphic[0]);

                if (choice != null) {
                    if (choice.equals(availableGraphic[0])) {
                        g = new MarkGraphic();
                        ((MarkGraphic) g).setTo3mmCircle();
                    } else if (choice.equals(availableGraphic[1])) {
                        g = new ExternalGraphic();
                    } else if (choice.equals(availableGraphic[2])) {
                        // TEXT
                        g = new PointTextGraphic();
                    } else if (choice.equals(availableGraphic[3])) {
                        // PIE
                        g = new PieChart();
                    } else if (choice.equals(availableGraphic[4])) {
                        // AXIS
                        g = new AxisChart();
                    }

                    gc.addGraphic(g);
                    DefaultListModel model = (DefaultListModel) list.getModel();
                    model.addElement(g);
                    currentGraphic = list.getModel().getSize() - 1;
                    list.setSelectedIndex(currentGraphic);

                    LegendUIComponent compForGraphic = getCompForGraphic(g);
                    graphics.add(compForGraphic);

                    displayGraphic(currentGraphic);
                }
            }
        });

        btnRm.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int showConfirmDialog = JOptionPane.showConfirmDialog(LegendUICompositeGraphicPanel.this.controller.getMainPanel(), "Sur ?", "Sur ?", JOptionPane.YES_NO_OPTION);

                if (showConfirmDialog == 0) {
                    int i = list.getSelectedIndex();

                    if (gc.delGraphic(i)) {
                        DefaultListModel model = (DefaultListModel) list.getModel();
                        model.remove(i);
                        list.setSelectedIndex(-1);
                        graphics.remove(i);

                        displayGraphic(-1);
                    }
                }
            }
        });

        btnUp.setEnabled(false);
        btnDown.setEnabled(false);
        btnRm.setEnabled(false);

        tools.setLayout(new FlowLayout(FlowLayout.TRAILING));


        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                list.setSelectedIndex(index);

                btnUp.setEnabled(false);
                btnDown.setEnabled(false);
                btnRm.setEnabled(false);

                if (index >= 0) {
                    // First, update button status
                    if (list.getModel().getSize() > 1) { //dont delete the last graphic
                        btnRm.setEnabled(true);
                    }

                    if (index > 0) {
                        // Turn the move down button on
                        btnUp.setEnabled(true);
                    }
                    if (index < list.getModel().getSize() - 1) {
                        btnDown.setEnabled(true);
                    }

                    displayGraphic(index);
                }
            }
        });
    }

    /**
     *
     */
    private void displayGraphic(int index) {
        int i;
        this.currentGraphic = index;

        for (i = 0; i < graphics.size(); i++) {
            graphics.get(i).makeOrphan();
        }

        try {
            LegendUIComponent g = graphics.get(currentGraphic);
            LegendUICompositeGraphicPanel.this.addChild(g);
            controller.structureChanged(g);
        } catch (Exception e) {
            controller.structureChanged(this);
        }
    }

    @Override
    public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette");
    }

    @Override
    protected void mountComponent() {
        left.removeAll();
        left.add(list, BorderLayout.NORTH);

        tools.removeAll();

        tools.add(btnUp);
        tools.add(btnAdd);
        tools.add(btnRm);
        tools.add(btnDown);

        left.add(tools, BorderLayout.SOUTH);


        right.removeAll();
        if (currentGraphic >= 0) {
            right.add(graphics.get(currentGraphic));
        } else {
            right.add(new JLabel("Please select one!"));
        }

        editor.add(left, BorderLayout.WEST);
        editor.add(right, BorderLayout.EAST);
    }

    @Override
    protected void turnOff() {
    }

    @Override
    protected void turnOn() {
    }

    private class CellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            Graphic g = (Graphic) value;

            this.setIcon(OrbisGISIcon.getIcon("image"));

            return this;
        }
    }

    @Override
    public Class getEditedClass() {
        return GraphicCollection.class;
    }
}
