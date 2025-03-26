import java.io.File;
import java.nio.file.*;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;

import guru.nidi.graphviz.model.*;
import guru.nidi.graphviz.parse.Parser;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

enum Algorithm {
	BFS, DFS
}

public class Grapher {

	public MutableGraph graph;
	ArrayList<MutableNode> nodeList = new ArrayList<>();
	ArrayList<Link> edgeList = new ArrayList<>();
	public Parser parser = new Parser();

	public MutableGraph parseGraph(String filepath) throws IOException {

		File txt = new File(filepath);

		if (!txt.exists()) {
			throw new IOException("Graph txt not found: " + filepath);
		}
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
		if (graph == null) {
			return;
		}

		prepare();
		Files.writeString(Paths.get(filepath), toString());
		System.out.println("Graph information written to: " + filepath + "\n");
	}

	public void addNode(String label) {

		if (graph == null) {
			System.out.println("No graph loaded into system\n");
			return;
		}

		for (MutableNode node : graph.nodes()) {
			if (node.name().value().equals(label)) {
				System.out.println("Duplicate node " + label + "\n");
				return;
			}
		}

		graph.add(Factory.mutNode(label));
		System.out.println("Node " + label + " has been added to graph\n");
		prepare();
		return;

	}

	public void addNodes(String[] label) {

		if (graph == null) {
			System.out.println("No graph loaded into system");
			return;
		}

		for (String node : label) {
			addNode(node);
		}
		prepare();
		return;
	}

	public void addEdge(String from1, String to1) {

		if (graph == null) {
			System.out.println("No graph loaded into system");
			return;
		}

		MutableNode from = null, to = null;

		for (MutableNode node : graph.nodes()) {
			if (node.name().value().equals(from1)) {
				from = node;
			}
			if (node.name().value().equals(to1)) {
				to = node;
			}
		}

		if (from == null) {
			from = Factory.mutNode(from1);
			graph.add(from);
			System.out.println("Node " + from1 + " as source node doesn't exist in graph, adding node " + from1);
		}
		if (to == null) {
			to = Factory.mutNode(to1);
			graph.add(to);
			System.out.println("Node " + to1 + " as destination node doesn't exist in graph, adding node " + to1);
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

		if (graph == null) {
			System.out.println("No graph loaded into system");
			return;
		}

		MutableNode node = null;

		for (MutableNode nodeIn : graph.nodes()) {
			if (nodeIn.name().value().equals(label)) {
				node = nodeIn;
				break;
			}
		}

		if (node == null) {
			throw new IllegalArgumentException("Node " + label + " doesn't exist in graph");
		}

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
				// System.out.println("looking at node " + nodeIn.toString());
				newGraph.add(nodeIn);
			}
		}

		graph = newGraph;

