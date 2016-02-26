/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsclient.view.utils.editor.process;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.sif.docking.DockingLocation;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.wpsclient.WpsClient;
import org.orbisgis.wpsclient.view.ui.dataui.DataUI;
import org.orbisgis.wpsclient.view.ui.dataui.DataUIManager;
import org.orbisgis.wpsclient.view.utils.ExecutionWorker;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.wpsservice.model.Input;
import org.orbisgis.wpsservice.model.Output;
import org.orbisgis.wpsservice.model.Process;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.AbstractMap;

/**
 * UI for the configuration and the run of a WPS process.
 * It extends the EditorDockable interface to be able to be docked in OrbisGIS.
 *
 * @author Sylvain PALOMINOS
 */
public class ProcessEditor extends JPanel implements EditorDockable, PropertyChangeListener {

    private static final int SCROLLBAR_UNIT_INCREMENT = 16;
    /** Name of the EditorDockable. */
    public static final String NAME = "PROCESS_EDITOR";

    private ProcessEditableElement pee;
    private WpsClient wpsClient;
    private DockingPanelParameters dockingPanelParameters;
    /** TabbedPane containing the configuration panel, the info panel and the execution panel */
    private JTabbedPane tabbedPane;
    /** DataUIManager used to create the UI corresponding the the data */
    private DataUIManager dataUIManager;
    /** Tells if the this editor has been open or not. */
    private boolean alive;
    private ExecutionWorker thread;
    private JPanel contentPanel;
    private WaitLayerUI layerUI;
    private JLayer<JPanel> layer;
    private JLabel waitLabel;

    public ProcessEditor(WpsClient wpsClient, ProcessEditableElement pee){
        this.alive = true;
        this.wpsClient = wpsClient;
        this.pee = pee;
        this.pee.addPropertyChangeListener(this);
        dockingPanelParameters = new DockingPanelParameters();
        dockingPanelParameters.setName(NAME+"_"+pee.getProcess().getTitle());
        dockingPanelParameters.setTitleIcon(ToolBoxIcon.getIcon("process"));
        dockingPanelParameters.setDefaultDockingLocation(
                new DockingLocation(DockingLocation.Location.STACKED_ON, WpsClient.TOOLBOX_REFERENCE));
        dockingPanelParameters.setTitle(pee.getProcessReference());
        this.setLayout(new BorderLayout());
        dataUIManager = wpsClient.getDataUIManager();

        contentPanel = new JPanel(new BorderLayout());
        layerUI = new WaitLayerUI();
        layer = new JLayer<>(contentPanel, layerUI);
        //Adds a mouse listener to listen the double click by the user to cancel the loading
        this.addMouseListener(EventHandler.create(MouseListener.class, this, "cancelLoad", "", "mouseClicked"));
        layer.addMouseListener(EventHandler.create(MouseListener.class, this, "cancelLoad", "", "mouseClicked"));
        contentPanel.addMouseListener(EventHandler.create(MouseListener.class, this, "cancelLoad", "", "mouseClicked"));
        this.add (layer);

        buildUI();
        tabbedPane.setSelectedIndex(0);
        this.revalidate();
    }

    /**
     * Cancel the current loading.
     */
    public void cancelLoad(MouseEvent me){
        if(me.getClickCount() >= 2){
            wpsClient.cancelLoadURI(((Process)pee.getObject()).getIdentifier());
            endWaiting();
        }
    }

    /**
     * Sets if this editor has been open..
     * @param alive True if this editor is open, false otherwise.
     */
    public void setAlive(boolean alive){
        this.alive = alive;
    }

    @Override
    public DockingPanelParameters getDockingParameters() {
        //if this editor is not visible but was open, close it.
        if(!dockingPanelParameters.isVisible() && alive){
            alive = false;
            wpsClient.killEditor(this);
        }
        return dockingPanelParameters;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public boolean match(EditableElement editableElement) {
        //Return true if the editable is the one contained by the Process editor
        return editableElement instanceof ProcessEditableElement && editableElement.getId().equals(pee.getId());
    }

    @Override
    public EditableElement getEditableElement() {
        return pee;
    }

    @Override
    public void setEditableElement(EditableElement editableElement) {
        this.pee = (ProcessEditableElement)editableElement;
        dockingPanelParameters.setTitle(pee.getProcessReference());
        pee.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals(ProcessEditableElement.LOG_PROPERTY)){
            AbstractMap.Entry<String, Color> entry = (AbstractMap.Entry)propertyChangeEvent.getNewValue();
        }
        if(propertyChangeEvent.getPropertyName().equals(ProcessEditableElement.CANCEL)){
            wpsClient.getWpsService().cancelProcess(pee.getProcess().getIdentifier());
        }
    }

