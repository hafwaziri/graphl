package ASTTraverser.Value;

public class BoolValue extends Value {
    public final boolean value;

    public BoolValue(boolean value) {
        this.value = value;
    }

    public Object get() {
        return value;
    }
}
