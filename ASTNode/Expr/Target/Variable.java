package ASTNode.Expr.Target;

import ASTNode.Expr.Name;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public class Variable extends Target {
    public Name name;

    /**
     * creates a new variable target
     */
    public Variable(Name name) {
        this.name = name;
    }

    /**
     * evaluates the Name
     * @param currentSymbolTable table to check the value of a variable
     * @return Value of any TypeValue
     */
    public Value eval(ScopedSymbolTable currentSymbolTable){
        return name.eval(currentSymbolTable);
    }
}
