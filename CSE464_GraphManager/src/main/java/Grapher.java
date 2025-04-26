import java.io.File;
import java.nio.file.*;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Random;

import guru.nidi.graphviz.model.*;
import guru.nidi.graphviz.parse.Parser;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

enum Algorithm {
	BFS, DFS, RANDOM
}

public class Grapher {

	public MutableGraph graph;
	ArrayList<MutableNode> nodeList = new ArrayList<>();
	ArrayList<Link> edgeList = new ArrayList<>();
	public Parser parser = new Parser();

	// refactor 1

	String ERROR_NO_GRAPH = "No graph loaded into system\n";
	String ERROR_NO_FILE = "Graph txt not found: ";

	// refactor 2

	public boolean graphExists() {

		if (graph == null) {
			System.out.println(ERROR_NO_GRAPH);
			return false;
		} else
			return true;
	}

	// refactor 3

	public File fileExists(String filepath) throws IOException {

		File txt = new File(filepath);

		if (!txt.exists()) {
			throw new IOException(ERROR_NO_FILE + filepath);
		}
		return txt;
	}

	// refactor 4

	public MutableNode nodeExists(String label) {

		for (MutableNode node : graph.nodes()) {
			if (node.name().value().equals(label))
				return node;
		}

		return null;
	}

	public MutableGraph parseGraph(String filepath) throws IOException {

		File txt = fileExists(filepath);

		graph = parser.read(txt);
		prepare();

		System.out.println("Graph file to be parsed with: " + filepath + "\n");

		return graph;

	}

	@Override
	public String toString() {
		if (graph == null) {
			return "No graph loaded into system";
		}

		String content = new String();

		content += "Graph data: \n\n";

		content += "Node amount: " + nodeList.size() + "\n";

		content += "Label of nodes: ";
		for (int i = 0; i < nodeList.size(); i++) {
			content += nodeList.get(i).name().value();
			if (!(i == nodeList.size() - 1)) {
				content += " , ";
			}
		}

		content += "\n\n";

		content += "Edge amount: " + edgeList.size() + "\n";

		content += "Label of edges: \n";
		for (int i = 0; i < edgeList.size(); i++) {
			content += edgeList.get(i).from().name().value() + " -> " + edgeList.get(i).to().name().value() + "\n";
		}

		return content;
	}

	public void outputGraph(String filepath) throws IOException {

		if (graphExists() == false)
			return;

		prepare();
		Files.writeString(Paths.get(filepath), toString());
		System.out.println("Graph information written to: " + filepath + "\n");
	}

	public void addNode(String label) {

		if (graphExists() == false)
			return;

		if (nodeExists(label) != null) {
			System.out.println("Duplicate node " + label + "\n");
			return;
		}

		graph.add(Factory.mutNode(label));
		System.out.println("Node " + label + " has been added to graph\n");
		prepare();
		return;

	}

	public void addNodes(String[] label) {

		if (graphExists() == false)
			return;

		for (String node : label) {
			addNode(node);
		}
		prepare();
		return;
	}

	public void addEdge(String from1, String to1) {

		if (graphExists() == false)
			return;

		MutableNode from = null, to = null;

		if (nodeExists(from1) != null)
			from = nodeExists(from1);
		else {
			from = Factory.mutNode(from1);
			graph.add(from);
			System.out.println("Node " + from1 + " as source node doesn't exist in graph, adding node " + from1);
		}

		if (nodeExists(to1) != null)
			to = nodeExists(to1);
		else {
			to = Factory.mutNode(to1);
			graph.add(to);
			System.out.println("Node " + to1 + " as destination node doesn't exist in graph, adding node " + from1);
		}

		for (Link edge : from.links()) {
			if (edge.to().name().value().equals(to1)) {
				System.out.println(from1 + " -> " + to1 + " already exists\n");
				return;
			}
		}

		from.addLink(to);
		System.out.println(from1 + " -> " + to1 + " has been added to graph\n");
		prepare();
	}

