import java.util.*;

public class Path {

	 public ArrayList<String> nodes;
	 
	 public Path(ArrayList<String> nodes) {
	        this.nodes = nodes;
	    }
	 
	 public String toString() {
		 
		 String path = "";
		 
		 if(nodes == null || nodes.size() == 0) {
			 System.out.println("Path doesn't exist");
			 return null;
		 }
		 
		 for(int i = 0; i < nodes.size(); i++) {
			 path += nodes.get(i);
			 if(i != nodes.size()-1)  path += "->";
		 }
		 
		 return path;
	 }
	 
}