package net.mightypork.rpw.utils;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.utils.OsUtils.EnumOS;


public class DesktopApi {

	public static boolean browse(URI uri) {

		if (openSystemSpecificURL(uri.toString())) return true;

		if (browseDESKTOP(uri)) return true;

		return false;
	}


	public static boolean open(File file) {

		if (openSystemSpecific(file.getPath())) return true;

		if (openDESKTOP(file)) return true;

		return false;
	}


	public static boolean editText(File file) {

		if (Config.USE_TEXT_EDITOR) {
			if (runCommand(Config.TEXT_EDITOR, Config.TEXT_EDITOR_ARGS, file.getPath())) return true;
		}

		if (openSystemSpecific(file.getPath())) return true;

		if (editDESKTOP(file)) return true;

		return false;
	}


	public static boolean editImage(File file) {

		if (Config.USE_IMAGE_EDITOR) {
			if (runCommand(Config.IMAGE_EDITOR, Config.IMAGE_EDITOR_ARGS, file.getPath())) return true;
		}

		if (openSystemSpecific(file.getPath())) return true;

		if (editDESKTOP(file)) return true;

		return false;
	}


	public static boolean editAudio(File file) {

		if (Config.USE_AUDIO_EDITOR) {
			if (runCommand(Config.AUDIO_EDITOR, Config.AUDIO_EDITOR_ARGS, file.getPath())) return true;
		}

		if (openSystemSpecific(file.getPath())) return true;

		if (editDESKTOP(file)) return true;

		return false;
	}


	private static boolean openSystemSpecificURL(String what) {

		EnumOS os = OsUtils.getOs();

		if (os.isLinux()) {
			if (runCommand("gnome-open", "%s", what)) return true;
			if (runCommand("xdg-open", "%s", what)) return true;
			if (runCommand("kde-open", "%s", what)) return true;
		}

		if (os.isMac()) {
			if (runCommand("open", "%s", what)) return true;
		}

		if (os.isWindows()) {
			if (runCommand("explorer", "%s", what)) return true;
		}

		return false;
	}


	private static boolean openSystemSpecific(String what) {

		EnumOS os = OsUtils.getOs();

		if (os.isLinux()) {
			if (runCommand("kde-open", "%s", what)) return true;
			if (runCommand("gnome-open", "%s", what)) return true;
			if (runCommand("xdg-open", "%s", what)) return true;
		}

		if (os.isMac()) {
			if (runCommand("open", "%s", what)) return true;
		}

		if (os.isWindows()) {
			if (runCommand("explorer", "%s", what)) return true;
		}

		return false;
	}


	private static boolean browseDESKTOP(URI uri) {

		Log.f2("Trying to use Desktop.getDesktop().browse() with " + uri.toString());
		try {
			if (!Desktop.isDesktopSupported()) {
				Log.w("Platform is not supported.");
				return false;
			}

			if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Log.w("BORWSE is not supported.");
				return false;
			}

			Desktop.getDesktop().browse(uri);

			return true;
		} catch (Throwable t) {
			Log.eh("Error using desktop browse.", t);
			return false;
		}
	}


	private static boolean openDESKTOP(File file) {

		Log.f2("Trying to use Desktop.getDesktop().open() with " + file.toString());
		try {
			if (!Desktop.isDesktopSupported()) {
				Log.w("Platform is not supported.");
				return false;
			}

			if (!Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
				Log.w("OPEN is not supported.");
				return false;
			}

			Desktop.getDesktop().open(file);

			return true;
		} catch (Throwable t) {
			Log.eh("Error using desktop open.", t);
			return false;
		}
	}


	private static boolean editDESKTOP(File file) {

		Log.f2("Trying to use Desktop.getDesktop().edit() with " + file);
		try {
			if (!Desktop.isDesktopSupported()) {
				Log.w("Platform is not supported.");
				return false;
			}

			if (!Desktop.getDesktop().isSupported(Desktop.Action.EDIT)) {
				Log.w("EDIT is not supported.");
				return false;
			}

			Desktop.getDesktop().edit(file);

			return true;
		} catch (Throwable t) {
			Log.eh("Error using desktop edit.", t);
			return false;
		}
	}


	private static boolean runCommand(String command, String args, String file) {

		Log.f2("Trying to exec:\n   cmd = " + command + "\n   args = " + args + "\n   %s = " + file);

		String[] parts = prepareCommand(command, args, file);

		try {
			Process p = Runtime.getRuntime().exec(parts);
			if (p == null) return false;

			try {
				int retval = p.exitValue();
				if (retval == 0) {
					Log.w("Process ended immediately.");
					return false;
				} else {
					Log.w("Process crashed.");
					return false;
				}
			} catch (IllegalThreadStateException itse) {
				Log.f3("Process is running.");
				return true;
			}
		} catch (IOException e) {
			Log.eh("Error running command.", e);
			return false;
		}
	}


	private static String[] prepareCommand(String command, String args, String file) {

		List<String> parts = new ArrayList<String>();
		parts.add(command);

		if (args != null) {
			for (String s : args.split(" ")) {
				s = String.format(s, file); // put in the filename thing

				parts.add(s.trim());
			}
		}

		return parts.toArray(new String[parts.size()]);
	}
}