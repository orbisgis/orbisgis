/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
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
