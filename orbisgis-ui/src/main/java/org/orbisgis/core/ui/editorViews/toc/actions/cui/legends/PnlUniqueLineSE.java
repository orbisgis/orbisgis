/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.components.preview.JNumericSpinner;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.ConstraintSymbolFilter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.SymbolFilter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.ColorPicker;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;

/**
 *
 * @author alexis
 */
public class PnlUniqueLineSE extends JPanel implements ILegendPanel, UIPanel {

        /**
         * Here we can put all the Legend instances we want... but they have to
         * be unique symbol (ie constant) Legends.
         */
        private UniqueSymbolLine uniqueLine;
        private LegendContext legendContext ;
        //We need a spinner for the width of the line and for its transparency...
	private JNumericSpinner spnLineWidth;
	private JNumericSpinner spnTransparency;
        //...and we need the corresponding labels.
        private JLabel lblLineWidth;
        private JLabel lblTransparency;
        //The text for the color
        private JLabel lblFill;
        

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public Legend getLegend() {
                return uniqueLine;
        }

        @Override
        public void setLegend(Legend legend) {
                if(legend instanceof UniqueSymbolLine){
                        uniqueLine = (UniqueSymbolLine) legend;
                } else {
                        throw new IllegalArgumentException("The given Legend is not"
                                + "a UniqueSymbolLine");
                }
        }

        @Override
        public void initialize(LegendContext lc) {
                uniqueLine = new UniqueSymbolLine();
                legendContext = lc;
		CRFlowLayout flowLayout = new CRFlowLayout();
		this.setLayout(flowLayout);
		JPanel pnlTexts = getColorText();
		this.add(pnlTexts);
		JPanel pnlColorChoosers = getColorPanel();
		this.add(pnlColorChoosers);
		this.add(new CarriageReturn());
		JPanel pnlSizeTexts = getSpinnersTextPannel();
		this.add(pnlSizeTexts);
		JPanel pnlSizeControls = getSpinnersPannel();
		this.add(pnlSizeControls);
                initFields();
        }

        @Override
        public boolean acceptsGeometryType(int geometryType) {
                return geometryType == GeometryProperties.LINE;
        }

        @Override
        public ILegendPanel newInstance() {
                return new PnlUniqueLineSE();
        }

        @Override
        public String validateInput() {
                return null;
        }

        @Override
        public URL getIconURL() {
		return UIFactory.getDefaultIcon();
        }

        @Override
        public String getTitle() {
                return "Unique symbol for lines.";
        }

        @Override
        public String initialize() {
                return null;
        }

        @Override
        public String postProcess() {
                return null;
        }

        @Override
        public String getInfoText() {
                return "Configure a line to be displayed as a unique symbol.";
        }

	private SymbolFilter getSymbolFilter() {
		return new ConstraintSymbolFilter(new Type[]{TypeFactory.createType(Type.GEOMETRY)});
	}

        /**
         * There are some fields we have to initialize :
         *  - Line width
         *  - Line Color
         *  - Dash pattern
         *  - Name of the Symbolizer.
         */
        private void initFields(){
                spnLineWidth.setValue(uniqueLine.getWidth());
                lblFill.setBackground(uniqueLine.getColor());
                lblFill.setOpaque(true);

        }

        /**
         * Get a pannel that contains all the spinner we need to configure our
         * unique symbol.
         * @return
         */
        private JPanel getSpinnersPannel(){
		JPanel pnlSizeControls = new JPanel();
		pnlSizeControls.setLayout(new CRFlowLayout());
                spnLineWidth = getSpinner(0, Integer.MAX_VALUE, 1.0);
		pnlSizeControls.add(spnLineWidth);
		pnlSizeControls.add(new CarriageReturn());
		spnTransparency = getSpinner(0, 255, 1.0);
		pnlSizeControls.add(spnTransparency);
		pnlSizeControls.add(new CarriageReturn());
                return pnlSizeControls;
        }

        /**
         * Retrieve a spinner with the wanted listener.
         * @param min
         * @param max
         * @param inc
         * @return
         */
        private JNumericSpinner getSpinner(int min, int max, double inc){
                JNumericSpinner jns = new JNumericSpinner(4, min, max, inc);
		jns.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent evt) {
				symbolChanged();
			}
		});
                return jns;
        }

        /**
         * Gets the texts associated to all the pannels returned by
         * getSpinnersPannel().
         * @return
         */
        private JPanel getSpinnersTextPannel(){
		JPanel pnlSizeTexts = new JPanel();
		CRFlowLayout flowLayout = new CRFlowLayout();
                flowLayout.setVgap(18);
                pnlSizeTexts.setLayout(flowLayout);
		flowLayout.setAlignment(CRFlowLayout.RIGHT);
		lblLineWidth = new JLabel();
		lblLineWidth.setText("Line width: ");
		pnlSizeTexts.add(lblLineWidth);
		pnlSizeTexts.add(new CarriageReturn());
		lblTransparency = new JLabel();
		lblTransparency.setText("Transparency: ");
		pnlSizeTexts.add(lblTransparency);
		pnlSizeTexts.add(new CarriageReturn());
                return pnlSizeTexts;
        }

        /**
         * Gets the text associated to the ColorPicker.
         * @return
         */
        private JPanel getColorText(){
		JPanel pnlTexts = new JPanel();
		CRFlowLayout flowLayout = new CRFlowLayout();
		pnlTexts.setLayout(flowLayout);
		flowLayout.setAlignment(CRFlowLayout.RIGHT);
		lblFill = new JLabel();
		lblFill.setText("Fill:");
		pnlTexts.add(lblFill);
                return pnlTexts;
        }

        private JPanel getColorPanel(){
		JPanel pnlColorChoosers = new JPanel();
		pnlColorChoosers.setLayout(new CRFlowLayout());
		lblFill = new JLabel();
		lblFill.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				chooseFillColor();
			}
		});
		lblFill.setBorder(BorderFactory.createLineBorder(Color.black));
		lblFill.setPreferredSize(new Dimension(40, 20));
		lblFill.setOpaque(true);
		pnlColorChoosers.add(lblFill);
                return pnlColorChoosers;
        }

	private void chooseFillColor() {
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
			lblFill.setBackground(color);
			lblFill.setOpaque(true);
		}
		symbolChanged();
	}

        /**
         * Each time an action is performed on a paremeter, we notify everybody
         * through this method.
         */
        private void symbolChanged(){
                uniqueLine.setColor(lblFill.getBackground());
                uniqueLine.setWidth(spnLineWidth.getValue());
                
        }
        
}
