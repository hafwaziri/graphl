package BackendClasses;

import ASTTraverser.Value.BoolValue;
import ASTTraverser.Value.FloatValue;
import ASTTraverser.Value.ListValue;
import ASTTraverser.Value.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BackendList extends ArrayList<Value> {
    /**
     * runs method with given name
     * @param name - the name of the method. can be any of: append, remove, appendAll, copy, insert, removeAt
     * @param params - the parameters that should be passed into the function
     * @return whatever the equivalent ArrayList method returns
     * @throws IllegalArgumentException - if the name is not one of the above
     */
    public Value runMethod(String name, List<Value> params) throws IllegalArgumentException {

        switch (name) {
            case "append":
                this.add(params.getFirst());
                return null;
            case "remove":
                this.remove(params.getFirst());
                return null;
            case "appendAll":
                this.addAll(((ListValue) params.getFirst()).value);
                return null;
            case "copy":
                return new ListValue(new BackendList(this));
            case "insert":
                this.add((int) ((FloatValue) params.getFirst()).value, params.get(1));
                return null;
            case "removeAt":
                this.remove((int) ((FloatValue) params.getFirst()).value);
                return null;
            default:
                throw new IllegalArgumentException("List has no method of name %s".formatted(name));
        }
    }

    /**
     * get the value of the attribute with the given name
     * @param name - the name of the attribute, can currently only be length
     * @return the value of the attribute
     * @throws IllegalArgumentException - if the name given is not one of the above
     */
    public Value getAttribute(String name) throws IllegalArgumentException {
        return switch(name){
            case "length" -> new FloatValue(this.size());
            default -> throw new IllegalArgumentException("List has no attribute of name %s".formatted(name));
        };
    }

    public BackendList(List<Value> other){
        super(other);
    }

    public BackendList(Collection<Value> other){
        super(other);
    }

    public BackendList(int initialCapacity){
        super(initialCapacity);
    }

    public BackendList(){
        super();
    }
}