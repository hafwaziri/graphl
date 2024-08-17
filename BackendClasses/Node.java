package BackendClasses;

import ASTTraverser.Value.*;

import java.util.*;

public class Node implements Comparable<Node> {

    private static int idNum;
    private final int _id;
    public double weight;
    protected final Set<Edge> ingoing;
    protected final Set<Edge> outgoing;

    /**
     * creates new Node - begin of Constructors
     * @param weight weight of the node
     */
    public Node (double weight){
        this._id = idNum;
        idNum++;
        this.weight = weight;
        this.ingoing = new HashSet<>();
        this.outgoing = new HashSet<>();
    }

    public Node (){
        this(0.0);
    }
    /*end of constructors*/

    /**
     * returns _id
     * @return integer wich represents the id
     */
    public int getId() {
        return _id;
    }

    /**
     * returns all of the adjacent edges
     * @return Set of all adjacent edges
     */
    public List<Edge> getNeighbours(){
        List<Edge> n = new ArrayList<>(ingoing);
        Collections.sort(n);
        List<Edge> m = new ArrayList<>(outgoing);
        Collections.sort(m);
        n.addAll(m);
        return n;
    }

    /**
     * checks whether it is an ingoing or outgoing edge and adds edge to the correct list
     * @param edge ingoing or outging edge to or from the node
     * @throws IllegalArgumentException if edge is not adjacent to node
     */
    public void addEdge(Edge edge) throws IllegalArgumentException{
        if(edge.getFrom() == this){
            outgoing.add(edge);
        }else if(edge.getTo() == this){
            ingoing.add(edge);
        }else{
            throw new IllegalArgumentException(String.format("The edge %s has no connection to node %s\n", edge, this));
        }
    }

    /**
     * checks whether it is an ingoing or outgoing edge and deletes edge from the correct list
     * @param edge ingoing or outgoing edge to or from the node
     * @throws IllegalArgumentException if edge is not adjacent to node
     */
    public void removeEdge(Edge edge)throws IllegalArgumentException{
        if(edge.getFrom() == this){
            outgoing.remove(edge);
        }else if(edge.getTo() == this){
            ingoing.remove(edge);
        }else{
            throw new IllegalArgumentException(String.format("The edge %s has no connection to node %s\n", edge, this));
        }
    }

    /**
     * checks if this node has an ingoing edge to the given node
     * @param node node object of the graph
     * @return ingoing edge object
     * @throws IllegalArgumentException if edge is not adjacent to node
     */
    public Edge findIngoingedge(Node node)throws IllegalArgumentException{
        for (Edge edge : ingoing){
            if (edge.getFrom() == node){
                return edge;
            }
        }
        throw new IllegalArgumentException(String.format("The node %s has no ingoing edge from node %s\n", this, node ));
    }

    /**
     * checks if this node has an outgoing edge to the given node
     * @param node node object of the graph
     * @return outgoing edge object
     * @throws IllegalArgumentException if edge is not adjacent to node
     */
    public Edge findOutgoingedge(Node node)throws IllegalArgumentException{
        for (Edge edge : outgoing){
            if (edge.getTo() == node){
                return edge;
            }
        }
        throw new IllegalArgumentException(String.format("The node %s has no outgoing edge to node %s\n", this, node ));
    }

    /**
     * checks if it is an ingoing edge and adds the edge
     * @param e ingoing edge to the node
     * @throws IllegalArgumentException if edge is not ingoing
     */
    public void addIngoing(Edge e) throws IllegalArgumentException{
        if(e.getTo() == this){
            ingoing.add(e);
        }else{
            throw new IllegalArgumentException(String.format("The edge %s is no ingoing edge to node %s\n", e, this));
        }
    }

    /**
     * checks if it is an ingoing edge and adds the edge
     * @param e outgiong edge from the node
     * @throws IllegalArgumentException if edge is not outgoing
     */
    public void addOutgoing(Edge e) throws IllegalArgumentException{
        if(e.getFrom() == this){
            outgoing.add(e);
        }else{
            throw new IllegalArgumentException(String.format("The edge %s is no outgoing edge from node %s\n", e, this));
        }
    }

