package ASTNode.Statement;

import ASTNode.Block;
import ASTNode.ElifStmt;
import ASTNode.Expr.Expr;

import java.util.List;

public class IfStatement extends Statement {
    public Expr condition;
    public Block thenBlock;
    public List<ElifStmt> elifStmts;
    public Block elseBlock;

    /**
     * creates a new if-statement with else-if statements and an else-block
     */
    public IfStatement(Expr condition, Block thenBlock, List<ElifStmt> elifStmts, Block elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elifStmts = elifStmts;
        this.elseBlock = elseBlock;
    }

    /**
     * creates a new if-statement with else-if statements but without an else-block
     */
    public IfStatement(Expr condition, Block thenBlock, List<ElifStmt> elifStmts) {
        this(condition, thenBlock, elifStmts, null);
    }

    /**
     * creates a new if-statement without else-if statements but with an else-block
     */
    public IfStatement(Expr condition, Block thenBlock, Block elseBlock) {
        this(condition, thenBlock, null, elseBlock);
    }

    /**
     * creates a new if-statement without else-if statements and without an else-block
     */
    public IfStatement(Expr condition, Block thenBlock) {
        this(condition, thenBlock, null, null);
    }
}
