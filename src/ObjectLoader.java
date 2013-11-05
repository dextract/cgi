

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


class ObjectLoader {
    
    private final static String OBJ_VERTEX = "v";
    private final static String OBJ_VERTEX_TEXTURE = "vt";
    private final static String OBJ_FACE = "f";

    public ObjectLoader() {
    	
    }

	public void load(File objFile) throws IOException {
				
		FileReader fileReader = new FileReader(objFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		int lineCount = 0;
		
		String line = null;
		while(true) {
			line = bufferedReader.readLine();
			if(null == line) {
				break;
			}
			
			line = line.trim();
			
			if(line.length() == 0)
				continue;
			
			String tokens[] = line.split("[\t ]+");

            if(line.startsWith("#")) {
                continue;
            }
            else if(tokens[0].equals(OBJ_VERTEX)) {
                // parse vertex line
			} else if(tokens[0].equals(OBJ_VERTEX_TEXTURE)) {
				// parse texture coordinates
			}
			else if(tokens[0].equals(OBJ_FACE)) {
				// parse face information
			}

            lineCount++;
        }
        bufferedReader.close();

        System.err.println("Loaded " + lineCount + " lines");
	}

    // member variables here...

}



