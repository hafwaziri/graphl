package Test;

import ASTNode.Block;
import ASTNode.Expr.BinaryExpr.Add;
import ASTNode.Expr.BinaryExpr.Comparison.Eq;
import ASTNode.Expr.BinaryExpr.Comparison.Less;
import ASTNode.Expr.Expr;
import ASTNode.Expr.ListExpr;
import ASTNode.Expr.LogicalExpr.FalseExpr;
import ASTNode.Expr.LogicalExpr.TrueExpr;
import ASTNode.Expr.Name;
import ASTNode.Expr.Numeral;
import ASTNode.Expr.Target.Attribute;
import ASTNode.Expr.Target.ListAccess;
import ASTNode.Expr.Target.Variable;
import ASTNode.Program;
import ASTNode.Statement.Assignment;
import ASTNode.Statement.IfStatement;
import ASTNode.Statement.Statement;
import ASTNode.Statement.WhileStmt;
import ASTTraverser.ASTTraverser;
import ASTTraverser.Value.FloatValue;
import ASTTraverser.Value.ListValue;
import ASTTraverser.Value.NodeValue;
import ASTTraverser.Value.Value;
import BackendClasses.Node;
import SymbolTable.ScopedSymbolTable;
import SymbolTable.VarSymbol;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TraverserTest {
    ASTTraverser buildSingleStepTraverser(Statement statement){
        List<Statement> s = new ArrayList<>();
        s.add(statement);
        return new ASTTraverser(new Program(s));
    }

    Block buildSingleStepBlock(Statement statement){
        List<Statement> s = new ArrayList<>();
        s.add(statement);
        return new Block(s);
    }

    void assertFloatVariableCorrect(ASTTraverser traverser, String variableName, float expected){
        ScopedSymbolTable symbolTable = traverser.getSymbolTable();
        assert symbolTable.hasSymbol(variableName);
        assert symbolTable.lookUp(variableName) instanceof VarSymbol;
        assert ((VarSymbol)symbolTable.lookUp(variableName)).value instanceof FloatValue;
        assert ((FloatValue)((VarSymbol) symbolTable.lookUp(variableName)).value).value == expected;
    }

    @Nested
    class AssignmentTest{

        @Test
        void variableAssignment(){ //assigning to variable
            Assignment assignment = new Assignment(new Variable(new Name("myVar")), new Numeral(3));
            ASTTraverser traverser = buildSingleStepTraverser(assignment);
            traverser.run();
            ScopedSymbolTable table = traverser.getSymbolTable();
            assertFloatVariableCorrect(traverser, "myVar", 3);
        }

        @Test
        void ListAssignment0(){//assigning to beginning of list
            Assignment assignment = new Assignment(new ListAccess(new Variable(new Name("myList")), new Numeral(0)), new Numeral(3));
            ASTTraverser traverser = buildSingleStepTraverser(assignment);

            List<Value> myList = new ArrayList<>();
            myList.add(new FloatValue(1));

            traverser.getSymbolTable().insert(new VarSymbol("myList", new ListValue(myList)));
            traverser.run();
            ScopedSymbolTable table = traverser.getSymbolTable();

            assert table.hasSymbol("myList");
            assert ((FloatValue)((ListValue)((VarSymbol)table.lookUp("myList")).value).value.get(0)).value == 3.0;
        }

        @Test
        void ListAssignment1(){//assigning to different spot of list
            Assignment assignment = new Assignment(new ListAccess(new Variable(new Name("myList")), new Numeral(3)), new Numeral(3));
            ASTTraverser traverser = buildSingleStepTraverser(assignment);
            List<Value> myList = List.of(new FloatValue(0), new FloatValue(0), new FloatValue(0), new FloatValue(0));
            traverser.getSymbolTable().insert(new VarSymbol("myList", new ListValue(myList)));
            traverser.run();
            ScopedSymbolTable table = traverser.getSymbolTable();
            assert table.hasSymbol("myList");
            assert ((FloatValue)((ListValue)((VarSymbol)table.lookUp("myList")).value).value.get(3)).value == 3.0;
        }

        @Test
        void AttributeAssignment(){ //assigning to attribute
            Attribute target = new Attribute(new Variable(new Name("myNode")), "weight");
            Assignment assignment = new Assignment(target, new Numeral(3));
            ASTTraverser traverser = buildSingleStepTraverser(assignment);
            traverser.getSymbolTable().insert(new VarSymbol("myNode", new NodeValue(new Node(1.0))));
            traverser.run();
            ScopedSymbolTable table = traverser.getSymbolTable();
            assert table.hasSymbol("myNode");
            assert ((NodeValue)((VarSymbol)table.lookUp("myNode")).value).value.weight == 3.0;
        }
    }

    @Nested
    class IfTest{

        @Test
        void TrueCondition(){ // if with true condition
            Expr condition = new TrueExpr();
            Assignment assignment = new Assignment(new Variable(new Name("myVar")), new Numeral(3));

            IfStatement ifStatement = new IfStatement(condition, buildSingleStepBlock(assignment));
            ASTTraverser traverser = buildSingleStepTraverser(ifStatement);
            traverser.getSymbolTable().insert(new VarSymbol("myVar", new FloatValue(1)));

            traverser.run();
            ScopedSymbolTable symbolTable = traverser.getSymbolTable();
            assertFloatVariableCorrect(traverser, "myVar", 3);
            assert symbolTable.hasSymbol("myVar");
        }

        @Test
        void FalseCondition(){//if with false condition
            Expr condition = new FalseExpr();
            Assignment assignment = new Assignment(new Variable(new Name("myVar")), new Numeral(3));

            IfStatement ifStatement = new IfStatement(condition, buildSingleStepBlock(assignment));
            ASTTraverser traverser = buildSingleStepTraverser(ifStatement);
            traverser.getSymbolTable().insert(new VarSymbol("myVar", new FloatValue(1)));

            traverser.run();
            assertFloatVariableCorrect(traverser, "myVar", 1);

        }
    }

    @Nested
    class WhileTest{
        @Test void NoBodyRuns(){ //condition false at beginning
            Expr condition =  new FalseExpr();
            Block body = buildSingleStepBlock(new Assignment(new Variable(new Name("myVar")), new Numeral(3)));
            WhileStmt whileStmt = new WhileStmt(condition, body);
            ASTTraverser traverser = buildSingleStepTraverser(whileStmt);
            traverser.getSymbolTable().insert(new VarSymbol("myVar", new FloatValue(1)));
            traverser.run();
            assertFloatVariableCorrect(traverser, "myVar", 1);
        }

        @Test
        void OneBodyRun(){ //condition is true for exactly one run
            Expr condition = new Eq(new Name("myVar"), new Numeral(0));
            Block body = buildSingleStepBlock(new Assignment(new Variable(new Name("myVar")), new Add(new Name("myVar"), new Numeral(1))));
            WhileStmt whileStmt = new WhileStmt(condition, body);
            ASTTraverser traverser = buildSingleStepTraverser(whileStmt);
            traverser.getSymbolTable().insert(new VarSymbol("myVar", new FloatValue(0)));
            traverser.run();
            assertFloatVariableCorrect(traverser, "myVar", 1);
        }

        @Test
        void MultipleBodyRun(){
            Expr condition = new Less(new Name("myVar"), new Numeral(5));
            Block body = buildSingleStepBlock(new Assignment(new Variable(new Name("myVar")), new Add(new Name("myVar"), new Numeral(1))));
            WhileStmt whileStmt = new WhileStmt(condition, body);
            ASTTraverser traverser = buildSingleStepTraverser(whileStmt);
            traverser.getSymbolTable().insert(new VarSymbol("myVar", new FloatValue(0)));
            traverser.run();
            assertFloatVariableCorrect(traverser, "myVar", 5);
        }

        @Test
        void NestedWhiles(){// test nested below is the source could this should represent
            /*
            a = 0
            b = 0
            while(a<5)
            {
                c = 0
                while(c<5){
                    b = b + 1
                    c = c + 1
                }
                a = a + 1
            }
             */

            Assignment aAdd = new Assignment(new Variable(new Name("a")), new Add(new Name("a"), new Numeral(1)));
            Assignment bAdd = new Assignment(new Variable(new Name("b")), new Add(new Name("b"), new Numeral(1)));
            Assignment cAdd = new Assignment(new Variable(new Name("c")), new Add(new Name("c"), new Numeral(1)));
            List<Statement> innerBlockList = new ArrayList<>();
            innerBlockList.add(bAdd);
            innerBlockList.add(cAdd);
            Expr innerCondition = new Less(new Name("c"), new Numeral(5));
            WhileStmt innerWhile = new WhileStmt(innerCondition, new Block(innerBlockList));

            Assignment cReset = new Assignment(new Variable(new Name("c")), new Numeral(0));
            List<Statement> outerBlockList = new ArrayList<>();
            outerBlockList.add(cReset);
            outerBlockList.add(innerWhile);
            outerBlockList.add(aAdd);
            Expr outerCondition = new Less(new Name("a"), new Numeral(5));
            WhileStmt outerWhile = new WhileStmt(outerCondition, new Block(outerBlockList));

            Assignment aDecl = new Assignment(new Variable(new Name("a")), new Numeral(0));
            Assignment bDecl = new Assignment(new Variable(new Name("b")), new Numeral(0));
            List<Statement> statements = new ArrayList<>();
            statements.add(aDecl);
            statements.add(bDecl);
            statements.add(outerWhile);

            ASTTraverser traverser = new ASTTraverser(new Program(statements));
            traverser.run();

            assertFloatVariableCorrect(traverser, "a", 5);
            assertFloatVariableCorrect(traverser, "b", 25);
        }
    }

    @Nested
    class VisitBlockTest{
        @Test
        void innerscopeKnowsOuterscope(){
            /*Tested source:
            a=0
            b=1
            if(true) {
                b=a
            }
            check: b is 0 an no errors
             */

            Assignment aDecl = new Assignment(new Variable(new Name("a")), new Numeral(0));
            Assignment bDecl = new Assignment(new Variable(new Name("b")), new Numeral(1));
            Assignment bodyAssignment = new Assignment(new Variable(new Name("b")), new Name("a"));
            IfStatement ifStatement = new IfStatement(new TrueExpr(), buildSingleStepBlock(bodyAssignment));
            List<Statement> statements = new ArrayList<>();
            statements.add(aDecl);
            statements.add(bDecl);
            statements.add(ifStatement);
            ASTTraverser traverser = new ASTTraverser(new Program(statements));
            traverser.run();
            assertFloatVariableCorrect(traverser, "b", 0);
        }

        @Test
        void outerScopeDoesnotKnowInnerscope(){
            /*Tested source:
            if(true) {
            b=1
            }
            check: b is not in symboltable
             */

            ASTTraverser traverser = buildSingleStepTraverser(
                    new IfStatement(
                            new TrueExpr(),
                            buildSingleStepBlock(new Assignment(new Variable(new Name("b")), new Numeral(1)))
                    )
            );
            traverser.run();
            assert !traverser.getSymbolTable().hasSymbol("b");
        }
    }

    @Nested
    class ComplexTests{

        Assignment buildAssignment(String name, Expr value){
            return new Assignment(new Variable(new Name(name)), value);
        }

        Assignment buildIncrease(String name, float amount){
            return buildAssignment(name, new Add(new Name(name), new Numeral(amount)));
        }

        @Test
        void countOnes(){
            /* tested source code:
            a = [1, 0, 1, 1, 0, 1, 0]
            length = 7
            i = 0
            n = 0
            while(i<length){
                if(a[i] == 1){
                    n=n+1
                }
                i = i+1
            }

            checks: n==4
             */
            List<Expr> exprs = List.of(new Numeral(1), new Numeral(0), new Numeral(1), new Numeral(1), new Numeral(0), new Numeral(1), new Numeral(0));
            Assignment aDecl = buildAssignment("a", new ListExpr(exprs));
            Assignment lengthDecl = buildAssignment("length", new Numeral(exprs.size()));
            Assignment iDecl = buildAssignment("i", new Numeral(0));
            Assignment nDecl = buildAssignment("n", new Numeral(0));

            IfStatement ifStatement = new IfStatement(
                    new Eq(new ListAccess(new Variable(new Name("a")), new Name("i")), new Numeral(1)),
                    buildSingleStepBlock(buildIncrease("n", 1))
            );

            WhileStmt whileStmt = new WhileStmt(
                    new Less(new Name("i"), new Name("length")),
                    new Block(List.of(ifStatement, buildIncrease("i", 1)))
            );

            ASTTraverser traverser = new ASTTraverser(new Program(List.of(aDecl, lengthDecl, iDecl, nDecl, whileStmt)));
            traverser.run();
            assertFloatVariableCorrect(traverser, "n", 4);
        }

    }
}
