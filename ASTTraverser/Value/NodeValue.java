package ASTTraverser.Value;

import BackendClasses.Node;

public class NodeValue extends Value {
    public final Node value;

    public NodeValue(Node value) {
        this.value = value;
    }

    public Object get() {
        return value;
    }
}
