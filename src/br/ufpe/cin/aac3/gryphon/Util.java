package br.ufpe.cin.aac3.gryphon;

import org.apache.log4j.Logger;

public class Util {
	public static void logInfo(Logger logger, String info){
		if(GryphonConfig.isLogEnabled())
			logger.info(info);
	}
	
	public static void logError(Logger logger, String error){
		if(GryphonConfig.isLogEnabled())
			logger.error(error);
	}
	
	public static void logWarn(Logger logger, String warn){
		if(GryphonConfig.isLogEnabled())
			logger.warn(warn);
	}
	
	public static boolean isWindows(){
		String os = System.getProperty("os.name").toLowerCase();
		return os.indexOf("win") >= 0 ? true : false;
	}
}