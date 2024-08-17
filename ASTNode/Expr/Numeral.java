package ASTNode.Expr;

import ASTTraverser.Value.FloatValue;
import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;

public class Numeral extends Expr{

    public float value;

    /**
     * Creates a new Numeral
     * @param value float number - value of the numeral
     */
    //type() fehlt noch, was genau?
    public Numeral(float value){
        super();
        this.value = value;
    }

    /**
     * evaluates to the value itself
     * @param currentSymbolTable table to check the value of a variable
     * @return Value of FloatValue
     */
    public Value eval(ScopedSymbolTable currentSymbolTable){
        return new FloatValue(value);
    }
}
