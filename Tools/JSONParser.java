package Tools;
import ASTNode.Block;
import ASTNode.ElifStmt;
import ASTNode.Expr.BinaryExpr.*;
import ASTNode.Expr.LogicalExpr.LogicalExpr;
import ASTNode.Expr.Target.Attribute;
import ASTNode.Expr.Target.ListAccess;
import ASTNode.Expr.Target.ProcedureCall.FunctionCallExpr;
import ASTNode.Expr.Target.ProcedureCall.MethodCallExpr;
import ASTNode.Expr.Target.ProcedureCall.ProcedureCallExpr;
import ASTNode.Expr.Target.Target;
import ASTNode.Expr.Target.Variable;
import ASTNode.Program;
import ASTNode.Statement.*;
import ASTTraverser.ASTTraverser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import ASTNode.Expr.BinaryExpr.Comparison.*;
import ASTNode.Expr.*;
import ASTNode.Expr.LogicalExpr.And;
import ASTNode.Expr.LogicalExpr.Or;
import ASTNode.Expr.LogicalExpr.TrueExpr;
import ASTNode.Expr.LogicalExpr.FalseExpr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/*
 * Jackson core, databind and annotations required when compiling.
 * The Print AST function should be used with caution, was implemented only for debugging, not tested exhaustively.
 * Method Call, Function Call, List assignment/access should be also tested...
 */



public class JSONParser {
    public static void main(String[] args) throws IOException {
        InputStream sourcefile = System.in;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(sourcefile);

        Program program = parseProgram(root);

        if (args.length > 0 && args[0].equals("-o")) {

            System.out.println(String.join("", Collections.nCopies(50, "#")));
            
            System.out.println("\nImported JSON:\n");

            System.out.println(root.toPrettyString() + "\n");

            System.out.println(String.join("", Collections.nCopies(50, "#")));

            System.out.println("\nAbstract Syntax Tree:\n");

            printAST(program, 0);

            System.out.println("\n" + String.join("", Collections.nCopies(50, "#")));

            System.out.println("\nAST Traverser Output:\n");

            ASTTraverser traverser = new ASTTraverser(program);
            traverser.run();

            System.out.println("\n" + String.join("", Collections.nCopies(50, "#")));
        }
        else {
            ASTTraverser traverser = new ASTTraverser(program);
            traverser.run();
        }
    }

    private static Program parseProgram(JsonNode node) {
        List<Statement> statements = new ArrayList<>();
        Iterator<JsonNode> children = node.get("children").iterator();
        while (children.hasNext()) {
            JsonNode child = children.next();
            Statement statement = parseStatement(child);
            statements.add(statement);
        }
        return new Program(statements);
    }

    private static IfStatement parseIfStatement(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode conditionNode = children.next();
        JsonNode thenBlockNode = children.next();

        Expr condition = parseExpression(conditionNode);
        Block thenBlock = parseBlock(thenBlockNode);

        List<ElifStmt> elifStmts = new ArrayList<>();
        Block elseBlock = null;

        while (children.hasNext()) {
            JsonNode child = children.next();
            String childType = child.get("type").asText();

            if (childType.equals("ElifStmt")) {
                ElifStmt elifStmt = parseElifStmt(child);
                elifStmts.add(elifStmt);
            } else if (childType.equals("Block")) {
                elseBlock = parseBlock(child);
            } else {
                throw new IllegalArgumentException("Unexpected child node type in IfStatement: " + childType);
            }
        }

        if (elseBlock == null) {
            if (elifStmts.isEmpty()) {
                return new IfStatement(condition, thenBlock);
            } else {
                return new IfStatement(condition, thenBlock, elifStmts);
            }
        } else {
            if (elifStmts.isEmpty()) {
                return new IfStatement(condition, thenBlock, elseBlock);
            } else {
                return new IfStatement(condition, thenBlock, elifStmts, elseBlock);
            }
        }
    }

    private static ElifStmt parseElifStmt(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode conditionNode = children.next();
        JsonNode thenBlockNode = children.next();

        Expr condition = parseExpression(conditionNode);
        Block thenBlock = parseBlock(thenBlockNode);

        return new ElifStmt(condition, thenBlock);
    }

    private static Statement parseStatement(JsonNode node) {
        String type = node.get("type").asText();
        switch (type) {
            case "Assignment":
                return parseAssignment(node);
            case "ProcedureCallStmt":
                return parseProcedureCallStatement(node);
            case "IfStatement":
                return parseIfStatement(node);
            case "WhileStmt":
                return parseWhileStmt(node);
            default:
                throw new IllegalArgumentException("Unknown statement type: " + type);
        }
    }

