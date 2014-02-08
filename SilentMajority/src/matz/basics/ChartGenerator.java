package matz.basics;

import java.io.File;
import java.io.IOException;

public interface ChartGenerator {
	
	void generateGraph(File outDir, String outFile) throws IOException;
}
