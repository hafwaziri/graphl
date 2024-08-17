from lark import Transformer, v_args

from ASTNodes import *

# @v_args(inline=True)
class ASTtransformer(Transformer):
    def program(self, args):
        return Program(args[0])
    
    def statements(self, args):
        # args = list of statements
        return args

    def _ignore_one(self, args):
        return args[0]
    
    def _ignore_many(self, args):
        return args
    
    
    def assignment(self, args):
        return Assignment(args[0], args[1])

    def if_stmt(self, args):
        return IfStatement(args[0], args[1], None, args[2])
    
    def if_elif_stmt(self, args):
        ifstmt = IfStatement(args[0], args[1], args[2], None if len(args[2]) == 0 else args[2][-1].else_block)
        # print(ifstmt.elif_stmts[-1].else_block)
        
        if len(args[2]) > 0:
            ifstmt.elif_stmts[-1].else_block = None
        return ifstmt


    def elif_elif_stmt(self, args):
        return [ElifStmt(args[0], args[1])] + args[2]
    
    def elif_stmt(self, args):
        return [ElifStmt(args[0], args[1], args[2])]
    
    def block(self, args):
        return Block(args[0])
    
    def while_stmt(self, args):
        return WhileStmt(args[0], args[1])
    
    def foreach_stmt(self, args):
        return ForeachStmt(args[0], args[1], args[2])
    
    def variable(self, args):
        return Variable(args[0])
    
    def attribute(self, args):
        return Attribute(args[0], args[1])
    
    def method_call_expr(self, args):
        return MethodCallExpr(args[0], args[1])
    
    def list_access(self, args):
        return ListAccess(args[0], args[1])
    
    def disjunction(self, args):
        return Or(args)

    def conjunction(self, args):

        return And(args)
    
    def inversion(self, args):
        return Not(args[0])
    

    def comparison(self, args):
        args[1].left = args[0]
        args[1].children = args[1].children[::-1]
        return args[1]
    
    def eq(self, args):
        return Eq(None, args[0])
    
    def noteq(self, args):
        return NotEq(None, args[0])
    
    def greater(self, args):
        return Greater(None, args[0])
    
    def less(self, args):
        return Less(None, args[0])
    
    def lesseq(self, args):
        return LessEq(None, args[0])
    
    def greateq(self, args):
        return GreatEq(None, args[0])
    
    def list_expr(self, args):
        return List(args)

    def list_expr_empty(self, args):
        return List([])
    
    def add(self, args):
        return Add(args[0], args[1])
    
    def sub(self, args):
        return Sub(args[0], args[1])
    
    def mul(self, args):
        return Mul(args[0], args[1])
    
    def div(self, args):
        return Div(args[0], args[1])
    
    def true_expr(self, args):
        return TrueExpr()
    
    def false_expr(self, args):
        return FalseExpr()
    
    def negative(self, args):
        return UnaryMinus(args[0])

    def procedure_call_stmt(self, args):
        # args[0] = NAME, args[1] = exprList
        return ProcedureCallStmt(args[0])

    def function_call_expr(self, args):
        return FunctionCallExpr(args[0], [] if len(args) == 1 else args[1])

    def string_expr(self, args):
        return StringExpr(args[0].strip("\""))
    
    def numeral(self, args):
        return Numeral(args[0])
    
    def name(self, args):
        return Name(args[0])
    
