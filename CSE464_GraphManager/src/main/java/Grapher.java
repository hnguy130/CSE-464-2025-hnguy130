import java.io.File;
import java.nio.file.*;
import java.io.IOException;

import java.util.ArrayList;

import guru.nidi.graphviz.model.*;
import guru.nidi.graphviz.parse.Parser;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine;

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
			System.out.println("Node " + from1 + " doesn't exist in graph, adding node " + from1);
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
	}
}