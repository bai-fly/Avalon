package avalon.tool.pool;

import avalon.tool.database.DatabaseOperator;
import avalon.tool.database.MySQLDatabaseOperator;
import avalon.tool.database.SQLiteDatabaseOperator;
import avalon.util.servlet.AvalonServlet;
import avalon.util.servlet.CoolQServlet;
import avalon.util.servlet.MojoWebqqServlet;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import static avalon.tool.system.ConfigSystem.getInstance;

/**
 * Created by Eldath on 2017/2/3 0003.
 *
 * @author Eldath
 */
public class ConstantPool {
	private ConstantPool() {
	}

	public static class Database {
		private static final String datasource =
				getInstance().getJSONObject("datasource").getString("datasource").toLowerCase();
		public static final DatabaseOperator currentDatabaseOperator =
				"mysql".equals(datasource) ? MySQLDatabaseOperator.getInstance() : SQLiteDatabaseOperator.getInstance();
	}

	public static class Address {
		public static final String servlet = Basic.currentServlet.apiAddress();
		public static final String servletScriptFile = Basic.currentServlet.scriptFilePath();
		public static final String perlFileOfWechat = Basic.currentPath +
				File.separator + "bin" + File.separator + "Mojo-Weixin.pl";
		public static final String dataPath = Basic.currentPath + File.separator + "data";
	}

	public static class Version {
		public static final String avalon = "1.0.0";
		private static final String servlet = Basic.currentServlet.version();
		private static Version instance = null;

		public static Version instance() {
			if (instance == null) instance = new Version();
			return instance;
		}

		public String servlet() {
			return servlet;
		}
	}

	public static class Basic {
		public static final AvalonServlet currentServlet =
				getInstance().getJSONObject("servlet")
						.getString("servlet").trim().toLowerCase().equals("coolq") ?
						new CoolQServlet() :
						new MojoWebqqServlet();
		public static final boolean localOutput = (boolean) getInstance().get("local_output");
		public static final boolean debug = (boolean) getInstance().get("debug");
		public static final long startTime = System.currentTimeMillis();
		public static final int pid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
		public static final String currentPath;

		static {
			String path1;
			try {
				path1 = new File("").getCanonicalPath();
			} catch (IOException e) {
				System.err.println("Fatal error: " + e.toString());
				path1 = "";
				System.exit(-1);
			}
			currentPath = path1;
		}
	}

	public static class Setting {
		public static final boolean Block_Words_Punishment_Mode_Enabled =
				(boolean) getInstance().get("block_words_punishment_mode_enabled");

		public static final boolean AnswerMe_Enabled = getInstance().isCommandEnable("AnswerMe");
		public static final boolean Wolfram_Enabled = getInstance().isCommandEnable("Wolfram");
		public static final boolean Execute_Enable = getInstance().isCommandEnable("Execute");
	}
}