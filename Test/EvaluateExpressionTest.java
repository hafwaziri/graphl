package Test;

import ASTNode.Expr.BinaryExpr.Add;
import ASTNode.Expr.BinaryExpr.Comparison.*;
import ASTNode.Expr.BinaryExpr.Div;
import ASTNode.Expr.BinaryExpr.Mul;
import ASTNode.Expr.BinaryExpr.Sub;
import ASTNode.Expr.*;
import ASTNode.Expr.LogicalExpr.And;
import ASTNode.Expr.LogicalExpr.FalseExpr;
import ASTNode.Expr.LogicalExpr.Or;
import ASTNode.Expr.LogicalExpr.TrueExpr;
import ASTNode.Expr.Target.Attribute;
import ASTNode.Expr.Target.ListAccess;
import ASTNode.Expr.Target.ProcedureCall.FunctionCallExpr;
import ASTNode.Expr.Target.ProcedureCall.MethodCallExpr;
import ASTNode.Expr.Target.Target;
import ASTNode.Expr.Target.Variable;
import ASTTraverser.Value.*;
import BackendClasses.Edge;
import BackendClasses.Graph;
import BackendClasses.Node;
import SymbolTable.ScopedSymbolTable;
import SymbolTable.VarSymbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvaluateExpressionTest {
    @BeforeEach
    void setup() {
        // reset the ids of the nodes before each test
        Node.resetIDNum();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // ---------- helper functions -------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    private static void assertExpr(Object expected, Expr e) {
        ScopedSymbolTable t = new ScopedSymbolTable(true);
        assertExpr(expected, e, t);
    }

    private static void assertExpr(Object expected, Expr e, ScopedSymbolTable t) {
        Value val = e.eval(t);
        assertEquals(expected, val.get());
    }

    private static List<Expr> boolExprList(boolean... boolValues) {
        List<Expr> exprList = new ArrayList<>(boolValues.length);
        for (boolean b : boolValues)
            exprList.add(b ? new TrueExpr() : new FalseExpr());
        return exprList;
    }

    private static void testPrint(Expr e) {
        // switch system output to capture the output of the print function
        PrintStream oldOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(baos);
        System.setOut(newOut);

        Expr fcall = new FunctionCallExpr("print", List.of(e));
        ScopedSymbolTable t = new ScopedSymbolTable(true);
        String expected = e.eval(t).toString() + '\n';
        fcall.eval(t);

        // restore old system output and compare the print results
        System.setOut(oldOut);
        String captured = baos.toString();
        assertEquals(expected, captured);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // ---------- tests ------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Nested
    class SimpleTest {
        @Nested
        class BinaryExprTest {
            @Nested
            class ComparisonTest {
                @Test
                void eqTrue() {
                    Expr op = new Eq(new Numeral(1), new Numeral(1));
                    assertExpr(true, op);
                }

                @Test
                void eqFalse() {
                    Expr op = new Eq(new Numeral(2), new Numeral(1));
                    assertExpr(false, op);
                }

                @Test
                void eqDifferentTypes() {
                    Expr boolOp = new Eq(new Numeral(1), new Numeral(1));
                    Expr numOp = new Numeral(3);
                    Expr op = new Eq(boolOp, numOp);
                    assertExpr(false, op);
                }

                @Test
                void greaterTrue() {
                    Expr op = new Greater(new Numeral(2), new Numeral(1));
                    assertExpr(true, op);
                }

                @Test
                void greaterFalse() {
                    Expr op = new Greater(new Numeral(1), new Numeral(2));
                    assertExpr(false, op);
                }

                @Test
                void greaterFalseEq() {
                    Expr op = new Greater(new Numeral(1), new Numeral(1));
                    assertExpr(false, op);
                }

                @Test
                void greaterEqTrue() {
                    Expr op = new GreaterEq(new Numeral(2), new Numeral(1));
                    assertExpr(true, op);
                }

                @Test
                void greaterEqFalse() {
                    Expr op = new GreaterEq(new Numeral(1), new Numeral(2));
                    assertExpr(false, op);
                }

                @Test
                void greaterEqTrueEq() {
                    Expr op = new GreaterEq(new Numeral(1), new Numeral(1));
                    assertExpr(true, op);
                }

                @Test
                void lessTrue() {
                    Expr op = new Less(new Numeral(1), new Numeral(2));
                    assertExpr(true, op);
                }

                @Test
                void lessFalse() {
                    Expr op = new Less(new Numeral(2), new Numeral(1));
                    assertExpr(false, op);
                }

                @Test
                void lessFalseEq() {
                    Expr op = new Less(new Numeral(1), new Numeral(1));
                    assertExpr(false, op);
                }

                @Test
                void lessEqTrue() {
                    Expr op = new LessEq(new Numeral(1), new Numeral(2));
                    assertExpr(true, op);
                }

                @Test
                void lessEqFalse() {
                    Expr op = new LessEq(new Numeral(2), new Numeral(1));
                    assertExpr(false, op);
                }

                @Test
                void lessEqTrueEq() {
                    Expr op = new LessEq(new Numeral(1), new Numeral(1));
                    assertExpr(true, op);
                }

                @Test
                void notEqTrue() {
                    Expr op = new NotEq(new Numeral(1), new Numeral(2));
                    assertExpr(true, op);
                }

                @Test
                void notEqFalse() {
                    Expr op = new NotEq(new Numeral(1), new Numeral(1));
                    assertExpr(false, op);
                }
            }

            @Test
            void add1() {
                Expr op = new Add(new Numeral(1), new Numeral(2));
                assertExpr(3f, op);
            }

            @Test
            void add2() {
                Expr op = new Add(new Numeral(1.5f), new Numeral(2));
                assertExpr(3.5f, op);
            }

            @Test
            void addNegative() {
                Expr op = new Add(new Numeral(1), new Numeral(-1));
                assertExpr(0f, op);
            }

            @Test
            void div() {
                Expr op = new Div(new Numeral(3), new Numeral(2));
                assertExpr(1.5f, op);
            }

            @Test
            void mul1() {
                Expr op = new Mul(new Numeral(1.5f), new Numeral(1.5f));
                assertExpr(2.25f, op);
            }

            @Test
            void mul2() {
                Expr op = new Mul(new Numeral(1.5f), new Numeral(-1));
                assertExpr(-1.5f, op);
            }

            @Test
            void sub() {
                Expr op = new Sub(new Numeral(2.5f), new Numeral(3));
                assertExpr(-0.5f, op);
            }
        }

        @Nested
        class LogicalExprTest {
            @Test
            void trueAndTrue() {
                Expr op = new And(boolExprList(true, true));
                assertExpr(true, op);
            }

            @Test
            void trueAndFalse() {
                Expr op = new And(boolExprList(true, false));
                assertExpr(false, op);
            }

            @Test
            void falseAndFalse() {
                Expr op = new And(boolExprList(false, false));
                assertExpr(false, op);
            }

            @Test
            void andListTrue() {
                Expr op = new And(boolExprList(true, true, true, true, true));
                assertExpr(true, op);
            }

            @Test
            void andListFalse() {
                Expr op = new And(boolExprList(true, true, true, true, false));
                assertExpr(false, op);
            }

            @Test
            void trueOrTrue() {
                Expr op = new Or(boolExprList(true, true));
                assertExpr(true, op);
            }

            @Test
            void trueOrFalse() {
                Expr op = new Or(boolExprList(true, false));
                assertExpr(true, op);
            }

            @Test
            void falseOrFalse() {
                Expr op = new Or(boolExprList(false, false));
                assertExpr(false, op);
            }

            @Test
            void falseListTrue() {
                Expr op = new Or(boolExprList(false, true, true, false, true));
                assertExpr(true, op);
            }

            @Test
            void falseListFalse() {
                Expr op = new Or(boolExprList(false, false, false, false, false));
                assertExpr(false, op);
            }

            @Test
            void trueExprTest() {
                Expr op = new TrueExpr();
                assertExpr(true, op);
            }

            @Test
            void falseExprTest() {
                Expr op = new FalseExpr();
                assertExpr(false, op);
            }
        }

        @Nested
        class TargetTest {
            @Nested
            class ProcedureCallExprStatementTest {
                @Test
                void printFloat() {
                    testPrint(new Numeral(2.5f));
                }

                @Test
                void nodes() {
                    Expr op = new FunctionCallExpr("Nodes", List.of(new Numeral(5)));
                    Value ret = op.eval(new ScopedSymbolTable(true));
                    assertInstanceOf(ListValue.class, ret);
                    ListValue lv = (ListValue) ret;
                    assertEquals(5, lv.value.size());
                    for (int i = 0; i < 5; i++) {
                        Value element = lv.value.get(i);
                        assertInstanceOf(NodeValue.class, element);
                        Node node = ((NodeValue) element).value;
                        assertEquals(i, node.getId());
                        assertEquals(0f, node.weight);
                    }
                }

                @Test
                void graphInt() {
                    Expr op = new FunctionCallExpr("Graph", List.of(new Numeral(5)));
                    Value ret = op.eval(new ScopedSymbolTable(true));
                    assertInstanceOf(GraphValue.class, ret);
                    Graph graph = ((GraphValue) ret).value;
                    assertFalse(graph.getProperty(Graph.Property.EdgeWeighted));
                    assertFalse(graph.getProperty(Graph.Property.NodeWeighted));
                    assertTrue(graph.getProperty(Graph.Property.Directional));
                    for (int i = 0; i < 5; i++) {
                        Node node = graph.getNode(i);
                        assertEquals(i, node.getId());
                        assertEquals(0f, node.weight);
                    }
                }

                @Test
                void graphList() {
                    Expr nodes = new FunctionCallExpr("Nodes", List.of(new Numeral(5)));
                    Expr op = new FunctionCallExpr("Graph", List.of(nodes));
                    Value ret = op.eval(new ScopedSymbolTable(true));
                    assertInstanceOf(GraphValue.class, ret);
                    Graph graph = ((GraphValue) ret).value;
                    assertFalse(graph.getProperty(Graph.Property.EdgeWeighted));
                    assertFalse(graph.getProperty(Graph.Property.NodeWeighted));
                    assertTrue(graph.getProperty(Graph.Property.Directional));
                    for (int i = 0; i < 5; i++) {
                        Node node = graph.getNode(i);
                        assertEquals(i, node.getId());
                        assertEquals(0f, node.weight);
                    }
                }

                @Test
                void edge() {
                    Expr node1 = new FunctionCallExpr("Node", List.of());
                    Expr node2 = new FunctionCallExpr("Node", List.of());
                    Expr op = new FunctionCallExpr("Edge", List.of(node1, node2));
                    Value ret = op.eval(new ScopedSymbolTable(true));
                    assertInstanceOf(EdgeValue.class, ret);
                    Edge edge = ((EdgeValue) ret).value;
                    assertEquals(0f, edge.getFrom().getId());
                    assertEquals(1f, edge.getTo().getId());
                    assertEquals(1f, edge.weight);
                }

                @Test
                void edgeWeighted() {
                    Expr node1 = new FunctionCallExpr("Node", List.of());
                    Expr node2 = new FunctionCallExpr("Node", List.of());
                    Expr weight = new Numeral(2.5f);
                    Expr op = new FunctionCallExpr("Edge", List.of(node1, node2, weight));
                    Value ret = op.eval(new ScopedSymbolTable(true));
                    assertInstanceOf(EdgeValue.class, ret);
                    Edge edge = ((EdgeValue) ret).value;
                    assertEquals(0f, edge.getFrom().getId());
                    assertEquals(1f, edge.getTo().getId());
                    assertEquals(2.5f, edge.weight);
                }

                @Test
                void nodeDefault() {
                    Expr op = new FunctionCallExpr("Node", List.of());
                    Value ret = op.eval(new ScopedSymbolTable(true));
                    assertInstanceOf(NodeValue.class, ret);
                    Node node = ((NodeValue) ret).value;
                    assertEquals(0f, node.getId());
                    assertEquals(0f, node.weight);
                }

                @Test
                void nodeWeighted() {
                    Expr op = new FunctionCallExpr("Node", List.of(new Numeral(1.5f)));
                    Value ret = op.eval(new ScopedSymbolTable(true));
                    assertInstanceOf(NodeValue.class, ret);
                    Node node = ((NodeValue) ret).value;
                    assertEquals(0f, node.getId());
                    assertEquals(1.5, node.weight);
                }
            }

            @Test
            void methodCall1() {
                Target graph = new FunctionCallExpr("Graph", List.of(new Numeral(1)));
                Expr op = new MethodCallExpr(graph, new FunctionCallExpr("getNode", List.of(new Numeral(0))));
                assertEquals(0f, ((Node) op.eval(new ScopedSymbolTable(true)).get()).getId());
            }

            @Test
            void methodCall2() {
                Target graph = new FunctionCallExpr("Graph", List.of(new Numeral(1)));
                Target node = new MethodCallExpr(graph, new FunctionCallExpr("getNode", List.of(new Numeral(0))));
                Expr op = new Attribute(node, "id");
                assertEquals(0f, op.eval(new ScopedSymbolTable(true)).get());
            }

            @Test
            void attribute1() {
                Target node = new FunctionCallExpr("Node", List.of());
                Expr op = new Attribute(node, "id");
                assertEquals(0f, op.eval(new ScopedSymbolTable(true)).get());
            }

            @Test
            void attribute2() {
                Expr node1 = new FunctionCallExpr("Node", List.of());
                Expr node2 = new FunctionCallExpr("Node", List.of());
                Target edge = new FunctionCallExpr("Edge", List.of(node1, node2));
                Target fromNode = new Attribute(edge, "from");
                Expr op = new Attribute(fromNode, "id");
                assertEquals(0f, op.eval(new ScopedSymbolTable(true)).get());
            }

            @Test
            void listAccess1() {
                Target target = new FunctionCallExpr("Nodes", List.of(new Numeral(2)));
                Expr index = new Numeral(1);
                Expr op = new ListAccess(target, index);
                assertEquals(1f, ((Node) op.eval(new ScopedSymbolTable(true)).get()).getId());
            }

            @Test
            void listAccess2() {
                Target target = new FunctionCallExpr("Nodes", List.of(new Numeral(2)));
                Expr index = new Numeral(1);
                Target node = new ListAccess(target, index);
                Expr op = new Attribute(node, "id");
                assertEquals(1f, op.eval(new ScopedSymbolTable(true)).get());
            }

            @Test
            void variable1() {
                ScopedSymbolTable t = new ScopedSymbolTable(true);
                t.insert(new VarSymbol("a", new FloatValue(2.5f)));
                Expr op = new Variable(new Name("a"));
                assertEquals(2.5f, op.eval(t).get());
            }

            @Test
            void variable2() {
                ScopedSymbolTable t = new ScopedSymbolTable(true);
                t.insert(new VarSymbol("n", new NodeValue(new Node())));
                Target var = new Variable(new Name("n"));
                Expr op = new Attribute(var, "id");
                assertEquals(0f, op.eval(t).get());
            }
        }

        @Nested
        class UnaryExprTest {
            @Test
            void notTrue() {
                Expr op = new Not(new TrueExpr());
                assertExpr(false, op);
            }

            @Test
            void notFalse() {
                Expr op = new Not(new FalseExpr());
                assertExpr(true, op);
            }

            @Test
            void unaryMinusPos() {
                Expr op = new UnaryMinus(new Numeral(2.5f));
                assertExpr(-2.5f, op);
            }

            @Test
            void unaryMinusNeg() {
                Expr op = new UnaryMinus(new Numeral(-2.5f));
                assertExpr(2.5f, op);
            }
        }

        @Nested
        class LiteralTest {
            @Test
            void numeralFloat() {
                Expr op = new Numeral(3.5f);
                assertExpr(3.5f, op);
            }

            @Test
            void numeralInt() {
                Expr op = new Numeral(2);
                assertExpr(2f, op);
            }

            @Test
            void listExprTest() {
                Expr op = new ListExpr(List.of(new TrueExpr(), new FalseExpr(), new Add(new Numeral(1), new Numeral(2))));
                assertExpr(new ArrayList<>(List.of(new BoolValue(true), new BoolValue(false), new FloatValue(3))), op);
            }
        }

        @Nested
        class NameTest {
            @Test
            void nameInt() {
                ScopedSymbolTable t = new ScopedSymbolTable(true);
                t.insert(new VarSymbol("f", new FloatValue(5)));
                Expr op = new Name("f");
                assertExpr(5f, op, t);
            }
        }
    }
}
