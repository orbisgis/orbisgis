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
package org.orbisgis.view.toc.actions.cui.stroke;

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

import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

import org.orbisgis.core.renderer.se.stroke.AlternativeStrokeElements;
import org.orbisgis.core.renderer.se.stroke.Stroke;
import org.orbisgis.core.renderer.se.stroke.CompoundStroke;
import org.orbisgis.core.renderer.se.stroke.CompoundStrokeElement;
//import org.orbisgis.core.renderer.se.stroke.StrokeAnnotationGraphic;
import org.orbisgis.core.renderer.se.stroke.StrokeElement;

import org.orbisgis.view.toc.actions.cui.LegendUIAbstractPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.components.CheckBoxInput;
import org.orbisgis.view.toc.actions.cui.components.UomInput;
import org.orbisgis.view.toc.actions.cui.parameter.real.LegendUIMetaRealPanel;

import org.orbisgis.view.icons.OrbisGISIcon;

/**
 *
 * @author Maxence Laurent
 */
public abstract class LegendUICompoundStrokePanel extends LegendUIComponent implements LegendUIStrokeComponent {

    private final CompoundStroke compoundStroke;


    private UomInput uom;


    private LegendUIMetaRealPanel preGap;


    private LegendUIMetaRealPanel postGap;

    private CheckBoxInput linearRapport;

    private LegendUIAbstractPanel content1;
    private LegendUIAbstractPanel content1a;
    private LegendUIAbstractPanel content1b;


    private LegendUIAbstractPanel content2;


    private LegendUIAbstractPanel content3;


    private LegendUIAbstractPanel elemLeft;


    private LegendUIAbstractPanel elemTools;


    private LegendUIAbstractPanel elemEditor;


    private static String[] availableElement = {"Element", "Alternative List"};


    private JButton btnElemUp;


    private JButton btnElemDown;


    private JButton btnElemAdd;


    private JButton btnElemRm;


    private ArrayList<LegendUIComponent> elements;


    private JList elemList;


    private DefaultListModel elemModel;


    private int currentElem;

    /*
    private LegendUIAbstractPanel annoLeft;
    private LegendUIAbstractPanel annoTools;
    private LegendUIAbstractPanel annoEditor;
    private JButton btnAnnoUp;
    private JButton btnAnnoDown;
    private JButton btnAnnoAdd;
    private JButton btnAnnoRm;
    private ArrayList<LegendUIComponent> annotations;
    private JList annoList;
    private DefaultListModel annoModel;
    private int currentAnno;*/

    //private LegendUIComponent getCompForAnnotation(StrokeAnnotationGraphic anno) {
    //    return new LegendUIStrokeAnnotationGraphicPanel(controller, this, anno);
    //}


    private LegendUIComponent getCompForElement(CompoundStrokeElement elem) {
        if (elem instanceof StrokeElement) {
            return new LegendUIStrokeElementPanel(controller, this, (StrokeElement) elem);
        } else if (elem instanceof AlternativeStrokeElements) {
            throw new RuntimeException("NOT YET :: (h)ALT ELEM !!!");
        }
        return null;
    }


