package ASTNode.Statement;

import ASTNode.Expr.Expr;
import ASTNode.Expr.Target.Target;

public class Assignment extends Statement {
    public Target target;
    public Expr value;

    /**
     * creates a new assignment statement
     */
    public Assignment(Target target, Expr value) {
        this.target = target;
        this.value = value;
    }
}
