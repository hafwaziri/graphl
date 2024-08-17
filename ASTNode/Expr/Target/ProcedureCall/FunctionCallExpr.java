package ASTNode.Expr.Target.ProcedureCall;

import java.util.ArrayList;
import java.util.List;

import ASTTraverser.Value.*;
import SymbolTable.FunctionSymbol;
import SymbolTable.ScopedSymbolTable;
import ASTNode.Expr.Expr;

public class FunctionCallExpr extends ProcedureCallExpr {

    /**
     * creates a new function call expression
     */
    public FunctionCallExpr(String name, List<Expr> arglist) {

        super(name,arglist);
    }

    /**
     * evaluates funktionCalls
     * @param currentSymbolTable table to check the value of a variable
     * @return Value a boolean Value
     */
    public Value eval(ScopedSymbolTable currentSymbolTable) {
        List<Value> exprs = new ArrayList<>();

        for (Expr e : arglist) {
            exprs.add(e.eval(currentSymbolTable));
        }

        FunctionSymbol x = (FunctionSymbol) currentSymbolTable.lookUp(name);
        if (x == null) {
            throw new RuntimeException("Function '" + name + "' does not exist!");
        }
        return x.value.apply(exprs);
    }
}
