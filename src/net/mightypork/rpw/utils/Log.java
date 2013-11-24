package net.mightypork.rpw.utils;


import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.Paths;


/**
 * Static logger class.
 * 
 * @author MightyPork
 * @copy (c) 2013
 */
public class Log {

	/**
	 * Global logger.
	 */
	private static final Logger logger = Logger.getLogger("McRpMgr");
	/** Logging enabled */
	public static boolean loggingEnabled = Config.LOGGING_ENABLED;
	/** Stdout printing enabled */
	public static boolean printToStdout = Config.LOG_TO_STDOUT;


	/**
	 * Prepare logs for logging
	 */
	public static void init() {

		try {
			File logfile = OsUtils.getAppDir(Paths.FILE_LOG);

			// delete old numbered logs
			for (File f : FileUtils.listDirectory(logfile.getParentFile())) {
				if (!f.isFile()) continue;
				if (f.getName().startsWith(Paths.FILENAME_LOG)) {
					f.delete();
				}
			}

			FileHandler handler1 = new FileHandler(logfile.getPath());
			handler1.setFormatter(new LogFormatter(true));
			logger.addHandler(handler1);

			loggingEnabled = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.setUseParentHandlers(false);
		logger.setLevel(Level.ALL);
		logger.info("Main logger initialized.");
		logger.info((new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date()));
	}


	/**
	 * Enable logging.
	 * 
	 * @param flag do enable logging
	 */
	public static void enable(boolean flag) {

		loggingEnabled = flag;
	}


	/**
	 * Enable debug mode - log also printed to stdout.
	 * 
	 * @param printToStdout
	 */
	public static void setPrintToStdout(boolean printToStdout) {

		Log.printToStdout = printToStdout;
	}


	/**
	 * Log INFO message
	 * 
	 * @param msg message
	 */
	private static void info(String msg) {

		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.INFO, msg);
	}


	/**
	 * Log FINE (important) message
	 * 
	 * @param msg message
	 */
	private static void fine(String msg) {

		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.FINE, msg);
	}


	/**
	 * Log FINER (loss important) message
	 * 
	 * @param msg message
	 */
	private static void finer(String msg) {

		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.FINER, msg);
	}


	/**
	 * Log FINEST (least important) message
	 * 
	 * @param msg message
	 */
	private static void finest(String msg) {

		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.FINEST, msg);
	}


	/**
	 * Log WARNING message
	 * 
	 * @param msg message
	 */
	private static void warning(String msg) {

		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.WARNING, msg);
	}


	/**
	 * Log FINE message
	 * 
	 * @param msg message
	 */
	public static void f1(String msg) {

		fine(msg);
	}


	/**
	 * Log FINER message
	 * 
	 * @param msg message
	 */
	public static void f2(String msg) {

		finer(msg);
	}


	/**
	 * Log FINEST message
	 * 
	 * @param msg message
	 */
	public static void f3(String msg) {

		finest(msg);
	}


	/**
	 * Log INFO message
	 * 
	 * @param msg message
	 */
	public static void i(String msg) {

		info(msg);
	}


	/**
	 * Log WARNING message (less severe than ERROR)
	 * 
	 * @param msg message
	 */
	public static void w(String msg) {

		warning(msg);
	}


	/**
	 * Log SEVERE (critical warning) message
	 * 
	 * @param msg message
	 */
	private static void severe(String msg) {

		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.SEVERE, msg);
	}


	/**
	 * Log ERROR message
	 * 
	 * @param msg message
	 */
	public static void e(String msg) {

		severe(msg);
	}


	/**
	 * Log THROWING message
	 * 
	 * @param msg message
	 * @param thrown thrown exception
	 */
	public static void e(String msg, Throwable thrown) {

		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.SEVERE, msg + "\n" + getStackTrace(thrown));
	}


	/**
	 * Log THROWING message
	 * 
	 * @param msg message
	 * @param thrown thrown exception
	 */
	public static void eh(String msg, Throwable thrown) {

		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.SEVERE, msg + "\n" + getStackTraceHeader(thrown));
	}


	/**
	 * Log exception thrown
	 * 
	 * @param thrown thrown exception
	 */
	public static void e(Throwable thrown) {

		if (!loggingEnabled) {
			return;
		}
		logger.log(Level.SEVERE, getStackTrace(thrown));
	}


	/**
	 * Get stack trace from throwable
	 * 
	 * @param t
	 * @return trace
	 */
	private static String getStackTrace(Throwable t) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}


	/**
	 * Get stack trace header from throwable
	 * 
	 * @param t
	 * @return trace
	 */
	private static String getStackTraceHeader(Throwable t) {

		return t.getMessage();
//		StackTraceElement[] trace = t.getStackTrace();
//		return trace[0].toString();
	}


	/**
	 * PowerCraft Log file formatter.
	 * 
	 * @author MightyPork
	 * @copy (c) 2012
	 */
	private static class LogFormatter extends Formatter {

		/** Newline string constant */
		private static final String nl = System.getProperty("line.separator");
		private boolean sysout;


		public LogFormatter(boolean sysout) {

			this.sysout = sysout;
		}


		@Override
		public String format(LogRecord record) {

			StringBuffer buf = new StringBuffer(180);

			if (record.getMessage().equals("\n")) {
				return nl;
			}

			if (record.getMessage().charAt(0) == '\n') {
				buf.append(nl);
				record.setMessage(record.getMessage().substring(1));
			}

			Level level = record.getLevel();
			String trail = "[ ? ]";
			if (level == Level.FINE) {
				trail = "[ # ] ";
			}
			if (level == Level.FINER) {
				trail = "[ - ] ";
			}
			if (level == Level.FINEST) {
				trail = "[   ] ";
			}
			if (level == Level.INFO) {
				trail = "[ i ] ";
			}
			if (level == Level.SEVERE) {
				trail = "[!E!] ";
			}
			if (level == Level.WARNING) {
				trail = "[!W!] ";
			}

			record.setMessage(record.getMessage().replaceAll("\n", nl + trail));

			buf.append(trail);
			buf.append(formatMessage(record));

			buf.append(nl);

			Throwable throwable = record.getThrown();
			if (throwable != null) {

				buf.append("at ");
				buf.append(record.getSourceClassName());
				buf.append('.');
				buf.append(record.getSourceMethodName());
				buf.append(nl);

				StringWriter sink = new StringWriter();
				throwable.printStackTrace(new PrintWriter(sink, true));
				buf.append(sink.toString());

				buf.append(nl);
			}

			if (Log.printToStdout && sysout) {
				if (level == Level.FINE || level == Level.FINER || level == Level.FINEST || level == Level.INFO) {
					System.out.print(buf.toString());
				} else if (level == Level.SEVERE || level == Level.WARNING) {
					System.err.print(buf.toString());
				}
			}

			return buf.toString();
		}
	}


}
