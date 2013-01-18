package org.orbisgis.omanager.ui;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.orbisgis.view.components.renderers.ListLaFRenderer;

/**
 * @author Nicolas Fortin
 */
public class BundleListRenderer extends ListLaFRenderer {
    private static final Icon defaultIcon = new ImageIcon(BundleListRenderer.class.getResource("defaultIcon.png"));

    public BundleListRenderer(JList list) {
        super(list);
    }

    public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b2) {
        Component lafComp = lookAndFeelRenderer.getListCellRendererComponent(jList,o,i,b,b2);
        if(lafComp instanceof JLabel) {
            JLabel label = (JLabel)lafComp;
            BundleItem bi = (BundleItem)o;
            label.setIcon(defaultIcon);
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