    /**
     * Build the UI of the ProcessFrame with the data of the processUIData.
     */
    private void buildUI(){
        //Adds to the tabbedPane the 3 panels
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Configuration", buildUIConf());
        tabbedPane.addTab("Information", buildUIInfo());
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Run the process.
     * @return True if the process has already been launch, false otherwise.
     */
    public boolean runProcess(){
        wpsClient.validateInstance(this);
        pee.setProcessState(ProcessEditableElement.ProcessState.RUNNING);
        //Run the process in a separated thread
        thread = new ExecutionWorker(pee, wpsClient.getWpsService());
        wpsClient.getExecutorService().execute(thread);
        return false;
    }

    /**
     * Build the UI of the given process according to the given data.
     * @return The UI for the configuration of the process.
     */
    private JComponent buildUIConf(){
        JPanel panel = new JPanel(new MigLayout("fill"));
        JScrollPane scrollPane = new JScrollPane(panel);
        // Put all the default values in the datamap
        pee.setDefaultInputValues(dataUIManager.getInputDefaultValues(pee.getProcess()));
        //Creates the panel that will contains all the inputs.
        JPanel inputPanel = new JPanel(new MigLayout("fill"));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Inputs"));
        panel.add(inputPanel, "growx, span");

        for(Input i : pee.getProcess().getInput()){
            DataUI dataUI = dataUIManager.getDataUI(i.getDataDescription().getClass());

            if(dataUI!=null) {
                //Retrieve the component containing all the UI components.
                JComponent uiComponent = dataUI.createUI(i, pee.getInputDataMap());
                if(uiComponent != null) {
                    //If the input is optional, hide it
                    if(i.getMinOccurs()==0) {
                        uiComponent.setVisible(false);
                        //This panel is the one which contains the header with the title of the input and
                        // the hide/show button
                        JPanel contentPanel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
                        JPanel hideShowPanel = new JPanel(new MigLayout("ins 0, gap 0"));
                        //Sets the button to make it shown as just an icon
                        JButton showButton = new JButton(ToolBoxIcon.getIcon("btnright"));
                        showButton.setBorderPainted(false);
                        showButton.setMargin(new Insets(0, 0, 0, 0));
                        showButton.setContentAreaFilled(false);
                        showButton.setOpaque(false);
                        showButton.setFocusable(false);
                        showButton.putClientProperty("upPanel", hideShowPanel);
                        showButton.addMouseListener(EventHandler.create(MouseListener.class,
                                this, "onClickButton", "source", "mouseClicked"));
                        hideShowPanel.add(showButton);
                        hideShowPanel.add(new JLabel(i.getTitle()), "growx, span");
                        hideShowPanel.setToolTipText("Hide/Show option");
                        hideShowPanel.putClientProperty("body", uiComponent);
                        hideShowPanel.putClientProperty("parent", contentPanel);
                        hideShowPanel.putClientProperty("button", showButton);
                        hideShowPanel.putClientProperty("scrollPane", scrollPane);
                        hideShowPanel.addMouseListener(EventHandler.create(MouseListener.class,
                                this, "onClickHeader", "source", "mouseClicked"));
                        contentPanel.add(hideShowPanel, "growx, span");
                        inputPanel.add(contentPanel, "growx, span");
                    }
                    else{
                        inputPanel.add(new JLabel(i.getTitle()), "growx, span");
                        inputPanel.add(uiComponent, "growx, span");
                    }
                    inputPanel.add(new JSeparator(), "growx, span");
                }
            }
        }

        //Creates the panel that will contains all the inputs.
        JPanel outputPanel = new JPanel(new MigLayout("fill"));
        outputPanel.setBorder(BorderFactory.createTitledBorder("Outputs"));
        panel.add(outputPanel, "growx, span");

        for(Output o : pee.getProcess().getOutput()){
            DataUI dataUI = dataUIManager.getDataUI(o.getDataDescription().getClass());
            if(dataUI!=null) {
                JComponent component = dataUI.createUI(o, pee.getOutputDataMap());
                if(component != null) {
                    outputPanel.add(component, "growx, span");
                }
                outputPanel.add(new JSeparator(), "growx, span");
            }
        }
        JButton runButton = new JButton("Run", ToolBoxIcon.getIcon("execute"));
        runButton.setBorderPainted(false);
        runButton.addActionListener(EventHandler.create(ActionListener.class, this, "runProcess"));
        panel.add(runButton, "growx, wrap");
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLLBAR_UNIT_INCREMENT);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(SCROLLBAR_UNIT_INCREMENT);
        return scrollPane;
    }

