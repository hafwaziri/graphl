package Test;

import BackendClasses.*;
import ASTTraverser.Value.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

// TODO diese Tests sind nicht auf dem neuesten Stand
public class GraphClassesTest {

    @BeforeEach
    void setUp() {
        Node.resetIDNum();
    }

    @Nested
    class GraphTests{

        @Test
        void testGetProperty(){
            Graph graph = new Graph(0);
            assertTrue(graph.getProperty(Graph.Property.Directional));
            assertFalse(graph.getProperty(Graph.Property.EdgeWeighted));
            assertFalse(graph.getProperty(Graph.Property.NodeWeighted));
        }

        @Test
        void testAddNode(){
            Graph graph = new Graph(0);
            graph.addNode();
            try{
                graph.getNode(0);
                assertTrue(true);

            }catch(IllegalArgumentException e){
                fail();
            }
        }

        @Test
        void testAddEdge(){
            Graph graph = new Graph(4);
            try {
                graph.addEdge(1, 3);
                assertTrue(true);
            }catch(IllegalArgumentException e){ fail(); }

            assertThrows(IllegalArgumentException.class, () -> graph.addEdge(1, 5));

            try{
                graph.getEdge(1,3);
                assertTrue(true);
            }catch(IllegalArgumentException e){ fail(); }
        }

        @Test
        void testGetNode(){
            Graph graph = new Graph(4);
            try {
                Node node = graph.getNode(3);
                assertNotNull(node);
                assertEquals("Node(id: 3, weight: 0.0)", node.toString());
            }catch(IllegalArgumentException e){ fail(); }

            assertThrows(IllegalArgumentException.class, () -> graph.getNode(5));
        }

        @Test
        void testGetEdge(){
            Graph graph = new Graph(7);
            graph.addEdge(1,5);
            graph.addEdge(1,6);
            graph.addEdge(4,2);

            try {
                Edge edge = graph.getEdge(1,5);
                assertNotNull(edge);
                assertEquals("Edge(from: Node(id: 1, weight: 0.0), to: Node(id: 5, weight: 0.0), weight: 1.0)", edge.toString());
                edge = graph.getEdge(1,6);
                assertEquals("Edge(from: Node(id: 1, weight: 0.0), to: Node(id: 6, weight: 0.0), weight: 1.0)", edge.toString());
                edge = graph.getEdge(4,2);
                assertEquals("Edge(from: Node(id: 4, weight: 0.0), to: Node(id: 2, weight: 0.0), weight: 1.0)", edge.toString());
            }catch (IllegalArgumentException e){ fail(); }

            assertThrows(IllegalArgumentException.class, () -> graph.getEdge(2,4));

        }

        @Test
        void testSetEdgeWeight(){
            Graph graph = new Graph(3);
            graph.addEdge(1,2);
            Edge edge = graph.getEdge(1,2);
            assertEquals(1.0, edge.weight);
            graph.setEdgeWeight(1,2,6);
            edge = graph.getEdge(1,2);
            assertEquals(6.0, edge.weight);
        }

        @Test
        void testSwapDirektion(){
            Graph graph = new Graph(3);
            graph.addEdge(1,2);
            Edge edge = graph.getEdge(1,2);
            assertEquals("Node(id: 1, weight: 0.0)", (edge.getFrom()).toString());
            assertEquals("Node(id: 2, weight: 0.0)", (edge.getTo()).toString());
            graph.swapDirection(1,2);
            assertThrows(IllegalArgumentException.class, () ->  graph.getEdge(1,2));
            try {
                edge = graph.getEdge(2, 1);
                assertTrue(true);
            }catch (IllegalArgumentException e){ fail(); }
            assertEquals("Node(id: 2, weight: 0.0)", (edge.getFrom()).toString());
            assertEquals("Node(id: 1, weight: 0.0)", (edge.getTo()).toString());
        }

