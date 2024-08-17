package ASTNode.Statement;

import ASTNode.Expr.Target.ProcedureCall.ProcedureCallExpr;

public class ProcedureCallStatement extends Statement {
    public ProcedureCallExpr procedureCallExpr;

    /**
     * creates a new function call statement
     */
    public ProcedureCallStatement(ProcedureCallExpr procedureCallExpr) {
        this.procedureCallExpr = procedureCallExpr;
    }
}
