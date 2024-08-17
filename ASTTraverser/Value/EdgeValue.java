package ASTTraverser.Value;

import BackendClasses.Edge;

public class EdgeValue extends Value {
    public final Edge value;

    public EdgeValue(Edge value) {
        this.value = value;
    }

    public Object get() {
        return value;
    }
}
