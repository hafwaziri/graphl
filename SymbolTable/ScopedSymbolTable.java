package SymbolTable;

import ASTTraverser.Value.*;
import BackendClasses.Graph;
import BackendClasses.Node;
import BackendClasses.Edge;

import java.util.*;

import ASTTraverser.Value.Value;

public class ScopedSymbolTable {

    private final static boolean ShowDebugOutput = false;

    /**
     * The number of steps from the root table to this one
     */
    private int _scopeLevel;

    /**
     * The table above this one in the hierarchy
     */
    private ScopedSymbolTable _parent;

    /**
     * The map of symbols for which this is the closest scope
     */
    private final HashMap<String, Symbol> _symbols;

    /**
     * Create a new ScopedSymbolTable with default values
     */
    private ScopedSymbolTable(){
        _symbols = new HashMap<>();
        _parent = null;
        _scopeLevel = 0;
    }

    /**
     * Create a new ScopedSymbolTable
     * @param addBuiltins whether the builtin symbols should be added to this scope. should only be true if this is the root symboltable
     */
    public ScopedSymbolTable(boolean addBuiltins){
        this();
        if(addBuiltins) initBuiltIns();
    }

    /**
     * Create a new ScopedSymbolTable
     * @param parent the parent of the new Table. Scope Level is derived from parent
     */
    public ScopedSymbolTable(ScopedSymbolTable parent){
        this();
        _parent = parent;
        _scopeLevel = _parent.getScopeLevel()+1;
    }

    /**
     * Create a string describing this table
     * @return a string representation of the table
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("SymbolTable on level ");
        builder.append(getScopeLevel());
        builder.append("\nContents:\n");
        builder.append("-------------------------\n");
        for (Symbol s: _symbols.values()){

            builder.append(s);
            builder.append("\n");
        }
        builder.append("-------------------------");
        return builder.toString();
    }

    /**
     * insert a new symbol into the table
     * @param symbol the symbol to insert. the name will be derived from the symbol directly
     * @throws IllegalArgumentException if the name of the symbol is already defined in this scope or a parent scope
     */
    public void insert(Symbol symbol){
        if(hasSymbol(symbol.getName())) throw new IllegalArgumentException("There already is a symbol with that name");
        if(ShowDebugOutput) {
            System.out.print("[" + getScopeLevel() + "]");
            System.out.println("Insert: " + symbol.getName());
        }
        _symbols.put(symbol.getName(), symbol);

    }

    /**
     * get a symbol from the table
     * @param name the name of the symbol you are looking for
     * @return the symbol or null if there is no symbol by that name in this or parenting scopes
     */
    public Symbol lookUp(String name){
        if(ShowDebugOutput) {
            System.out.print("[" + getScopeLevel() + "]");
            System.out.println("Lookup: " + name);
        }
        if(_symbols.containsKey(name)) return _symbols.get(name);
        else if (_parent != null) return _parent.lookUp(name);
        else return null;

    }

    /**
     * checks if a symbol is present in the current scope
     * @param name the name of the symbol to be checked
     * @return true if the symbol is present in this or parenting scope
     */
    public boolean hasSymbol(String name){
        return _symbols.containsKey(name) || (_parent != null && _parent.hasSymbol(name));
    }

    /**
     * add the builtin types to the current scope. should only be called for level 0 scope
     * Nodes: creates many nodes
     * Graph, Edge, Node: Constructors
     * random: returns random float within the range [x,y]
     */
    private void initBuiltIns(){
        if(getScopeLevel()>0) System.out.println("WARNING you are adding builtin symbols to a not 0-level scope. are you sure this is right?");

        //types
        insert(new TypeSymbol("integer"));
        insert(new TypeSymbol("boolean"));
        insert(new TypeSymbol("node"));
        insert(new TypeSymbol("edge"));
        insert(new TypeSymbol("graph"));
        insert(new TypeSymbol("list"));

        //static functions
        insert(new FunctionSymbol("Nodes", values -> {
            int amount = (int)((FloatValue)values.getFirst()).value;
            List<Value> nodes = new ArrayList<>(amount);
            for (int i = 0; i < amount; i++) {
                nodes.add(new NodeValue(new Node()));
            }
            return new ListValue(nodes);
        }));

        insert(new FunctionSymbol("Graph", exprs -> {
            if(exprs.size() == 1){
                if(exprs.getFirst() instanceof FloatValue){
                    return new GraphValue(new Graph((int)((FloatValue) exprs.getFirst()).value));
                }else if(exprs.getFirst() instanceof ListValue){
                    Set<Node> nodes = new HashSet<>();
                    for (Value edge : ((ListValue) exprs.getFirst()).value){
                        nodes.add(((NodeValue) edge).value);
                    }
                    return new GraphValue(new Graph(nodes));
                }
            }
            throw new IllegalArgumentException("There is no constructor for Graph that takes " + exprs.size() + "arguments");
        }));

        insert(new FunctionSymbol("Edge", exprs -> {
            switch (exprs.size()) {
                case 2 -> {
                    if (exprs.getFirst() instanceof NodeValue && exprs.getLast() instanceof NodeValue) {
                        Edge edge = new Edge(((NodeValue) exprs.getFirst()).value, ((NodeValue) exprs.getLast()).value);
                        return new EdgeValue(edge);
                    }
                }
                case 3 -> {
                    if (exprs.get(0) instanceof NodeValue && exprs.get(1) instanceof NodeValue && exprs.get(2) instanceof FloatValue) {
                        Edge edge = new Edge(((NodeValue) exprs.get(0)).value, ((NodeValue) exprs.get(1)).value, ((FloatValue) exprs.get(2)).value);
                        return new EdgeValue(edge);
                    }
                }
            }
            throw new IllegalArgumentException("There is no constructor for edge, that takes " + exprs.size() + " arguments");
        }));

        insert(new FunctionSymbol("Node", exprs -> {
            switch (exprs.size()){
                case 0 ->{
                    return new NodeValue(new Node());
                }
                case 1 ->{
                    if(exprs.getFirst() instanceof FloatValue){
                        return new NodeValue(new Node(((FloatValue) exprs.getFirst()).value));
                    }
                }
            }
            throw new IllegalArgumentException("There is no constructor for node, that takes " + exprs.size() + "arguments");
        }));

        insert(new FunctionSymbol("print", values -> {
            System.out.println(values.getFirst());
            return null;
        }));

        insert(new FunctionSymbol("random", exprs ->{
            if(exprs.size() != 2){
                throw new IllegalArgumentException("you have to enter two numbers for the range");
            }
            float min  = ((FloatValue) exprs.getFirst()).value;
            float max = ((FloatValue) exprs.getLast()).value;
            if(min > max){
                float temp = min;
                min = max;
                max = temp;
            }
            return new FloatValue((float)Math.random()*(max - min)+min);
        }));
    }

    /**
     * @return the scope level of this table
     */
    public int getScopeLevel(){
        return _scopeLevel;
    }

// <editor-fold desc="For manual testing only">
    public static void main(String[] args) {
        ScopedSymbolTable rootTable = new ScopedSymbolTable(true);
        ScopedSymbolTable myTable = new ScopedSymbolTable(rootTable);
        myTable.lookUp("integer");
        myTable.lookUp("a");
        System.out.println(myTable);
        System.out.println(rootTable);
    }
// </editor-fold>
}
