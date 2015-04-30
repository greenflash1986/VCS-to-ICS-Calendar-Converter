package dragomerlin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class fileconverter {
	public static void filetoUTF8(File aFile) throws IOException {
		String encodingType = null;
		StringBuilder contents = new StringBuilder();
		BufferedReader input = null;
		BufferedWriter output = null;
		encodingType = TestDetector.main(aFile.getAbsolutePath().toString());
		if (encodingType == null) {
			input = new BufferedReader(new InputStreamReader(new FileInputStream(aFile)));	
		}
		else {
			input = new BufferedReader(new InputStreamReader(new FileInputStream(aFile), encodingType));	
		}
		String line = null;
		boolean firsttime = true;
		while ((line = input.readLine()) != null) {
			if (!firsttime) {
				contents.append(System.getProperty("line.separator"));
				firsttime = false;
			}
			contents.append(line);
		}
		
		//System.out.println(contents.toString());
		input.close();
		aFile.delete();
		aFile.createNewFile();
		output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aFile), "UTF-8")); 
		output.write(contents.toString());
		output.flush();
		output.close();
	}
}
