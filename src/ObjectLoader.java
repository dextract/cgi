import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;



class ObjectLoader {
    
    private final static String OBJ_VERTEX = "v";
    private final static String OBJ_VERTEX_TEXTURE = "vt";
    private final static String OBJ_FACE = "f";
    
    private ArrayList<float[]> v = new ArrayList<float[]>();
    private ArrayList<int[]> fv = new ArrayList<int[]>();

    public ObjectLoader() {
        // ...
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
            	float[] vertice = {	Float.parseFloat(tokens[1]),
            						Float.parseFloat(tokens[2]),
            						Float.parseFloat(tokens[3])	};
            	v.add(vertice);
                // parse vertex line
			} else if(tokens[0].equals(OBJ_VERTEX_TEXTURE)) {
				// parse texture coordinates
			}
			else if(tokens[0].equals(OBJ_FACE)) {
				ProcessfData(line);
			}

            lineCount++;
        }
        bufferedReader.close();

        System.err.println("Loaded " + lineCount + " lines");
	}
	
	private void ProcessfData(String fread) {
		polyCount++;
		String s[] = fread.split("\\s+");
		if (fread.contains("//")) { //pattern is present if obj has only v and vn in face data
		    for (int i = 1; i < s.length; i++) {
		        s[i] = s[i].replaceAll("//", "/0/"); //insert a zero for missing vt data
		    }
		}
		ProcessfIntData(s); //pass in face data
	}
	
	private void ProcessfIntData(String sdata[]) {
        int vdata[] = new int[sdata.length - 1];
        int vtdata[] = new int[sdata.length - 1];
        int vndata[] = new int[sdata.length - 1];
        for (int loop = 1; loop < sdata.length; loop++) {
            String s = sdata[loop];
            String[] temp = s.split("/");
            vdata[loop - 1] = Integer.valueOf(temp[0]);         //always add vertex indices
            if (temp.length > 1) {                              //we have v and vt data
                vtdata[loop - 1] = Integer.valueOf(temp[1]);    //add in vt indices
            } else {
                vtdata[loop - 1] = 0;                           //if no vt data is present fill in zeros
            }
            if (temp.length > 2) {                              //we have v, vt, and vn data
                vndata[loop - 1] = Integer.valueOf(temp[2]);    //add in vn indices
            } else {
                vndata[loop - 1] = 0;                           //if no vn data is present fill in zeros
            }
        }
        fv.add(vdata);
        //ft.add(vtdata);
        //fn.add(vndata);
    }
	
	
    // member variables here...
	 
	 private int polyCount = 0;

}