    /**
     * When the title arrow button is clicked, expand the input/output components.
     * @param source Arrow button.
     */
    public void onClickButton(Object source) {
        JButton button = (JButton)source;
        onClickHeader(button.getClientProperty("upPanel"));
    }

    /**
     * When the title is clicked, expand the input/output components.
     * @param source Title text.
     */
    public void onClickHeader(Object source){
        JPanel panel = (JPanel)source;
        JButton showButton = (JButton)panel.getClientProperty("button");
        final JComponent body = (JComponent)panel.getClientProperty("body");
        JComponent parent = (JComponent)panel.getClientProperty("parent");
        final JScrollPane scrollPane = (JScrollPane)panel.getClientProperty("scrollPane");
        boolean isVisible = body.isVisible();
        if(isVisible) {
            body.setVisible(false);
            parent.remove(body);
            showButton.setIcon(ToolBoxIcon.getIcon("btnright"));
        }
        else{
            body.setVisible(true);
            parent.add(body, "growx, span");
            showButton.setIcon(ToolBoxIcon.getIcon("btndown"));
            //Later scrollDown to the element
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    scrollPane.scrollRectToVisible(body.getBounds());
                }
            });
            scrollPane.scrollRectToVisible(body.getBounds());
        }
        parent.revalidate();
    }

    /**
     * Build the UI of the given process according to the given data.
     * @return The UI for the configuration of the process.
     */
    private JComponent buildUIInfo(){
        JPanel panel = new JPanel(new MigLayout("fill"));
        Process p  = pee.getProcess();
        //Process info
        JLabel titleContentLabel = new JLabel(p.getTitle());
        JTextArea resumeContentLabel;
        if(p.getResume() != null) {
            resumeContentLabel = createResumeLabel(p.getResume());
        }
        else{
            resumeContentLabel = createResumeLabel("-");
        }
        resumeContentLabel.setFont(resumeContentLabel.getFont().deriveFont(Font.ITALIC));

        JPanel processPanel = new JPanel(new MigLayout("fill"));
        processPanel.setBorder(BorderFactory.createTitledBorder("Process :"));
        processPanel.add(titleContentLabel, "wrap, align left");
        processPanel.add(resumeContentLabel, "growx, wrap, align left");

        //Input info
        JPanel inputPanel = new JPanel(new MigLayout("fill"));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Inputs :"));

        for(Input i : p.getInput()){
            JPanel title = new JPanel(new BorderLayout());
            title.add(new JLabel(dataUIManager.getIconFromData(i)), BorderLayout.LINE_START);
            title.add(new JLabel(i.getTitle()), BorderLayout.CENTER);
            inputPanel.add(title, "align left, growx, wrap");
            if(i.getResume() != null) {
                JTextArea resume = createResumeLabel(i.getResume());
                inputPanel.add(resume, "growx, wrap");
            }
            else {
                inputPanel.add(new JLabel("-"), "growx, wrap");
            }
        }

        //Output info
        JPanel outputPanel = new JPanel(new MigLayout("fill"));
        outputPanel.setBorder(BorderFactory.createTitledBorder("Outputs :"));

        for(Output o : p.getOutput()){
            JPanel title = new JPanel(new BorderLayout());
            title.add(new JLabel(dataUIManager.getIconFromData(o)), BorderLayout.LINE_START);
            title.add(new JLabel(o.getTitle()), BorderLayout.CENTER);
            outputPanel.add(title, "align left, growx, wrap");
            if(o.getResume() != null) {
                JTextArea resume = createResumeLabel(o.getResume());
                outputPanel.add(resume, "growx, wrap");
            }
            else {
                outputPanel.add(new JLabel("-"), "growx, wrap");
            }
        }

        panel.add(processPanel, "growx, wrap");
        panel.add(inputPanel, "growx, wrap");
        panel.add(outputPanel, "growx, wrap");

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(SCROLLBAR_UNIT_INCREMENT);
        return scrollPane;
    }

    private JTextArea createResumeLabel(String text){
        JTextArea label = new JTextArea(text);
        label.setEditable(false);
        label.setCursor(null);
        label.setOpaque(false);
        label.setFocusable(false);
        label.setWrapStyleWord(true);
        label.setLineWrap(true);
        label.setFont(UIManager.getFont("Label.font").deriveFont(Font.ITALIC));
        return label;
    }

    public void startWaiting(){
        int w = contentPanel.getWidth();
        int h = contentPanel.getHeight();
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        waitLabel = new JLabel(new ImageIcon(bi));
        contentPanel.paint(g);
        contentPanel.remove(tabbedPane);
        contentPanel.add(waitLabel);
        layerUI.start();
    }

    public void endWaiting(){
        layerUI.stop();
        contentPanel.remove(waitLabel);
        contentPanel.add(tabbedPane);
    }

    class WaitLayerUI extends LayerUI<JPanel> implements ActionListener {
        private boolean mIsRunning;
        private boolean mIsFadingOut;
        private Timer mTimer;

        private int mAngle;
        private int mFadeCount;
        private int mFadeLimit = 15;

        @Override
        public void paint(Graphics g, JComponent c) {
            int w = c.getWidth();
            int h = c.getHeight();

            // Paint the view.
            super.paint (g, c);

            if (!mIsRunning) {
                return;
            }

            Graphics2D g2 = (Graphics2D)g.create();

            float fade = (float)mFadeCount / (float)mFadeLimit;
            // Gray it out.
            Composite urComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, .5f * fade));
            g2.fillRect(0, 0, w, h);
            g2.setComposite(urComposite);

            // Paint the wait indicator.
            int s = Math.min(w, h) / 5;
            int cx = w / 2;
            int cy = h / 2;
            g2.setPaint(Color.white);
            //"Loading source" painting
            Font font = g2.getFont().deriveFont(Font.PLAIN, s / 3);
            g2.setFont(font);
            FontMetrics metrics = g2.getFontMetrics(font);
            int w1 = metrics.stringWidth("Loading");
            int w2 = metrics.stringWidth("source");
            int h1 = metrics.getHeight();
            g2.drawString("Loading", cx - w1 / 2, cy - h1 / 2);
            g2.drawString("source", cx - w2 / 2, cy + h1 / 2);
            int space = h1;
            //"double-click to cancel" painting
            font = g2.getFont().deriveFont(Font.PLAIN, s / 10);
            g2.setFont(font);
            metrics = g2.getFontMetrics(font);
            w1 = metrics.stringWidth("Double-click");
            w2 = metrics.stringWidth("to cancel");
            h1 = metrics.getHeight();
            g2.drawString("Double-click", cx - w1 / 2, cy + space - h1 / 2);
            g2.drawString("to cancel", cx - w2 / 2, cy + space + h1 / 2);
            //waiter painting
            g2.setStroke(new BasicStroke(s / 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.rotate(Math.PI * mAngle / 180, cx, cy);
            for (int i = 1; i < 12; i++) {
                float scale = (11.0f - (float)i) / 11.0f;
                g2.drawLine(cx + s, cy, cx + s * 2, cy);
                g2.rotate(-Math.PI / 6, cx, cy);
                g2.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, scale * fade));
            }
            Toolkit.getDefaultToolkit().sync();
            g2.dispose();
        }

        public void actionPerformed(ActionEvent e) {
            if (mIsRunning) {
                firePropertyChange("tick", 0, 1);
                mAngle += 3;
                if (mAngle >= 360) {
                    mAngle = 0;
                }
                if (mIsFadingOut) {
                    if (--mFadeCount <= 0) {
                        mIsRunning = false;
                        mTimer.stop();
                    }
                }
                else if (mFadeCount < mFadeLimit) {
                    mFadeCount++;
                }
            }
        }

        public void start() {
            if (mIsRunning) {
                return;
            }

            // Run a thread for animation.
            mIsRunning = true;
            mIsFadingOut = false;
            mFadeCount = 0;
            int fps = 24;
            int tick = 1000 / fps;
            mTimer = new Timer(tick, this);
            mTimer.start();
        }

        public void stop() {
            mIsFadingOut = true;
        }

        @Override
        public void applyPropertyChange(PropertyChangeEvent pce, JLayer l) {
            if ("tick".equals(pce.getPropertyName())) {
                l.repaint();
            }
        }
    }
}
