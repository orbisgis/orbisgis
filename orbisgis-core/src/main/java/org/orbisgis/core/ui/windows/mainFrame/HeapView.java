/*
 * GCComponent.java
 *
 * Created on May 29, 2006, 8:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.orbisgis.core.ui.windows.mainFrame;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.text.MessageFormat;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * 
 * @author sky
 */
public class HeapView extends JComponent {
	/*
	 * Style for overlay on top of grid.
	 */
	private static final int STYLE_DEFAULT = 0;

	/**
	 * Grid overlayed on top of heap. This is the default.
	 */
	private static final int STYLE_OVERLAY = 1;

	/*
	 * How often the display is updated.
	 */
	private static final int TICK = 1000;

	/**
	 * Time (in ms) to animate heap growing.
	 */
	private static final int HEAP_GROW_ANIMATE_TIME = 1000;

	/**
	 * Width of the border.
	 */
	private static final int BORDER_W = 2;

	/**
	 * Height of the border area.
	 */
	private static final int BORDER_H = 4;

	/**
	 * Colors for the grid. This is alternating pairs for a linear gradient.
	 */
	private static final Color[] GRID_COLORS = new Color[] {
			new Color(0xE3DFCF), new Color(0xE7E4D3), new Color(0xDAD7C6),
			new Color(0xDFDCCB), new Color(0xD3CFBF), new Color(0xD7D3C3),
			new Color(0xCECABA), new Color(0xD0CCBC) };

	/**
	 * Border color.
	 */
	private static final Color BORDER1_COLOR = new Color(0xA6A295);

	/**
	 * Border color.
	 */
	private static final Color BORDER2_COLOR = new Color(0xC0BCAD);

	/**
	 * Start color for the tick gradient.
	 */
	private static final Color MIN_TICK_COLOR = Color.ORANGE;//new Color(0xC7D6AD);

	/**
	 * End color for the tick gradient.
	 */
	private static final Color MAX_TICK_COLOR = Color.BLUE;//new Color(0x615d0f);

	/**
	 * Color for the text before blurred.
	 */
	private static final Color TEXT_BLUR_COLOR = Color.WHITE;

	/**
	 * Color for text drawn on top of blurred text.
	 */
	private static final Color TEXT_COLOR = Color.WHITE;

	/**
	 * Start color for the background gradient.
	 */
	private static final Color BACKGROUND1_COLOR = new Color(0xD0CCBC);

	/**
	 * End color for the background gradient.
	 */
	private static final Color BACKGROUND2_COLOR = new Color(0xEAE7D7);

	/**
	 * Sized used for Kernel used to generate drop shadow.
	 */
	private static final int KERNEL_SIZE = 3;

	/**
	 * Factor used for Kernel used to generate drop shadow.
	 */
	private static final float BLUR_FACTOR = 0.1f;

	/**
	 * How far to shift the drop shadow along the horizontal axis.
	 */
	private static final int SHIFT_X = 0;

	/**
	 * How far to shift the drop shadow along the vertical axis.
	 */
	private static final int SHIFT_Y = 1;

	/**
	 * Used to generate drop shadown.
	 */
	private final ConvolveOp blur;

	/**
	 * MessageFormat used to generate text.
	 */
	private final MessageFormat format;

	/**
	 * Whether or not to show a drop shadow.
	 */
	private boolean showDropShadow;

	/**
	 * Style to render things in.
	 */
	private int tickStyle;

	/**
	 * Whether or not text is shown.
	 */
	private boolean showText;

	/**
	 * Data for the graph as a percentage of the heap used.
	 */
	private float[] graph;

	/**
	 * Index into graph for the next tick.
	 */
	private int graphIndex;

	/**
	 * If true, graph contains all valid data, otherwise valid data starts at 0
	 * and ends at graphIndex - 1.
	 */
	private boolean graphFilled;

	/**
	 * Last total heap size.
	 */
	private long lastTotal;

