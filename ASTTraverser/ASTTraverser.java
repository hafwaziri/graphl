package ASTTraverser;

import ASTNode.Block;
import ASTNode.Expr.Target.Attribute;
import ASTNode.Expr.Target.ListAccess;
import ASTNode.Expr.Target.ProcedureCall.MethodCallExpr;
import ASTNode.Expr.Target.Variable;
import ASTNode.Program;
import ASTNode.Statement.*;
import ASTTraverser.Value.*;
import SymbolTable.ScopedSymbolTable;
import SymbolTable.VarSymbol;

import java.util.List;

public class ASTTraverser {
    private final Program program;
    private ScopedSymbolTable currentSymbolTable;

    public ASTTraverser(Program program){
        this.program = program;
        this.currentSymbolTable = new ScopedSymbolTable(true);
    }

    public void run(){
        for(Statement statement : program.statements){
            visitStatement(statement);
        }
    }

    public ScopedSymbolTable getSymbolTable(){
        return currentSymbolTable;
    }

    private void visitStatement(Statement statement){
        switch (statement) {
            case WhileStmt whileStmt -> visitWhileStatement(whileStmt);
            case IfStatement ifStatement -> visitIfStatement(ifStatement);
            case Assignment assignment -> visitAssignment(assignment);
            case ProcedureCallStatement procedureCallStatement -> visitProcedureCallStatement(procedureCallStatement);
            case null, default ->
                    throw new RuntimeException(String.format("The method for visiting %s is not yet defined", statement.getClass()));
        }
    }
    //eval() gibt value zurück nicht bool/float/...
    private void visitWhileStatement(WhileStmt whileStmt){
        while (((BoolValue) whileStmt.condition.eval(currentSymbolTable)).value){
            visitBlock(whileStmt.body);
        }
    }

    private void visitBlock(Block block){
        ScopedSymbolTable superTable = currentSymbolTable;
        currentSymbolTable = new ScopedSymbolTable(superTable);
        for(Statement statement: block.statements){
            visitStatement(statement);
        }
        currentSymbolTable = superTable;
    }
    //eval() gibt value zurück nicht bool/float/...
    private void visitIfStatement(IfStatement statement){
        if(((BoolValue) statement.condition.eval(currentSymbolTable)).value){
            visitBlock(statement.thenBlock);
        } else {
            if(statement.elseBlock != null){
                visitBlock(statement.elseBlock);
            }
        }
    }

    private void visitAssignment(Assignment statement){
        if(statement.target instanceof MethodCallExpr) throw new RuntimeException("Cannot Assign to Methodcall");
        if(statement.target instanceof ListAccess){
            List<Value> list = ((ListValue)((ListAccess) statement.target).target.eval(currentSymbolTable)).value;
            int index = (int)((FloatValue)((ListAccess) statement.target).expr.eval(currentSymbolTable)).value;
            list.set(index, statement.value.eval(currentSymbolTable));
        } else if (statement.target instanceof Variable){
            String name = ((Variable) statement.target).name.value;
            if(currentSymbolTable.hasSymbol(name)){
                ((VarSymbol)currentSymbolTable.lookUp(name)).value = statement.value.eval(currentSymbolTable);
            } else {
                currentSymbolTable.insert(new VarSymbol(name, statement.value.eval(currentSymbolTable)));
            }
        } else if (statement.target instanceof Attribute){
            Value target = ((Attribute) statement.target).target.eval(currentSymbolTable);
            if(target instanceof EdgeValue){
                ((EdgeValue)target).value.setAttribute(((Attribute) statement.target).name, statement.value.eval(currentSymbolTable));
            } else if (target instanceof GraphValue) {
                ((GraphValue)target).value.setAttribute(((Attribute) statement.target).name, statement.value.eval(currentSymbolTable));
            } else if (target instanceof ListValue){
                throw new RuntimeException("List does not have any assignable attribute. you tried to assign to " + ((Attribute) statement.target).name);
            } else if (target instanceof NodeValue){
                ((NodeValue)target).value.setAttribute(((Attribute) statement.target).name, statement.value.eval(currentSymbolTable));
            }
        } else {
            throw new RuntimeException("The assignment for target type " + statement.getClass() + "has not been implemented");
        }
    }

    private void visitProcedureCallStatement(ProcedureCallStatement procedureCallStatement){
        procedureCallStatement.procedureCallExpr.eval(currentSymbolTable);
    }
}
