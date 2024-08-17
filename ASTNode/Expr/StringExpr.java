package ASTNode.Expr;

import ASTTraverser.Value.StringValue;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public class StringExpr extends Expr{

    public String value;

    /**
     * Creates a new StringExpr
     * @param value the represented string
     */
    public StringExpr(String value){
        super();
        this.value = value;
    }

    /**
     * evaluates the expression
     * @param currentSymbolTable the current state of the programm
     * @return the value this stringexpression represents
     */
    @Override
    public Value eval(ScopedSymbolTable currentSymbolTable) {
        return new StringValue(value);
    }
}
