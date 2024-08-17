package ASTNode.Expr;

import ASTTraverser.Value.ListValue;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

import java.util.ArrayList;
import java.util.List;

public class ListExpr extends Expr{
    public List<Expr> values;

    public ListExpr(List<Expr> values) {
        this.values = values;
    }

    @Override
    public Value eval(ScopedSymbolTable currentSymbolTable) {
        List<Value> valuesEvaled = new ArrayList<>(values.size());
        for(Expr expr:values){
            valuesEvaled.add(expr.eval(currentSymbolTable));
        }
        return new ListValue(valuesEvaled);
    }
}
