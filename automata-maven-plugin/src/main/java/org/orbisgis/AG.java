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
