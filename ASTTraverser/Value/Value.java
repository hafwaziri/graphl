package ASTTraverser.Value;

public abstract class Value {
    public abstract Object get();

    @Override
    public String toString() {
        return get().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Value)
            return get().equals(((Value)obj).get());
        return false;
    }
}
