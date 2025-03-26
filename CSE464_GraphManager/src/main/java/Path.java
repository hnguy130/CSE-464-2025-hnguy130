import java.util.ArrayList;

public class Path {

	public ArrayList<String> nodes;
	public String searchType;

	public Path(ArrayList<String> nodes, String searchType) {
		this.nodes = nodes;
		this.searchType = searchType;
	}

	public int size() {
		return nodes.size();
	}

	@Override
	public String toString() {

		String path = searchType + ": ";

		if (nodes == null || nodes.size() == 0) {
			System.out.println("Path doesn't exist");
			return null;
		}

		for (int i = 0; i < nodes.size(); i++) {
			path += nodes.get(i);
			if (i != nodes.size() - 1)
				path += "->";
		}

		path += "\n";

		return path;
	}

}