	/**
	 * Timer used to update data.
	 */
	private Timer updateTimer;

	/**
	 * Image containing the background gradient and tiles.
	 */
	private Image bgImage;

	/**
	 * Width data is cached at.
	 */
	private int cachedWidth;

	/**
	 * Height data is cached at.
	 */
	private int cachedHeight;

	/**
	 * Image containing text.
	 */
	private BufferedImage textImage;

	/**
	 * Image containing the drop shadow.
	 */
	private BufferedImage dropShadowImage;

	/**
	 * Timer used to animate heap size growing.
	 */
	private HeapGrowTimer heapGrowTimer;

	/**
	 * Max width needed to display 999.9/999.9MB. Used to calcualte pref size.
	 */
	private int maxTextWidth;

	/**
	 * Current text being displayed.
	 */
	private String heapSizeText;

	/**
	 * Image containing gradient for ticks.
	 */
	private Image tickGradientImage;

	/**
	 * Image drawn on top of the ticks.
	 */
	private BufferedImage gridOverlayImage;

	public HeapView() {
		// Configure structures needed for rendering drop shadow.
		int kw = KERNEL_SIZE, kh = KERNEL_SIZE;
		float blurFactor = BLUR_FACTOR;
		float[] kernelData = new float[kw * kh];
		for (int i = 0; i < kernelData.length; i++) {
			kernelData[i] = blurFactor;
		}
		blur = new ConvolveOp(new Kernel(kw, kh, kernelData));
		format = new MessageFormat("{0,number,0.0}/{1,number,0.0}MB");
		heapSizeText = "";
		showDropShadow = true;
		showText = true;
		tickStyle = STYLE_OVERLAY;
		// Enable mouse events. This is the equivalent to adding a mouse
		// listener.
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		updateUI();
	}

	/**
	 * Overriden to return true, GCComponent paints in its entire bounds in an
	 * opaque manner.
	 */
	public boolean isOpaque() {
		return true;
	}

	/**
	 * Updates the look and feel for this component.
	 */
	public void updateUI() {
		// Set the font to correspond to that of a JLabel.
		setFont(new JLabel().getFont());
		revalidate();
		repaint();
	}

	/**
	 * Sets the style used to draw the ticks. The default is STYLE_DEFAULT.
	 * 
	 * @param style
	 *            the tick style, one of STYLE_DEFAULT or STYLE_OVERLAY
	 */
	public void setTickStyle(int style) {
		tickStyle = style;
		repaint();
	}

	/**
	 * Returns the style used to draw ticks.
	 * 
	 * @return the style used to draw ticks, one of STYLE_DEFAULT or
	 *         STYLE_OVERLAY
	 */
	public int getTickStyle() {
		return tickStyle;
	}

	/**
	 * Sets whether the text displaying the heap size should be shown. The
	 * default is true.
	 * 
	 * @param showText
	 *            whether the text displaying the heap size should be shown.
	 */
	public void setShowText(boolean showText) {
		this.showText = showText;
		repaint();
	}

	/**
	 * Returns whether the text displaying the heap size should be shown.
	 * 
	 * @return whether the text displaying the heap size should be shown
	 */
	public boolean getShowText() {
		return showText;
	}

	/**
	 * Sets whether a drop shadow should be shown around the text. The default
	 * is true.
	 * 
	 * @param show
	 *            whether a drop shadow should be shown around the text
	 */
	public void setShowDropShadow(boolean show) {
		showDropShadow = show;
		repaint();
	}

	/**
	 * Returns whether a drop shadow should be shown around the text.
	 */
	public boolean getShowDropShadow() {
		return showDropShadow;
	}

	/**
	 * Sets the font used to display the heap size.
	 * 
	 * @param font
	 *            the font used to display the heap size
	 */
	public void setFont(Font font) {
		super.setFont(font);
		updateTextWidth();
	}