    private static ProcedureCallStatement parseProcedureCallStatement(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode procedureCallNode = children.next();

        ProcedureCallExpr procedureCallExpr = parseProcedureCallExpr(procedureCallNode);
        return new ProcedureCallStatement(procedureCallExpr);
    }

    private static ProcedureCallExpr parseProcedureCallExpr(JsonNode node) {
        String type = node.get("type").asText();
        switch (type) {
            case "FunctionCallExpr":
                 return parseFunctionCallExpr(node);
            case "MethodCallExpr":
                return parseMethodCallExpr(node);
        }
        return null;
    }

    private static MethodCallExpr parseMethodCallExpr(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode targetNode = children.next();
        JsonNode FunctionCallExprNode = children.next();

        Target target = parseTarget(targetNode);
        FunctionCallExpr functionCallExpr = parseFunctionCallExpr(FunctionCallExprNode);
        
        return new MethodCallExpr(target, functionCallExpr);
    }

    

    private static FunctionCallExpr parseFunctionCallExpr(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode nameNode = children.next();
        String name = nameNode.get("value").asText();

        List<Expr> argList = new ArrayList<>();
        while (children.hasNext()) {
            JsonNode argNode = children.next();
            Expr arg = parseExpression(argNode);
            argList.add(arg);
        }

        return new FunctionCallExpr(name, argList);
    }

    private static Assignment parseAssignment(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode targetNode = children.next();
        JsonNode valueNode = children.next();

        Target target = parseTarget(targetNode);
        Expr value = parseExpression(valueNode);

        return new Assignment(target, value);
    }

    private static Target parseTarget(JsonNode node) {
        String type = node.get("type").asText();
        switch (type) {
            case "Variable":
                return parseVariable(node);
            // case "MethodCall":
            //     return parseMethodCall(node);
            case "ListAccess":
                return parseListAccess(node);
            case "Attribute":
                return parseAttribute(node);
            case "MethodCallExpr":
                return parseMethodCallExpr(node);
            case "FunctionCallExpr":
                return parseFunctionCallExpr(node);
            default:
                throw new IllegalArgumentException("Unknown target type: " + type);
        }
    }

    private static Variable parseVariable(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode nameNode = children.next();
        Name name = parseName(nameNode);
        return new Variable(name);
    }

    private static Name parseName(JsonNode node) {
        String value = node.get("value").asText();
        return new Name(value);
    }

    private static Expr parseExpression(JsonNode node) {
        String type = node.get("type").asText();
        switch (type) {
            case "Numeral":
                return parseNumeral(node);
            case "Name":
                return parseName(node);
            case "Not":
                return parseNot(node);
            case "UnaryMinus":
                return parseUnaryMinus(node);
            case "FunctionCallExpr":
                return parseFunctionCallExpr(node);
            case "ProcedureCallExpr":
                return parseProcedureCallExpr(node);
            case "MethodCallExpr":
                return parseMethodCallExpr(node);
            case "Eq":
                return parseEq(node);
            case "Greater":
                return parseGreater(node);
            case "GreatEq":
                return parseGreatEq(node);
            case "Less":
                return parseLess(node);
            case "LessEq":
                return parseLessEq(node);
            case "NotEq":
                return parseNotEq(node);
            case "And":
                return parseAnd(node);
            case "Or":
                return parseOr(node);
            case "Add":
                return parseAdd(node);
            case "Div":
                return parseDiv(node);
            case "Mul":
                return parseMul(node);
            case "Sub":
                return parseSub(node);
            case "Variable":
                return parseTarget(node);
            case "Attribute":
                return parseTarget(node);
            case "TrueExpr":
                return parseTrueExpr(node);
            case "FalseExpr":
                return parseFalseExpr(node);
            case "List":
                return parseListExpr(node);
            case "ListAccess":
                return parseListAccess(node);
            case "StringExpr":
                return parseStringExpr(node);

            default:
                throw new IllegalArgumentException("Unknown expression type: " + type);
        }
    }

    private static StringExpr parseStringExpr(JsonNode node) {
        String value = node.get("value").asText();
        return new StringExpr(value);
    }

    private static ListExpr parseListExpr(JsonNode node) {
        List<Expr> values = new ArrayList<>();
        JsonNode childrenNode = node.get("children");
        if (childrenNode != null) {
            Iterator<JsonNode> children = childrenNode.iterator();
            while (children.hasNext()) {
                JsonNode child = children.next();
                Expr expr = parseExpression(child);
                values.add(expr);
            }
        }
        return new ListExpr(values);
    }

