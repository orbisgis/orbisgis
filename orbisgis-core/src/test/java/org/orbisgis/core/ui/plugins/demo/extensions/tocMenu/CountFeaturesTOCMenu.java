package org.orbisgis.core.ui.plugins.demo.extensions.tocMenu;

import java.awt.Color;

import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;

public class CountFeaturesTOCMenu extends AbstractPlugIn {

	/**
	 * Dans cette méthode est spécifiée le type de plugin qui sera construit.
	 * Ici il s'agira d'un menu qui apparaitra dans le TOC lorsque l'utilisateur
	 * réalisera un clic-droit. Le menu s'appelera "Compter les objet".
	 */
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { "Compter les objets" }, wbContext);
	}

	/**
	 * Cette méthode permet de préciser les conditions d'affichage du menu
	 * "Compter les objets" lors d'un clic-droit sur une couche du TOC.
	 */
	public boolean isEnabled() {
		//L'objet MapContext courant est récupéré. Il donne les informations sur
		//le nombre de couches chargées, selectionnées...
		MapContext mc = getPlugInContext().getMapContext();
		if (mc != null) {
			//Si le nombre de couches selectionnées est égale à 1 alors 
			//le plugin est visible.
			if (mc.getSelectedLayers().length == 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Méthode d'execution du plugin
	 */
	public boolean execute(PlugInContext context) throws Exception {
		MapContext mapContext = getPlugInContext().getMapContext();
		ILayer[] selectedLayers = mapContext.getSelectedLayers();

		//Récupération de l'objet GDMS contenant les données de la couche.
		SpatialDataSourceDecorator sds = selectedLayers[0]
				.getSpatialDataSource();
		//Le résultat est affiché dans OrbisGIS avec la fenêtre OutputManager.
		Services.getOutputManager().print(
				"Nombre d'objets :" + sds.getRowCount(), Color.RED);

		return false;
	}
}
