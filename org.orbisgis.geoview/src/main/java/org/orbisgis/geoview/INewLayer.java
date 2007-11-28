package org.orbisgis.geoview;

import org.orbisgis.core.wizards.IWizard;
import org.orbisgis.geoview.layerModel.ILayer;

public interface INewLayer extends IWizard {

	ILayer[] getLayers();

}