	/**
	 * Returns the minimum size.
	 * 
	 * @return the minimum size
	 */
	public Dimension getMinimumSize() {
		if (isMinimumSizeSet()) {
			return super.getMinimumSize();
		}
		return getPreferredSize0();
	}

	/**
	 * Returns the preferred size.
	 * 
	 * @return the preferred size
	 */
	public Dimension getPreferredSize() {
		if (isPreferredSizeSet()) {
			return super.getPreferredSize();
		}
		return getPreferredSize0();
	}

	private Dimension getPreferredSize0() {
		return new Dimension(maxTextWidth + 8, getFontMetrics(getFont())
				.getHeight() + 8);
	}

	/**
	 * Recalculates the width needed to display the heap size string.
	 */
	private void updateTextWidth() {
		String maxString = format.format(new Object[] { new Float(999.9f),
				new Float(999.9f) });
		maxTextWidth = getFontMetrics(getFont()).stringWidth(maxString) + 4;
	}

	/**
	 * Processes a mouse event.
	 * 
	 * @param e
	 *            the MouseEvent
	 */
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		if (!e.isConsumed()) {
			if (e.isPopupTrigger()) {
				// Show a popup allowing to configure the various options
				showPopup(e.getX(), e.getY());
			} else if (e.getID() == MouseEvent.MOUSE_CLICKED
					&& SwingUtilities.isLeftMouseButton(e)
					&& e.getClickCount() == 1) {
				// Trigger a gc
				System.gc();
			}
		}
	}

	/**
	 * Shows a popup at the specified location that allows you to configure the
	 * various options.
	 */
	private void showPopup(int x, int y) {
		JPopupMenu popup = new JPopupMenu();
		JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem("Show Text");
		cbmi.setSelected(getShowText());
		cbmi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setShowText(((JCheckBoxMenuItem) e.getSource()).isSelected());
			}
		});
		popup.add(cbmi);
		cbmi = new JCheckBoxMenuItem("Drop Shadow");
		cbmi.setSelected(getShowDropShadow());
		cbmi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setShowDropShadow(((JCheckBoxMenuItem) e.getSource())
						.isSelected());
			}
		});
		popup.add(cbmi);
		cbmi = new JCheckBoxMenuItem("Overlay Grid");
		cbmi.setSelected(getTickStyle() == STYLE_OVERLAY);
		cbmi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int style = ((JCheckBoxMenuItem) e.getSource()).isSelected() ? STYLE_OVERLAY
						: STYLE_DEFAULT;
				setTickStyle(style);
			}
		});
		popup.add(cbmi);
		popup.show(this, x, y);
	}

	/**
	 * Returns the first index to start rendering from.
	 */
	private int getGraphStartIndex() {
		if (graphFilled) {
			return graphIndex;
		} else {
			return 0;
		}
	}

	/**
	 * Paints the component.
	 */
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int width = getWidth();
		int height = getHeight();
		if (width - BORDER_W > 0 && height - BORDER_H > 0) {
			startTimerIfNecessary();
			updateCacheIfNecessary(width, height);
			paintCachedBackground(g2, width, height);
			g.translate(1, 2);
			int innerW = width - BORDER_W;
			int innerH = height - BORDER_H;
			if (heapGrowTimer != null) {
				// Render the heap growing animation.
				Composite lastComposite = ((Graphics2D) g).getComposite();
				float percent = 1f - heapGrowTimer.getPercent();
				((Graphics2D) g).setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, percent));
				g.drawImage(heapGrowTimer.image, 0, 0, null);
				((Graphics2D) g).setComposite(lastComposite);
			}
			paintTicks(g2, innerW, innerH);
			if (getTickStyle() == STYLE_OVERLAY) {
				g2.drawImage(getGridOverlayImage(), 0, 0, null);
			}
			if (getShowText()) {
				if (getShowDropShadow()) {
					paintDropShadowText(g, innerW, innerH);
				} else {
					g.setColor(Color.WHITE);
					paintText(g, innerW, innerH);
				}
			}
			g.translate(-1, -2);
		} else {
			stopTimerIfNecessary();
			// To honor opaque contract, fill in the background
			g.setColor(getBackground());
			g.fillRect(0, 0, width, height);
		}
	}

	private void paintTicks(Graphics2D g, int width, int height) {
		int numCells = GRID_COLORS.length / 2;
		int cellSize = (height - numCells - 1) / numCells;

		if (graphIndex > 0 || graphFilled) {
			int index = getGraphStartIndex();
			int x = 0;
			if (!graphFilled) {
				x = width - graphIndex;
			}
			float min = graph[index];
			index = (index + 1) % graph.length;
			while (index != graphIndex) {
				min = Math.min(min, graph[index]);
				index = (index + 1) % graph.length;
			}
			int minHeight = (int) (min * (float) height);
			if (minHeight > 0) {
				g.drawImage(tickGradientImage, x, height - minHeight, width,
						height, x, height - minHeight, width, height, null);
			}
			index = getGraphStartIndex();
			do {
				int tickHeight = (int) (graph[index] * (float) height);
				if (tickHeight > minHeight) {
					g.drawImage(tickGradientImage, x, height - tickHeight,
							x + 1, height - minHeight, x, height - tickHeight,
							x + 1, height - minHeight, null);
				}
				index = (index + 1) % graph.length;
				x++;
			} while (index != graphIndex);
		}
	}

	/**
	 * Renders the text.
	 */
	private void paintText(Graphics g, int w, int h) {
		g.setFont(getFont());
		String text = getHeapSizeText();
		FontMetrics fm = g.getFontMetrics();
		int textWidth = fm.stringWidth(text);
		g.drawString(text, (w - maxTextWidth) / 2 + (maxTextWidth - textWidth),
				h / 2 + fm.getAscent() / 2);
	}

	/**
	 * Renders the text using a drop shadow.
	 */
	private void paintDropShadowText(Graphics g, int w, int h) {
		if (textImage == null) {
			textImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			dropShadowImage = new BufferedImage(w, h,
					BufferedImage.TYPE_INT_ARGB);
		}
		// Step 1: render the text.
		Graphics2D textImageG = textImage.createGraphics();
		textImageG.setComposite(AlphaComposite.Clear);
		textImageG.fillRect(0, 0, w, h);
		textImageG.setComposite(AlphaComposite.SrcOver);
		textImageG.setColor(TEXT_BLUR_COLOR);
		paintText(textImageG, w, h);
		textImageG.dispose();

		// Step 2: copy the image containing the text to dropShadowImage using
		// the blur effect, which generates a nice drop shadow.
		Graphics2D blurryImageG = dropShadowImage.createGraphics();
		blurryImageG.setComposite(AlphaComposite.Clear);
		blurryImageG.fillRect(0, 0, w, h);
		blurryImageG.setComposite(AlphaComposite.SrcOver);
		blurryImageG.drawImage(textImage, blur, SHIFT_X, SHIFT_Y);
		blurryImageG.setColor(TEXT_COLOR);
		blurryImageG.setFont(getFont());

		// Step 3: render the text again on top.
		paintText(blurryImageG, w, h);
		blurryImageG.dispose();

		// And finally copy it.
		g.drawImage(dropShadowImage, 0, 0, null);
	}

	private String getHeapSizeText() {
		return heapSizeText;
	}

	/**
	 * Paints the grid on top of the ticks.
	 */
	private void paintGridOverlay(Graphics2D g, int w, int h) {
		int numCells = GRID_COLORS.length / 2;
		int cellSize = (h - numCells - 1) / numCells;
		int c1 = 0xD0CCBC;
		int c2 = 0xEAE7D7;
		g.setPaint(new GradientPaint(0, 0, new Color((c1 >> 16) & 0xFF,
				(c1 >> 8) & 0xFF, c1 & 0xFF, 0x30), 0, h, new Color(
				(c2 >> 16) & 0xFF, (c2 >> 8) & 0xFF, c2 & 0xFF, 0x40)));
		for (int x = 0; x < w; x += cellSize + 1) {
			g.fillRect(x, 0, 1, h);
		}
		for (int y = h - cellSize - 1; y >= 0; y -= (cellSize + 1)) {
			g.fillRect(0, y, w, 1);
		}
	}

	private void paintCachedBackground(Graphics2D g, int w, int h) {
		if (bgImage != null) {
			g.drawImage(bgImage, 0, 0, null);
		}
	}

	private void paintBackgroundTiles(Graphics2D g, int w, int h) {
		g.translate(1, 2);
		w -= BORDER_W;
		h -= BORDER_H;
		int numCells = GRID_COLORS.length / 2;
		int cellSize = (h - numCells - 1) / numCells;
		for (int i = 0; i < numCells; i++) {
			int colorIndex = i;
			int y = h - cellSize * (i + 1) - i;
			int x = 1;
			g.setPaint(new GradientPaint(0, y, GRID_COLORS[colorIndex * 2], 0,
					y + cellSize - 1, GRID_COLORS[colorIndex * 2 + 1]));
			while (x < w) {
				int endX = Math.min(w, x + cellSize);
				g.fillRect(x, y, endX - x, cellSize);
				x = endX + 1;
			}
			y += cellSize + 1;
		}
		g.translate(-1, -2);
	}

	private void paintBackground(Graphics2D g, int w, int h) {
		g.setPaint(new GradientPaint(0, 0, BACKGROUND1_COLOR, 0, h,
				BACKGROUND2_COLOR));
		g.fillRect(0, 0, w, h);
	}

	private void paintBorder(Graphics g, int w, int h) {
		// Draw the border
		g.setColor(BORDER1_COLOR);
		g.drawRect(0, 0, w - 1, h - 2);
		g.setColor(BORDER2_COLOR);
		g.fillRect(1, 1, w - 2, 1);
		g.setColor(Color.WHITE);
		g.fillRect(0, h - 1, w, 1);
	}

	private void updateCacheIfNecessary(int w, int h) {
		if (cachedWidth != w || cachedHeight != h) {
			cachedWidth = w;
			cachedHeight = h;
			updateCache(w, h);
		}
	}

	private Image getGridOverlayImage() {
		if (gridOverlayImage == null) {
			gridOverlayImage = new BufferedImage(getInnerWidth(),
					getInnerHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = gridOverlayImage.createGraphics();
			paintGridOverlay(g, getInnerWidth(), getInnerHeight());
			g.dispose();
		}
		return gridOverlayImage;
	}

	/**
	 * Recreates the various state information needed for rendering.
	 */
	private void updateCache(int w, int h) {
		disposeImages();
		textImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		dropShadowImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		bgImage = createImage(w, h);
		Graphics2D imageG = (Graphics2D) bgImage.getGraphics();
		paintBackground(imageG, w, h);
		paintBackgroundTiles(imageG, w, h);
		paintBorder(imageG, w, h);
		imageG.dispose();
		w -= BORDER_W;
		h -= BORDER_H;
		if (graph == null || graph.length != w) {
			graph = new float[w];
			graphFilled = false;
			graphIndex = 0;
		}
		GradientPaint tickGradient = new GradientPaint(0, h, MIN_TICK_COLOR, w,
				0, MAX_TICK_COLOR);
		tickGradientImage = createImage(w, h);
		imageG = (Graphics2D) tickGradientImage.getGraphics();
		imageG.setPaint(tickGradient);
		imageG.fillRect(0, 0, w, h);
		imageG.dispose();
		if (gridOverlayImage != null) {
			gridOverlayImage.flush();
			gridOverlayImage = null;
		}
	}

	/**
	 * Invoked when component removed from a heavy weight parent. Stops the
	 * timer.
	 */
	public void removeNotify() {
		super.removeNotify();
		stopTimerIfNecessary();
	}

	/**
	 * Restarts the timer.
	 */
	private void startTimerIfNecessary() {
		if (updateTimer == null) {
			updateTimer = new Timer(TICK, new ActionHandler());
			updateTimer.setRepeats(true);
			updateTimer.start();
		}
	}

	/**
	 * Stops the timer.
	 */
	private void stopTimerIfNecessary() {
		if (updateTimer != null) {
			graph = null;
			graphFilled = false;
			updateTimer.stop();
			updateTimer = null;
			lastTotal = 0;
			disposeImages();
			cachedHeight = cachedHeight = -1;
			if (heapGrowTimer != null) {
				heapGrowTimer.stop();
				heapGrowTimer = null;
			}
		}
	}

	private void disposeImages() {
		if (bgImage != null) {
			bgImage.flush();
			bgImage = null;
		}
		if (textImage != null) {
			textImage.flush();
			textImage = null;
		}
		if (dropShadowImage != null) {
			dropShadowImage.flush();
			dropShadowImage = null;
		}
		if (tickGradientImage != null) {
			tickGradientImage.flush();
			tickGradientImage = null;
		}
		if (gridOverlayImage != null) {
			gridOverlayImage.flush();
			gridOverlayImage = null;
		}
	}

	/**
	 * Invoked when the update timer fires. Updates the necessary data
	 * structures and triggers repaints.
	 */
	private void update() {
		if (!isShowing()) {
			// Either we've become invisible, or one of our ancestors has.
			// Stop the timer and bale. Next paint will trigger timer to
			// restart.
			stopTimerIfNecessary();
			return;
		}
		Runtime r = Runtime.getRuntime();
		long total = r.totalMemory();
		if (total != lastTotal) {
			if (lastTotal != 0) {
				// Total heap size has changed, start an animation.
				startHeapAnimate();
				// Readjust the graph size based on the new max.
				int index = getGraphStartIndex();
				do {
					graph[index] = (float) (((double) graph[index] * (double) lastTotal) / (double) total);
					index = (index + 1) % graph.length;
				} while (index != graphIndex);
			}
			lastTotal = total;
		}
		if (heapGrowTimer == null) {
			// Not animating a heap size change, update the graph data and text.
			long used = total - r.freeMemory();
			graph[graphIndex] = (float) ((double) used / (double) total);
			graphIndex = (graphIndex + 1) % graph.length;
			if (graphIndex == 0) {
				graphFilled = true;
			}
			heapSizeText = format.format(new Object[] {
					new Double((double) used / 1024 / 1024),
					new Double((double) total / 1024 / 1024) });
		}
		repaint();
	}

	private void startHeapAnimate() {
		if (heapGrowTimer == null) {
			heapGrowTimer = new HeapGrowTimer();
			heapGrowTimer.start();
		}
	}

	private void stopHeapAnimate() {
		if (heapGrowTimer != null) {
			heapGrowTimer.stop();
			heapGrowTimer = null;
		}
	}

	private int getInnerWidth() {
		return getWidth() - BORDER_W;
	}

	private int getInnerHeight() {
		return getHeight() - BORDER_H;
	}

	private final class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			update();
		}
	}

	private final class HeapGrowTimer extends Timer {
		private final long startTime;
		private float percent;
		BufferedImage image;

		HeapGrowTimer() {
			super(30, null);
			setRepeats(true);
			startTime = System.currentTimeMillis();
			percent = 0f;
			int w = getWidth() - BORDER_W;
			int h = getHeight() - BORDER_H;
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			paintTicks(g, w, h);
			g.dispose();
		}

		public float getPercent() {
			return percent;
		}

		protected void fireActionPerformed(ActionEvent e) {
			long time = System.currentTimeMillis();
			long delta = time - startTime;
			if (delta > HEAP_GROW_ANIMATE_TIME) {
				stopHeapAnimate();
			} else {
				percent = (float) delta / (float) HEAP_GROW_ANIMATE_TIME;
				repaint();
			}
		}
	}
}
