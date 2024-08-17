from ASTNodes import *

""""

Traverses the AST (Top Down). It keeps track of the variables and functions declared in a symbol table.
The symbol table is a list of dictionaries. Each dictionary represents a scope. The last element of the list is the current scope.

New scopes for Program and Block nodes.

When visiting an Assignment node, it checks if the target is a Variable node and not a Function name. If it is, it adds the variable to the current scope.

When visiting a Variable node, it checks if the variable is in the symbol table.

When visiting a FunctionCallExpr node, it checks if the function is in the symbol table.

*Better to implement the symbol table/scope as a separate class - Will be done later.

"""

class NameAnalysisVisitor:
    
    def __init__(self):
        self.symbol_table = [{'variables': set(), 'functions': set()}]
        self.standard_functions = {"print"}
        
    def create_new_scope(self):
        self.symbol_table.append({'variables': set(), 'functions': set()})
        
    def visit(self, node):
        method_name = f"visit_{type(node).__name__}"
        visit_method = getattr(self, method_name, self.generic_visit)
        return visit_method(node)
    
    def generic_visit(self, node):
        for child in node.children:
            self.visit(child)
    
    def visit_Program(self, node):
        self.create_new_scope()
        for statement in node.statements:
            self.visit(statement)
        self.symbol_table.pop()

    
    def visit_Block(self, node):
        self.create_new_scope()
        for statement in node.statements:
            self.visit(statement)
        self.symbol_table.pop()
    
    def visit_Assignment(self, node):
        if isinstance(node.target, Variable):
            self.visit(node.assigned)
            if node.target.name.value not in self.symbol_table[-1]['variables']: 
                self.symbol_table[-1]['variables'].add(node.target.name.value)
            self.visit(node.target)
        else:
            self.visit(node.assigned)
            self.visit(node.target)

    def visit_Variable(self, node):
        var_name = node.name.value
        if not any(var_name in scope['variables'] for scope in reversed(self.symbol_table)) and var_name not in self.standard_functions:
            raise Exception(f"Variable '{var_name}' used before declaration or out of scope.")
        
    # def visit_FunctionCallExpr(self, node):
    #     func_name = node.name.value
    #     if not any(func_name in scope['functions'] for scope in reversed(self.symbol_table)) and func_name not in self.standard_functions:
    #         raise Exception(f"Function '{func_name}' used before definition")
    #     for argument in node.arguments:
    #         self.visit(argument)