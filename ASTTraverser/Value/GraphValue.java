package ASTTraverser.Value;

import BackendClasses.Graph;

public class GraphValue extends Value {
    public final Graph value;

    public GraphValue(Graph value) {
        this.value = value;
    }

    public Object get() {
        return value;
    }
}
