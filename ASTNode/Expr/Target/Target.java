package ASTNode.Expr.Target;

import ASTNode.Expr.Expr;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public abstract class Target extends Expr {
    // empty (for now)
    public abstract Value eval(ScopedSymbolTable currentSymbolTable);
}
