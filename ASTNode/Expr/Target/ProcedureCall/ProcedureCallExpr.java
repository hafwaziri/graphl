package ASTNode.Expr.Target.ProcedureCall;

import ASTNode.Expr.Expr;
import ASTNode.Expr.Target.Target;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

import java.util.List;

public abstract class ProcedureCallExpr extends Target {

    public String name;
    public List<Expr> arglist;

    public ProcedureCallExpr(String name, List<Expr> arglist){
        this.name = name;
        this.arglist = arglist;
    }

    public abstract Value eval(ScopedSymbolTable currentSymbolTable);

}
