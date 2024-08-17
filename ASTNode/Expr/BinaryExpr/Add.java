package ASTNode.Expr.BinaryExpr;

import ASTNode.Expr.Expr;
import ASTTraverser.Value.FloatValue;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public class Add extends BinaryExpr{

    /**
     * Creates a Binary Expression that adds two Expressions
     * @param left Expr-Object
     * @param right Expr-Object
     */
    public Add(Expr left, Expr right){
        super(left, right);
    }

    /**
     * evaluates the Expression and adds them
     * @param currentSymbolTable table to check the value of a variable
     * @return Value a numereal value
     */
    public Value eval(ScopedSymbolTable currentSymbolTable){

        Value leftExpr = left.eval(currentSymbolTable);
        Value rightExpr = right.eval(currentSymbolTable);

        if(leftExpr instanceof FloatValue && rightExpr instanceof FloatValue){
            float result = ((FloatValue) leftExpr).value + ((FloatValue) rightExpr).value;
            return new FloatValue(result);
        }

        return null;

    }
}
