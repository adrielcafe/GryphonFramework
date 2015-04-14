package br.ufpe.cin.aac3.gryphon;

import java.io.File;
import java.io.FileOutputStream;

import com.hp.hpl.jena.ontology.OntModel;

public class GryphonUtil {
	public static void saveModel(OntModel model, Gryphon.Format format, File file){
		try {
			FileOutputStream fos = new FileOutputStream(file);
			model.write(fos, format.toString());
		} catch (Exception e) { 
			logError(e.getMessage());
		}
	}
	
	public static void logInfo(String info){
		if(GryphonConfig.isLogEnabled()){
			System.out.println(info);
		}
	}
	
	public static void logError(String error){
		if(GryphonConfig.isLogEnabled()){
			System.err.println(error);
		}
	}
	
	public static boolean isWindows(){
		String os = System.getProperty("os.name").toLowerCase();
		return os.indexOf("win") >= 0 ? true : false;
	}
}