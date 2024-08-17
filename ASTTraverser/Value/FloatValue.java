package ASTTraverser.Value;

public class FloatValue extends Value {
    public final float value;

    public FloatValue(float value) {
        this.value = value;
    }

    public Object get() {
        return value;
    }
}