    /**
     * creates string of node: example - "Node(id: [id], weight: [weight])"
     * @return representation of the node
     */
    public String toString(){
        return "Node(id: " + _id + ", weight: " + weight +")";
    }

    /**
     * @return string of ingoing edges
     */
    public String toStringIngoing(){
        String result = "";
        for (Edge e : ingoing){
            result = result + e.toString()+ ", ";
        }
        return result;
    }

    /**
     * @return string of outgoing edges
     */
    public String toStringOutgoing(){
        String result = "";
        for (Edge e : outgoing){
            result = result + e.toString()+ ", ";
        }
        return result;
    }

    /**
     * @return string of all adjacent edges
     */
    public String toStringNeighbours(){
        String result = toStringIngoing();
        result = result + "\n";
        result = result + toStringOutgoing();
        return result;
    }

    /** creates a string of all edges to represent them in the Graph
     * example: id: [id], weight: [weight], edges: ([idFrom] -> [idTo]), [weight]), ...
     * @return string of all edges
     */
    public String toStringGraph(){
        String result = "\tid: " + _id + ", weight: " + weight + " edges:";
        List<Edge> edges = getNeighbours();
        for (Edge e : edges){
            int idFrom = (e.getFrom()).getId();
            int idTo = (e.getTo()).getId();
            result = result + " (("+ idFrom +" -> "+ idTo+"), "+ e.weight  + "),";
        }

        return result;

    }

    /**
     * Get the value of a named attribute
     * id:                  returns the id of the Node
     * weight:              returns the weight of the Node
     * neighbours:          returns all adjacent Nodes
     * ingoingEdges:        returns all ingoing Edges
     * outgoingEdges:       returns all outgoing Edges
     * ingoingNodes:        returns the Nodes from an ingiong Edge
     * outgoingNodes:       returns the Nodes from an outgoing Edge
     *
     * @param attributeName the name of the attribute. can be id, weight, neighbours, ingoing our outgoing
     * @return the value of the given attribute
     * @throws IllegalArgumentException if the given attribute is unknown
     */
    public Value getAttribute(String attributeName) {
        switch (attributeName) {
            case "id" -> {
                return new FloatValue(getId());
            }
            case "weight" -> {
                return new FloatValue((float) weight);
            }
            case "neighbours" -> {
                ArrayList<Edge> sortedNeighbours = new ArrayList<>(getNeighbours());
                Collections.sort(sortedNeighbours);
                BackendList neighbours = new BackendList();
                for (Edge edge : sortedNeighbours) {
                    if (edge.getFrom() == this) {
                        neighbours.add(new NodeValue(edge.getTo()));
                    } else {
                        neighbours.add(new NodeValue(edge.getFrom()));
                    }
                }
                return new ListValue(neighbours);
            }
            case "ingoingEdges" -> {
                ArrayList<Edge> sortedIngoing = new ArrayList<>(ingoing);
                Collections.sort(sortedIngoing);
                BackendList list = new BackendList(sortedIngoing.size());
                for (Edge edge : sortedIngoing) {
                    list.add(new EdgeValue(edge));
                }
                return new ListValue(list);
            }
            case "outgoingEdges" -> {
                ArrayList<Edge> sortedOutgoing = new ArrayList<>(outgoing);
                Collections.sort(sortedOutgoing);
                BackendList list = new BackendList(sortedOutgoing.size());
                for (Edge edge : sortedOutgoing) {
                    list.add(new EdgeValue(edge));
                }
                return new ListValue(list);
            }
            case "ingoingNodes" -> {
                ArrayList<Node> sorted = new ArrayList<>(ingoing.size());
                for (Edge edge : ingoing) {
                    sorted.add(edge.getFrom());
                }
                Collections.sort(sorted);
                BackendList list = new BackendList(sorted.size());
                for (Node node : sorted) {
                    list.add(new NodeValue(node));
                }
                return new ListValue(list);
            }
            case "outgoingNodes" -> {
                ArrayList<Node> sorted = new ArrayList<>(outgoing.size());
                for (Edge edge : outgoing) {
                    sorted.add(edge.getTo());
                }
                Collections.sort(sorted);
                BackendList list = new BackendList(sorted.size());
                for (Node node : sorted) {
                    list.add(new NodeValue(node));
                }
                return new ListValue(list);
            }
        }
        throw new IllegalArgumentException("Node has no attribute of name " + attributeName);
    }

