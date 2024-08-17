package ASTNode.Expr;

import ASTTraverser.Value.BoolValue;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public class Not extends Expr {
    public Expr expression;

    /**
     * creates a new not-expression
     */
    public Not(Expr expression) {
        this.expression = expression;
    }

    /**
     * evaluates the Expression and negate it
     * @param currentSymbolTable table to check the value of a variable
     * @return Value a boolean Value
     */
    public Value eval(ScopedSymbolTable currentSymbolTable){
        BoolValue result = (BoolValue) expression.eval(currentSymbolTable);
        return new BoolValue(!result.value);
    }
}
