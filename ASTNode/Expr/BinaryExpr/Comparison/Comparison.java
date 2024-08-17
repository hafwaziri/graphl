package ASTNode.Expr.BinaryExpr.Comparison;

import ASTNode.Expr.Expr;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public abstract class Comparison extends ASTNode.Expr.BinaryExpr.BinaryExpr {

    /**
     * Creates a Binary Expression that compares two Expressions
     * @param left Expr-Object
     * @param right Expr-Object
     */
    public Comparison(Expr left, Expr right){
        super(left, right);
    }

    public abstract Value eval(ScopedSymbolTable currentSymbolTable);
}
