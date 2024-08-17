package BackendClasses;

import ASTTraverser.Value.FloatValue;
import ASTTraverser.Value.NodeValue;
import ASTTraverser.Value.Value;

import java.util.Objects;

public class Edge implements Comparable<Edge> {
    private Node _from;
    private Node _to;
    public double weight; // default value is 1.0

    /**
     * creates a new edge with the given weight
     * @param from starting node of the edge
     * @param to target node of the edge
     * @param weight weight of the edge
     */
    public Edge(Node from, Node to, double weight) {
        _from = from;
        _to = to;
        this.weight = weight;
    }

    /**
     * creates an edge with the default weight 1.0
     * @param from starting node of the edge
     * @param to target node of the edge
     */
    public Edge(Node from, Node to) {
        this(from, to, 1);
    }

    /**
     * swaps the from and to nodes of the edge
     */
    public void swapDirection() {
        _from.removeEdge(this);
        _to.removeEdge(this);

        Node temp = _from;
        _from = _to;
        _to = temp;

        _from.addEdge(this);
        _to.addEdge(this);
    }

    /**
     * example: "Edge(from: [Node], to: [Node], weight: 1.0)"
     * @return a string representation of the edge
     */
    public String toString() {
        return "Edge(from: " + _from + ", to: " + _to + ", weight: " + weight + ")";
    }

    // ------------------------------------------------
    // getters and setters ----------------------------

    /**
     * @return the starting node of the edge
     */
    public Node getFrom() {
        return _from;
    }

    /**
     * @return the target node of the edge
     */
    public Node getTo() {
        return _to;
    }

    /**
     * Get the value of a named attribute
     * from:        returns the Node _from
     * to:          returns the Node _to
     * weight:      returns the weight (float)
     *
     * @param attributeName the name of the attribute. can be from, to or weight
     * @return the value of the given attribute
     * @throws IllegalArgumentException if the name of the attribute is unknown
     */
    public Value getAttribute(String attributeName){
        return switch (attributeName) {
            case "from" -> new NodeValue(getFrom());
            case "to" -> new NodeValue(getTo());
            case ("weight") -> new FloatValue(((float) weight));
            default -> throw new IllegalArgumentException("Edge does not have any attribute called " + attributeName);
        };
    }

    /**runs a method of the Edge
     * swapDirection:   param: -  -- void
     *
     * @param methodName the name of the method
     * @return the output of the method
     */
    public Value runMethod(String methodName){
        switch (methodName) {
            case "swapDirection" -> {
                this.swapDirection();
                return null;
            }
            default -> throw new IllegalArgumentException("Edge does not have any method called " + methodName);
        }
    }

    /**
     * Set the value of a named attribute
     * weight   param: float value
     *
     * @param attributeName the name of the attribute. can only be weight at the moment
     * @param attributeValue the value of the attribute. must be fitting type to the given name
     * @throws IllegalArgumentException if te name of the given attribute is unknown or the attribut cannot be assigned to
     */
    public void setAttribute(String attributeName, Value attributeValue){
        if(attributeName.equals("weight")) weight = ((FloatValue) attributeValue).value;
        else throw new IllegalArgumentException("Attribute " + attributeName + " can not be assigned to");
    }

    /**
     * @param edge another edge object
     * @return boolean true if two edges are equal
     */
    public boolean equals(Edge edge){
        return _from.equals(edge.getFrom()) && _to.equals(edge.getTo()) && edge.weight == weight;
    }

    /** compares two edges
     * example: (0,1) < (1,1); (0,2) > (0,1); (0,1) == (0,1)
     * @param edge another edge object
     * @return 0 if two edges have the same order, -1 if an edge has a higher order, 1 if an edge's order is less
     */
    @Override
    public int compareTo(Edge edge){
        int fromID = edge._from.getId();
        int toID = edge._to.getId();

        if(fromID < _from.getId()){
            return 1;
        }else if(fromID > _from.getId()){
            return -1;
        }else{
            if(toID < _to.getId()){
                return 1;
            }else if(toID > _to.getId()){
                return -1;
            }
        }
        return 0;
    }

}
