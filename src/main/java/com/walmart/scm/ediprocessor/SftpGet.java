package com.walmart.scm.ediprocessor;

import java.io.File;
import com.jcraft.jsch.UserInfo;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

public class SftpGet {

	public static void main(String[] args) {
		downloadFile();

	}

	private static boolean downloadFile() {

		String host = "52.32.141.49";
		String user = "ec2-user";
		String password = "";
		String fileName = "/local/apps";
		//String localFilePath = "c:/cygwin64/home/woddle/wrote_lines.txt";

		// without passphrase
		String keyPath = "c:/Anand/AWSKeys/MyEC2Key.pem";
		String passphrase = null;

		// with passphrase
		// String keyPath = "c:/cygwin64/home/woddle/.ssh/id_dsa_withpass";
		// String passphrase = "super-secrets";

		StandardFileSystemManager sysManager = new StandardFileSystemManager();

		// download der Datei
		try {
			sysManager.init();

			//FileObject localFile = sysManager.resolveFile(localFilePath);

			FileObject remoteFile = sysManager.resolveFile(createConnectionString(host, user, password, keyPath, passphrase, fileName), createDefaultOptions(keyPath, passphrase));
			
			FileObject[] rfiles = remoteFile.getChildren();
			for(FileObject rfile: rfiles)
			{
				if(rfile.isFile())
				{
					System.out.println(rfile.getPublicURIString());
				}
			}

			// Selectors.SELECT_FILES --> A FileSelector that selects only the
			// base file/folder.
			//localFile.copyFrom(remoteFile, Selectors.SELECT_FILES);

		} catch (Exception e) {
			System.out.println("Downloading file failed: " + e.toString());
			return false;
		} finally {
			sysManager.close();
		}
		return true;
	}

	public static String createConnectionString(String hostName, String username, String password, String keyPath,
			String passphrase, String remoteFilePath) {

		if (keyPath != null) {
			return "sftp://" + username + "@" + hostName + "/" + remoteFilePath;
		} else {
			return "sftp://" + username + ":" + password + "@" + hostName + "/" + remoteFilePath;
		}
	}

	private static FileSystemOptions createDefaultOptions(final String keyPath, final String passphrase)
			throws FileSystemException {

		// create options for sftp
		FileSystemOptions options = new FileSystemOptions();
		// ssh key
		
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(options, "no");
		// set root directory to user home
		SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, true);
		// timeout
		SftpFileSystemConfigBuilder.getInstance().setTimeout(options, 10000);

		if (keyPath != null) {
			SftpFileSystemConfigBuilder.getInstance().setUserInfo(options, new SftpPassphraseUserInfo(passphrase));
			SftpFileSystemConfigBuilder.getInstance().setIdentities(options, new File[] { new File(keyPath) });
			SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
		}

		return options;
	}

	public static class SftpPassphraseUserInfo implements UserInfo {

		private String passphrase = null;

		public SftpPassphraseUserInfo(final String pp) {
			passphrase = pp;
		}

		public String getPassphrase() {
			return passphrase;
		}

		public String getPassword() {
			return null;
		}

		public boolean promptPassphrase(String arg0) {
			return true;
		}

		public boolean promptPassword(String arg0) {
			return false;
		}

		public void showMessage(String message) {

		}

		public boolean promptYesNo(String str) {
			return true;
		}

	}

}
