package BackendClasses;

import ASTTraverser.Value.*;

import java.util.*;

public class Graph {

    /**
     * Properties a graph can have. Can be checked with GetProperty().
     */
    public enum Property {
        NodeWeighted,
        EdgeWeighted,
        Directional
    }

    /**
     * set of properties the graph has,
     * should be updated automatically
     */
    private final Set<Property> _properties;

    /**
     * the list of edges in the graph
     */
    private final Set<Edge> _edges;

    /**
     * the list of nodes in the graph
     */
    private final Set<Node> _nodes;

    /**
     * Creates a new graph
     *
     * @param nodeAmount the amount of nodes in the graph
     */
    public Graph(int nodeAmount) {
        HashSet<Node> nodes = new HashSet<>();
        for (int i = 0; i < nodeAmount; i++) {
            Node node = new Node();
            nodes.add(node);
        }
        _nodes = nodes;
        _edges = new HashSet<>();
        _properties = new HashSet<>();
        _properties.add(Property.Directional); //since we currently only allow directional graphs
    }

    /**
     * Creates a new graph
     *
     * @param nodes the nodes in the graph
     */
    public Graph(Set<Node> nodes) {
        _nodes = nodes;
        _edges = new HashSet<>();
        _properties = new HashSet<>();
        _properties.add(Property.Directional); //since we currently only allow directional graphs
    }

    /**
     * create a descriptive string for the graph
     *
     * g: Graph
     *     id: 0, weight: 0.0, edges: ((0 -> 1, 0.0), ...)
     *     ...
     */
    public String toString() {
        StringBuilder builder = new StringBuilder("Graph\n");
        ArrayList<Node> sortedNodes = new ArrayList<>(_nodes);
        Collections.sort(sortedNodes);

        for (Node node : sortedNodes) {
            builder.append(node.toStringGraph());
            builder.append("\n");
        }

        return builder.toString();
    }

    /**
     * Checks if the graph has the given property
     *
     * @param property the property to check
     * @return true if the graph has the given property else false
     */
    public boolean getProperty(Property property) {
        return _properties.contains(property);
    }

    /**
     * add a node to the graph
     */
    public void addNode() {
        _nodes.add(new Node());
    }

    /**
     * add a new edge to the graph
     *
     * @param idFrom id of the node the edge comes from
     * @param idTo   id of the node the edge leads to
     * @throws IllegalArgumentException thrown if there is no node in the graph with either id
     */
    public void addEdge(int idFrom, int idTo) throws IllegalArgumentException {
        Node from = getNode(idFrom);
        Node to = getNode(idTo);
        Edge edge = new Edge(getNode(idFrom), getNode(idTo));
        from.addEdge(edge);
        to.addEdge(edge);
        _edges.add(edge);
    }

    /**
     * get the node object with the given id
     *
     * @param id id of the node
     * @return the node object with said id
     * @throws IllegalArgumentException if there is no node with the given id in the graph
     */
    public Node getNode(int id) throws IllegalArgumentException {
        for (Node node : _nodes) {
            if (node.getId() == id) {
                return node;
            }
        }
        throw new IllegalArgumentException(String.format("There is no node with id %d", id));
    }

    /**
     * get the edge object going from the node with id fromId to node with id toId
     *
     * @param fromId the id of the node the edge starts at
     * @param toId   the id of the node the edge ends at
     * @return the edge object
     * @throws IllegalArgumentException if either id is not present in the nodes or if there is no edge from fromId to toId
     */
    public Edge getEdge(int fromId, int toId) throws IllegalArgumentException {
        for (Edge edge : getNode(fromId).outgoing) {
            if (edge.getTo().getId() == toId) return edge;
        }
        throw new IllegalArgumentException(String.format("There is no Edge from %d to %d", fromId, toId));
    }

    /**
     * Set the weight of the edge from fromId to toId
     *
     * @param fromId the id of the node the edge starts at
     * @param toId   the id of the node the edge ends at
     * @param weight the new weight of the edge
     * @throws IllegalArgumentException if there is no edge between the given nodes or if either node is not present in the graph
     */
    public void setEdgeWeight(int fromId, int toId, int weight) throws IllegalArgumentException {
        getEdge(fromId, toId).weight = weight;
    }

    /**
     * swap the direction of the edge going from fromId to toId
     *
     * @param fromId the id of the node the edge starts at before the swap
     * @param toId   the id of the node the edge ends at before the swap
     * @throws IllegalArgumentException if there is no edge between the given nodes or if either node id is not present in the graph
     */
    public void swapDirection(int fromId, int toId) throws IllegalArgumentException {
        getEdge(fromId, toId).swapDirection();
    }

