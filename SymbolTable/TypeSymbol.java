package SymbolTable;

public class TypeSymbol extends Symbol {
    public TypeSymbol(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return getName() + ": TypeSymbol";
    }
}
