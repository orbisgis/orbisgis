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
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.utils.I18N;

public abstract class AbstractPlugIn implements PlugIn {

	private String name;
	private PlugInContext plugInContext;
	// Action component (Button) to run plugIn (In case that plugIn made up of
	// several swing component)
	private JComponent actionComponent;
	private String typeListener;
	// private ResourceBundle i18n;
	private String langAndCountry;

	// Constructors
    public AbstractPlugIn() {
    	getI18n();
    }	    
    //I18N : defaut language is locale system. But method can be redefine by plugin
	public void i18nConfigure(String langAndCountry) {
		delI18n();
		this.langAndCountry = langAndCountry;				
		getI18n();
	}

	private void delI18n() {
		I18N.delI18n(null, this.getClass());		
	}

	public void getI18n() {
		I18N.addI18n(langAndCountry, null, this.getClass());
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
