package org.orbisgis.core.ui.pluginSystem;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Observable;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.gdms.source.SourceManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.images.IconLoader;
import org.orbisgis.core.language.I18N;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;

public abstract class AbstractPlugIn implements PlugIn {

	private String name;
	private PlugInContext plugInContext;
	// Action component (Button) to run plugIn (In case that plugIn made up of
	// several swing component)
	private JComponent actionComponent;
	private String typeListener;
	// private ResourceBundle i18n;
	private I18N i18n = I18N.getInstance();
	private String langAndCountry;

	// Constructors
     
	
	
	// Default PlugIn methods
	public  boolean execute(PlugInContext context) throws Exception {return false;};
	public  void initialize(PlugInContext context) throws Exception {};	
/*	public abstract boolean execute(PlugInContext context) throws Exception;
	public abstract void initialize(PlugInContext context) throws Exception;*/
	
	public void update(Observable arg0, Object arg1) {}

	public void i18nConfigure(String langAndCountry) {
		this.langAndCountry = langAndCountry;
	}

	public I18N getI18n() {
		return i18n.configure(langAndCountry, this.getClass());
	}

	// implemented by PlugIns
	// Factory for adapt PlugIn context (Visibility, Enabled)
	public void createPlugInContext(WorkbenchContext context) {
		plugInContext = context.createPlugInContext(this);
	}

	// Specific implemented methods by some PlugIn (use in UdpateFactory)
	// test in TOC for swing visibility
	public void execute(MapContext mapContext, ILayer layer) {}
	public boolean acceptsSelectionCount(int layerCount) {
		return false;
	}
	public boolean accepts(MapContext mc, ILayer layer) {
		return false;
	}
	public boolean acceptsAll(ILayer[] layers) {
		return false;
	}

	// idem for Geocognition
	public void execute(Geocognition geocognition, GeocognitionElement element) {
	}

	public boolean accepts(Geocognition geocog, GeocognitionElement element) {
		return false;
	}

	public boolean acceptsSelectionCount(Geocognition geocog, int selectionCount) {
		return false;
	}

	// idem for Geocatalog
	public void execute(SourceManager srcManager, String srcName) {
	}

	public boolean accepts(SourceManager srcManager, String srcName) {
		return false;
	}

	public boolean isVisible() {
		return false;
	}

	public boolean isEnabled() {
		return false;
	}

	public boolean isSelected() {
		return false;
	}

	// listeners
	public static ActionListener toActionListener(final PlugIn plugIn,
			final WorkbenchContext workbenchContext) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					PlugInContext plugInContext = workbenchContext
							.createPlugInContext();
					boolean executeComplete = plugIn.execute(plugInContext);
				} catch (Exception e) {
					Services.getErrorManager().error(
							"Add Action listener to plugin failed !", e);
				}
			}
		};
	}

	public static ItemListener toItemListener(final PlugIn plugIn,
			final WorkbenchContext workbenchContext) {
		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {
				System.out.println("JComboBox : itemStateChanged ");
				try {
					PlugInContext plugInContext = workbenchContext
							.createPlugInContext();
					boolean executeComplete = plugIn.execute(plugInContext);
				} catch (Exception e) {
					Services.getErrorManager().error(
							"Add Item listener to plugin failed !", e);
				}
			}
		};
	}

	// getters & setters

	public void setI18n(I18N i18n) {
		this.i18n = i18n;
	}

	public String getName() {
		return name == null ? createName(getClass()) : name;
	}

	public static String createName(Class plugInClass) {
		return plugInClass.getName();
	}

	protected PlugInContext getPlugInContext() {
		return plugInContext;
	}

	public void setPlugInContext(PlugInContext context) {
		this.plugInContext = context;
	}

	public Component getActionComponent() {
		return actionComponent;
	}

	public void setActionComponent(JComponent actionComponent) {
		this.actionComponent = actionComponent;
	}

	public String getTypeListener() {
		return typeListener;
	}

	public void setTypeListener(String typeListener) {
		this.typeListener = typeListener;
	}

	public static ImageIcon getIcon(String nameIcone) {
		return IconLoader.getIcon(nameIcone);
	}
}
