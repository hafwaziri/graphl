package ASTNode.Expr.BinaryExpr.Comparison;

import ASTNode.Expr.Expr;
import ASTTraverser.Value.*;
import SymbolTable.ScopedSymbolTable;

public class Eq extends ASTNode.Expr.BinaryExpr.Comparison.Comparison{

    /**
     * Creates a Binary Expression that that checks if two Expressions are equal
     * @param left Expr-Object
     * @param right Expr-Object
     */
    public Eq(Expr left, Expr right){
        super(left, right);
    }

    public Value eval(ScopedSymbolTable currentSymbolTable){
        Value leftExpr = left.eval(currentSymbolTable);
        Value rightExpr = right.eval(currentSymbolTable);

        boolean result = false;

        if(leftExpr instanceof BoolValue && rightExpr instanceof BoolValue){

            result = ((BoolValue) leftExpr).value != ((BoolValue) rightExpr).value;

        }else if(leftExpr instanceof EdgeValue && rightExpr instanceof EdgeValue){
            result = (((EdgeValue) leftExpr).value).equals(((EdgeValue) rightExpr).value);
        }else if(leftExpr instanceof NodeValue && rightExpr instanceof NodeValue){
            result = (((NodeValue) leftExpr).value).equals(((NodeValue) rightExpr).value);
        }else if(leftExpr instanceof FloatValue && rightExpr instanceof FloatValue){
            result = ((FloatValue) leftExpr).value == ((FloatValue) rightExpr).value;
        }else if(leftExpr instanceof GraphValue && rightExpr instanceof GraphValue){
            result = (((GraphValue) leftExpr).value).equals(((GraphValue) rightExpr).value);
        }else if(leftExpr instanceof ListValue && rightExpr instanceof ListValue){
            result = (((ListValue) leftExpr).value).equals(((ListValue) rightExpr).value);
        }

        return new BoolValue(result);
    }
}
