package org.orbisgis.plugins.core.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;
import java.util.Observable;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.gdms.source.SourceManager;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.geocognition.Geocognition;
import org.orbisgis.plugins.core.geocognition.GeocognitionElement;
import org.orbisgis.plugins.core.language.I18N;
import org.orbisgis.plugins.core.layerModel.ILayer;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.images.IconLoader;

public abstract class AbstractPlugIn implements PlugIn {

	private String name;
	private PlugInContext plugIncontext;
	private UpdatePlugInFactory updatePlugInFactory;
	// Action component (Button) to run plugIn (In case that plugIn made up of
	// several swing component)
	private JComponent actionComponent;
	private String typeListener;
	private ResourceBundle i18n;
	//private I18N i18n;

	private Locale locale;

	// Constructors
	public AbstractPlugIn() {
	}

	public AbstractPlugIn(String name) {
		this.name = name;
	}

	// Default PlugIn methods
	public boolean execute(PlugInContext context) throws Exception {
		return false;
	}

	public void initialize(PlugInContext context) throws Exception {
	}

	public void update(Observable arg0, Object arg1) {
	}

	public void i18nConfigure(String path, String langcountry) {
		Locale locale = Locale.getDefault();
		if(path==null || path.equals(""))
			path = "OrbisGIS";
		else {		
			if(langcountry!=null) {
				String[] lc = langcountry.split("_");
				
				if (lc.length > 1) {
					//Services.getErrorManager().error("lang:" + lc[0] + " " + "country:" + lc[1]);
					locale = new Locale(lc[0], lc[1]);
				} else if (lc.length > 0) {
					//Services.getErrorManager().error("lang:" + lc[0]);
					locale = new Locale(lc[0]);
				} else {
					//Services.getErrorManager().error(langcountry
							//+ " is an illegal argument to define lang [and country]");
				}	
			}
		}
		//i18n = I18N.getInstance(this.getClass(),path,locale);
		i18n = ResourceBundle.getBundle(path, locale, this.getClass().getClassLoader());		
	}
	/*public void i18nConfigure() {
		i18nOJ = I18N.getInstance();
		i18nOJ.setResourceBundle(ResourceBundle.getBundle("text_i18n", Locale.getDefault(), this.getClass().getClassLoader()));
		I18N.setClassLoader(this.getClass().getClassLoader());
	}*/
	
	// implemented by PlugIns
	// Factory for adapt PlugIn context (Visibility, Enabled)
	public void createUpdatePlugInFactory(WorkbenchContext workbenchContext) {	
		setPlugInContext(workbenchContext.createPlugInContext());
		updatePlugInFactory = new UpdatePlugInFactory(workbenchContext, this);
	}


	// Specific implemented methods by some PlugIn (use in UdpateFactory)
	// test in TOC for swing visibility
	public void execute(MapContext mapContext, ILayer layer) {
	}

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

	// listeners
	public static ActionListener toActionListener(final PlugIn plugIn,
			final WorkbenchContext workbenchContext) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.out.println("JButton : actionPerformed ");
				try {
					PlugInContext plugInContext = workbenchContext
							.createPlugInContext();
					boolean executeComplete = plugIn.execute(plugInContext);
					if (executeComplete)
						workbenchContext.setLastAction("update");
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
					if (executeComplete)
						workbenchContext.setLastAction("update");
					// context.notifyObservers();
				} catch (Exception e) {
					Services.getErrorManager().error(
							"Add Item listener to plugin failed !", e);
				}
			}
		};
	}

	// getters & setters
	public ResourceBundle getI18n() {
		return i18n;
	}

	public void setI18n(ResourceBundle i18n) {
		this.i18n = i18n;
	}
/*	public I18N getI18n() {
		return i18n;
	}

	public void setI18n(I18N i18n) {
		this.i18n = i18n;
	}*/

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	
	public String getName() {
		return name == null ? createName(getClass()) : name;
	}

	public static String createName(Class plugInClass) {
		return plugInClass.getName();
	}

	protected PlugInContext getPlugIncontext() {
		return plugIncontext;
	}

	protected void setPlugInContext(PlugInContext context) {
		this.plugIncontext = context;
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

	public UpdatePlugInFactory getUpdateFactory() {
		return updatePlugInFactory;
	}

	public static ImageIcon getIcon(String nameIcone) {
		return IconLoader.getIcon(nameIcone);
	}
}
