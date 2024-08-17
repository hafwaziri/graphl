package ASTNode.Expr;

import ASTTraverser.Value.Value;
import SymbolTable.ScopedSymbolTable;
import SymbolTable.VarSymbol;

public class Name extends Expr{

    public String value;

    /**
     * Creates a new Name
     * @param value name-string
     */
    public Name(String value){
        super();
        this.value = value;
    }

    /**
     * evaluates the Name
     * @param currentSymbolTable table to check the value of a variable
     * @return Value of any TypeValue
     */
    public Value eval(ScopedSymbolTable currentSymbolTable){

        VarSymbol sb= (VarSymbol) currentSymbolTable.lookUp(value);

        return sb.value;
    }
}