    private static TrueExpr parseTrueExpr(JsonNode node) {
        return new TrueExpr();
    }

    private static FalseExpr parseFalseExpr(JsonNode node) {
        return new FalseExpr();
    }

    private static Not parseNot(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode expressionNode = children.next();

        Expr expression = parseExpression(expressionNode);

        return new Not(expression);
    }

    private static Numeral parseNumeral(JsonNode node) {
        double value = node.get("value").asDouble();
        return new Numeral((float) value);
    }

    private static WhileStmt parseWhileStmt(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode conditionNode = children.next();
        JsonNode bodyNode = children.next();

        Expr condition = parseExpression(conditionNode);
        Block body = parseBlock(bodyNode);

        return new WhileStmt(condition, body);
    }

    private static Block parseBlock(JsonNode node) {
        List<Statement> statements = new ArrayList<>();
        Iterator<JsonNode> children = node.get("children").iterator();
        while (children.hasNext()) {
            JsonNode child = children.next();
            Statement statement = parseStatement(child);
            statements.add(statement);
        }
        return new Block(statements);
    }

    private static UnaryMinus parseUnaryMinus(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode expressionNode = children.next();

        Expr expression = parseExpression(expressionNode);

        return new UnaryMinus(expression);
    }

    private static Eq parseEq(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode leftNode = children.next();
        JsonNode rightNode = children.next();

        Expr left = parseExpression(leftNode);
        Expr right = parseExpression(rightNode);

        return new Eq(left, right);
    }

    private static Greater parseGreater(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode leftNode = children.next();
        JsonNode rightNode = children.next();

        Expr left = parseExpression(leftNode);
        Expr right = parseExpression(rightNode);

        return new Greater(left, right);
    }

    private static GreaterEq parseGreatEq(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode leftNode = children.next();
        JsonNode rightNode = children.next();

        Expr left = parseExpression(leftNode);
        Expr right = parseExpression(rightNode);

        return new GreaterEq(left, right);
    }

    private static Less parseLess(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode leftNode = children.next();
        JsonNode rightNode = children.next();

        Expr left = parseExpression(leftNode);
        Expr right = parseExpression(rightNode);

        return new Less(left, right);
    }

    private static LessEq parseLessEq(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode leftNode = children.next();
        JsonNode rightNode = children.next();

        Expr left = parseExpression(leftNode);
        Expr right = parseExpression(rightNode);

        return new LessEq(left, right);
    }

    private static NotEq parseNotEq(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode leftNode = children.next();
        JsonNode rightNode = children.next();

        Expr left = parseExpression(leftNode);
        Expr right = parseExpression(rightNode);

        return new NotEq(left, right);
    }

    private static And parseAnd(JsonNode node) {
        List<Expr> exprList = new ArrayList<>();
        Iterator<JsonNode> children = node.get("children").iterator();
        while (children.hasNext()) {
            JsonNode child = children.next();
            Expr expr = parseExpression(child);
            exprList.add(expr);
        }

        return new And(exprList);
    }

    private static Or parseOr(JsonNode node) {
        List<Expr> exprList = new ArrayList<>();
        Iterator<JsonNode> children = node.get("children").iterator();
        while (children.hasNext()) {
            JsonNode child = children.next();
            Expr expr = parseExpression(child);
            exprList.add(expr);
        }
        return new Or(exprList);
    }

    private static Add parseAdd(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode leftNode = children.next();
        JsonNode rightNode = children.next();

        Expr left = parseExpression(leftNode);
        Expr right = parseExpression(rightNode);

        return new Add(left, right);
    }

    private static Div parseDiv(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode leftNode = children.next();
        JsonNode rightNode = children.next();

        Expr left = parseExpression(leftNode);
        Expr right = parseExpression(rightNode);

        return new Div(left, right);
    }

    private static Mul parseMul(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode leftNode = children.next();
        JsonNode rightNode = children.next();

        Expr left = parseExpression(leftNode);
        Expr right = parseExpression(rightNode);

        return new Mul(left, right);
    }

    private static Sub parseSub(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode leftNode = children.next();
        JsonNode rightNode = children.next();

        Expr left = parseExpression(leftNode);
        Expr right = parseExpression(rightNode);

        return new Sub(left, right);
    }

    private static Attribute parseAttribute(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode targetNode = children.next();
        JsonNode nameNode = children.next();

        Target target = parseTarget(targetNode);
        String name = nameNode.get("value").asText();

        return new Attribute(target, name);
    }

