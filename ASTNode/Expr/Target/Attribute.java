package ASTNode.Expr.Target;

import ASTNode.Expr.Name;
import ASTTraverser.Value.*;
import SymbolTable.ScopedSymbolTable;

public class Attribute extends Target {
    public Target target;
    public String name;

    /**
     * creates a new attribute target
     */
    public Attribute(Target target, String name) {
        this.target = target;
        this.name = name;
    }

    /**
     * evaluates the Target and gets the attribute
     * @param currentSymbolTable table to check the value of a variable
     * @return Value of any TypeValue
     */
    //was ist mit ListValue -> haben keine Offiziellen Listenobjekte
    public Value eval(ScopedSymbolTable currentSymbolTable){
        Value val = target.eval(currentSymbolTable);
        Value result= null;

        if(val instanceof GraphValue){
            result = (((GraphValue) val).value).getAttribute(name);
        }else if(val instanceof EdgeValue){
            result = (((EdgeValue) val).value).getAttribute(name);
        }else if(val instanceof NodeValue){
            result = (((NodeValue) val).value).getAttribute(name);
        } else if(val instanceof ListValue){
            result = (((ListValue) val).value).getAttribute(name);
        }

        return result;
    }
}
