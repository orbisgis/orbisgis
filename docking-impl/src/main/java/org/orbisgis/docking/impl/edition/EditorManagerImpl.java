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
package org.orbisgis.docking.impl.edition;


import org.orbisgis.docking.impl.edition.dialogs.SaveDocuments;
import org.orbisgis.mainframe.api.MainWindow;
import org.orbisgis.sif.docking.DockingManager;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.sif.edition.EditorFactory;
import org.orbisgis.sif.edition.EditorManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import javax.swing.JFrame;
import java.awt.event.WindowListener;
import java.beans.EventHandler;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The editor Manager is responsible of all EditorFactories.
 * This service is used to register editors and open editable elements.
 */
@Component(immediate = true)
public class EditorManagerImpl implements EditorManager {
    private List<EditorFactory> factories = new ArrayList<>();
    private DockingManager dockingManager;
    private final WindowListener exitListener = EventHandler.create(WindowListener.class, //The listener class
            this, //The event target object
            "onMainWindowClosing",//The event target method to call
            null, //the event parameter to pass(none)
            "windowClosing");
    private MainWindow mainWindow;

    @Reference
    public void setDockingManager(DockingManager dockingManager) {
        this.dockingManager = dockingManager;
    }

    public void unsetDockingManager(DockingManager dockingManager) {
        this.dockingManager = null;
    }

    // Save documents on main frame closing
    @Reference
    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        JFrame mainFrame = mainWindow.getMainFrame();
        mainFrame.addWindowListener(exitListener);    //The listener method to use
    }

    public void unsetMainWindow(MainWindow mainWindow) {
        JFrame mainFrame = mainWindow.getMainFrame();
        mainFrame.removeWindowListener(exitListener);
        this.mainWindow = null;
    }

    /**
     * The user want to close the main window Then the application has to be
     * closed
     */
    public void onMainWindowClosing() throws PropertyVetoException {
        isShutdownVetoed();
    }



    /**
     * Save or discard editable element modification. Show a dialog if there is
     * at least one unsaved editable element.
     *
     * @return True if the application must cancel the close shutdown operation
     */
    private boolean isShutdownVetoed() {
        List<EditableElement> modifiedElements = new ArrayList<>();
        Collection<EditableElement> editableElement = getEditableElements();
        for (EditableElement editable : editableElement) {
            if (editable.isModified()) {
                modifiedElements.add(editable);
            }
        }
        if (!modifiedElements.isEmpty()) {
            SaveDocuments.CHOICE userChoice = SaveDocuments.showModal(mainWindow.getMainFrame(), modifiedElements);
            // If the user do not want to save the editable elements
            // Then cancel the modifications
            if (userChoice == SaveDocuments.CHOICE.SAVE_NONE) {
                for (EditableElement element : modifiedElements) {
                    element.setModified(false);
                }
            }
            return userChoice == SaveDocuments.CHOICE.CANCEL;
        } else {
            return false;
        }
    }

    @Override
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addEditor(EditorDockable editor) {
        dockingManager.addDockingPanel(editor);
    }

    @Override
    public void removeEditor(EditorDockable editor) {
        dockingManager.removeDockingPanel(editor.getDockingParameters().getName());
    }

    @Override
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addEditorFactory(EditorFactory editorFactory) {
        factories.add(editorFactory);
        dockingManager.registerPanelFactory(editorFactory.getId(), new EditorPanelFactoryDecorator(editorFactory));
    }

    @Override
    public void removeEditorFactory(EditorFactory editorFactory) {
        factories.remove(editorFactory);
        dockingManager.unregisterPanelFactory(editorFactory.getId());
    }

    @Override
    public Collection<EditableElement> getEditableElements() {
        Set<EditableElement> editables = new HashSet<EditableElement>();
        for (EditorDockable editor : getEditors()) {
            if (editor.getEditableElement() != null) {
                editables.add(editor.getEditableElement());
            }
        }
        return editables;
    }

    @Override
    public Collection<EditorDockable> getEditors() {
        List<EditorDockable> editors = new ArrayList<>();
        for (DockingPanel panel : dockingManager.getPanels()) {
            if (panel instanceof EditorDockable) {
                editors.add((EditorDockable) panel);
            }
        }
        return editors;
    }

    @Override
    public void openEditable(EditableElement editableElement) {
        Set<EditableElement> ignoreModifiedEditables = new HashSet<EditableElement>();
        // Open the element in editors
        for (EditorDockable editor : getEditors()) {
            if (editor.match(editableElement)) {
                // Get the current loaded document
                EditableElement oldEditable = editor.getEditableElement();
                if (oldEditable != null) {
                    if (oldEditable.isModified() && !ignoreModifiedEditables.contains(oldEditable)) {
                        //Ask the user to save changes
                        //before loosing the old editable
                        List<EditableElement> modifiedDocs = new ArrayList<EditableElement>();
                        modifiedDocs.add(oldEditable);
                        SaveDocuments.CHOICE userChoice = SaveDocuments.showModal(dockingManager.getOwner(),
                                modifiedDocs);
                        if (userChoice == SaveDocuments.CHOICE.CANCEL) {
                            //The user cancel the loading of elements
                            return;
                        }
                        if (oldEditable.isModified()) {
                            // The user do not want to save this editable, do not ask for it in for the next editor
                            ignoreModifiedEditables.add(oldEditable);
                        }
                    }
                }
                editor.setEditableElement(editableElement);
            }
        }

        //Open the element in MultipleEditorFactories
        for (EditorFactory mFactory : factories) {
            DockingPanelLayout data = mFactory.makeEditableLayout(editableElement);
            if (data != null) {
                dockingManager.show(mFactory.getId(), data);
            }
        }
    }

    /**
     * Release all factories resources
     */
    public void dispose() {
        for (EditorFactory factory : factories) {
            factory.dispose();
        }
    }
}
