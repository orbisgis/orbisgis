package org.orbisgis.omanager.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import org.orbisgis.view.components.list.PanelList;

/**
 * Dialog that handle bundles.
 * @author Nicolas Fortin
 */
public class MainPanel extends JDialog {
    private static final Dimension DEFAULT_DIMENSION = new Dimension(640,480);
    private static final Dimension DEFAULT_DETAILS_DIMENSION = new Dimension(200,-1);
    // Bundle Category filter
    private JList bundleCategory = new JList(new String[] {"All","DataBase","Network","SQL Functions"});
    private JTextArea bundleDetails = new JTextArea();
    // PanelList can be replaced by a standard JList.
    private PanelList bundleList = new PanelList();

    public MainPanel(Frame frame) {
        super(frame);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        // Put components
        JPanel leftOfSplitGroup = new JPanel(new BorderLayout());
        bundleCategory.setBorder(BorderFactory.createEtchedBorder());
        leftOfSplitGroup.add(bundleCategory,BorderLayout.WEST);
        leftOfSplitGroup.add(bundleList,BorderLayout.CENTER);
        bundleDetails.setPreferredSize(DEFAULT_DETAILS_DIMENSION);
        JSplitPane contentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftOfSplitGroup,bundleDetails);
        setContentPane(contentPane);
        setSize(DEFAULT_DIMENSION);
    }
}
