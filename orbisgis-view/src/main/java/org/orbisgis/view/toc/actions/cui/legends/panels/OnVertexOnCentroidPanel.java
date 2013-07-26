package org.orbisgis.view.toc.actions.cui.legends.panels;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.OnVertexOnCentroid;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 26/07/13
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */
public class OnVertexOnCentroidPanel extends JPanel {

    private static final I18n I18N = I18nFactory.getI18n(OnVertexOnCentroidPanel.class);

    private static final String VERTEX = I18n.marktr("Vertex");
    private static final String CENTROID = I18n.marktr("Centroid");

    private OnVertexOnCentroid legend;
    private CanvasSE preview;
    private TablePanel tablePanel;

    public OnVertexOnCentroidPanel(OnVertexOnCentroid legend,
                                   CanvasSE preview,
                                   TablePanel tablePanel) {
        super(new MigLayout("wrap 1"));
        this.legend = legend;
        this.preview = preview;
        this.tablePanel = tablePanel;
        init();
    }

    public OnVertexOnCentroidPanel(OnVertexOnCentroid legend,
                                   CanvasSE preview) {
        this(legend, preview, null);
    }

    /**
     * Initializes the panel used to configure if the symbol must be drawn on
     * the vertices or on the centroid.
     */
    private void init() {

        // Create the buttons and add the listeners.
        JRadioButton bVertex = new JRadioButton(I18N.tr(VERTEX));
        bVertex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                legend.setOnVertex();
            }
        });
        bVertex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClickVertex();
            }
        });

        JRadioButton bCentroid = new JRadioButton(I18N.tr(CENTROID));
        bCentroid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                legend.setOnCentroid();
            }
        });
        bCentroid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClickCentroid();
            }
        });

        // Select the right button.
        Symbolizer symbol = ((Legend) legend).getSymbolizer();
        if (symbol instanceof PointSymbolizer) {
            boolean onVertex = ((PointSymbolizer) symbol).isOnVertex();
            bVertex.setSelected(onVertex);
            bCentroid.setSelected(!onVertex);
        }

        // Make the button group.
        ButtonGroup bg = new ButtonGroup();
        bg.add(bVertex);
        bg.add(bCentroid);

        // Make and return the panel.
        add(bVertex);
        add(bCentroid);
    }

    /**
     * Called when the user wants to put the points on the vertices of the geometry.
     */
    private void onClickVertex() {
        changeOnVertex(true);
    }

    /**
     * Called when the user wants to put the points on the centroid of the geometry.
     */
    private void onClickCentroid() {
        changeOnVertex(false);
    }

    /**
     * Called by listeners to update the fallback symbol preview (and the
     * table preview if necessary).
     *
     * @param onVertex True if the symbol is to be placed on vertices; false
     *                 if on the centroid
     */
    private void changeOnVertex(boolean onVertex) {
        Symbolizer symbol = preview.getSymbol();
        if (symbol instanceof PointSymbolizer) {
            ((PointSymbolizer) symbol).setOnVertex(onVertex);
            preview.imageChanged();
            if (tablePanel != null) {
                tablePanel.updateTable();
            }
        }
    }
}
