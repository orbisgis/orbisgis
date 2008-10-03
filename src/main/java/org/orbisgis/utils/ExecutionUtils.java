package org.orbisgis.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExecutionUtils {

	public static byte[] executeThread(String[] command, byte[] input)
			throws IOException, ExecutionException {
		ProcessBuilder pb = new ProcessBuilder(command);
		Process p = pb.start();
		StreamReader output = new StreamReader(p.getInputStream());
		StreamReader error = new StreamReader(p.getErrorStream());
		output.start();
		error.start();
		try {
			if (input != null) {
				OutputStream os = p.getOutputStream();
				FileUtils.copy(new ByteArrayInputStream(input), os);
				os.close();
			}
			int exitCode = p.waitFor();
			while (!(output.isDone() && error.isDone())) {
				Thread.yield();
			}
			if (error.getContent().length > 0) {
				throw new ExecutionException(new String(error.getContent(),
						"UTF-8")
						+ new String(output.getContent(), "UTF-8"));
			}
			if (exitCode == 0) {
				return output.getContent();
			} else {
				throw new ExecutionException(new String(error.getContent(),
						"UTF-8")
						+ new String(output.getContent(), "UTF-8"));
			}

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static class StreamReader extends Thread {
		private static final int BUFFER_SIZE = 1024;

		private InputStream is;

		private byte[] content = null;

		private IOException error = null;

		public StreamReader(InputStream is) {
			this.is = is;
		}

		public boolean isDone() {
			return content != null;
		}

		public byte[] getContent() throws IOException {
			if (error != null) {
				throw error;
			}
			return content;
		}

		public void run() {
			try {
				BufferedInputStream br = new BufferedInputStream(is,
						BUFFER_SIZE);
				ByteArrayOutputStream bou = new ByteArrayOutputStream();
				FileUtils.copy(br, bou);
				content = bou.toByteArray();
			} catch (IOException ioe) {
				error = ioe;
			}
		}
	}
}