	public void removeNode(String label) {

		if (graphExists() == false)
			return;

		MutableNode node = null;

		if (nodeExists(label) != null) {
			node = nodeExists(label);
		} else
			throw new IllegalArgumentException("Node " + label + " doesn't exist in graph");

		ArrayList<MutableNode> nodes = new ArrayList<>();
		for (MutableNode nodeIn : graph.nodes()) {
			nodes.add(nodeIn);
		}

		for (MutableNode nodeIn : graph.nodes()) {

			ArrayList<Link> links = new ArrayList<>();

			for (Link link : nodeIn.links()) {
				if (link.to().name().value().equals(label) || link.from().name().value().equals(label)) {
					links.add(link);
				}
			}

			for (Link link : links) {
				nodeIn.links().remove(link);
			}
		}

		MutableGraph newGraph = Factory.mutGraph().setDirected(graph.isDirected()).setStrict(graph.isStrict())
				.setCluster(graph.isCluster());

		for (MutableNode nodeIn : nodes) {
			if (!nodeIn.equals(node)) {
				newGraph.add(nodeIn);
			}
		}

		graph = newGraph;

		prepare();
		System.out.println("Node " + label + " has been removed from graph\n");

	}

	public void removeNodes(String[] label) {

		if (graphExists() == false)
			return;

		for (String node : label) {
			removeNode(node);
		}
	}

	public void removeEdge(String from1, String to1) {

		if (graphExists() == false)
			return;

		MutableNode from = null, to = null;

		if (nodeExists(from1) != null)
			from = nodeExists(from1);
		else
			throw new IllegalArgumentException("Node " + from1 + " as source node doesn't exist in graph\n");

		if (nodeExists(to1) != null)
			to = nodeExists(to1);
		else
			throw new IllegalArgumentException("Node " + to1 + " as a node doesn't exist in graph\n");

		Link edge = null;
		for (Link link : from.links()) {
			if (link.to().name().value().equals(to1)) {
				edge = link;
				break;
			}
		}
		if (edge == null) {
			throw new IllegalArgumentException("Node " + to1 + " as a destination node doesn't exist in graph\n");
		}
		from.links().remove(edge);
		System.out.println("Edge from " + from1 + " to " + to1 + " has been removed from graph\n");
		prepare();
	}
	
	// template class for pattern template
	public abstract class templateSearch {

		public Path search(String from, String to) {
			if (graphExists() == false)
				return null;

			if (nodeExists(from) == null)
				throw new IllegalArgumentException("Node " + from + " as source node doesn't exist in graph\n");

			if (nodeExists(to) == null)
				throw new IllegalArgumentException("Node " + to + " as a node doesn't exist in graph\n");

			Path path = searchHelper(from, to);
			return path;
		}

		public abstract Path searchHelper(String from, String to);
	}

	public ArrayList<String> buildPath(String start, String end, HashMap<String, String> parent) {
		ArrayList<String> path = new ArrayList<>();
		String node = end;

		while (!node.equals("none")) {
			path.add(0, node);
			node = parent.get(node);
		}

		return path;
	}
	
	public class searchBFS extends templateSearch {

		@Override
		public Path searchHelper(String from, String to) {

			ArrayList<String> queue = new ArrayList<>();
			ArrayList<String> visited = new ArrayList<>();
			HashMap<String, String> parent = new HashMap<>();
			
			ArrayList<String> currentPath = new ArrayList<>();
			
			queue.add(from);
			visited.add(from);
			parent.put(from, "none");
			
			currentPath = buildPath(from, from, parent);
			System.out.print(" ");
			printPath(currentPath);
			System.out.println();

			while (!queue.isEmpty()) {
				String current = queue.remove(0);

				for (Link link : edgeList) {
					if (link.from().name().value().equals(current)) {
						String child = link.to().name().value();

						if (!visited.contains(child)) {
							queue.add(child);
							visited.add(child);
							parent.put(child, current);
							
							currentPath = buildPath(from, child, parent);
							System.out.print(" ");
							printPath(currentPath);
							System.out.println();

							if (child.equals(to)) {
								
								ArrayList<String> path = buildPath(from, child, parent);

								return new Path(path, "BFS");
							}
						}
					}
				}
			}

			return null;
		}
	}