        @Test
        void testRemoveEdge(){
            Graph graph = new Graph(5);
            assertThrows(IllegalArgumentException.class, () -> graph.removeEdge(0,4));
            graph.addEdge(0,4);
            try {
               graph.removeEdge(0,4);
               assertTrue(true);
            }catch(Exception e){ fail(); }
            graph.addEdge(0,4);
            Edge edge = graph.getEdge(0,4);
            try {
                graph.removeEdge(edge);
                assertTrue(true);
            }catch(Exception e){ fail(); }

        }

        @Test
        void testRemoveNode(){
            Graph graph = new Graph(2);
            try{
                graph.removeNode(1);
                assertTrue(true);
            }catch(Exception e){ fail(); }
            Node node = graph.getNode(0);
            try{
                graph.removeNode(node);
                assertTrue(true);
            }catch(IllegalArgumentException e){ fail(); }
            assertThrows(IllegalArgumentException.class, () -> graph.removeNode(0));

        }

        @Nested
        class testAttribute{

            @Test
            void testGetAttributeNodes(){
                Graph graph = new Graph(3);
                Value val = graph.getAttribute("nodes");
                assertInstanceOf(ListValue.class, val);
                List<Value> list = ((ListValue) val).value;
                assertEquals(3, list.size());
                List<Integer> idList = new ArrayList<>();
                for(Value v : list){
                    assertInstanceOf(NodeValue.class, v);
                    int id = (((NodeValue) v).value).getId();
                    assertTrue(id<3);
                    assertFalse(idList.contains(id));
                    idList.add(id);
                }
            }

            @Test
            void testGetAttribiuteEdges(){
                Graph graph = new Graph(3);
                graph.addEdge(1,2);
                graph.addEdge(0,2);
                graph.addEdge(2,1);
                graph.addEdge(1,0);

                Value val = graph.getAttribute("edges");
                assertInstanceOf(ListValue.class, val);
                List<Value> list = ((ListValue) val).value;
                assertEquals(4, list.size());

                for(Value v : list){
                    assertInstanceOf(EdgeValue.class, v);
                }
            }

            @Test
            void testGetAttributeIllegal(){
                Graph graph = new Graph(3);
                assertThrows(IllegalArgumentException.class, ()-> graph.getAttribute("node"));
            }

            @Test
            void testSetAttribute(){
                Graph graph = new Graph(3);
                assertThrows(IllegalArgumentException.class, ()-> graph.setAttribute("nodes", null));
                assertThrows(IllegalArgumentException.class, ()-> graph.setAttribute("edges", null));
            }
        }

        @Nested
        class testRunMethod {

            @Test
            void testRunAddNode() {
                Graph graph = new Graph(3);
                Value val = graph.runMethod("addNode", null);
                assertNull(val);
                try {
                    Node node = graph.getNode(3);
                    assertNotNull(node);
                } catch (IllegalArgumentException e) {
                    fail();
                }
            }

            @Test
            void testRunAddEdge() {
                Graph graph = new Graph(3);
                assertThrows(IllegalArgumentException.class, () -> graph.runMethod("addEdge", null));

                BackendList list = new BackendList();
                NodeValue node1 = new NodeValue(graph.getNode(0));
                NodeValue node2 = new NodeValue(graph.getNode(1));
                list.add(node1);
                list.add(node2);
                Value val = graph.runMethod("addEdge", list);
                assertNull(val);
                try {
                    Edge edge = graph.getEdge(0, 1);
                    assertTrue(true);
                } catch (IllegalArgumentException e) {
                    fail();
                }

                List<Value> list2 = new ArrayList<>();
                FloatValue float1 = new FloatValue(1);
                FloatValue float2 = new FloatValue(2);
                list2.add(float1);
                list2.add(float2);
                Value val2 = graph.runMethod("addEdge", list2);
                assertNull(val2);
                try {
                    Edge edge = graph.getEdge(1, 2);
                    assertTrue(true);
                } catch (IllegalArgumentException e) {
                    fail();
                }
            }

            @Test
            void testRunAddEdges() {
                Graph graph = new Graph(3);
                assertThrows(RuntimeException.class, () -> graph.runMethod("addEdges", null));
            }

