package ASTNode.Statement;

import ASTNode.Block;
import ASTNode.Expr.Expr;

public class WhileStmt extends Statement {
    public Expr condition;
    public Block body;

    /**
     * creates a new while-statement
     */
    public WhileStmt(Expr condition, Block body) {
        this.condition = condition;
        this.body = body;
    }
}
