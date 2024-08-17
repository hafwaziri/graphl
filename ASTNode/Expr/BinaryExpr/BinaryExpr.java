package ASTNode.Expr.BinaryExpr;

import ASTNode.Expr.Expr;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public abstract class BinaryExpr extends ASTNode.Expr.Expr {
    public Expr left;
    public Expr right;



    /**
     * Creates a Binary Expression and adds the left and right child to it's own children
     * @param left Expr-Object
     * @param right Expr-Object
     */
    public BinaryExpr(Expr left, Expr right){
        super();
        this.left = left;
        this.right = right;
    }

    public abstract Value eval(ScopedSymbolTable currentSymbolTable);

}