    private static ListAccess parseListAccess(JsonNode node) {
        Iterator<JsonNode> children = node.get("children").iterator();
        JsonNode targetNode = children.next();
        JsonNode exprNode = children.next();

        Target target = parseTarget(targetNode);
        Expr expr = parseExpression(exprNode);

        return new ListAccess(target, expr);
    }

    private static <ASTNode> void printAST(ASTNode node, int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }
        System.out.print(indent.toString() + node.getClass().getSimpleName());
        if (node instanceof Name) {
            Name nameNode = (Name) node;
            System.out.println(": " + nameNode.value);
            return;
        } else if (node instanceof Numeral) {
            Numeral numeralNode = (Numeral) node;
            System.out.println(": " + numeralNode.value);
            return;
        } else if (node instanceof TrueExpr) {
            System.out.println(": true");
            return;
        } else if (node instanceof FalseExpr) {
            System.out.println(": false");
            return;
        } else if (node instanceof StringExpr) {
            StringExpr stringExpr = (StringExpr) node;
            System.out.println(": \"" + stringExpr.value + "\"");
            return;
        } else {
            System.out.println();
        }
        if (node instanceof Program) {
            Program program = (Program) node;
            for (Statement statement : program.statements) {
                printAST(statement, depth + 1);
            }
        } else if (node instanceof Assignment) {
            Assignment assignment = (Assignment) node;
            printAST(assignment.target, depth + 1);
            printAST(assignment.value, depth + 1);
        } else if (node instanceof Variable) {
            Variable variable = (Variable) node;
            printAST(variable.name, depth + 1);
        } else if (node instanceof IfStatement) {
            IfStatement ifStatement = (IfStatement) node;
            printAST(ifStatement.condition, depth + 1);
            printAST(ifStatement.thenBlock, depth + 1);
            if (ifStatement.elifStmts != null) {
                for (ElifStmt elifStmt : ifStatement.elifStmts) {
                    printAST(elifStmt, depth + 1);
                }
            }
            if (ifStatement.elseBlock != null) {
                printAST(ifStatement.elseBlock, depth + 1);
            }
        } else if (node instanceof ElifStmt) {
            ElifStmt elifStmt = (ElifStmt) node;
            printAST(elifStmt.condition, depth + 1);
            printAST(elifStmt.thenBlock, depth + 1);
        } else if (node instanceof WhileStmt) {
            WhileStmt whileStmt = (WhileStmt) node;
            printAST(whileStmt.condition, depth + 1);
            printAST(whileStmt.body, depth + 1);
        } else if (node instanceof Block) {
            Block block = (Block) node;
            for (Statement statement : block.statements) {
                printAST(statement, depth + 1);
            }
        } else if (node instanceof Not) {
            Not not = (Not) node;
            printAST(not.expression, depth + 1);
        } else if (node instanceof UnaryMinus) {
            UnaryMinus unaryMinus = (UnaryMinus) node;
            printAST(unaryMinus.expression, depth + 1);
        } else if (node instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) node;
            printAST(binaryExpr.left, depth + 1);
            printAST(binaryExpr.right, depth + 1);
        } else if (node instanceof LogicalExpr) {
            LogicalExpr logicalExpr = (LogicalExpr) node;
            for (Expr expr : logicalExpr.exprList) {
                printAST(expr, depth + 1);
            }
        } else if (node instanceof Attribute) {
            Attribute attribute = (Attribute) node;
            printAST(attribute.target, depth + 1);
            System.out.println(indent.toString() + "  Name: " + attribute.name);
        } else if (node instanceof ListAccess) {
            ListAccess listAccess = (ListAccess) node;
            printAST(listAccess.target, depth + 1);
            printAST(listAccess.expr, depth + 1);
        } else if (node instanceof ProcedureCallStatement) {
            ProcedureCallStatement procedureCallStatement = (ProcedureCallStatement) node;
            printAST(procedureCallStatement.procedureCallExpr, depth + 1);
        } else if (node instanceof FunctionCallExpr) {
            FunctionCallExpr functionCallExpr = (FunctionCallExpr) node;
            System.out.println(indent.toString() + "Name: " + functionCallExpr.name);
            for (Expr arg : functionCallExpr.arglist) {
                printAST(arg, depth + 1);
            }
        } else if (node instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) node;
            printAST(methodCallExpr.target, depth + 1);
            printAST(methodCallExpr.functionCallExpr, depth + 1);
        } else if (node instanceof ListExpr) {
            ListExpr listExpr = (ListExpr) node;
            for (Expr expr : listExpr.values) {
                printAST(expr, depth + 1);
            }
        } else {
            throw new IllegalArgumentException("Unknown ASTNode type: " + node.getClass().getSimpleName());
        }
    }
    
    


}
