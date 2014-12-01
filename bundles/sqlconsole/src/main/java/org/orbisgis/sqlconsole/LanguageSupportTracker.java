package org.orbisgis.sqlconsole;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * When a new LanguageSupport is exposed as an OSGi service, this tracker register it to a RSyntaxArea.
 * @author Nicolas Fortin
 */
public class LanguageSupportTracker implements ServiceTrackerCustomizer<LanguageSupport, LanguageSupport> {
    private BundleContext bc;
    private RSyntaxTextArea textArea;

    public LanguageSupportTracker(BundleContext bc, RSyntaxTextArea textArea) {
        this.bc = bc;
        this.textArea = textArea;
    }

    @Override
    public LanguageSupport addingService(ServiceReference<LanguageSupport> reference) {
        LanguageSupport ls = bc.getService(reference);
        ls.install(textArea);
        return ls;
    }

    @Override
    public void modifiedService(ServiceReference<LanguageSupport> reference, LanguageSupport service) {
        // Property change, does nothing
    }

    @Override
    public void removedService(ServiceReference<LanguageSupport> reference, LanguageSupport service) {
        service.uninstall(textArea);
    }
}