    /**
     * runs a method of the Node
     * addEdge:             param: Edge             -- void
     * removeEdge:          param: Edge             -- void
     * findIngoingedge:     param: Node             -- return: Edge, ingoing
     * findOutgoingedge:    param: Node             -- return: Edge, outgoing
     * addIngoing:          param: Edge, ingoing    -- void
     * addOutgoing:         param: Edge, ingoing    -- void
     *
     * @param methodName the name of the method
     * @param arglist the arguments for the method
     * @return the output of the given method
     * @throws IllegalArgumentException if the given attribute is unknown
     */
    public Value runMethod(String methodName, List<Value> arglist){
        switch (methodName){
            case "addEdge" ->{
                if(arglist.size() == 1 && arglist.getFirst() instanceof EdgeValue){
                    this.addEdge(((EdgeValue) arglist.getFirst()).value);
                    return null;
                }
                throw new IllegalArgumentException("wrong arguments");
            }
            case "removeEdge" ->{
                if(arglist.size() == 1 && arglist.getFirst() instanceof EdgeValue){
                    this.removeEdge(((EdgeValue) arglist.getFirst()).value);
                    return null;
                }
                throw new IllegalArgumentException("wrong arguments");
            }
            case "findIngoingedge" ->{
                if(arglist.size() == 1 && arglist.getFirst() instanceof NodeValue){
                    Edge edge = this.findIngoingedge(((NodeValue) arglist.getFirst()).value);
                    return new EdgeValue(edge);
                }
                throw new IllegalArgumentException("wrong arguments");
            }
            case "findOutgoingedge" -> {
                if(arglist.size() == 1 && arglist.getFirst() instanceof NodeValue){
                    Edge edge = this.findOutgoingedge(((NodeValue) arglist.getFirst()).value);
                    return new EdgeValue(edge);
                }
                throw new IllegalArgumentException("wrong arguments");
            }
            case "addIngoing" -> {
                if(arglist.size() == 1 && arglist.getFirst() instanceof EdgeValue){
                    this.addIngoing(((EdgeValue) arglist.getFirst()).value);
                    return null;
                }
                throw new IllegalArgumentException("wrong arguments");
            }
            case "addOutgoing" -> {
                if(arglist.size() == 1 && arglist.getFirst() instanceof EdgeValue){
                    this.addOutgoing(((EdgeValue) arglist.getFirst()).value);
                    return null;
                }
                throw new IllegalArgumentException("wrong arguments");
            }
            default -> throw new IllegalArgumentException("Node has no method of name %s".formatted(methodName));
        }
    }

    /**
     * Set the value of a named attribute
     * weight:      param: float value
     *
     * @param attributeName the name of the attribute. Can only be weight at the moment
     * @param value the value the attribute should be set to. Must be of a fitting type to the given attribute
     * @throws IllegalArgumentException if the attributename is unknown or cant be assigned to
     */
    public void setAttribute(String attributeName, Value value){
        switch (attributeName){
            case "weight"-> {
                weight = ((FloatValue) value).value;
            }
            default -> throw new IllegalArgumentException("Attribute %s cannot be assigned to".formatted(attributeName));
        }
    }

    public static void resetIDNum(){
        idNum = 0;
    }

    /**
     * @param o another object
     * @return boolean true if two nodes are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return _id == node._id && Double.compare(weight, node.weight) == 0 && Objects.equals(ingoing, node.ingoing) && Objects.equals(outgoing, node.outgoing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, weight, ingoing, outgoing);
    }

    @Override
    public int compareTo(Node node){
        if(this == node) return 0;
        if(_id<node._id) return -1;
        return 1;
    }

    public static void main(String[] args) {
        Node node = new Node();
        System.out.println(node.getId());
        Node node1 = new Node();
        System.out.println(node1.getId());

        System.out.println(node);
        System.out.println(node1);

        Set<Node> set = new HashSet<>();
        set.add(node);
        set.add(node1);

        resetIDNum();
        Node node3 = new Node();
        System.out.println(set.contains(node3));
    }
}
