package org.orbisgis.core.ui.views.geocatalog.newSourceWizard;

import javax.swing.Icon;

import org.gdms.source.SourceManager;

public interface SourceRenderer {

	/**
	 * Get an icon for the specified source. Return null if this renderer
	 * doesn't affect the specified source
	 * 
	 * @param sourceManager
	 * @param source
	 * @return
	 */
	Icon getIcon(SourceManager sourceManager, String source);

	/**
	 * Get the text to show in the specified source. Return null if this
	 * renderer doesn't affect the specified source
	 * 
	 * @param sourceManager
	 * @param source
	 * @return
	 */
	String getText(SourceManager sourceManager, String source);

}