    /**
     * remove the edge between the given nodes
     *
     * @param fromId id of the node the edge starts at
     * @param toId   id of the node the edge ends at
     * @throws Exception if there is no node between the given nodes or if either node id is not present in the graph
     */
    public void removeEdge(int fromId, int toId) throws Exception {
        removeEdge(getEdge(fromId, toId));
    }

    /**
     * remove the given edge
     *
     * @param edge the edge to be removed
     * @throws IllegalArgumentException if the edge is not connected to either of the given node
     */
    public void removeEdge(Edge edge) throws IllegalArgumentException {
        edge.getTo().removeEdge(edge);
        edge.getFrom().removeEdge(edge);
        _edges.remove(edge);
    }

    /**
     * remove the node with the given id from the graph. also removes all edges from and to that node
     *
     * @param id the id of the node to be removed
     * @throws Exception if there is no node with the given id
     */
    public void removeNode(int id) throws Exception {
        removeNode(getNode(id));
    }

    /**
     * remove the given node from the graph. also removes all edges from and to that node
     *
     * @param node the node to be removed
     * @throws IllegalArgumentException if there is an invalid state in which a neighbour of node does not store the edge to node in its own sets
     */
    public void removeNode(Node node) throws IllegalArgumentException {
        for (Edge edge : node.ingoing) {
            edge.getFrom().removeEdge(edge);
            _edges.remove(edge);
        }
        for (Edge edge : node.outgoing) {
            edge.getTo().removeEdge(edge);
            _edges.remove(edge);
        }
        _nodes.remove(node);
    }

    /**
     * Get the value of a named attribute
     * nodes:       returns the nodes of the Graph
     * edges:       returns the edges of the Graph
     *
     * @param attributeName the name of the attribute. can be nodes or edges
     * @return the value of the given attribute
     * @throws IllegalArgumentException if the attributename is unknown
     */
    public Value getAttribute(String attributeName) {
        switch (attributeName) {
            case "nodes" -> {
                ArrayList<Node> sortedNodes = new ArrayList<>(_nodes);
                Collections.sort(sortedNodes);
                BackendList nodes = new BackendList();
                for (Node node : sortedNodes) {
                    nodes.add(new NodeValue(node));
                }
                return new ListValue(nodes);
            }
            case "edges" -> {
                ArrayList<Edge> sortedEdges = new ArrayList<>(_edges);
                Collections.sort(sortedEdges);
                BackendList edges = new BackendList();
                for (Edge edge : sortedEdges) {
                    edges.add(new EdgeValue(edge));
                }
                return new ListValue(edges);
            }
        }
        throw new IllegalArgumentException("Graph has no attribute by the name " + attributeName);
    }

    /**
     * set the value of a named attribute - no attributes to set jet
     *
     * @param attributeName the name of the attribute. can be nodes or edges
     * @throws IllegalArgumentException if the attributename is unknown
     */
    public void setAttribute(String attributeName, Value attributeValue) {
        throw new IllegalArgumentException("Attribute " + attributeName + " cannot be assigned to");
    }