            @Test
            void testRunGetNode() {
                Graph graph = new Graph(3);
                List<Value> list = new ArrayList<>();
                list.add(new FloatValue(1));
                NodeValue node = (NodeValue) graph.runMethod("getNode", list);
                assertEquals(node.value, graph.getNode(1));

                List<Value> list2 = new ArrayList<>();
                list2.add(new FloatValue(5));
                assertThrows(IllegalArgumentException.class, () -> graph.runMethod("getNode", list2));

                assertThrows(IllegalArgumentException.class, () -> graph.runMethod("getNode", null));

            }

            @Test
            void testRunGetEdge() {
                Graph graph = new Graph(3);
                graph.addEdge(1, 2);

                List<Value> list = new ArrayList<>();
                list.add(new FloatValue(1));
                list.add(new FloatValue(2));
                EdgeValue edge = (EdgeValue) graph.runMethod("getEdge", list);
                assertEquals(edge.value, graph.getEdge(1, 2));

                list.add(new FloatValue(3));
                assertThrows(IllegalArgumentException.class, () -> graph.runMethod("getEdge", list));

                List<Value> list2 = new ArrayList<>();
                list2.add(new FloatValue(5));
                list2.add(new FloatValue(2));
                assertThrows(IllegalArgumentException.class, () -> graph.runMethod("getEdge", list2));

            }

            @Test
            void testRunSetEdgeWeight() {

                Graph graph = new Graph(3);
                graph.addEdge(1, 2);

                List<Value> list = new ArrayList<>();
                list.add(new FloatValue(1));
                list.add(new FloatValue(2));
                list.add(new FloatValue(3));
                assertNull(graph.runMethod("setEdgeWeight", list));
                Edge edge = graph.getEdge(1, 2);
                assertEquals(3, edge.weight);

                list.removeLast();
                assertThrows(IllegalArgumentException.class, () -> graph.runMethod("setEdgeWeight", list));
            }

            @Test
            void testRunSwapDirection() {
                Graph graph = new Graph(3);
                graph.addEdge(1, 2);
                List<Value> list = new ArrayList<>();
                list.add(new FloatValue(1));
                list.add(new FloatValue(2));
                assertNull(graph.runMethod("swapDirection", list));
                assertThrows(IllegalArgumentException.class, () -> graph.runMethod("swapDirection", null));
            }

            @Test
            void testRunRemoveEdge() {
                Graph graph = new Graph(3);
                graph.addEdge(1, 2);

                List<Value> list = new ArrayList<>();
                list.add(new FloatValue(1));
                list.add(new FloatValue(2));
                assertNull(graph.runMethod("removeEdge", list));
                assertThrows(IllegalArgumentException.class, () -> graph.getEdge(1,2));

                graph.addEdge(0, 1);
                List<Value> list2 = new ArrayList<>();
                list2.add( new EdgeValue(graph.getEdge(0, 1)));
                assertNull(graph.runMethod("removeEdge", list2));
                assertThrows(IllegalArgumentException.class, () -> graph.getEdge(0,1));

            }

            @Test
            void testRunRemoveNode() {
                Graph graph = new Graph(3);

                List<Value> list = new ArrayList<>();
                list.add(new FloatValue(1));
                assertNull(graph.runMethod("removeNode", list));
                assertThrows(IllegalArgumentException.class, () -> graph.getNode(1));

                List<Value> list2 = new ArrayList<>();
                list2.add(new NodeValue(graph.getNode(2)));
                assertNull(graph.runMethod("removeNode", list2));
                assertThrows(IllegalArgumentException.class, () -> graph.getNode(2));

            }

            @Test
            void testRunIllegal() {
                Graph graph = new Graph(3);
                assertThrows(RuntimeException.class, () -> graph.runMethod("edge", null));
            }

        }

        @Test
        void testGraphEquals(){
            Graph graph = new Graph(3);
            Node.resetIDNum();
            Graph graph2 = new Graph(3);

            assertTrue(graph.equals(graph));
            assertTrue(graph.equals(graph2));
            graph.addEdge(1,2);
            assertFalse(graph.equals(graph2));
        }


    }

