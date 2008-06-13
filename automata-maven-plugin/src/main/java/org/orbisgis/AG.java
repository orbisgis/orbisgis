/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.codehaus.plexus.util.FileUtils;

public class AG {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err
					.println("Usage: java Transform [tooldir] [sourcefolder] [package]");
			System.exit(1);
		}

		File tooldir = new File(args[0]);
		File sourcefolder = new File(args[1] + File.separator
				+ args[2].replaceAll("\\Q.\\E", "/"));

		System.out.println("Getting tools from " + tooldir.getAbsolutePath());
		File[] tools = tooldir.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				return pathname.getAbsolutePath().toLowerCase().endsWith(
						".fsa.xml");
			}

		});

		if (tools == null) {
			System.err.println("No tool found");
			return;
		}

		TransformerFactory transFact = TransformerFactory.newInstance();

		String xml = "<messages package=\"" + args[2] + "\"/>";
		Source xmlSource = new StreamSource(new ByteArrayInputStream(xml
				.getBytes()));
		Source xsltSource = new StreamSource(AG.class
				.getResourceAsStream("messages.xsl"));
		Transformer trans = transFact.newTransformer(xsltSource);

		String name = "Messages.java";
		transformIfOutDated(trans, xmlSource, sourcefolder.getAbsolutePath()
				+ File.separator + name);

		for (int i = 0; i < tools.length; i++) {
			xmlSource = new StreamSource(tools[i]);
			InputStream is = AG.class.getResourceAsStream("tool.xsl");
			xsltSource = new StreamSource(replacePackage(is, args[2]));

			trans = transFact.newTransformer(xsltSource);

			name = tools[i].getName();
			name = name.substring(0, name.length() - 8) + ".java";
			transformIfOutDated(trans, xmlSource, sourcefolder
					.getAbsolutePath()
					+ File.separator + name);

		}
	}

	private static void transformIfOutDated(Transformer trans,
			Source xmlSource, String destFile) throws TransformerException,
			IOException {
		File temp = File.createTempFile("automata", ".java");
		trans.transform(xmlSource, new StreamResult(temp));

		String content = getContent(temp);
		File dest = new File(destFile);
		if (content.equals(getContent(dest))) {
			System.out.println(destFile + " is up to date");
			return;
		} else {
			FileUtils.copyFile(temp, dest);
		}
	}

	private static String getContent(File temp) throws IOException {
		if (!temp.exists()) {
			return null;
		}
		FileInputStream fis = new FileInputStream(temp);
		DataInputStream dis = new DataInputStream(fis);
		byte[] content = new byte[(int) fis.getChannel().size()];
		dis.readFully(content);
		dis.close();
		return new String(content);
	}

	private static InputStream replacePackage(InputStream is, String package_)
			throws IOException {
		DataInputStream dis = new DataInputStream(is);
		byte[] buffer = new byte[dis.available()];
		dis.readFully(buffer);
		String ret = new String(buffer);
		ret = ret.replaceAll("\\Q[PACKAGE]\\E", package_);
		return new ByteArrayInputStream(ret.getBytes());
	}

}
