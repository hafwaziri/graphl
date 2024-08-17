package ASTTraverser.Value;

import BackendClasses.BackendList;

import java.util.List;

// it is currently possible to construct a list of different values (e.g. a list containing ints and floats)
public class ListValue extends Value {
    public final BackendList value;

    public ListValue(List<Value> value) {
        this.value = new BackendList(value);
    }

    public ListValue(BackendList value){
        this.value = value;
    }

    public Object get() {
        return value;
    }
}