	public class searchDFS extends templateSearch {

		@Override
		public Path searchHelper(String from, String to) {
			
			ArrayList<String> stack = new ArrayList<>();
			ArrayList<String> visited = new ArrayList<>();
			HashMap<String, String> parent = new HashMap<>();
			
			ArrayList<String> currentPath = new ArrayList<>();

			stack.add(from);
			visited.add(from);
			parent.put(from, "none");
			
			currentPath = buildPath(from, from, parent);
			System.out.print(" ");
			printPath(currentPath);
			System.out.println();

			while (!stack.isEmpty()) {
				
				String current = stack.get(stack.size() - 1);

				for (Link link : edgeList) {
					
					for (Link edge : edgeList) {
						
						if (edge.from().name().value().equals(current)) {
							String next = edge.to().name().value();

							if (!visited.contains(next)) {
								stack.add(next);
								visited.add(next);
								parent.put(next, current);
								current = next;
								
								currentPath = buildPath(from, current, parent);
								System.out.print(" ");
								printPath(currentPath);
								System.out.println();
							}

							if (current.equals(to)) {

								ArrayList<String> path = buildPath(from, current, parent);

								return new Path(path, "DFS");
							}
						}
					}
				}
				stack.remove(stack.size() - 1);
			}
			return null;
		}
	}
	
	public void printPath(ArrayList<String> path) {
		
		System.out.print("visiting Path size " + path.size() + ": ");
		
		for(int i = 0; i <= path.size() - 1; i++) {
			
			String current = path.get(i);
			
			if(i == path.size() - 1)
				System.out.print(current);
			else
				System.out.print(current + " -> ");
		}
	}
	
	public class searchRANDOM extends templateSearch {
		
		Random random = new Random();
		
		@Override
		public Path searchHelper(String from, String to) {
			
			// try finding path for at most 100 searches, then return null
			
			ArrayList<String> path;
			ArrayList<String> visited;
			ArrayList<String> adjacent;
			
			for(int i = 0; i < 100; i++) {
				
				path = new ArrayList<>();
				path.add(from);
				visited = new ArrayList<>();
				visited.add(from);
				adjacent = new ArrayList<>();
				
				//current node at start
				String current = from;
				
				System.out.print(" - Search " + (i+1) + " - ");
				
				//check if there are adjacent nodes
				// if none, return null path
				//if yes, continue searching
				for(Link edge : edgeList) {
					if(edge.from().name().value().equals(current))
						adjacent.add(edge.to().name().value());
				}
				
				if(adjacent.isEmpty())
					return null;
				
				boolean dead_end = false;
				while (dead_end == false) {

				//selecting random adjacent node
				boolean all_visited = true;
				for(String node : adjacent)
					if(visited.contains(node) == false) {
						all_visited = false;
						break;
					}
				if(all_visited == true) {
					printPath(path);
					System.out.println(" - Dead End \n");
					break;
				}
				
				current = adjacent.get(random.nextInt(adjacent.size()));
				while(visited.contains(current)) {
					current = adjacent.get(random.nextInt(adjacent.size()));
				}
				
				path.add(current);
				visited.add(current);
				
				if(current.equals(to)) {
					printPath(path);
					System.out.println(" - Found Path ");
					return new Path(path, "RANDOM");
				}
				
				adjacent.clear();
				//gathering adjacent nodes
					for(Link edge : edgeList) {
						if(edge.from().name().value().equals(current))
							adjacent.add(edge.to().name().value());
					}
				
				if(adjacent.isEmpty()) {
					printPath(path);
					System.out.println(" - Dead End \n");
					dead_end = true;
				}		
			}
			
		}
			
			return null;
	}
		
	}
	
	public interface searchStrategy {
		public Path search(String from, String to);
	}
	
