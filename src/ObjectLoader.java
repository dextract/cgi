import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


class ObjectLoader {
    
    private final static String OBJ_VERTEX = "v";
    private final static String OBJ_VERTEX_TEXTURE = "vt";
    private final static String OBJ_FACE = "f";
    
    //private ArrayList<float[]> v = new ArrayList<float[]>();
    private HashMap<Integer, float[]> v = new HashMap<Integer, float[]>();
    private ArrayList<float[]> vt = new ArrayList<float[]>();
    private ArrayList<ArrayList<float[]>> f = new ArrayList<ArrayList<float[]>>();
    
    private float minx, miny, minz;
    private float maxx, maxy, maxz;
    
    private int polyCount = 0;
    private int vCount = 0;
	 
	private boolean noTexture = false;

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
            	
            	float x = Float.parseFloat(tokens[1]);
            	float y = Float.parseFloat(tokens[2]);
            	float z = Float.parseFloat(tokens[3]);
            	
            	if(v.size()==0) {
	            	minx=maxx=x;
	            	miny=maxy=y;
	            	minz=maxz=z;
            	}
            	
            	if(x<minx)
            		minx = x;
            	if(x>maxx)
            		maxx = x;
            	
            	if(y<miny)
            		miny = y;
            	if(y>maxy)
            		maxy = y;
            	
            	if(z<minz)
            		minz = z;
            	if(z>maxz)
            		maxz = z;
            	
            	float[] vline = {x, y, z};
            	//v.add(vline);
            	v.put(vCount, vline);
            	
            	vCount++;
            	
			} else if(tokens[0].equals(OBJ_VERTEX_TEXTURE)) {
				float[] vtline = {	Float.parseFloat(tokens[1]),
									Float.parseFloat(tokens[2])	};
				vt.add(vtline);
			}
			else if(tokens[0].equals(OBJ_FACE)) {
				processf(line);
			}

            lineCount++;

        }
        bufferedReader.close();

        System.err.println("Loaded " + lineCount + " lines");
        //printStruct();
	}
	
	private void processf(String fread) {
		polyCount++;
		String s[] = fread.split("\\s+");
		ArrayList<float[]> faces = new ArrayList<float[]>();
		String[] s1;
		for(int i=1;i<s.length;i++) {
			if(s[i].contains("//")) {
				s1 = s[i].split("//");
				noTexture = true;
			}
			else
				s1 = s[i].split("/");
			if(s1.length>1) {
				float[] fline = {	Float.parseFloat(s1[0]),
								Float.parseFloat(s1[1])
									};
				faces.add(fline);
				noTexture = false;
			}
			else {
				float[] fline = { Float.parseFloat(s[i]), 0 };
				faces.add(fline);
				noTexture = true;
			}
		}
		f.add(faces);
	}
	
	
	@SuppressWarnings("unused")
	private void printStruct() {
		Iterator<float[]> it = v.values().iterator();
		while(it.hasNext()) {
			float[] v = it.next();
			System.out.println("x: "+v[0]+" y: "+v[1]+" z: "+v[2]);
		}
		it = vt.iterator();
		while(it.hasNext()) {
			float[] v = it.next();
			System.out.println("u: "+v[0]+" v: "+v[1]);
		}
		Iterator<ArrayList<float[]>> it1 = f.iterator();
		ArrayList<float[]> ff;
		while(it1.hasNext()) {
			System.out.print("f ");
			ff = it1.next();
			it = ff.iterator();
			while(it.hasNext()) {
				float[] p = it.next();
				System.out.print(p[0]+"/"+p[1]+" ");
				if(!it.hasNext())
					System.out.println();
			}
		}
		System.out.println(polyCount);
		System.out.println(v.size());
	}
	
	public ArrayList<ArrayList<float[]>> getFaces() {
		return f;
	}
	
	public HashMap<Integer, float[]> getVertices() {
		return v;
	}
	
	public ArrayList<float[]> getTexturesVt() {
		return vt;
	}
	
	public boolean textureApplicable() {
		return !noTexture;
	}
	
	public float[] getMinVertices() {
		float[] mins = new float[3];
		mins[0] = minx;
		mins[1] = miny;
		mins[2] = minz;
		return mins;
	}

	public float[] getMaxVertices() {
		float[] maxes = new float[3];
		maxes[0] = maxx;
		maxes[1] = maxy;
		maxes[2] = maxz;
		return maxes;
	}
	 

}