    @Nested
    class EdgeTests{

        @Test
        void testSwapDirections(){
            Node node1 = new Node();
            Node node2 = new Node();
            Edge edge = new Edge(node1, node2);

            edge.swapDirection();

            assertEquals(node2, edge.getFrom());
            assertEquals(node1, edge.getTo());
        }

        @Test
        void testToString(){
            Node node1 = new Node();
            Node node2 = new Node();
            Edge edge = new Edge(node1, node2);
            String string = String.format("Edge(from: %s, to: %s, weight: 1.0)", node1.toString(), node2.toString());

            assertEquals(string, edge.toString());
        }

        @Test
        void testGetFrom(){
            Node node1 = new Node();
            Node node2 = new Node();
            Edge edge = new Edge(node1, node2);

            assertEquals(node1, edge.getFrom());
        }

        @Test
        void testGetTo(){
            Node node1 = new Node();
            Node node2 = new Node();
            Edge edge = new Edge(node1, node2);

            assertEquals(node2, edge.getTo());
        }

        @Nested
        class testAttribute{

            @Test
            void testGetAttributeFrom(){
                Node node1 = new Node();
                Node node2 = new Node();
                Edge edge = new Edge(node1, node2);

                NodeValue val = (NodeValue) edge.getAttribute("from");
                assertEquals(node1, val.value);
            }

            @Test
            void testGetAttributeTo(){
                Node node1 = new Node();
                Node node2 = new Node();
                Edge edge = new Edge(node1, node2);

                NodeValue val = (NodeValue) edge.getAttribute("to");
                assertEquals(node2, val.value);
            }

            @Test
            void testGetAttributeWeight(){
                Node node1 = new Node();
                Node node2 = new Node();
                Edge edge = new Edge(node1, node2);

                FloatValue val = (FloatValue) edge.getAttribute("weight");
                assertEquals(1.0, val.value);

            }

        }

        @Nested
        class testRunMethod{

            @Test
            void testRunSwapDirection(){
                Node node1 = new Node();
                Node node2 = new Node();
                Edge edge = new Edge(node1, node2);
                edge.runMethod("swapDirection");
                assertEquals(node2, edge.getFrom());
                assertEquals(node1, edge.getTo());

            }

            @Test
            void testRunToString(){
                Node node1 = new Node();
                Node node2 = new Node();
                Edge edge = new Edge(node1, node2);

                PrintStream oldOut = System.out;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream newOut = new PrintStream(baos);
                System.setOut(newOut);

                edge.runMethod("toString");
                String expected = String.format("Edge(from: %s, to: %s, weight: 1.0)\n", node1, node2);

                System.setOut(oldOut);
                String captured = baos.toString();
                assertEquals(expected, captured);

            }
        }

        @Test
        void testSetAttribute(){
            Node node1 = new Node();
            Node node2 = new Node();
            Edge edge = new Edge(node1, node2);
            Value val = new FloatValue(5);
            edge.setAttribute("weight",val);
            assertEquals(5.0, edge.weight);

            Value val2 = new FloatValue(9);
            assertThrows(IllegalArgumentException.class,()-> edge.setAttribute("direction",val2));
        }

        @Test
        void testEquals(){
            Node node1 = new Node();
            Node node2 = new Node();
            Node node3 = new Node();
            Edge edge = new Edge(node1, node2);
            Edge edge2 = new Edge(node1, node2);
            Edge edge3 = new Edge(node1, node3);

            assertTrue(edge.equals(edge));
            assertTrue(edge.equals(edge2));
            assertFalse(edge.equals(edge3));

        }

    }

    @Nested
    class NodeTest{

        @Test
        void testGetID(){
            Node node = new Node();
            assertEquals(0, node.getId());
        }

