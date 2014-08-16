package br.ufpe.cin.aac3.gryphon;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class GryphonConfig {
	private static Path workingDirectory = Paths.get("alignments");
	private static double alignmentThreshold = 0.5;
	private static boolean logEnabled = true;

	public static Path getWorkingDirectory() {
		return workingDirectory;
	}
	
	public static void setWorkingDirectory(Path workingDirectory) {
		if(!workingDirectory.toFile().exists())
			workingDirectory.toFile().mkdirs();
		GryphonConfig.workingDirectory = workingDirectory;
	}

	public static double getAlignmentThreshold() {
		return alignmentThreshold;
	}
	
	public static void setAlignmentThreshold(double alignmentThreshold) {
		GryphonConfig.alignmentThreshold = alignmentThreshold;
	}
	
	public static boolean isLogEnabled() {
		return logEnabled;
	}
	
	public static void setLogEnabled(boolean logEnabled) {
		GryphonConfig.logEnabled = logEnabled;
	}
}