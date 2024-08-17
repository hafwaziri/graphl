package ASTNode.Expr.LogicalExpr;

import ASTNode.Expr.Expr;
import java.util.List;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public abstract class LogicalExpr extends ASTNode.Expr.Expr{

    public List<Expr> exprList;
    /**
     * Creates a Binary Expression that checks if one expression is greater or equal than the other
     * @param exprList
     */
    public LogicalExpr(List<Expr> exprList){
        super();
        this.exprList=exprList;
    }

    public abstract Value eval(ScopedSymbolTable currentSymbolTable);
}
