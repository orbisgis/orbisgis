package org.orbisgis.views.geocognition.sync.tree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.orbisgis.Services;
import org.orbisgis.images.IconLoader;
import org.orbisgis.views.geocognition.sync.SyncManager;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;

public class CompareTreeRenderer extends DefaultTreeCellRenderer {
	private static final ImageIcon ADDED = IconLoader
			.getIcon("add_blended.png");
	private static final ImageIcon DELETED = IconLoader
			.getIcon("delete_blended.png");
	private static final ImageIcon CONTENT_MODIFIED = IconLoader
			.getIcon("modify_blended.png");
	private static final ImageIcon CONFLICT = IconLoader
			.getIcon("error_blended.png");

	private static final int ADDITIONAL_ICON_WIDTH = 5;
	private static final int BLENDED_ICON_EXPANSION = 3;

	private ElementRenderer[] renderers;
	private SyncManager manager;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		if (value instanceof TreeElement) {
			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
			TreeElement element = (TreeElement) value;

			// Get background icon
			ImageIcon background = (ImageIcon) getRendererIcon(element);
			setIcon(new BlendedIcon(background, background));

			// Get foreground icon
			ImageIcon foreground;
			ArrayList<String> path = element.getIdPath();
			if (manager.isAdded(path)) {
				foreground = ADDED;
			} else if (manager.isDeleted(path)) {
				foreground = DELETED;
			} else if (manager.isModified(path)) {
				foreground = CONTENT_MODIFIED;
			} else if (manager.isConflict(path)) {
				foreground = CONFLICT;
			} else {
				foreground = null;
			}

			// Set icon
			setIcon(new BlendedIcon(background, foreground));
		} else if (value instanceof String) {
			super.getTreeCellRendererComponent(tree, value, false, expanded,
					leaf, row, false);
			setIcon(null);
		} else {
			Services.getErrorManager().error("bug!",
					new RuntimeException("The tree cannot be displayed"));
		}

		return this;
	}

	/**
	 * Gets the icon for the specified element
	 * 
	 * @param element
	 *            the element to render
	 * @return the icon for the element
	 */
	private Icon getRendererIcon(TreeElement element) {
		String typeId = element.getTypeId();
		for (ElementRenderer renderer : renderers) {
			Icon icon = renderer.getDefaultIcon(typeId);
			if (icon != null) {
				return icon;
			}
		}

		return null;
	}

	/**
	 * Override method to get an slightly wider preferred size
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		size.width += ADDITIONAL_ICON_WIDTH;
		return size;
	}

	/**
	 * ImageIcon with two blended icons
	 * 
	 * @author victorzinho
	 * 
	 */
	private class BlendedIcon extends ImageIcon {
		Image background, foreground;

		/**
		 * Create a new blended icon
		 * 
		 * @param back
		 *            the ImageIcon for the back
		 * @param front
		 *            the ImageIcon for the front
		 */
		private BlendedIcon(ImageIcon back, ImageIcon front) {
			super(back.getImage());
			if (back == null) {
				throw new IllegalArgumentException(
						"Background icon cannot be null");
			}
			background = back.getImage();
			foreground = (front == null) ? null : front.getImage();
		}

		@Override
		public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
			if (getImageObserver() == null) {
				g.drawImage(background, x, y, c);
				if (foreground != null) {
					g.drawImage(foreground, x, y, c);
				}
			} else {
				g.drawImage(background, x, y, getImageObserver());
				if (foreground != null) {
					g.drawImage(foreground, x, y, getImageObserver());
				}
			}
		}
		
		@Override
		public int getIconWidth() {
			return super.getIconWidth() + BLENDED_ICON_EXPANSION;
		}
	}

	/**
	 * Sets the synchronization manager for this renderer
	 * 
	 * @param sm
	 *            the synchronization manager
	 */
	void setSyncManager(SyncManager sm) {
		manager = sm;
	}

	/**
	 * Sets the element renderers for the geocognition element icons
	 * 
	 * @param r
	 *            the element renderers
	 */
	void setIconRenderers(ElementRenderer[] r) {
		renderers = r;
	}
}