		prepare();
		System.out.println("Node " + label + " has been removed from graph\n");

	}

	public void removeNodes(String[] label) {
		if (graph == null) {
			System.out.println("No graph loaded into system");
			return;
		}

		for (String node : label) {
			removeNode(node);
		}
	}

	public void removeEdge(String from1, String to1) {
		if (graph == null) {
			System.out.println("No graph loaded into system");
			return;
		}

		MutableNode from = null, to = null;

		for (MutableNode node : graph.nodes()) {
			if (node.name().value().equals(from1)) {
				from = node;
			}
			if (node.name().value().equals(to1)) {
				to = node;
			}
		}

		if (from == null) {
			throw new IllegalArgumentException("Node " + from1 + " as source node doesn't exist in graph\n");
		}
		if (to == null) {
			throw new IllegalArgumentException("Node " + to1 + " as a node doesn't exist in graph\n");
		}
		if (from == null || to == null) {
			return;
		}

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

	public Path GraphSearch(String from, String to, Algorithm algo) {
		if (algo == Algorithm.BFS) {
			System.out.println(GraphSearchBFS(from, to).toString());
			return GraphSearchBFS(from, to);
		} else if (algo == Algorithm.DFS) {
			System.out.println(GraphSearchDFS(from, to).toString());
			return GraphSearchDFS(from, to);
		} else
			return null;
	}

	public Path GraphSearchBFS(String from, String to) {

		if (graph == null) {
			System.out.println("No graph loaded into system");
			return null;
		}

		MutableNode from1 = null, to1 = null;

		for (MutableNode node : graph.nodes()) {
			if (node.name().value().equals(from)) {
				from1 = node;
			}
			if (node.name().value().equals(to)) {
				to1 = node;
			}
		}

		if (from1 == null) {
			throw new IllegalArgumentException("Node " + from1 + " as source node doesn't exist in graph\n");
		}
		if (to1 == null) {
			throw new IllegalArgumentException("Node " + to1 + " as a node doesn't exist in graph\n");
		}
		if (from1 == null || to1 == null) {
			return null;
		}

		ArrayList<String> queue = new ArrayList<>();
		ArrayList<String> visited = new ArrayList<>();
		ArrayList<String> parents = new ArrayList<>();

		queue.add(from);
		visited.add(from);
		parents.add("none");

		while (!queue.isEmpty()) {
			String current = queue.remove(0);

			for (Link link : edgeList) {
				if (link.from().name().value().equals(current)) {
					String child = link.to().name().value();

					if (!visited.contains(child)) {
						queue.add(child);
						visited.add(child);
						parents.add(current);

						if (child.equals(to)) {
							ArrayList<String> path = new ArrayList<>();
							String node = child;

							while (true) {
								path.add(0, node);
								if (node.equals(from)) {
									break;
								} else {
									int index = visited.indexOf(node);
									node = parents.get(index);
								}
							}
							return new Path(path, "BFS");
						}
					}
				}
			}
		}

		return null;
	}

	public Path GraphSearchDFS(String from, String to) {

		if (graph == null) {
			System.out.println("No graph loaded into system");
			return null;
		}

		MutableNode from1 = null, to1 = null;

		for (MutableNode node : graph.nodes()) {
			if (node.name().value().equals(from)) {
				from1 = node;
			}
			if (node.name().value().equals(to)) {
				to1 = node;
			}
		}

		if (from1 == null) {
			throw new IllegalArgumentException("Node " + from1 + " as source node doesn't exist in graph\n");
		}
		if (to1 == null) {
			throw new IllegalArgumentException("Node " + to1 + " as a node doesn't exist in graph\n");
		}
		if (from1 == null || to1 == null) {
			return null;
		}

		ArrayList<String> stack = new ArrayList<>();
		ArrayList<String> visited = new ArrayList<>();
		HashMap<String, String> parent = new HashMap<>();

		stack.add(from);
		visited.add(from);
		parent.put(from, "none");

		while (!stack.isEmpty()) {

			String current = stack.getLast();

			for (Link link : edgeList) {
				for (Link edge : edgeList) {

					if (edge.from().name().value().equals(current)) {
						String next = edge.to().name().value();

						if (!visited.contains(next)) {
							stack.add(next);
							visited.add(next);
							parent.put(next, current);
							current = next;
						}

						if (current.equals(to)) {
							ArrayList<String> path = new ArrayList<>();
							String node = current;

							while (!node.equals("none")) {
								path.add(0, node);
								node = parent.get(node);
							}
							return new Path(path, "DFS");
						}
					}
				}
			}
			stack.removeLast();
		}
		return null;
	}

	public void outputDOTGraph(String path) throws IOException {
		if (graph == null) {
			System.out.println("No graph loaded into system");
			return;
		}

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
		if (graph == null) {
			System.out.println("No graph loaded into system");
			return;
		}

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
		String test = "test.txt";
		grapher.parseGraph(test);

		grapher.GraphSearch("a", "g", Algorithm.BFS);
		grapher.GraphSearch("a", "g", Algorithm.DFS);

		grapher.outputGraph("output5.txt");

	}
}