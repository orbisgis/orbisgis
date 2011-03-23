/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.classification.Range;
import org.orbisgis.core.renderer.classification.RangeMethod;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.JSE_ChoroplethDatas.DataChanged;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.JSE_ChoroplethDatas.DataChangedListener;

/**
 *
 * @author maxence
 */
public class ChoroplethWizardPanel extends JPanel implements UIPanel {

	private ILayer layer;
        private JSE_ChoroplethDatas ChoroDatas;
        private JButton btnApply;

        private JPanel topPanel;
        //Top panel controls
        private JLabel lblField;
        private JComboBox cbxField;
        private JLabel lblNbrOfClasses;
        private JComboBox cbxNbrOfClasses;
        private JLabel lblStatMethod;
        private JRadioButton rdbxStatMethodQuantiles;
        private JRadioButton rdbxStatMethodMean;
        private JRadioButton rdbxStatMethodJenks;
        private JRadioButton rdbxStatMethodManual;
        private JLabel lblColorBegin;
        private JLabel lblColorEnd;
        private JPanel pnlColorBegin;
        private JPanel pnlColorEnd;
        private JPanel centerPanel;
        private JPanel bottomPanel;
	/*
	 * Create a Choropleth wizard panel
	 * @param layer the layer to create a choropleth for
	 */
	public ChoroplethWizardPanel(ILayer layer) throws DriverException {
            super();
            this.setLayout(new BorderLayout());
            this.setSize(800, 600);

            ChoroDatas = new JSE_ChoroplethDatas(layer);
            ChoroDatas.readData();
            // Register for MyEvents from c
            ChoroDatas.addDataChangedListener(new DataChangedListener()
            {
                @Override
                public void dataChangedOccurred(DataChanged evt)
                {
                    // DataChanged was fired
                    System.out.println("DATA CHANGED : " + evt.dataType.toString());
                }
            });

            /* TopPanel (NORTH) of dialog */
            topPanel = new JPanel();topPanel.setBackground(Color.red);
            topPanel.setLayout((new BorderLayout()));

            lblField = new JLabel("Field");
            cbxField = new JComboBox();
            cbxField.addActionListener (new ActionListener () {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ChoroDatas.setFieldIndex(cbxField.getSelectedIndex());
                }
            });
            lblNbrOfClasses = new JLabel("Number Of Classes");
            cbxNbrOfClasses = new JComboBox();
            cbxNbrOfClasses.addActionListener (new ActionListener () {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(cbxNbrOfClasses.getSelectedItem() != null)
                        ChoroDatas.setNbrClasses(Integer.parseInt(cbxNbrOfClasses.getSelectedItem().toString()));
                }
            });

            lblStatMethod = new JLabel("Statistic Method");
            rdbxStatMethodQuantiles = new JRadioButton("Quantile");rdbxStatMethodQuantiles.setSelected(true);
            rdbxStatMethodMean = new JRadioButton("Mean");
            rdbxStatMethodJenks = new JRadioButton("Jenks");
            rdbxStatMethodManual = new JRadioButton("Manual");
            //Add radio button listener
            RadioListener rListener = new RadioListener();
            rdbxStatMethodQuantiles.addItemListener(rListener);
            rdbxStatMethodMean.addItemListener(rListener);
            rdbxStatMethodJenks.addItemListener(rListener);
            rdbxStatMethodManual.addItemListener(rListener);
            //Group the radio buttons.
            ButtonGroup group = new ButtonGroup();
            group.add(rdbxStatMethodQuantiles);
            group.add(rdbxStatMethodMean);
            group.add(rdbxStatMethodJenks);
            group.add(rdbxStatMethodManual);

            pnlColorBegin = new JPanel(); pnlColorBegin.setName("COLOR_BEGIN");
            pnlColorBegin.addMouseListener(new MouseAdapter() { 
                @Override
                public void mousePressed(MouseEvent me) { 
                changeColor(pnlColorBegin);
            }});
            pnlColorEnd = new JPanel(); pnlColorEnd.setName("COLOR_END");
            pnlColorEnd.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent me) {
                changeColor(pnlColorEnd);
            }});
            lblColorBegin = new JLabel("Starting Color");
            lblColorEnd = new JLabel("Ending Color");
            JPanel north = new JPanel();north.setLayout(new FlowLayout());
            JPanel west = new JPanel();west.setLayout(new FlowLayout());
            JPanel east = new JPanel();east.setLayout(new FlowLayout());
            topPanel.add(north, BorderLayout.NORTH);
            topPanel.add(west, BorderLayout.WEST);
            topPanel.add(east, BorderLayout.EAST);
            
            north.add(lblField);
            north.add(cbxField);
            north.add(lblNbrOfClasses);
            north.add(cbxNbrOfClasses);
            west.add(lblStatMethod);
            west.add(rdbxStatMethodQuantiles);
            west.add(rdbxStatMethodMean);
            west.add(rdbxStatMethodJenks);
            west.add(rdbxStatMethodManual);
            east.add(pnlColorBegin);east.add(lblColorBegin);
            east.add(pnlColorEnd);east.add(lblColorEnd);

            centerPanel = new JPanel();centerPanel.setBackground(Color.green);centerPanel.setSize(800,180);
            bottomPanel = new JPanel();bottomPanel.setBackground(Color.blue);bottomPanel.setSize(800,200);
            
            this.add(topPanel, BorderLayout.NORTH);
            this.add(centerPanel, BorderLayout.CENTER);
            this.add(bottomPanel, BorderLayout.SOUTH);

            btnApply = new JButton("Apply");
            btnApply.setMargin(new Insets(0, 0, 0, 0));
            btnApply.addActionListener(new btnApplyListener(this));

            pnlColorBegin.setBackground(ChoroDatas.getBeginColor());
            pnlColorEnd.setBackground(ChoroDatas.getEndColor());
            for(int i = 0; i < ChoroDatas.getFields().size(); i++)
                cbxField.addItem(ChoroDatas.getFields().get(i));
            if(cbxField.getItemCount() > 0)
                cbxField.setSelectedIndex(0);
            updateComboBoxNbrClasses(ChoroDatas.getStatisticMethod());

            //bottomPanel.add(new JSE_ChoroplethChartPanel(ChoroDatas));
            bottomPanel.add(btnApply);
            this.layer = layer;

            //INIT
            ChoroDatas.setNbrClasses(7);

            JSE_ChoroplethRangeTabPanel rangeTabPanel = new JSE_ChoroplethRangeTabPanel(ChoroDatas);
            centerPanel.add(rangeTabPanel);

            JSE_ChoroplethChartPanel chartPanel = new JSE_ChoroplethChartPanel(ChoroDatas);
            bottomPanel.add(chartPanel);

            // Register for MyEvents from c
            JSE_ChoroDatasChangedListener datasChangedListener = new JSE_ChoroDatasChangedListener(ChoroDatas,rangeTabPanel,chartPanel);
            ChoroDatas.addDataChangedListener(datasChangedListener);
	}

	@Override
	public URL getIconURL() {
		return UIFactory.getDefaultIcon();
	}

	@Override
	public String getTitle() {
		return "Choropleth Wizard";
	}

	@Override
	public String initialize() {
                System.out.println("***initialize***");
                Container parent = this.getParent();
                if(parent != null)
                {
                    
                }
		return null;
	}

	@Override
	public String postProcess() {
		return null;
	}

	@Override
	public String validateInput() {
		// Todo make sure the choropleth is valid !
		return null;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getInfoText() {
		return UIFactory.getDefaultOkMessage();
	}

        private AreaSymbolizer draw(){
            Categorize2Color choropleth = new Categorize2Color(new ColorLiteral(ChoroDatas.getBeginColor()), new ColorLiteral(ChoroDatas.getEndColor()), ChoroDatas.getField());
            Range[] ranges = ChoroDatas.getRange();
            for(int i= 0; i< ranges.length ; i++){
                choropleth.addClass(new RealLiteral(ranges[i].getMaxRange()), new ColorLiteral(ChoroDatas.getClassColor(i)));
            }
            SolidFill choroplethFill = new SolidFill();
            choroplethFill.setColor(choropleth);
            AreaSymbolizer as = new AreaSymbolizer();
            as.setFill(choroplethFill);
            return as;
        }

        private void changeColor(JPanel sender)
        {
            if(sender.getName().equals("COLOR_BEGIN"))
            {
                ChoroDatas.setBeginColor(JColorChooser.showDialog(sender, "Pick a begin color", ChoroDatas.getBeginColor()));
                pnlColorBegin.setBackground(ChoroDatas.getBeginColor());
                pnlColorBegin.invalidate();
            }
            else
            {
                ChoroDatas.setEndColor(JColorChooser.showDialog(sender, "Pick an end color", ChoroDatas.getEndColor()));
                pnlColorEnd.setBackground(ChoroDatas.getEndColor());
                pnlColorEnd.invalidate();
            }
        }

	/*
	 * Is called after the panel has been closed (and validated)
	 * This method return a new se:Rule based on the wizard values
	 */
	public Rule getRule() throws DriverException {
            Rule r = new Rule();
            r.setName("Choropleth (" + ChoroDatas.getField().getColumnName() + ")");
            r.getCompositeSymbolizer().addSymbolizer(this.draw());
            return r;
		/*Metadata metadata = layer.getDataSource().getMetadata();

		// Quick (and hugly) step to fetch the first numeric attribute
		String retainedFiledName = null;
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			int currentType = metadata.getFieldType(i).getTypeCode();

			if (currentType == 4 || (currentType >= 16 && currentType <= 256)) {
				retainedFiledName = metadata.getFieldName(i);
				break;
			}
		}


		if (retainedFiledName != null) {
			try {
				RealAttribute field = new RealAttribute(retainedFiledName);
				RangeMethod rangesHelper = new RangeMethod(layer.getDataSource(), field, 4);

				rangesHelper.disecMean();
				Range[] ranges = rangesHelper.getRanges();
                                rangesHelper.getIntervals();

				Categorize2Color choropleth = new Categorize2Color(new ColorLiteral("#dd0000"), new ColorLiteral("#FFFF00"), field);
				choropleth.addClass(new RealLiteral(ranges[0].getMaxRange()), new ColorLiteral("#aa0000"));
				choropleth.addClass(new RealLiteral(ranges[1].getMaxRange()), new ColorLiteral("#770000"));
				choropleth.addClass(new RealLiteral(ranges[2].getMaxRange()), new ColorLiteral("#330000"));

				SolidFill choroplethFill = new SolidFill();
				choroplethFill.setColor(choropleth);
				AreaSymbolizer as = new AreaSymbolizer();
				as.setFill(choroplethFill);
				Rule r = new Rule();
				r.setName("Choropleth (" + retainedFiledName + ")");
				r.getCompositeSymbolizer().addSymbolizer(as);
				return r;
			} catch (ParameterException ex) {
				Logger.getLogger(ChoroplethWizardPanel.class.getName()).log(Level.SEVERE, null, ex);
				return null;
			}
		}*/
	}

        private void updateComboBoxNbrClasses(JSE_ChoroplethDatas.StatisticMethod statMethod)
        {
            if(ChoroDatas != null)
            {
                int savedValue = 0;
                if(cbxNbrOfClasses.getSelectedItem() != null)
                    savedValue = Integer.parseInt(cbxNbrOfClasses.getSelectedItem().toString());

                cbxNbrOfClasses.removeAllItems();
                int[] allowed = ChoroDatas.getNumberOfClassesAllowed(statMethod);
                for(int i = 0; i < allowed.length; i++)
                {
                    cbxNbrOfClasses.addItem(allowed[i]);
                    if(allowed[i] == savedValue)
                        cbxNbrOfClasses.setSelectedIndex(i);
                }
                
                if(cbxNbrOfClasses.getSelectedItem()!= null)
                    ChoroDatas.setNbrClasses(Integer.parseInt(cbxNbrOfClasses.getSelectedItem().toString()));
            }
        }

        class RadioListener implements ItemListener
        {

            @Override
            public void itemStateChanged(ItemEvent e)
            {
                if(e.getItem().equals(rdbxStatMethodQuantiles) && e.getStateChange() == ItemEvent.SELECTED)
                {
                    //System.out.println("Quantiles Selected");
                    updateComboBoxNbrClasses(JSE_ChoroplethDatas.StatisticMethod.QUANTILES);
                    ChoroDatas.setStatisticMethod(JSE_ChoroplethDatas.StatisticMethod.QUANTILES);
                }
                else if(e.getItem().equals(rdbxStatMethodMean) && e.getStateChange() == ItemEvent.SELECTED)
                {
                    //System.out.println("Mean Selected");
                    updateComboBoxNbrClasses(JSE_ChoroplethDatas.StatisticMethod.AVERAGE);
                    ChoroDatas.setStatisticMethod(JSE_ChoroplethDatas.StatisticMethod.AVERAGE);
                }
                if(e.getItem().equals(rdbxStatMethodJenks) && e.getStateChange() == ItemEvent.SELECTED)
                {
                    //System.out.println("Jenks Selected");
                    updateComboBoxNbrClasses(JSE_ChoroplethDatas.StatisticMethod.JENKS);
                    ChoroDatas.setStatisticMethod(JSE_ChoroplethDatas.StatisticMethod.JENKS);
                }
                if(e.getItem().equals(rdbxStatMethodManual) && e.getStateChange() == ItemEvent.SELECTED)
                {
                    //System.out.println("Manual Selected");
                    updateComboBoxNbrClasses(JSE_ChoroplethDatas.StatisticMethod.MANUAL);
                    ChoroDatas.setStatisticMethod(JSE_ChoroplethDatas.StatisticMethod.MANUAL);
                }
            }
        }


        private class btnApplyListener implements ActionListener {

		private final ChoroplethWizardPanel metaPanel;

		public btnApplyListener(ChoroplethWizardPanel metaPanel) {
			super();
			this.metaPanel = metaPanel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
                    try{
                        Rule r = this.metaPanel.getRule();
                        if (r != null) {
                            // Add the rule in the current featureTypeStyle
                            layer.getFeatureTypeStyle().clear();
                            layer.getFeatureTypeStyle().addRule(r);
                            // And finally redraw the map
                            layer.fireStyleChangedPublic();
                        }
                    }catch(DriverException ex){
                        Logger.getLogger(ChoroplethWizardPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
		}
	}
}
