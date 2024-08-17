package ASTNode.Expr;

import ASTTraverser.Value.FloatValue;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public class UnaryMinus extends Expr {
    public Expr expression;

    /**
     * creates a new unary expression
     */
    public UnaryMinus(Expr expression) {
        this.expression = expression;
    }

    /**
     * evaluates the Expressions and negate it
     * @param currentSymbolTable table to check the value of a variable
     * @return Value of any TypeValue
     */
    public Value eval(ScopedSymbolTable currentSymbolTable){

        Value expr = expression.eval(currentSymbolTable);

        if(expr instanceof FloatValue) {
            float result = ((FloatValue) expression.eval(currentSymbolTable)).value;
            result = -result;

            return new FloatValue(result);
        }

        return null;
    }
}