	public class strategyBFS implements searchStrategy{
		public searchBFS bfs = new searchBFS();
		
		public Path search(String from, String to) {
			return bfs.search(from, to);
		}
	}
	
	public class strategyDFS implements searchStrategy{
		public searchDFS dfs = new searchDFS();
		
		public Path search(String from, String to) {
			return dfs.search(from, to);
		}
	}
	
	public class strategyRANDOM implements searchStrategy{
		public searchRANDOM random = new searchRANDOM();
		
		public Path search(String from, String to) {
			return random.search(from, to);
		}
	}
	
	public class Context {

		public searchStrategy searchType;
		
		
		public Context(Algorithm algo) {
			if(algo == Algorithm.BFS) {
				searchType = new strategyBFS();
			}
			else if (algo == Algorithm.DFS) {
				searchType = new strategyDFS();
			}
			else if (algo == Algorithm.RANDOM) {
				searchType = new strategyRANDOM();
			}
			else
				searchType = null;
		}

		public Path search(String from, String to) {
			if(searchType != null)
				return searchType.search(from, to);
			else
				return null;
		}
	}

	public Path GraphSearch(String from, String to, Algorithm algo){
		
		
		Context searcher = new Context(algo);
		
		System.out.println("--------------------------------------------------------------\n");
		
		if(algo == Algorithm.BFS) 
			System.out.println(" Breath first search: \n");
		else if (algo == Algorithm.DFS) 
			System.out.println(" Depth first search: \n");
		else if (algo == Algorithm.RANDOM) 
			System.out.println(" Random walk search: \n");


		Path path = searcher.search(from, to);
		System.out.println(path.toString());

		System.out.println("--------------------------------------------------------------");
		
		return path;

	}

	public void outputDOTGraph(String path) throws IOException {

		if (graphExists() == false)
			return;

		// String content = graph.toString();
		ArrayList<String> mentionedNodes = new ArrayList<>();
		String content = "";

		content += "digraph {\n";

		for (MutableNode node : nodeList) {

			for (Link edge : node.links()) {
				content += "  " + edge.from().name().value() + " -> " + edge.to().name().value() + ";\n";
				if (!mentionedNodes.contains(edge.from().name().value())) {
					mentionedNodes.add(edge.from().name().value());
				}
				if (!mentionedNodes.contains(edge.to().name().value())) {
					mentionedNodes.add(edge.to().name().value());
				}
			}

			if (!mentionedNodes.contains(node.name().value())) {
				content += "  " + node.name().value() + ";\n";
			}
		}

		content += "}";

		Files.writeString(Paths.get(path), content);
		System.out.println("Graph in DOT format written to " + path);
	}

	public void outputGraphics(String path) throws IOException {

		if (graphExists() == false)
			return;

		Graphviz.fromGraph(graph).render(Format.PNG).toFile(new File(path));
		System.out.println("PNG generated at : " + path);

	}

	// runs afer adding node, edge, parsing dot file
	public void prepare() {
		nodeList = new ArrayList<>();
		edgeList = new ArrayList<>();

		for (MutableNode node : graph.nodes()) {
			nodeList.add(node);
			for (Link link : node.links()) {
				edgeList.add(link);
			}
		}

		nodeList.sort((a, b) -> a.name().value().compareTo(b.name().value()));
		edgeList.sort((a, b) -> a.from().name().value().compareTo(b.from().name().value()));
	}

	public static void main(String[] args) throws IOException {

		Grapher grapher = new Grapher();

		grapher.parseGraph("test.txt");
		grapher.outputGraph("graphData.txt");

		// grapher.removeNode("b");

		// String[] nodes = {"a","b"};
		// grapher.removeNodes(nodes);

		// grapher.removeEdge("a","e");
		
		String start = "a";
		String end = "i";
		grapher.GraphSearch(start, end, Algorithm.RANDOM);
		grapher.GraphSearch(start, end, Algorithm.BFS);
		grapher.GraphSearch(start, end, Algorithm.DFS);
	}
}