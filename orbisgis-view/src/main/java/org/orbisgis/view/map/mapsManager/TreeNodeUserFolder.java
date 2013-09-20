package org.orbisgis.view.map.mapsManager;

import org.orbisgis.view.components.fstree.TreeNodeFileFactoryManager;
import org.orbisgis.view.components.fstree.TreeNodeFolder;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.util.MenuCommonFunctions;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A folder that can be unlinked with map manager.
 * @author Nicolas Fortin
 */
public class TreeNodeUserFolder extends TreeNodeFolder {
    private MapsManagerPersistence mapsManagerPersistence;

    public TreeNodeUserFolder(File folderPath, TreeNodeFileFactoryManager factoryManager, MapsManagerPersistence mapsManagerPersistence) {
        super(folderPath, factoryManager);
        this.mapsManagerPersistence = mapsManagerPersistence;
    }

    /**
     * Unlink this folder
     */
    public void onUnlinkFolder() {
        model.removeNodeFromParent(this);
        List<String> current = new ArrayList<String>(mapsManagerPersistence.getMapCatalogFolderList());
        if(current.remove(getFilePath().getAbsolutePath())) {
            mapsManagerPersistence.setMapCatalogFolderList(current);
        }
    }

    @Override
    public void onDeleteFolder() {
        // Do not delete, should never be called
        throw new IllegalStateException("TreeNodeUserFolder#onDeleteFolder should never be called");
    }

    @Override
    public void feedPopupMenu(JPopupMenu menu) {
        super.feedPopupMenu(menu);
        // Add unlink menu
        JMenuItem folderRemove = new JMenuItem(I18N.tr("Unlink"),
                OrbisGISIcon.getIcon("disconnect"));
        folderRemove.setToolTipText(I18N.tr("Unlink the folder, without deleting it"));
        folderRemove.setActionCommand("unlink");
        folderRemove.addActionListener(
                EventHandler.create(ActionListener.class,
                        this, "onUnlinkFolder"));
        MenuCommonFunctions.updateOrInsertMenuItem(menu, folderRemove, false);
    }
}