    public LegendUICompoundStrokePanel(LegendUIController controller,
                                       LegendUIComponent parent,
                                       CompoundStroke cStroke,
                                       boolean isNullable) {
        super("compound stroke", controller, parent, 0, isNullable);
        //this.setLayout(new GridLayout(0,2));
        this.compoundStroke = cStroke;

        content1 = new LegendUIAbstractPanel(controller);
        content1a = new LegendUIAbstractPanel(controller);
        content1b = new LegendUIAbstractPanel(controller);
        
        content2 = new LegendUIAbstractPanel(controller);
        content3 = new LegendUIAbstractPanel(controller);
        elemEditor = new LegendUIAbstractPanel(controller);
        elemTools = new LegendUIAbstractPanel(controller);
        elemLeft = new LegendUIAbstractPanel(controller);
        //annoEditor = new LegendUIAbstractPanel(controller);
        //annoTools = new LegendUIAbstractPanel(controller);
        //annoLeft = new LegendUIAbstractPanel(controller);

        uom = new UomInput(compoundStroke);

        preGap = new LegendUIMetaRealPanel("PreGap", controller, this, compoundStroke.getPreGap(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                compoundStroke.setPreGap(newReal);
            }


        };
        preGap.init();

        postGap = new LegendUIMetaRealPanel("PostGap", controller, this, compoundStroke.getPostGap(), true) {

            @Override
            public void realChanged(RealParameter newReal) {
                compoundStroke.setPostGap(newReal);
            }


        };
        postGap.init();

        this.linearRapport = new CheckBoxInput("LinRap.", compoundStroke.isLengthRapport()) {
            @Override
            protected void valueChanged(boolean newValue) {
                 compoundStroke.setLengthRapport(newValue);
            }
        };



        elements = new ArrayList<LegendUIComponent>();
        elemModel = new DefaultListModel();
        currentElem = -1;

        int i;
        for (i = 0; i < compoundStroke.getElements().size(); i++) {
            CompoundStrokeElement elem = compoundStroke.getElements().get(i);
            LegendUIComponent comp = getCompForElement(elem);
            elements.add(comp);
            elemModel.addElement(elem);
        }

        elemList = new JList(elemModel);
        elemList.setCellRenderer(new ElemCellRenderer());
        elemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnElemRm = new JButton(OrbisGISIcon.getIcon("remove"));
        btnElemAdd = new JButton(OrbisGISIcon.getIcon("add"));
        btnElemDown = new JButton(OrbisGISIcon.getIcon("go-down"));
        btnElemUp = new JButton(OrbisGISIcon.getIcon("go-up"));


        btnElemRm.setMargin(new Insets(0, 0, 0, 0));
        btnElemAdd.setMargin(new Insets(0, 0, 0, 0));
        btnElemUp.setMargin(new Insets(0, 0, 0, 0));
        btnElemDown.setMargin(new Insets(0, 0, 0, 0));

        btnElemUp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int i = elemList.getSelectedIndex();
                if (compoundStroke.moveElementUp(i)) {
                    DefaultListModel model = (DefaultListModel) elemList.getModel();
                    Object elem = model.remove(i);
                    model.add(i - 1, elem);
                    elemList.setSelectedIndex(i - 1);

                    LegendUIComponent remove = elements.remove(i);
                    elements.add(i - 1, remove);
                    currentElem = i - 1;
                }
            }


        });

        btnElemDown.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int i = elemList.getSelectedIndex();
                if (compoundStroke.moveElementDown(i)) {
                    // reflect the new order in the UI
                    DefaultListModel model = (DefaultListModel) elemList.getModel();
                    Object g = model.remove(i);
                    model.add(i + 1, g);
                    elemList.setSelectedIndex(i + 1);

                    LegendUIComponent remove = elements.remove(i);
                    elements.add(i + 1, remove);

                    currentElem = i + 1;
                }
            }


        });

        btnElemAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CompoundStrokeElement elem = null;

                String choice = (String) JOptionPane.showInputDialog(null,
                                                                     "Choose a new graphic type", "Choose a new graphic type",
                                                                     JOptionPane.PLAIN_MESSAGE, null,
                                                                     availableElement, availableElement[0]);

                if (choice != null) {
                    if (choice.equals(availableElement[0])) {
                        elem = new StrokeElement();
                    }
                    if (choice.equals(availableElement[1])) {
                        elem = new AlternativeStrokeElements();
                    }

                    compoundStroke.addElement(elem);
                    DefaultListModel model = (DefaultListModel) elemList.getModel();
                    model.addElement(elem);
                    currentElem = elemList.getModel().getSize() - 1;
                    elemList.setSelectedIndex(currentElem);

                    LegendUIComponent comp = getCompForElement(elem);
                    elements.add(comp);

                    displayElement(currentElem);
                }
            }


        });

        btnElemRm.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int showConfirmDialog = JOptionPane.showConfirmDialog(LegendUICompoundStrokePanel.this.controller.getMainPanel(),
                                                                      "Sur ?", "Sur ?", JOptionPane.YES_NO_OPTION);

                if (showConfirmDialog == 0) {
                    int i = elemList.getSelectedIndex();

                    if (compoundStroke.removeElement(i)) {
                        DefaultListModel model = (DefaultListModel) elemList.getModel();
                        model.remove(i);
                        elemList.setSelectedIndex(-1);
                        elements.remove(i);

                        displayElement(-1);
                    }
                }

            }


        });

        btnElemUp.setEnabled(false);
        btnElemDown.setEnabled(false);
        btnElemRm.setEnabled(false);

        elemTools.setLayout(new FlowLayout(FlowLayout.TRAILING));

        elemList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int index = elemList.locationToIndex(e.getPoint());
                elemList.setSelectedIndex(index);

                btnElemUp.setEnabled(false);
                btnElemDown.setEnabled(false);
                btnElemRm.setEnabled(false);

                if (index >= 0) {
                    // First, update button status
                    if (elemList.getModel().getSize() > 1) { //dont delete the last graphic
                        btnElemRm.setEnabled(true);
                    }

                    if (index > 0) {
                        // Turn the move down button on
                        btnElemUp.setEnabled(true);
                    }
                    if (index < elemList.getModel().getSize() - 1) {
                        btnElemDown.setEnabled(true);
                    }

                    displayElement(index);
                }
            }


        });


        /* STROKE ANNOTATION GRAPHIC */

        //annotations = new ArrayList<LegendUIComponent>();
        //annoModel = new DefaultListModel();
        //currentAnno = -1;

        /*
        for (i = 0; i < compoundStroke.getAnnotations().size(); i++) {
        StrokeAnnotationGraphic anno = compoundStroke.getAnnotations().get(i);
        LegendUIComponent comp = getCompForAnnotation(anno);
        annotations.add(comp);
        annoModel.addElement(anno);
        }
        
        annoList = new JList(annoModel);
        annoList.setCellRenderer(new AnnoCellRenderer());
        annoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        btnAnnoRm = new JButton(OrbisGISIcon.getIcon("remove"));
        btnAnnoAdd = new JButton(OrbisGISIcon.getIcon("add"));
        btnAnnoDown = new JButton(OrbisGISIcon.getIcon("go-down"));
        btnAnnoUp = new JButton(OrbisGISIcon.getIcon("go-up"));
        
        
        btnAnnoRm.setMargin(new Insets(0, 0, 0, 0));
        btnAnnoAdd.setMargin(new Insets(0, 0, 0, 0));
        btnAnnoUp.setMargin(new Insets(0, 0, 0, 0));
        btnAnnoDown.setMargin(new Insets(0, 0, 0, 0));
        
        btnAnnoUp.addActionListener(new ActionListener() {
        
        @Override
        public void actionPerformed(ActionEvent e) {
        int i = annoList.getSelectedIndex();
        if (compoundStroke.moveAnnotationUp(i)) {
        DefaultListModel model = (DefaultListModel) annoList.getModel();
        Object anno = model.remove(i);
        model.add(i - 1, anno);
        annoList.setSelectedIndex(i - 1);
        
        LegendUIComponent remove = annotations.remove(i);
        annotations.add(i - 1, remove);
        currentAnno = i - 1;
        }
        }
        });
        
        btnAnnoDown.addActionListener(new ActionListener() {
        
        @Override
        public void actionPerformed(ActionEvent e) {
        int i = annoList.getSelectedIndex();
        if (compoundStroke.moveAnnotationDown(i)) {
        // reflect the new order in the UI
        DefaultListModel model = (DefaultListModel) annoList.getModel();
        Object g = model.remove(i);
        model.add(i + 1, g);
        annoList.setSelectedIndex(i + 1);
        
        LegendUIComponent remove = annotations.remove(i);
        annotations.add(i + 1, remove);
        
        currentAnno = i + 1;
        }
        }
        });
        
        btnAnnoAdd.addActionListener(new ActionListener() {
        
        @Override
        public void actionPerformed(ActionEvent e) {
        StrokeAnnotationGraphic anno = new StrokeAnnotationGraphic();
        
        compoundStroke.addAnnotation(anno);
        DefaultListModel model = (DefaultListModel) annoList.getModel();
        model.addElement(anno);
        currentAnno = annoList.getModel().getSize() - 1;
        annoList.setSelectedIndex(currentAnno);
        
        LegendUIComponent comp = getCompForAnnotation(anno);
        annotations.add(comp);
        
        displayAnnotation(currentAnno);
        }
        });
        
        btnAnnoRm.addActionListener(new ActionListener() {
        
        @Override
        public void actionPerformed(ActionEvent e) {
        int showConfirmDialog = JOptionPane.showConfirmDialog(LegendUICompoundStrokePanel.this.controller.getMainPanel(),
        "Sur ?", "Sur ?", JOptionPane.YES_NO_OPTION);
        
        if (showConfirmDialog == 0) {
        int i = annoList.getSelectedIndex();
        
        if (compoundStroke.removeAnnotation(i)) {
        DefaultListModel model = (DefaultListModel) annoList.getModel();
        model.remove(i);
        annoList.setSelectedIndex(-1);
        annotations.remove(i);
        
        displayAnnotation(-1);
        }
        }
        
        }
        });
        
        btnAnnoUp.setEnabled(false);
        btnAnnoDown.setEnabled(false);
        btnAnnoRm.setEnabled(false);
        
        annoTools.setLayout(new FlowLayout(FlowLayout.TRAILING));
        
        annoList.addMouseListener(new MouseAdapter() {
        
        @Override
        public void mouseClicked(MouseEvent e) {
        int index = annoList.locationToIndex(e.getPoint());
        annoList.setSelectedIndex(index);
        
        btnAnnoUp.setEnabled(false);
        btnAnnoDown.setEnabled(false);
        btnAnnoRm.setEnabled(false);
        
        if (index >= 0) {
        // First, update button status
        
        if (annoList.getModel().getSize() > 0) { //dont delete the last graphic
        btnAnnoRm.setEnabled(true);
        }
        
        if (index > 0) {
        // Turn the move down button on
        btnAnnoUp.setEnabled(true);
        }
        if (index < annoList.getModel().getSize() - 1) {
        btnAnnoDown.setEnabled(true);
        }
        
        displayAnnotation(index);
        }
        }
        });
        }
        
         * 
         */
    }


    /*
    private void displayAnnotation(int index) {
        int i;
        this.currentAnno = index;

        for (i = 0; i < annotations.size(); i++) {
            annotations.get(i).makeOrphan();
        }

        try {
            LegendUIComponent g = annotations.get(currentAnno);
            LegendUICompoundStrokePanel.this.addChild(g);
            controller.structureChanged(g);
        } catch (Exception e) {
            controller.structureChanged(this);
        }
    }
    */

    private void displayElement(int index) {
        int i;
        this.currentElem = index;

        for (i = 0; i < elements.size(); i++) {
            elements.get(i).makeOrphan();
        }

        try {
            LegendUIComponent g = elements.get(currentElem);
            LegendUICompoundStrokePanel.this.addChild(g);
            controller.structureChanged(g);
        } catch (Exception e) {
            controller.structureChanged(this);
        }
    }


    @Override
    public Icon getIcon() {
        return OrbisGISIcon.getIcon("pencil");
    }


    @Override
    protected void mountComponent() {
        content1.removeAll();
        content1a.removeAll();
        content1b.removeAll();
        content2.removeAll();
        content3.removeAll();

        content1a.add(uom, BorderLayout.EAST);
        content1a.add(linearRapport, BorderLayout.WEST);
        content1b.add(preGap, BorderLayout.WEST);
        content1b.add(postGap, BorderLayout.EAST);

        content1.add(content1a, BorderLayout.NORTH);
        content1.add(content1b, BorderLayout.SOUTH);

        elemTools.removeAll();
        elemLeft.removeAll();
        elemEditor.removeAll();

        elemTools.add(btnElemAdd);
        elemTools.add(btnElemUp);
        elemTools.add(btnElemDown);
        elemTools.add(btnElemRm);

        elemLeft.add(elemList, BorderLayout.NORTH);
        elemLeft.add(elemTools, BorderLayout.SOUTH);

        if (currentElem >= 0) {
            elemEditor.add(elements.get(currentElem));
        } else {
            elemEditor.add(new JLabel("Please select one!"));
        }
        content2.add(elemLeft, BorderLayout.WEST);
        content2.add(elemEditor, BorderLayout.EAST);

        content2.setBorder(BorderFactory.createTitledBorder("Pattern"));

        /*
        annoTools.removeAll();
        annoLeft.removeAll();
        annoEditor.removeAll();
        
        annoTools.add(btnAnnoAdd);
        annoTools.add(btnAnnoUp);
        annoTools.add(btnAnnoDown);
        annoTools.add(btnAnnoRm);
        
        annoLeft.add(annoList, BorderLayout.NORTH);
        annoLeft.add(annoTools, BorderLayout.SOUTH);
        
        if (currentAnno >= 0) {
        annoEditor.add(annotations.get(currentAnno));
        } else {
        annoEditor.add(new JLabel("Please select one!"));
        }
        
        content3.add(annoLeft, BorderLayout.WEST);
        content3.add(annoEditor, BorderLayout.EAST);
        content3.setBorder(BorderFactory.createTitledBorder("Annotations"));
         */

        editor.add(content1, BorderLayout.NORTH);
        editor.add(content2, BorderLayout.CENTER);
        editor.add(content3, BorderLayout.SOUTH);
    }


    @Override
    public Stroke getStroke() {
        return this.compoundStroke;
    }


    @Override
    public Class getEditedClass() {
        return CompoundStroke.class;
    }


    private class ElemCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            this.setIcon(OrbisGISIcon.getIcon("line"));
            return this;
        }


    }

    private class AnnoCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            this.setIcon(OrbisGISIcon.getIcon("image"));
            return this;
        }


    }
}
