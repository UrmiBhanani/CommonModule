package com.androidcommonlibrary.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.androidcommonlibrary.constants.Constants;

public class Storage {

	public static void verifyDataPath() throws IOException {
		File dir = new File(Constants.APP_HOME);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		dir = null;
	}

	public static void verifyLogPath() throws IOException {

		File dir = new File(Constants.DIR_LOG);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		dir = null;
	}
	
	public static void verifyImagePath() throws IOException {
		File dir = new File(Constants.DIR_IMAGES);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		dir = null;
	}

	public static File verifyLogFile() throws IOException {
		File logFile = new File(Constants.DIR_LOG + "/RightVerify_Log_"
				+ new SimpleDateFormat("yyyy_MM_dd").format(new Date())
				+ ".html");
		FileOutputStream fos = null;

		Storage.verifyDataPath();
		Storage.verifyLogPath();

		if (!logFile.exists()) {
			logFile.createNewFile();

			fos = new FileOutputStream(logFile);

			String str = "<TABLE style=\"width:100%;border=1px\" cellpadding=2 cellspacing=2 border=1><TR>"
					+ "<TD style=\"width:30%\"><B>Date n Time</B></TD>"
					+ "<TD style=\"width:20%\"><B>Title</B></TD>"
					+ "<TD style=\"width:50%\"><B>Description</B></TD></TR>";

			fos.write(str.getBytes());
		}

		if (fos != null) {
			fos.close();
		}

		fos = null;

		return logFile;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static void createLogZip() {
		ZipOutputStream zout = null;
		FileInputStream fis = null;
		String files[] = null;
		int ch;
		try {
			files = new File(Constants.DIR_LOG).list();

			zout = new ZipOutputStream(new FileOutputStream(Constants.LOG_ZIP));

			zout.setLevel(Deflater.DEFAULT_COMPRESSION);

			for (int ele = 0; ele < files.length; ele++) {
				fis = new FileInputStream(Constants.DIR_LOG + "/" + files[ele]);

				/*
				 * To begin writing ZipEntry in the zip file, use
				 * 
				 * void putNextEntry(ZipEntry entry) method of ZipOutputStream
				 * class.
				 * 
				 * This method begins writing a new Zip entry to the zip file
				 * and positions the stream to the start of the entry data.
				 */

				zout.putNextEntry(new ZipEntry(Constants.DIR_LOG + "/" + files[ele]));

				/*
				 * After creating entry in the zip file, actually write the
				 * file.
				 */

				while ((ch = fis.read()) > 0) {
					zout.write(ch);
				}

				/*
				 * After writing the file to ZipOutputStream, use
				 * 
				 * void closeEntry() method of ZipOutputStream class to close
				 * the current entry and position the stream to write the next
				 * entry.
				 */

				zout.closeEntry();

				// close the InputStream
				fis.close();
			}

			// close the ZipOutputStream
			zout.close();

		} catch (Exception e) {
			Log.error(Storage.class + " :: create log zip :: ", e);
		}

		zout = null;
		fis = null;
		files = null;
	}

	public static void clearLog() {
		String files[] = null;
		File file = null;
		try {
			files = new File(Constants.DIR_LOG).list();

			for (int ele = 0; ele < files.length; ele++) {
				file = new File(Constants.DIR_LOG, files[ele]);

				file.delete();
			}

		} catch (Exception e) {
			Log.error(Storage.class + " :: clearLog :: ", e);
		}

		files = null;
		files = null;
	}

	public static void delete(String f) throws IOException {
		File file = new File(f);

		if (file.exists()) {
			file.delete();
		}

		file = null;
		f = null;
	}

	public static void copyImageFile(File sourceLocation, File targetLocation)
			throws IOException {
		Storage.verifyDataPath();
		InputStream in = new FileInputStream(sourceLocation);
		OutputStream out = new FileOutputStream(targetLocation);

		// Copy the bits from instream to outstream
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static boolean copy(String src, String dest) {
		System.out.println("=========FROM :: " + src);
		System.out.println("=========TO :: " + dest);
		boolean success = true;
		FileInputStream in = null;
		FileOutputStream out = null;

		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);

			byte[] buf = new byte[1024];
			int len;

			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return success;
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
			Log.error("Storage ::copy:: ", e);
		} finally {
			success = false;
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
		in = null;
		out = null;
		return success;

	}

	public static void copyFile(InputStream in, String dest) {
		// System.out.println("=========FROM :: " + src);
		// System.out.println("=========TO :: " + dest);

		FileOutputStream out = null;

		try {
			out = new FileOutputStream(dest);

			byte[] buf = new byte[1024];
			int len;

			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
		in = null;
		out = null;
	}

	public static String getRealPathFromURIForVideo(Activity context,
			Uri contentUri) {
		String[] proj = { MediaStore.Video.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = context.managedQuery(contentUri, proj, // Which columns
																// to //
				// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
		cursor.moveToFirst();
		System.out.println("Storage ::: >>................................ cursor.getString(column_index)........."
						+ cursor.getString(column_index));
		return cursor.getString(column_index);
	}

	public static String getRealPathFromURIForAudio(Activity context,
			Uri contentUri) {
		String[] proj = { MediaStore.Audio.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = context.managedQuery(contentUri, proj, // Which columns
																// to //
				// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
		cursor.moveToFirst();
		System.out
				.println("Storage ::: >>................................ cursor.getString(column_index)........."
						+ cursor.getString(column_index));
		return cursor.getString(column_index);
	}

	public static String getRealPathFromURIForImage(Activity context,
			Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = context.managedQuery(contentUri, proj, // Which columns
																// to //
				// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		System.out
				.println("Storage ::: >>................................ cursor.getString(column_index)........."
						+ cursor.getString(column_index));
		return cursor.getString(column_index);
	}

	public static void copyFromAssets(InputStream in, String dest) {
		FileOutputStream out = null;
		int ch;

		try {
			out = new FileOutputStream(dest);

			while ((ch = in.read()) != -1) {
				out.write(ch);
			}

			in.close();
			out.close();
		} catch (IOException e) {
			Log.error("Storage::copy()", e);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}

			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}

		in = null;
		out = null;
	}

	
}