        @Test
        void testGetNeighbours(){
            Node node = new Node();

            assertTrue(node.getNeighbours().isEmpty());

            Node node1 = new Node();
            Node node2 = new Node();
            Edge edge = new Edge(node, node1);
            Edge edge1 = new Edge(node2, node);

            node.addEdge(edge);
            node.addEdge(edge1);

            List<Edge> neighbours = node.getNeighbours();
            assertNotNull(neighbours);
            assertTrue(neighbours.contains(edge));
            assertTrue(neighbours.contains(edge1));
            assertEquals(2, neighbours.size());
        }

        @Test
        void testAddEdge(){
            Node node = new Node();
            Node node1 = new Node();
            Node node2 = new Node();

            Edge edge = new Edge(node, node1);
            Edge edge1 = new Edge(node1, node2);
            Edge edge2 = new Edge(node2, node);

            node.addEdge(edge);
            node.addEdge(edge2);

            assertThrows(IllegalArgumentException.class,()-> node.addEdge(edge1));

            List<Edge> neighbours = node.getNeighbours();
            assertNotNull(neighbours);
            assertTrue(neighbours.contains(edge));
            assertTrue(neighbours.contains(edge2));
            assertFalse(neighbours.contains(edge1));
        }

        @Test
        void testRemoveEdge(){
            Node node = new Node();
            Node node1 = new Node();
            Node node2 = new Node();

            Edge edge = new Edge(node, node1);
            Edge edge1 = new Edge(node, node2);
            Edge edge2 = new Edge(node2, node);
            Edge edge3 = new Edge(node1, node2);

            node.addEdge(edge);
            node.addEdge(edge1);
            node.addEdge(edge2);

            node.removeEdge(edge);
            node.removeEdge(edge2);

            assertTrue(node.getNeighbours().contains(edge1));
            assertFalse(node.getNeighbours().contains(edge2));
            assertFalse(node.getNeighbours().contains(edge));
            assertEquals(1, node.getNeighbours().size());

            assertThrows(IllegalArgumentException.class, ()-> node.removeEdge(edge3));

        }

        @Test
        void testFindIngoing(){
            Node node = new Node();
            Node node1 = new Node();
            Node node2 = new Node();

            Edge edge = new Edge(node1, node);
            Edge edge1 = new Edge(node2, node);

            node.addEdge(edge);
            node.addEdge(edge1);

            assertEquals(edge, node.findIngoingedge(node1));
        }

        @Test
        void testFindOutgoing(){
            Node node = new Node();
            Node node1 = new Node();
            Node node2 = new Node();

            Edge edge = new Edge(node, node1);
            Edge edge1 = new Edge(node, node2);

            node.addEdge(edge);
            node.addEdge(edge1);

            assertEquals(edge1, node.findOutgoingedge(node2));
        }

        @Test
        void testAddIngoing(){
            Node node = new Node();
            Node node1 = new Node();
            Node node2 = new Node();

            Edge edge = new Edge(node, node1);
            Edge edge1 = new Edge(node2, node);

            node.addIngoing(edge1);

            assertEquals(edge1, node.findIngoingedge(node2));
            assertThrows(IllegalArgumentException.class, () -> node.addIngoing(edge));
        }

        @Test
        void testAddOutgoing(){
            Node node = new Node();
            Node node1 = new Node();
            Node node2 = new Node();

            Edge edge = new Edge(node, node1);
            Edge edge1 = new Edge(node2, node);

            node.addOutgoing(edge);

            assertEquals(edge, node.findOutgoingedge(node1));
            assertThrows(IllegalArgumentException.class, () -> node.addOutgoing(edge1));
        }

        @Test
        void testToString(){
            Node node = new Node();
            String nodeString = "Node(id: 0, weight: 0.0)";
            assertEquals(nodeString, node.toString());
        }

        @Test
        void testToStringNeighbours(){
            Node node = new Node();
            Node node1 = new Node();
            Node node2 = new Node();

            Edge edge = new Edge(node, node1);
            Edge edge1 = new Edge(node2, node);

            node.addEdge(edge);
            node.addEdge(edge1);

            String nodeString = String.format("%s, \n%s, ", edge1, edge);

            assertEquals(nodeString, node.toStringNeighbours());
        }

