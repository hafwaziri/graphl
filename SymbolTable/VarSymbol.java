package SymbolTable;

import ASTTraverser.Value.Value;

public class VarSymbol extends Symbol{
    public VarSymbol(String name) {
        super(name);
    }

    public VarSymbol(String name, Value value){
        super(name);
        this.value = value;
    }

    @Override
    public String toString() {
        return getName() + ": VarSymbol";
    }

    public Value value;
}
