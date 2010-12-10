package org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

public class DefaultJavaManager implements JavaManager {

	private JavaCompiler compiler;

	private HashSet<File> additionalBuildPath = new HashSet<File>();

	private HashSet<File> buildPath = null;

	private PackageReflection pr;

	public DefaultJavaManager() {
		compiler = new EclipseCompiler();
	}

	private HashSet<File> getBuildPath(StandardJavaFileManager stdFileManager) {
		if (buildPath == null) {
			buildPath = new HashSet<File>();
			Iterator<? extends File> classPath = stdFileManager.getLocation(
					StandardLocation.CLASS_PATH).iterator();
			while (classPath.hasNext()) {
				buildPath.add(classPath.next());
			}
			buildPath.addAll(additionalBuildPath);
		}

		return buildPath;
	}

	@Override
	public void addFilesToClassPath(List<File> files) {
		additionalBuildPath.addAll(files);
		buildPath = null;
		pr = null;
	}

	@Override
	public PackageReflection getPackageReflection() {
		if (pr == null) {
			try {
				StandardJavaFileManager stdFileManager = compiler
						.getStandardFileManager(null, null, null);
				HashSet<File> systemClassPath = getBuildPath(stdFileManager);
				pr = new PackageReflection(systemClassPath
						.toArray(new File[systemClassPath.size()]));
			} catch (LinkageError e) {
				throw new RuntimeException("Bug. Malformed classpaths", e);
			}
		}

		return pr;
	}

}
