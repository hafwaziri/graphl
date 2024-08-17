package ASTNode.Expr.Target.ProcedureCall;

import ASTTraverser.Value.*;
import BackendClasses.BackendList;
import BackendClasses.Edge;
import BackendClasses.Graph;
import BackendClasses.Node;
import SymbolTable.ScopedSymbolTable;
import ASTNode.Expr.Expr;
import ASTNode.Expr.Target.Target;

import java.util.ArrayList;
import java.util.List;

public class MethodCallExpr extends ProcedureCallExpr {
    public Target target;
    public FunctionCallExpr functionCallExpr;
    /**
     * creates a new method call target
     */
    public MethodCallExpr(Target target, FunctionCallExpr func) {
        super(func.name, func.arglist);
        this.functionCallExpr = func;
        this.target = target;
    }

    /**
     * evaluates the target and calls the funcionCall
     * @param currentSymbolTable table to check the value of a variable
     * @return Value of any TypeValue
     */
    public Value eval(ScopedSymbolTable currentSymbolTable) throws IllegalArgumentException{

        Value tar = target.eval(currentSymbolTable);
        BackendList exprs = new BackendList();

        for(Expr e : arglist){
            exprs.add(e.eval(currentSymbolTable));
        }

        if(tar instanceof EdgeValue){
            Edge edge = ((EdgeValue) tar).value;
            return edge.runMethod(name);
        }else if(tar instanceof NodeValue){
            Node node = ((NodeValue) tar).value;
            return node.runMethod(name, exprs);
        }else if(tar instanceof GraphValue){
            Graph graph = ((GraphValue) tar).value;
            return graph.runMethod(name, exprs);
        } else if(tar instanceof ListValue){
            BackendList list = ((ListValue) tar).value;
            return list.runMethod(name, exprs);
        }
        throw new IllegalArgumentException("wrong target instance");
    }
}
