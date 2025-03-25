import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class GrapherTest {
	public Grapher grapher;

	@Before
	public void setup() {
		grapher = new Grapher();
	}

	@Test
	public void remove_node() throws IOException {
		System.out.println("\n-----------------");
		System.out.println("remove node test");
		System.out.println("-----------------\n");
		String test = "test.txt";
		String output = "output1.txt";
		grapher.parseGraph(test);
		
		grapher.removeNode("e");
		
		grapher.outputGraph(output);

		String result = Files.readString(Paths.get("output1.txt"));
		String expected = Files.readString(Paths.get("expected1.txt"));
		assertEquals(expected, result);
	}
	
	@Test
	public void remove_nodes() throws IOException {
		System.out.println("\n-----------------");
		System.out.println("remove nodes test");
		System.out.println("-----------------\n");
		String test = "test.txt";
		String output = "output2.txt";
		grapher.parseGraph(test);
		
		String[] labels = {"e","f"};
		grapher.removeNodes(labels);
		
		grapher.outputGraph(output);

		String result = Files.readString(Paths.get("output2.txt"));
		String expected = Files.readString(Paths.get("expected2.txt"));
		assertEquals(expected, result);
	}
	
	@Test
	public void remove_edge() throws IOException {
		System.out.println("\n-----------------");
		System.out.println("remove edge test");
		System.out.println("-----------------\n");
		String test = "test.txt";
		String output = "output3.txt";
		grapher.parseGraph(test);
		
		grapher.removeEdge("e", "f");
		
		grapher.outputGraph(output);

		String result = Files.readString(Paths.get("output3.txt"));
		String expected = Files.readString(Paths.get("expected3.txt"));
		assertEquals(expected, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void remove_wrong_node() throws IOException {
		System.out.println("\n-----------------");
		System.out.println("remove wrong node test");
		System.out.println("-----------------\n");
		String test = "test.txt";
		grapher.parseGraph(test);
		
		String[] labels = {"e","e"};
		grapher.removeNodes(labels);

	}
	
	@Test(expected = IllegalArgumentException.class)
	public void remove_wrong_edge() throws IOException {
		System.out.println("\n-----------------");
		System.out.println("remove wrong edge test");
		System.out.println("-----------------\n");
		String test = "test.txt";
		grapher.parseGraph(test);
		
		grapher.removeEdge("e", "e");
		

	}
	
}
	
