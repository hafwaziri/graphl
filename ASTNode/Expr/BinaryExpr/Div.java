package ASTNode.Expr.BinaryExpr;

import ASTNode.Expr.Expr;
import ASTTraverser.Value.FloatValue;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public class Div extends BinaryExpr{

    /**
     * Creates a Binary Expression that divides two Expressions
     * @param left Expr-Object
     * @param right Expr-Object
     */
    public Div(Expr left, Expr right){
        super(left, right);
    }

    /**
     * evaluates the Expression and divedes them
     * @param currentSymbolTable table to check the value of a variable
     * @return Value a numereal value
     */
    public Value eval(ScopedSymbolTable currentSymbolTable){

        Value leftExpr = left.eval(currentSymbolTable);
        Value rightExpr = right.eval(currentSymbolTable);

        if(leftExpr instanceof FloatValue && rightExpr instanceof FloatValue){
            float dividend = ((FloatValue) leftExpr).value;
            float divisor = ((FloatValue) rightExpr).value;
            if(divisor == 0){
                throw new ArithmeticException("Division by zero");
            }
            float result = dividend / divisor;
            return new FloatValue(result);
        }

        return null;

    }

}
