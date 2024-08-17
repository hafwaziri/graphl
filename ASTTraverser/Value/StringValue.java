package ASTTraverser.Value;

public class StringValue extends Value {
    public final String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public Object get() {
        return value;
    }
}
