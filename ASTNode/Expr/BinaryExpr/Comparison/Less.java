package ASTNode.Expr.BinaryExpr.Comparison;

import ASTNode.Expr.Expr;
import ASTTraverser.Value.BoolValue;
import ASTTraverser.Value.FloatValue;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public class Less extends ASTNode.Expr.BinaryExpr.Comparison.Comparison{

    /**
     * Creates a Binary Expression that checks if one expression is less than the other
     * @param left Expr-Object
     * @param right Expr-Object
     */
    public Less(Expr left, Expr right){
        super(left, right);
    }

    /**
     * evaluates the Expression and tests if one numbers is less than the other
     * @param currentSymbolTable table to check the value of a variable
     * @return Value a boolean value
     */
    public Value eval(ScopedSymbolTable currentSymbolTable){
        Value leftExpr = left.eval(currentSymbolTable);
        Value rightExpr = right.eval(currentSymbolTable);

        boolean result = false;

        if(leftExpr instanceof FloatValue && rightExpr instanceof FloatValue){
            result = ((FloatValue) leftExpr).value < ((FloatValue) rightExpr).value;
        }

        return new BoolValue(result);
    }
}
