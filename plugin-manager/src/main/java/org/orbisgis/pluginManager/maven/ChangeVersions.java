package org.orbisgis.pluginManager.maven;

import java.io.File;
import java.io.FileFilter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class ChangeVersions {

	public static void main(String[] args) throws Exception {
		File platform = new File("..");
		File[] projects = platform.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				return pathname.isDirectory()
						&& new File(pathname, "pom.xml").exists();
			}

		});

		for (File project : projects) {
			transform(new File(project, "pom.xml"));
		}
		
		System.out.println("Don't forget to change parent pom version");
	}

	private static void transform(File xmlFile)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException {

		Source xmlSource = new StreamSource(xmlFile);
		Source xsltSource = new StreamSource(ChangeVersions.class
				.getResourceAsStream("PomVersion.xsl"));
		Result result = new StreamResult(xmlFile);

		// create an instance of TransformerFactory
		TransformerFactory transFact = TransformerFactory.newInstance();

		Transformer trans = transFact.newTransformer(xsltSource);

		trans.transform(xmlSource, result);
	}
}
