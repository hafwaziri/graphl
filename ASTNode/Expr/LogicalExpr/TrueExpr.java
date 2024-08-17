package ASTNode.Expr.LogicalExpr;

import ASTTraverser.Value.BoolValue;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public class TrueExpr extends ASTNode.Expr.LogicalExpr.LogicalExpr{

    public TrueExpr(){
        super(null);
    }

    public Value eval(ScopedSymbolTable currentSymbolTable){
        return new BoolValue(true);
    }
}
