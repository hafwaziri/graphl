package ASTNode.Expr.LogicalExpr;

import ASTNode.Expr.Expr;
import java.util.List;

import ASTTraverser.Value.BoolValue;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public class Or extends ASTNode.Expr.LogicalExpr.LogicalExpr{

    /**
     * Creates a Binary Expression that checks if one or the other expression is true
     * @param exprList list of expressions
     */
    public Or(List<Expr> exprList){
        super(exprList);
    }

    /**
     * evaluates the Expressions
     * @param currentSymbolTable table to check the value of a variable
     * @return Value a boolean value
     */
    public Value eval(ScopedSymbolTable currentSymbolTable){
        boolean result = false;
        for(Expr expr : exprList){
            result = result || ((BoolValue) expr.eval(currentSymbolTable)).value;
        }
        return new BoolValue(result);
    }
}
