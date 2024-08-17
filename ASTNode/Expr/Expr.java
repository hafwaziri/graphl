package ASTNode.Expr;

import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public abstract class Expr extends ASTNode.ASTNode {

    /**
     * Creates a new Expression
     */
    public Expr(){
        super();
    }

    public abstract Value eval(ScopedSymbolTable currentSymbolTable);
}
