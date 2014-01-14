package org.orbisgis.view.background;

import org.h2gis.h2spatialapi.ProgressVisitor;
import org.orbisgis.progress.ProgressMonitor;

import java.sql.Statement;

/**
 * Wrapper between ProgressVisitor and ProgressMonitor.
 * @author Nicolas Fortin
 */
public class H2GISProgressMonitor implements ProgressVisitor {
    private ProgressMonitor progressMonitor;
    private Statement statement;

    public H2GISProgressMonitor(ProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
    }

    @Override
    public void endOfProgress() {
        progressMonitor.endTask();
    }

    /**
     * @param statement Statement to cancel if the processing is canceled
     */
    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public ProgressVisitor subProcess(int i) {
        return new H2GISProgressMonitor(progressMonitor.startTask(i));
    }

    @Override
    public void endStep() {
        progressMonitor.endTask();
    }

    @Override
    public void setStep(int i) {
        progressMonitor.progressTo(i);
    }

    @Override
    public int getStepCount() {
        return (int)progressMonitor.getEnd();
    }

    @Override
    public double getProgression() {
        return progressMonitor.getOverallProgress();
    }
}
