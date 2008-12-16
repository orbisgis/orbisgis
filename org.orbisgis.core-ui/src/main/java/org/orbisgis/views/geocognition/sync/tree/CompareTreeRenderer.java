package org.orbisgis.views.geocognition.sync.tree;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;

import org.orbisgis.Services;
import org.orbisgis.images.IconLoader;
import org.orbisgis.ui.resourceTree.AbstractTreeRenderer;
import org.orbisgis.views.geocognition.sync.IdPath;
import org.orbisgis.views.geocognition.sync.SyncManager;
import org.orbisgis.views.geocognition.sync.SyncPanel;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;

public class CompareTreeRenderer extends AbstractTreeRenderer {
	// Blended icons
	private static final ImageIcon RIGHT_PLUS = IconLoader
			.getIcon("blended_rightarrow_plus.png");
	private static final ImageIcon RIGHT_MINUS = IconLoader
			.getIcon("blended_rightarrow_minus.png");
	private static final ImageIcon RIGHT_CROSS = IconLoader
			.getIcon("blended_rightarrow_cross.png");
	private static final ImageIcon RIGHT = IconLoader
			.getIcon("blended_rightarrow.png");
	private static final ImageIcon LEFT_PLUS = IconLoader
			.getIcon("blended_leftarrow_plus.png");
	private static final ImageIcon LEFT_MINUS = IconLoader
			.getIcon("blended_leftarrow_minus.png");
	private static final ImageIcon LEFT_CROSS = IconLoader
			.getIcon("blended_leftarrow_cross.png");
	private static final ImageIcon LEFT = IconLoader
			.getIcon("blended_leftarrow.png");
	private static final ImageIcon BOTH_CROSS = IconLoader
			.getIcon("blended_both_cross.png");
	private static final ImageIcon BOTH = IconLoader
			.getIcon("blended_both.png");

	// Integer spacing constants for the icon width
	private static final int BLENDED_ICON_OVERLAPING = 5;

	private ElementRenderer[] renderers;
	private SyncManager syncManager;
	private int synchronizationType;

	@Override
	protected void updateIconAndTooltip(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		icon = null;

		if (value instanceof TreeElement) {
			TreeElement element = (TreeElement) value;

			// Get background icon
			ImageIcon background = (ImageIcon) getRendererIcon(element);

			// Get foreground icon
			ImageIcon foreground = null;
			IdPath path = element.getIdPath();

			if (synchronizationType == SyncPanel.IMPORT) {
				if (syncManager.isAdded(path)) {
					foreground = LEFT_MINUS;
				} else if (syncManager.isDeleted(path)) {
					foreground = LEFT_PLUS;
				} else if (syncManager.isModified(path)) {
					foreground = LEFT;
				} else if (syncManager.isConflict(path)) {
					foreground = LEFT_CROSS;
				}
			} else if (synchronizationType == SyncPanel.EXPORT) {
				if (syncManager.isAdded(path)) {
					foreground = RIGHT_PLUS;
				} else if (syncManager.isDeleted(path)) {
					foreground = RIGHT_MINUS;
				} else if (syncManager.isModified(path)) {
					foreground = RIGHT;
				} else if (syncManager.isConflict(path)) {
					foreground = RIGHT_CROSS;
				}
			} else if (synchronizationType == SyncPanel.SYNCHRONIZATION) {
				if (syncManager.isAdded(path)) {
					foreground = RIGHT_PLUS;
				} else if (syncManager.isDeleted(path)) {
					foreground = LEFT_PLUS;
				} else if (syncManager.isModified(path)) {
					foreground = BOTH;
				} else if (syncManager.isConflict(path)) {
					foreground = BOTH_CROSS;
				}
			} else {
				Services.getErrorManager().error("bug!",
						new RuntimeException("The tree cannot be displayed"));
			}

			// Set icon
			icon = new BlendedIcon(background, foreground);
		} else if (value instanceof String) {
			icon = null;
		} else {
			Services.getErrorManager().error("bug!",
					new RuntimeException("The tree cannot be displayed"));
		}
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
			background = back.getImage();
			foreground = (front == null) ? null : front.getImage();
		}

		@Override
		public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
			if (getImageObserver() == null) {
				g.drawImage(background, x, y, c);
				if (foreground != null) {
					g.drawImage(foreground, x
							+ background.getWidth(getImageObserver())
							- BLENDED_ICON_OVERLAPING, y, c);
				}
			} else {
				g.drawImage(background, x, y, getImageObserver());
				if (foreground != null) {
					g.drawImage(foreground, x
							+ background.getWidth(getImageObserver())
							- BLENDED_ICON_OVERLAPING, y, getImageObserver());
				}
			}
		}

		@Override
		public int getIconWidth() {
			if (foreground != null) {
				return foreground.getWidth(getImageObserver())
						+ background.getWidth(getImageObserver())
						- BLENDED_ICON_OVERLAPING;
			} else {
				return super.getIconWidth();
			}
		}

		@Override
		public int getIconHeight() {
			if (foreground != null) {
				return Math.max(foreground.getHeight(getImageObserver()),
						foreground.getHeight(getImageObserver()));

			} else {
				return super.getIconWidth();
			}
		}
	}

	/**
	 * Sets the synchronization manager for this renderer
	 * 
	 * @param sm
	 *            the synchronization manager
	 */
	void setModel(SyncManager sm, int syncType) {
		syncManager = sm;
		synchronizationType = syncType;
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