        @Test
        void testGetAttribute(){
            Node node = new Node();
            Node node1 = new Node();
            Node node2 = new Node();

            Edge edge = new Edge(node, node1);
            Edge edge1 = new Edge(node2, node);

            node.addEdge(edge);
            node.addEdge(edge1);

            FloatValue Value = (FloatValue) node.getAttribute("id");
            assertEquals(node.getId(), (int) Value.value);

            Value = (FloatValue) node.getAttribute("weight");
            assertEquals(node.weight, (int) Value.value);

            List<Value> list = ((ListValue) node.getAttribute("neighbours")).value;
            Set<Node> neighbours = new HashSet<>();

            for(Value val : list){
                neighbours.add(((NodeValue) val).value);
            }
            Set<Node> getNeigbors = new HashSet<>();

            for(Edge e : node.getNeighbours()){
                if(e.getTo().equals(node)){
                    getNeigbors.add(e.getFrom());
                }else{
                    getNeigbors.add(e.getTo());
                }
            }

            assertEquals(getNeigbors, neighbours);


            list = ((ListValue) node.getAttribute("ingoing")).value;
            Set<Edge> edges = new HashSet<>();

            for(Value val : list){
                edges.add(((EdgeValue) val).value);
            }
            Set<Edge> getEdges = new HashSet<>();

            for(Edge e : node.getNeighbours()){
                if(e.getTo().equals(node)){
                    getEdges.add(e);
                }
            }

            assertEquals(getEdges, edges);

            list.clear();
            list = ((ListValue) node.getAttribute("outgoing")).value;
            edges.clear();

            for(Value val : list){
                edges.add(((EdgeValue) val).value);
            }
            getEdges.clear();

            for(Edge e : node.getNeighbours()){
                if(e.getFrom().equals(node)){
                    getEdges.add(e);
                }
            }

            assertEquals(getEdges, edges);

            assertThrows(IllegalArgumentException.class, () -> node.getAttribute("edges"));

        }

        @Nested
        class testRunMethod{

            @Test
            void testRunAddEdge(){
                Node node = new Node();
                Node node1 = new Node();

                Edge edge = new Edge(node, node1);
                Edge edge1 = new Edge(node1, node);
                List<Value> list = new ArrayList<>();
                list.add(new EdgeValue(edge));

                node.runMethod("addEdge", list);
                assertEquals(edge, node.findOutgoingedge(node1));

                list.add(new EdgeValue(edge1));
                assertThrows(IllegalArgumentException.class, () -> node.runMethod("addEdge", list));

                list.clear();
                list.add(new FloatValue(2));
                assertThrows(IllegalArgumentException.class, () -> node.runMethod("addEdge", list));
            }

            @Test
            void testRunRemoveEdge(){
                Node node = new Node();
                Node node1 = new Node();

                Edge edge = new Edge(node, node1);
                Edge edge1 = new Edge(node1, node);

                node.addEdge(edge);
                node.addEdge(edge1);

                List<Value> list = new ArrayList<>();
                list.add(new EdgeValue(edge));

                node.runMethod("removeEdge", list);
                assertThrows(IllegalArgumentException.class, () -> node.findOutgoingedge(node1));
                assertEquals(edge1, node.findIngoingedge(node1));

                list.add(new EdgeValue(edge1));
                assertThrows(IllegalArgumentException.class, () -> node.runMethod("removeEdge", list));

                list.clear();
                list.add(new FloatValue(2));
                assertThrows(IllegalArgumentException.class, () -> node.runMethod("removeEdge", list));
            }

