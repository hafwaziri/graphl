package ASTNode.Expr.LogicalExpr;

import ASTTraverser.Value.BoolValue;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public class FalseExpr extends ASTNode.Expr.LogicalExpr.LogicalExpr{

    public FalseExpr(){
        super(null);
    }

    public Value eval(ScopedSymbolTable currentSymbolTable){
        return new BoolValue(false);
    }
}
