package org.orbisgis.views.geocognition.sync.tree;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.orbisgis.Services;
import org.orbisgis.images.IconLoader;
import org.orbisgis.views.geocognition.sync.ComparePanel;
import org.orbisgis.views.geocognition.sync.IdPath;
import org.orbisgis.views.geocognition.sync.SyncManager;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;

public class CompareTreeRenderer extends DefaultTreeCellRenderer {
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

	private static final int ADDITIONAL_ICON_WIDTH = 5;
	private static final int BLENDED_ICON_EXPANSION = 9;

	private ElementRenderer[] renderers;
	private SyncManager manager;
	private int synchronizationType;

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

			ImageIcon foreground = null;
			// Get foreground icon
			IdPath path = element.getIdPath();

			if (synchronizationType == ComparePanel.IMPORT) {
				if (manager.isAdded(path)) {
					foreground = LEFT_MINUS;
				} else if (manager.isDeleted(path)) {
					foreground = LEFT_PLUS;
				} else if (manager.isModified(path)) {
					foreground = LEFT;
				} else if (manager.isConflict(path)) {
					foreground = LEFT_CROSS;
				}
			} else if (synchronizationType == ComparePanel.EXPORT) {
				if (manager.isAdded(path)) {
					foreground = RIGHT_PLUS;
				} else if (manager.isDeleted(path)) {
					foreground = RIGHT_MINUS;
				} else if (manager.isModified(path)) {
					foreground = RIGHT;
				} else if (manager.isConflict(path)) {
					foreground = RIGHT_CROSS;
				}
			} else if (synchronizationType == ComparePanel.SYNCHRONIZATION) {
				if (manager.isAdded(path)) {
					foreground = RIGHT_PLUS;
				} else if (manager.isDeleted(path)) {
					foreground = LEFT_PLUS;
				} else if (manager.isModified(path)) {
					foreground = BOTH;
				} else if (manager.isConflict(path)) {
					foreground = BOTH_CROSS;
				}
			} else {
				Services.getErrorManager().error("bug!",
						new RuntimeException("The tree cannot be displayed"));
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
					g.drawImage(foreground, x + BLENDED_ICON_EXPANSION, y, c);
				}
			} else {
				g.drawImage(background, x, y, getImageObserver());
				if (foreground != null) {
					g.drawImage(foreground, x + BLENDED_ICON_EXPANSION, y,
							getImageObserver());
				}
			}
		}

		@Override
		public int getIconWidth() {
			if (foreground != null) {
				return foreground.getWidth(getImageObserver())
						+ BLENDED_ICON_EXPANSION;
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
	void setSyncManager(SyncManager sm, int syncType) {
		manager = sm;
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
