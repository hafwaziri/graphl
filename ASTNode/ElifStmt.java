package ASTNode;

import ASTNode.Expr.Expr;

// the elif-statements do not have an else block anymore, because that would be redundant
public class ElifStmt extends ASTNode {
    public Expr condition;
    public Block thenBlock;

    /**
     * creates a new else-if statement
     */
    public ElifStmt(Expr condition, Block thenBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
    }
}