    /**
     * runs a method of the Graph
     * addNode:             param: -                 -- void
     * addEdge:             param: Edge                 -- void
     * addEdges:            param: list of Edges        -- void
     * getNode:             param: id                   -- return: Node
     * getEdge:             param: idFrom, idTo         -- return: Edge
     * setEdgeWeight:       param: idFrom, idTo, weight -- void
     *                      param: Edge, weight         -- void
     * swapDirection:       param: idFrom, idTo         -- void
     *                      param: Edge                 -- void
     * removeEdge:          param: idFrom, idTo         -- void
     *                      param: Edge                 -- void
     * removeNode:          param: Node                 -- void
     *                      param: id                   -- void
     *
     * @param methodName the name of the method
     * @param parameters the arguments for the method
     * @return the output of the given method
     * @throws IllegalArgumentException if the given attribute is unknown
     */
    public Value runMethod(String methodName, List<Value> parameters) {
        switch (methodName) {
            case "addNode" -> {
                this.addNode();
                return null;
            }
            case "addEdge" -> {
                try {
                    if (parameters != null && parameters.size() == 2 && parameters.getFirst() instanceof NodeValue) {
                        int i = (((NodeValue) parameters.getFirst()).value).getId();
                        int j = (((NodeValue) parameters.getLast()).value).getId();
                        this.addEdge(i, j);
                        return null;
                    }else if(parameters != null && parameters.size() == 2 && parameters.getFirst() instanceof FloatValue) {
                        int i = (int) ((FloatValue) parameters.getFirst()).value;
                        int j = (int) ((FloatValue) parameters.getLast()).value;
                        this.addEdge(i, j);
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                throw new IllegalArgumentException("wrong arguments");
            }
            case "addEdges" -> {
                throw new RuntimeException("tuple values have not been implemented"); //TODO we dont have an expression for tuple values
            }
            case "getNode" -> {
                try {
                    if (parameters != null && parameters.size() == 1 && parameters.getFirst() instanceof FloatValue) {
                        int i = (int)((FloatValue) parameters.getFirst()).value;
                        return new NodeValue(this.getNode(i));
                    }
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("there is no node with id " + parameters.getFirst());
                }
                throw new IllegalArgumentException("wrong arguments");
            }
            case "getEdge" -> {
                try {
                    if (parameters != null && parameters.size() == 2 && parameters.getFirst() instanceof FloatValue) {
                        int i = (int)((FloatValue) parameters.getFirst()).value;
                        int j = (int)((FloatValue) parameters.getLast()).value;
                        Edge edge = this.getEdge(i, j);
                        return new EdgeValue(edge);
                    }
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("There is no node with id " + parameters.getFirst() + " or id " + parameters.getLast());
                }
                throw new IllegalArgumentException("wrong arguments");
            }
            case "setEdgeWeight" -> {
                try {
                    if (parameters != null && parameters.size() == 3 && parameters.getFirst() instanceof FloatValue) {
                        int i = (int)((FloatValue) parameters.getFirst()).value;
                        int j = (int)((FloatValue) parameters.get(1)).value;
                        int k = (int)((FloatValue) parameters.getLast()).value;
                        this.setEdgeWeight(i, j, k);
                        return null;
                    }else if (parameters != null && parameters.size() == 2 && parameters.getFirst() instanceof NodeValue && parameters.getLast() instanceof FloatValue) {
                        Edge e = ((EdgeValue) parameters.getFirst()).value;
                        int from = e.getFrom().getId();
                        int to = e.getTo().getId();
                        int i = (int)((FloatValue) parameters.getLast()).value;
                        this.setEdgeWeight(from, to, i);
                        return null;
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                throw new IllegalArgumentException("wrong arguments");
            }
            case "swapDirection" -> {
                try {
                    if (parameters != null && parameters.size() == 2 && parameters.getFirst() instanceof FloatValue) {
                        int i = (int)((FloatValue) parameters.getFirst()).value;
                        int j = (int)((FloatValue) parameters.getLast()).value;
                        this.swapDirection(i, j);
                        return null;
                    }else if(parameters != null && parameters.size() == 2 && parameters.getFirst() instanceof EdgeValue) {
                        Edge e = ((EdgeValue) parameters.getFirst()).value;
                        int from = e.getFrom().getId();
                        int to = e.getTo().getId();
                        this.swapDirection(from, to);
                        return null;
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                throw new IllegalArgumentException("wrong arguments");
            }
            case "removeEdge" -> {
                try {
                    if (parameters != null && parameters.size() == 2 && parameters.getFirst() instanceof FloatValue) {
                        int i = (int)((FloatValue) parameters.getFirst()).value;
                        int j = (int)((FloatValue) parameters.getLast()).value;
                        this.removeEdge(i, j);
                        return null;
                    } else if (parameters.size() == 1 && parameters.getFirst() instanceof EdgeValue) {
                        Edge edge = ((EdgeValue) parameters.getFirst()).value;
                        this.removeEdge(edge);
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                throw new IllegalArgumentException("wrong arguments");
            }
            case "removeNode" -> {
                try {
                    if (parameters != null && parameters.size() == 1 && parameters.getFirst() instanceof FloatValue) {
                        int i = (int)((FloatValue) parameters.getFirst()).value;
                        this.removeNode(i);
                        return null;
                    } else if (parameters != null && parameters.size() == 1 && parameters.getFirst() instanceof NodeValue) {
                        Node node = ((NodeValue) parameters.getFirst()).value;
                        this.removeNode(node);
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                throw new IllegalArgumentException("wrong arguments");
            }
            default -> throw new RuntimeException("Method ”" + methodName + "” has not been implemented");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graph graph = (Graph) o;
        return _properties.equals(graph._properties) && _edges.equals(graph._edges) && _nodes.equals(graph._nodes);
    }

    public static void main(String[] args) {
        Graph g = new Graph(4);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 0);
        System.out.println("g: " + g);
        g.addNode();
        System.out.println("g': " + g);

    }
}
