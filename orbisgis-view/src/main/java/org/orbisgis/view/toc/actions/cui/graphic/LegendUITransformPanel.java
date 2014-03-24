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
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import org.orbisgis.core.renderer.se.graphic.TransformNode;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.transform.Matrix;
import org.orbisgis.core.renderer.se.transform.Rotate;
import org.orbisgis.core.renderer.se.transform.Scale;
import org.orbisgis.core.renderer.se.transform.Transform;
import org.orbisgis.core.renderer.se.transform.Transformation;
import org.orbisgis.core.renderer.se.transform.Translate;
import org.orbisgis.view.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.icons.OrbisGISIcon;


/**
 *
 * @author Maxence Laurent
 */
public class LegendUITransformPanel extends LegendUIComponent {

    private TransformNode tNode;
    private Transform transform;

    private LegendUIAbstractPanel tools;
    private LegendUIAbstractPanel left;
    private LegendUIAbstractPanel right;


    private JButton btnAdd;
    private JButton btnRm;
    private JButton btnUp;
    private JButton btnDown;

    private JList list;
    private DefaultListModel model;

    private String[] availableTrans = {"Translate", "Scale", "Rotate", "Matrix"};
    private ArrayList<LegendUIComponent> transformations;
    private int currentTrans;


    public LegendUITransformPanel(LegendUIController ctrl, LegendUIComponent parent, TransformNode tNode) {
        super("Transform", ctrl, parent, 0, true);
        this.tNode = tNode;
        this.setBorder(BorderFactory.createTitledBorder("Transform"));

        transform = tNode.getTransform();

        if (transform == null){
            transform = new Transform();
            this.isNullComponent = true;
        }

        transformations = new ArrayList<LegendUIComponent>();

        model = new DefaultListModel();
        currentTrans = -1;
        int i;
        for (i=0;i<transform.getNumTransformation();i++){
            Transformation t = transform.getTransformation(i);
            LegendUIComponent comp = getCompForTransformation(t);
            transformations.add(comp);
            model.addElement(t);
        }

        list = new JList(model);
        list.setCellRenderer(new CellRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        btnRm = new JButton(OrbisGISIcon.getIcon("remove"));
        btnAdd = new JButton(OrbisGISIcon.getIcon("add"));
        btnDown = new JButton(OrbisGISIcon.getIcon("go-up"));
        btnUp = new JButton(OrbisGISIcon.getIcon("go-down"));


        btnRm.setMargin(new Insets(0, 0, 0, 0));
        btnAdd.setMargin(new Insets(0, 0, 0, 0));
        btnUp.setMargin(new Insets(0, 0, 0, 0));
        btnDown.setMargin(new Insets(0, 0, 0, 0));

        btnUp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int i = list.getSelectedIndex();
                if (transform.moveUp(i)) {
                    // reflect the new order in the UI
                    DefaultListModel model = (DefaultListModel) list.getModel();
                    Object g = model.remove(i);
                    model.add(i - 1, g);
                    list.setSelectedIndex(i - 1);

                    LegendUIComponent remove = transformations.remove(i);
                    transformations.add(i - 1, remove);
                    currentTrans = i - 1;
                }
            }
        });

        btnDown.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int i = list.getSelectedIndex();
                if (transform.moveDown(i)) {
                    // reflect the new order in the UI
                    DefaultListModel model = (DefaultListModel) list.getModel();
                    Object g = model.remove(i);
                    model.add(i + 1, g);
                    list.setSelectedIndex(i + 1);

                    LegendUIComponent remove = transformations.remove(i);
                    transformations.add(i + 1, remove);

                    //displayGraphic(i+1);
                    currentTrans = i + 1;
                }
            }
        });

        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Transformation t = null;

                String choice = (String) JOptionPane.showInputDialog(null,
                        "Choose a Transformation type", "Choose a transformation type",
                        JOptionPane.PLAIN_MESSAGE, null,
                        availableTrans, availableTrans[0]);

                if (choice != null) {
                    if (choice.equals(availableTrans[0])) {
                        t = new Translate(null, null);
                    } else if (choice.equals(availableTrans[1])) {
                        t = new Scale(null);
                    } else if (choice.equals(availableTrans[2])) {
                        t = new Rotate(new RealLiteral(90));
                    } else if (choice.equals(availableTrans[3])) {
                        t = new Matrix();
                    }

                    transform.addTransformation(t);
                    DefaultListModel model = (DefaultListModel) list.getModel();
                    model.addElement(t);
                    currentTrans = list.getModel().getSize() - 1;
                    list.setSelectedIndex(currentTrans);

                    LegendUIComponent comp = getCompForTransformation(t);
                    transformations.add(comp);

                    displayTransformation(currentTrans);
                }
            }
        });

        btnRm.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int showConfirmDialog = JOptionPane.showConfirmDialog(LegendUITransformPanel.this.controller.getMainPanel(), "Sur ?", "Sur ?", JOptionPane.YES_NO_OPTION);

                if (showConfirmDialog == 0) {
                    int i = list.getSelectedIndex();

                    if (transform.removeTransformation(i)) {
                        DefaultListModel model = (DefaultListModel) list.getModel();
                        model.remove(i);
                        list.setSelectedIndex(-1);
                        transformations.remove(i);

                        displayTransformation(-1);
                    }
                }
            }
        });

        btnUp.setEnabled(false);
        btnDown.setEnabled(false);
        btnRm.setEnabled(false);


        left = new LegendUIAbstractPanel(controller);
        right = new LegendUIAbstractPanel(controller);
        tools = new LegendUIAbstractPanel(controller);
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

                    displayTransformation(index);
                }
            }
        });

    }

    private void displayTransformation(int index) {
        int i;
        this.currentTrans = index;

        for (i = 0; i < transformations.size(); i++) {
            transformations.get(i).makeOrphan();
        }

        try {
            LegendUIComponent g = transformations.get(currentTrans);
            addChild(g);
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
        if (currentTrans >= 0){
           right.add(transformations.get(currentTrans));
        } else {
           right.add(new JLabel("Please select one!"));
        }

        editor.add(left, BorderLayout.WEST);
        editor.add(right, BorderLayout.EAST);
    }

    @Override
    protected void turnOff() {
        this.isNullComponent = true;
        tNode.setTransform(null);
        controller.structureChanged(this);
    }

    @Override
    protected void turnOn() {
        this.isNullComponent = false;
        tNode.setTransform(transform);
        controller.structureChanged(this);
    }

    @Override
    public Class getEditedClass() {
        return Transform.class;
    }

    private LegendUIComponent getCompForTransformation(Transformation t) {
        if (t instanceof Translate){
            return new LegendUITranslatePanel(controller, this, (Translate)t) {
                @Override
                public void translateChange(Translate newTranslate) {
                }
            };
        } else if (t instanceof Scale){
            return new LegendUIScalePanel(controller, this, (Scale)t);
        } else if (t instanceof Rotate){
            return new LegendUIRotatePanel(controller, this, (Rotate)t);
        } else if (t instanceof Matrix){
            return new LegendUIMatrixPanel(controller, this, (Matrix)t);
        }
        return null;
    }


    private static class CellRenderer extends DefaultListCellRenderer{
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            this.setIcon(OrbisGISIcon.getIcon("palette"));
            return this;
        }
    }
}
