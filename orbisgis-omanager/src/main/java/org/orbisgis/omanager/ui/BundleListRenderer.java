package org.orbisgis.omanager.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.orbisgis.view.components.renderers.ListLaFRenderer;
import org.osgi.framework.Bundle;

/**
 * @author Nicolas Fortin
 */
public class BundleListRenderer extends ListLaFRenderer {
    private static Dimension bundleIconDimension = new Dimension(32,32);
    private static final ImageIcon defaultIcon = new ImageIcon(BundleListRenderer.class.getResource("defaultIcon.png"));
    private static final ImageIcon activeLayer = new ImageIcon(BundleListRenderer.class.getResource("active_layer.png"));

    public BundleListRenderer(JList list) {
        super(list);
    }
    private Icon mergeIcons(Image bottom,Image top) {
        BufferedImage image = new BufferedImage(bundleIconDimension.width, bundleIconDimension.height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.drawImage(bottom,0,0,null);
        g.drawImage(top, 0, 0, null);
        return new ImageIcon(image);
    }
    public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b2) {
        Component lafComp = lookAndFeelRenderer.getListCellRendererComponent(jList,o,i,b,b2);
        if(lafComp instanceof JLabel) {
            JLabel label = (JLabel)lafComp;
            BundleItem bi = (BundleItem)o;
            if(bi.getBundle()!=null && bi.getBundle().getState()== Bundle.ACTIVE) {
                label.setIcon(mergeIcons(defaultIcon.getImage(),activeLayer.getImage()));
            } else {
                label.setIcon(defaultIcon);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<html><h4>");
            sb.append(bi.getPresentationName());
            sb.append("</h4>");
            sb.append(bi.getShortDescription());
            sb.append("</html>");
            label.setText(sb.toString());
        }
        return lafComp;
    }
}
