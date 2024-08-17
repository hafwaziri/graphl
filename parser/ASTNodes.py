import json

# base AST Node class
class ASTNode:
    def __init__(self):
        self.children = []
        self.value = None
        self.parent = None

    def add_child(self, child):
        if isinstance(child, ASTNode) and child not in self.children:
            self.children.append(child)
            child.parent = self


        if isinstance(child, List) and child not in self.children:
            print("enfjanfoi")
            for node in child.expressions:
                self.children.append(node)
                node.parent = self

    def __setattr__(self, name, value):
        try:
            super().__getattribute__(name)
            for i, child in enumerate(self.children):
                if child == super().__getattribute__(name):
                    self.children.pop(i)
                    return
        except AttributeError:
            pass
            
        if isinstance(value, ASTNode):
            if name != 'parent':  # Avoid setting parent through __setattr__
                self.add_child(value)
        elif isinstance(value, list):
            for item in value:
                if isinstance(item, list):
                    for val in item:
                        self.add_child(val)
                if isinstance(item, ASTNode):
                    self.add_child(item)
        super().__setattr__(name, value)

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        original_init = cls.__init__

        def new_init(self, *args, **kwargs):
            original_init(self, *args, **kwargs)
            for name, value in self.__dict__.items():
                if isinstance(value, ASTNode):
                    self.add_child(value)
                elif isinstance(value, list):
                    for item in value:
                        if isinstance(item, ASTNode):
                            self.add_child(item)

        cls.__init__ = new_init

    def __str__(self, indent=0):
        tree = ' ' * indent + self.__class__.__name__ + (f"(value={self.value})" if self.value else "") + "\n"
        for child in self.children:
            tree += child.__str__(indent + 2)
        return tree
        

    def to_json(self):
        def node_to_dict(node):
            node_dict = {"type": node.__class__.__name__}
            if node.value is not None:
                node_dict["value"] = node.value
            if hasattr(node, "children") and node.children:
                node_dict["children"] = [node_to_dict(child) for child in node.children]
            return node_dict

        return json.dumps(node_to_dict(self), indent=2)

# Specific AST Node classes
class Expr(ASTNode):
    # Base class
    def __init__(self):
        super().__init__()

class Numeral(Expr):
    def __init__(self, value : float):
        super().__init__()
        self.value = value
        self.type = type(value)

class StringExpr(Expr):
    def __init__(self, value : str):
        super().__init__()
        self.value = value
        self.type = type(value)

class Name(Expr):
    def __init__(self, value : str):
        super().__init__()
        self.value = value

class BinaryExpr(Expr):
    # Base class
    def __init__(self, left : Expr, right : Expr):
        super().__init__()
        self.left = left
        self.right = right

class Add(BinaryExpr):
    def __init__(self, left: Expr, right: Expr):
        super().__init__(left, right)

class Sub(BinaryExpr):
    def __init__(self, left: Expr, right: Expr):
        super().__init__(left, right)

class Mul(BinaryExpr):
    def __init__(self, left: Expr, right: Expr):
        super().__init__(left, right)

class Div(BinaryExpr):
    def __init__(self, left: Expr, right: Expr):
        super().__init__(left, right)

class Comparison(BinaryExpr):
    # Base class
    def __init__(self, left: Expr, right: Expr):
        super().__init__(left, right)

class NotEq(Comparison):
    def __init__(self, left: Expr, right: Expr):
        super().__init__(left, right)

class Eq(Comparison):
    def __init__(self, left: Expr, right: Expr):
        super().__init__(left, right)

class Less(Comparison):
    def __init__(self, left: Expr, right: Expr):
        super().__init__(left, right)

class Greater(Comparison):
    def __init__(self, left: Expr, right: Expr):
        super().__init__(left, right)

class LessEq(Comparison):
    def __init__(self, left: Expr, right: Expr):
        super().__init__(left, right)

class GreatEq(Comparison):
    def __init__(self, left: Expr, right: Expr):
        super().__init__(left, right)

class LogicalExpr(Expr):
    # Base class
    def __init__(self, expressions : list[Expr]):
        super().__init__()
        self.expressions = expressions

class Or(LogicalExpr):
    def __init__(self, expressions : list[Expr]):
        super().__init__(expressions)

class And(LogicalExpr):
    def __init__(self, expressions : list[Expr]):
        super().__init__(expressions)

class TrueExpr(LogicalExpr):
    def __init__(self):
        super().__init__([])
        self.value = True

class FalseExpr(LogicalExpr):
    def __init__(self):
        super().__init__([])
        self.value = False


class ListOrTarget(Expr):
    def __init__(self):
        super().__init__()

class List(ListOrTarget):
    def __init__(self, expressions : list[Expr]):
        super().__init__()
        self.expressions = expressions

class UnaryMinus(Expr):
    def __init__(self, expression : Expr):
        super().__init__()
        self.expression = expression

class Not(Expr):
    def __init__(self, expression : Expr):
        super().__init__()
        self.expression = expression


class Target(ListOrTarget):
    # Base class
    def __init__(self):
        super().__init__()

class Statement(ASTNode):
    # Base Class
    def __init__(self):
        super().__init__()

class Block(ASTNode):
    def __init__(self, statements : list[Statement]):
        super().__init__()
        self.statements = statements

class Assignment(Statement):
    def __init__(self, target, value):
        super().__init__()
        self.target = target
        self.assigned = value

class ElifStmt(ASTNode):
    def __init__(self, condition : Expr, then_block : Block, else_block : Block = None):
        super().__init__()
        self.condition = condition
        self.then_block = then_block
        self.else_block = else_block

class IfStatement(Statement):
    def __init__(self, condition : Expr, then_block : Block, elif_stmts : list[ElifStmt] = [], else_block : Block = None):
        super().__init__()
        self.condition = condition
        self.then_block = then_block
        self.elif_stmts = elif_stmts
        self.else_block = else_block

class WhileStmt(Statement):
    def __init__(self, condition : Expr, body : Block):
        super().__init__()
        self.condition = condition
        self.body = body

class ForeachStmt(Statement):
    def __init__(self, run_variable : Name, iterable : ListOrTarget, body : Block):
        super().__init__()
        self.run_variable = run_variable
        self.iterable = iterable
        self.body = body

class ProcedureCallExpr(Target):
    def __init__(self):
        super().__init__()

class FunctionCallExpr(ProcedureCallExpr):
    def __init__(self, name : Name, arguments : list[Expr]):
        super().__init__()
        self.name = name
        self.arguments = arguments

class MethodCallExpr(ProcedureCallExpr):
    def __init__(self, target : Target, functionCallExpr : FunctionCallExpr):
        super().__init__()
        self.target = target
        self.functionCallExpr = functionCallExpr


class ProcedureCallStmt(Statement):
    def __init__(self, procedurCallExpr : ProcedureCallExpr):
        super().__init__()
        self.procedurCallExpr = procedurCallExpr

class Variable(Target):
    def __init__(self, name : Name):
        super().__init__()
        self.name = name

class Attribute(Target):
    def __init__(self, target : Target, attribute_name : Name):
        super().__init__()
        self.target = target
        self.name = attribute_name




class ListAccess(Target):
    def __init__(self, target : Target, expr : Expr):
        super().__init__()
        self.target = target
        self.expr = expr

class Program(ASTNode):
    def __init__(self, statements : list[Statement]):
        super().__init__()
        self.statements = statements
