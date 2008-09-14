package org.orbisgis;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.orbisgis.pluginManager.PluginManager;

public class SendLog implements Runnable {

	public void run() {
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			ftp.connect("fergonco.scienceontheweb.net");
			// After connection attempt, you should check the reply code to
			// verify success.
			reply = ftp.getReplyCode();

			if (FTPReply.isPositiveCompletion(reply)) {
				if (ftp.login("82522_orbislog", "orbislog")) {
					ftp.enterLocalPassiveMode();
					FTPFile[] files = ftp.listFiles();
					HashSet<String> fileSet = new HashSet<String>();
					for (FTPFile file : files) {
						fileSet.add(file.getName());
					}
					String name;
					int serial = 0;
					do {
						name = "log" + serial + ".txt";
						serial++;
					} while (fileSet.contains(name));

					File log = new File(Services
							.getService(PluginManager.class).getLogFile());
					FileInputStream fis = new FileInputStream(log);
					System.out.println(ftp.storeFile(name, fis));
					fis.close();
					ftp.logout();
				}
			}
		} catch (Throwable e) {
			// ignore
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (Throwable ioe) {
					// do nothing
				}
			}
		}
	}
}
