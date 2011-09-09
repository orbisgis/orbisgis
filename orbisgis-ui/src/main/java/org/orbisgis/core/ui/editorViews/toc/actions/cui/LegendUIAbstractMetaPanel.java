/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

/**
 *
 * @author maxence
 */
public abstract class LegendUIAbstractMetaPanel extends LegendUIComponent {

    private JButton changeType;
    //private LegendUIType[] types;
    private LegendUIComponent[] comps;
    //private LegendUIType currentType;
    private int currentComp;
    private ClassWrapper[] classes;

    public LegendUIAbstractMetaPanel(String name, LegendUIController controller, LegendUIComponent parent,
            float weight, boolean isNullable) {
        super(name, controller, parent, weight, isNullable);

        this.setBorder(BorderFactory.createTitledBorder(name));

        changeType = new JButton(OrbisGISIcon.SE_CHANGE_TYPE);
        changeType.setMargin(new Insets(0, 0, 0, 0));
        changeType.addActionListener(new ChangeTypeListener(this));

        currentComp = 0;
    }

    protected void updateTitle() {
        if (LegendUIAbstractMetaPanel.this.getName() == null) {
            TitledBorder border = (TitledBorder) LegendUIAbstractMetaPanel.this.getBorder();
            border.setTitle(comps[currentComp].getEditedClass().getSimpleName());
        }
    }

    /**
     *
     * @param availableTypes
     * @param initialType
     * @param initialPanel
     */
    protected void init(Class[] classes, LegendUIComponent comp) {
        this.classes = new ClassWrapper[classes.length];

        int i = 0;
        for (Class cl : classes) {
            this.classes[i] = new ClassWrapper(cl);
            i++;
        }

        this.comps = new LegendUIComponent[classes.length];
        if (comp != null) {
            this.currentComp = getIndex(comp);
            comps[currentComp] = comp;
            updateTitle();
            switchTo(comps[currentComp]);
        } else {
            isNullComponent = true;
        }

    }

    /**
     * call right after creating new instances !
     */
    public abstract void init();

    @Override
    protected void turnOff() {
        this.isNullComponent = true;
        switchTo(null);
        controller.structureChanged(this.getParentComponent());
    }

    @Override
    protected void turnOn() {
        this.isNullComponent = false;

        if (comps[currentComp] == null) {
            comps[currentComp] = getCompForClass(classes[currentComp].mClass);
        }

        switchTo(comps[currentComp]);
        controller.structureChanged(this.getParentComponent());
    }

    /**
     *
     * @param type the new type of sub panel as selected by the user
     * @param newActiveComp the corresponding UI component
     */
    protected abstract void switchTo(LegendUIComponent newActiveComp);

    private int getIndex(LegendUIComponent comp) {
        Class cl = comp.getEditedClass();
        int i;
        for (i = 0; i < classes.length; i++) {
            if (classes[i].mClass.equals(cl)) {
                return i;
            }
        }
        return 0;
    }

    private int getClassIndex(Class cl) {
        int i;
        for (i = 0; i < classes.length; i++) {
            if (classes[i].mClass.equals(cl)) {
                return i;
            }
        }
        return 0;
    }

    private class ChangeTypeListener implements ActionListener {

        private final LegendUIComponent metaPanel;

        public ChangeTypeListener(LegendUIComponent metaPanel) {
            super();
            this.metaPanel = metaPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ClassWrapper newClass = (ClassWrapper) JOptionPane.showInputDialog(null,
                    "Choose a new type", "Choose a new type",
                    JOptionPane.PLAIN_MESSAGE, null,
                    classes, classes[currentComp]);

            if (newClass != null) {
                // First detach old-child
                comps[currentComp].makeOrphan();

                // then be sure the new child is OK
                currentComp = getClassIndex(newClass.mClass);

                if (comps[currentComp] == null) {
                    // If child doesn't exists, request it !
                    comps[currentComp] = getCompForClass(newClass.mClass);
                }
                // Insert the new one in the tree
                metaPanel.addChild(comps[currentComp]);

                LegendUIAbstractMetaPanel.this.updateTitle();

                // Update SE Model
                switchTo(comps[currentComp]);
                // And finally update the UI Structure
                controller.structureChanged(comps[currentComp]);

            }
        }
    }

    protected abstract LegendUIComponent getCompForClass(Class newClass);

    @Override
    protected void mountComponent() {
        toolbar.add(changeType);

        if (comps != null) {
            if (comps[currentComp] == null) {
                comps[currentComp] = getCompForClass(classes[currentComp].mClass);
            }
            editor.add(comps[currentComp]);
        }
    }

    public LegendUIComponent getCurrentComponent() {
        return this.comps[currentComp];
    }

    private class ClassWrapper {

        Class mClass;

        public ClassWrapper(Class theClass) {
            mClass = theClass;
        }

        @Override
        public String toString() {
            return mClass.getSimpleName();
        }
    }
}