            @Test
            void testRunFindIngoing(){
                Node node = new Node();
                Node node1 = new Node();

                Edge edge = new Edge(node, node1);
                Edge edge1 = new Edge(node1, node);

                node.addEdge(edge);
                node.addEdge(edge1);

                List<Value> list = new ArrayList<>();
                list.add(new NodeValue(node1));
                EdgeValue edgeV = (EdgeValue) node.runMethod("findIngoingedge", list);
                assertEquals(edge1,edgeV.value);

                list.add(new EdgeValue(edge1));
                assertThrows(IllegalArgumentException.class, () -> node.runMethod("findIngoingedge", list));

                list.clear();
                list.add(new FloatValue(2));
                assertThrows(IllegalArgumentException.class, () -> node.runMethod("findIngoingedge", list));
            }

            @Test
            void testRunFindOutgoing(){
                Node node = new Node();
                Node node1 = new Node();

                Edge edge = new Edge(node, node1);
                Edge edge1 = new Edge(node1, node);

                node.addEdge(edge);
                node.addEdge(edge1);

                List<Value> list = new ArrayList<>();
                list.add(new NodeValue(node1));
                EdgeValue edgeV = (EdgeValue) node.runMethod("findOutgoingedge", list);
                assertEquals(edge,edgeV.value);

                list.add(new EdgeValue(edge1));
                assertThrows(IllegalArgumentException.class, () -> node.runMethod("findOutgoingedge", list));

                list.clear();
                list.add(new FloatValue(2));
                assertThrows(IllegalArgumentException.class, () -> node.runMethod("findOutgoingedge", list));
            }

            @Test
            void testRunAddIngoing(){
                Node node = new Node();
                Node node1 = new Node();

                Edge edge = new Edge(node, node1);
                Edge edge1 = new Edge(node1, node);

                List<Value> list = new ArrayList<>();
                list.add(new EdgeValue(edge1));
                assertNull(node.runMethod("addIngoing", list));
                assertEquals(edge1, node.findIngoingedge(node1));

                list.add(new EdgeValue(edge));
                assertThrows(IllegalArgumentException.class, () -> node.runMethod("addIngoing", list));

                list.clear();
                list.add(new NodeValue(node1));
                assertThrows(IllegalArgumentException.class, () -> node.runMethod("addIngoing", list));
            }

            @Test
            void testRunAddOutgoing(){
                Node node = new Node();
                Node node1 = new Node();

                Edge edge = new Edge(node, node1);
                Edge edge1 = new Edge(node1, node);

                List<Value> list = new ArrayList<>();
                list.add(new EdgeValue(edge));
                assertNull(node.runMethod("addOutgoing", list));
                assertEquals(edge, node.findOutgoingedge(node1));

                list.add(new EdgeValue(edge1));
                assertThrows(IllegalArgumentException.class, () -> node.runMethod("addOutgoing", list));

                list.remove(new EdgeValue(edge));
                assertThrows(IllegalArgumentException.class, () -> node.runMethod("addOutgoing", list));

                list.clear();
                list.add(new NodeValue(node1));
                assertThrows(IllegalArgumentException.class, () -> node.runMethod("addOutgoing", list));
            }

            @Test
            void testRunToStringNeighbours(){
                Node node1 = new Node();
                Node node2 = new Node();
                Edge edge = new Edge(node1, node2);
                Edge edge1 = new Edge(node2, node1);
                node1.addEdge(edge);
                node1.addEdge(edge1);

                PrintStream oldOut = System.out;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream newOut = new PrintStream(baos);
                System.setOut(newOut);

                node1.runMethod("toStringNeighbours", null);
                String expected = String.format("%s, \n%s, \n", edge1, edge);

                System.setOut(oldOut);
                String captured = baos.toString();
                assertEquals(expected, captured);
            }


        }

        @Test
        void testSetAttribute(){
            Node node = new Node();
            FloatValue Value = new FloatValue(7);
            node.setAttribute("weight", Value);
            assertEquals((int) Value.value, node.weight);
            assertThrows(IllegalArgumentException.class, () -> node.setAttribute("ingoing", null));
        }

        @Test
        void testEquals(){
            Node node1 = new Node();
            Node.resetIDNum();
            Node node2 = new Node();
            Node node3 = new Node();

            assertTrue(node1.equals(node2));
            assertFalse(node1.equals(node3));
        }
    }

}
