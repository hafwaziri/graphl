package SymbolTable;

import ASTTraverser.Value.Value;

import java.util.List;
import java.util.function.Function;

public class FunctionSymbol extends Symbol{
    public Function<List<Value>, Value> value;
    public FunctionSymbol(String name, Function<List<Value>, Value> function) {
        super(name);
        value = function;
    }
}
