package ASTNode.Expr.Target;

import ASTNode.Expr.Expr;
import ASTTraverser.Value.FloatValue;
import ASTTraverser.Value.ListValue;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public class ListAccess extends Target {
    public Target target;
    public Expr expr;

    /**
     * creates a new list access target
     */
    public ListAccess(Target target, Expr expr) {
        this.target = target;
        this.expr = expr;
    }

    /**
     * evaluates the Expression and the target and excesses the Target
     * @param currentSymbolTable table to check the value of a variable
     * @return Value of any TypeValue
     */
    public Value eval(ScopedSymbolTable currentSymbolTable){
        ListValue list = (ListValue) target.eval(currentSymbolTable);
        FloatValue index = (FloatValue) expr.eval(currentSymbolTable);

        return (list.value).get((int)index.value);
    }
}
