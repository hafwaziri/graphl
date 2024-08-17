import sys
from lark import ast_utils
from ASTNodes import *
import ASTtransformer

typeEnv = {}
# NOTE: Graph, Edge, Node, etc. do not have their own ASTNode classes, 
# which means that we cannot check if the attribute or procedure being 
# referenced is an actual attribute or procedure of the class. This must 
# be done at runtime. Since custom classes and functions are not supported, 
# we just have a list of all predefined functions/methods and attributes, which is 
# much easier to set up. 
# Furthermore, Edges, Nodes and Graphs are all saved as "Variable", since the AST is not any
# more granular than that.

# NOTE on lists: The list name is saved as "List" in typeEnv, and "{listname}[]" is 
# saved as the type of element the list contains. So a = [1, 2, 3] results in "a": List 
# and "a[]": Numeral being stored.

attributeReturns = {
    "id": Numeral,
    "outgoingEdges": List,
    "ingoingEdges": List,
    "weight": Numeral,
    "from": Variable,
    "to": Variable,
    "neighbours": List,
    "edges": List,
    "nodes": List,
    "length": Numeral,
    "outgoingNodes": List,
    "ingoingNodes": List
}
procedureReturns = {
    "print": None,
    "random": Numeral,
    "Graph": Variable,
    "Edge": Variable,
    "Node": Variable,
    "addEdge": None,
    "addNode": None,
    "addEdges": None,
    "removeEdge": None,
    "removeNode": None,
    "getNode": Variable,
    "getEdge": Variable, 
    "swapDirection": None,
    "setEdgeWeight": None,
    "Edge": ["weight", "from", "to"],
    "Node": ["weight", "outgoing", "ingoing", "id", "neighbours"],
    "append": None,
    "remove": None,
    "appendAll": None,
    "copy": List, 
    "insert": None,
    "removeAt": None, 
    "findIngoingedge": Variable,
    "findOutgoingedge": Variable
}
classMethods = {
    "Graph": ["addEdge", "addNode", "addEdges", "removeEdge", "removeNode", "getNode", "getEdge", "setEdgeWeight", "swapDirection"],
    "Edge": ["swapDirection"],
    "Node": ["addEdge", "removeEdge", "findIngoingEdge", "findOutgoingedge", "addIngoing", "addOutgoing"]
}

def isUniformList(list : List):
    lastMemberType = None
    for member in list.children:
        if (not lastMemberType):
            lastMemberType = checkTypeRecursively(member)
            continue
        if (checkTypeRecursively(member) != lastMemberType):
            return False
    return True

def checkType(program : Program):
    for statement in program.children:
        checkTypeRecursively(statement) 

def resolveNodeType(node : ASTNode): # For resolving types of **custom** vars and lists? Attribute and Method types are taken care of in checkTypeRecursively
    if (issubclass(node.__class__, Name)): # Case 1: Variable --> return saved value
        if(node.value in typeEnv):
            return typeEnv[node.value]
    elif(node.__class__ == Assignment): # Case 2: New assigment --> check if the variable already exists and if the assignment is allowed
        if(node.target.__class__ == Variable):
            if (node.target.name.value in typeEnv):
                return typeEnv[node.target.name.value]
        elif(node.target.__class__ == ListAccess):
            if (node.target.target.name.value + "[]" in typeEnv):
                return typeEnv[node.target.target.name.value + "[]"]
    elif(node.__class__ == ListAccess): # Case 3: List access --> if index is a variable, resolve it
        if(node.children[1].__class__ in [Numeral, Add, Sub, Mul, Div]): # Number value
            if((node.children[0].name.value + "[]") in typeEnv): 
                return typeEnv[node.children[0].name.value + "[]"] 
            elif(node.children[0].__class__ == Attribute):
                if((node.children[0].name.value) in attributeReturns):
                    return Variable # Cannot go any more granular
        elif(node.children[1].__class__ == Variable):
            if((node.children[0].name.value + "[]") in typeEnv):
                membertype = typeEnv[node.children[0].name.value.value + "[]"] 
                if(membertype):
                    return typeEnv[node.children[0].name.value.value + "[]"] 
                #else: # Member type = None means that it must be an 
            elif(node.children[0].name.value in attributeReturns):
                return Variable
        elif(node.children[1].__class__ == Attribute):
            if(node.children[1].name.value in attributeReturns):
                return attributeReturns[node.children[1].name.value]
    # Resolve types of known procedures
    elif(node.__class__ == MethodCallExpr):
        if(node.functionCallExpr.name.value in procedureReturns):
            return procedureReturns[node.functionCallExpr.name.value]
    elif(node.__class__ == FunctionCallExpr):
        if(node.name.value in procedureReturns):
            return procedureReturns[node.name.value]
    elif(issubclass(node.__class__, LogicalExpr)):
        return LogicalExpr
    # If nothing is found: 
    return None

def checkTypeRecursively(node : ASTNode): 
    # At the bottom 
    if len(node.children) == 0:
        try:
            lookupResult = resolveNodeType(node)
            if (lookupResult): 
                return lookupResult
            else:
                return node.__class__
        except:
            raise Exception(repr(node) + "does not resolve to a constant.")
    
    # Not yet at the bottom 
    childTypes = []
    for child in node.children:
        childTypes.append(checkTypeRecursively(child))

    # Variable and Assignment
    if(node.__class__ == Variable):
        return childTypes[0]
    elif(node.__class__ == Assignment):
        lookupResult = resolveNodeType(node)
        if (lookupResult and lookupResult != childTypes[1]): 
            raise Exception("Variable type cannot be changed after initialization!")
        if (node.target.__class__ == Variable):
            typeEnv[node.target.name.value] = childTypes[1]
        elif (node.target.__class__ == ListAccess):
            if(node.target.target.name.value + "[]" in typeEnv):
                if(typeEnv[node.target.target.name.value + "[]"] != childTypes[1]):
                    raise Exception("Cannot change list member type after initialization.")
            typeEnv[node.target.target.name.value + "[]"] = childTypes[1]
        if (childTypes[1] == List): # Direct list assignment: add list member type
            # Account for different "list types" (assign list member type separately)
            if (len(node.assigned.children) == 0): # Creating empty list
                typeEnv[node.target.name.value + "[]"] = None
            else: # List with at least 1 element
                # If members are referenced via list access
                if (node.assigned.children[0].__class__ == ListAccess): # Only checking first child (for now)
                    lookupResult = resolveNodeType(node.assigned.children[0]) # Get list member type
                    if (lookupResult): 
                        if(node.target.name.value + "[]" in typeEnv and lookupResult != typeEnv[node.target.name.value + "[]"]):
                            raise Exception("Cannot reassign list member to a different type.")
                        typeEnv[node.target.name.value + "[]"] = lookupResult
                    else:
                        raise Exception("Cannot resolve list member type when creating new list. Does the list being referenced exist?")
                # If members are referenced via procedure call
                elif (issubclass(node.assigned.children[0].__class__, ProcedureCallExpr)):
                    lookupResult = resolveNodeType(node.assigned.children[0])
                    if (lookupResult): 
                        if(node.target.name.value + "[]" in typeEnv and lookupResult != typeEnv[node.target.name.value + "[]"]):
                            raise Exception("Cannot reassign list member to a different type.")
                        typeEnv[node.target.name.value + "[]"] = lookupResult
                    else:
                        raise Exception("Cannot resolve procedure return type when creating new list. Does the procedure exist?")
                elif(issubclass(node.assigned.children[0].__class__, LogicalExpr)):
                    typeEnv[node.target.name.value + "[]"] = LogicalExpr
                elif(node.assigned.children[0].__class__ in [Numeral, Add, Sub, Mul, Div]):
                    typeEnv[node.target.name.value + "[]"] = Numeral
                else:
                    typeEnv[node.target.name.value + "[]"] = node.assigned.children[0].__class__ # This line used to be children[1]. Was there a reason for this?

            # Via attribute
            if(node.assigned.__class__ == Attribute):
                if (node.assigned.name.value not in attributeReturns): 
                    raise Exception("Attribute does not exist")
                typeEnv[node.target.name.value + "[]"] = Variable
        return node.__class__
    elif(node.__class__ == ListAccess):
        lookupResult = resolveNodeType(node)
        if(lookupResult):
            return lookupResult
        raise Exception(node.children[0].name.value + "[" + str(node.children[1].value) + "]" + " does not exist.") 
                
    # Math expressions
    elif(issubclass(node.__class__, BinaryExpr)): # All operators: all child types must be the same
        
        if (issubclass(node.__class__, Comparison)):
            if (all(issubclass(child, Numeral) for child in childTypes) or all(issubclass(child, LogicalExpr) for child in childTypes)):
                return LogicalExpr
            else: 
                raise Exception(repr(childTypes) + " have incompatible types.") 
        else: # Numeral
            if (all(issubclass(child, Numeral) for child in childTypes)):
                return Numeral
            else: 
                raise Exception(repr(childTypes) + " have incompatible types.") 
            
    # Logic expressions
    elif (issubclass(node.__class__, LogicalExpr) or issubclass(node.__class__, Not)):
        if (all(issubclass(child.__class__, LogicalExpr)) for child in childTypes):
            return LogicalExpr
        else: 
            raise Exception(repr(childTypes) + " have incompatible types.") 
    
    # Control flow (if, while, etc.)
    elif (node.__class__ in [IfStatement, ElifStmt, WhileStmt, ForeachStmt]):
        if(node.__class__ == ForeachStmt):
            if(len(node.children) == 3):
                # NOTE: Evtl. sinnvoller mit childtypes oder issubclass(), die resolved werden
                if(childTypes[0] in [Name, Variable] and issubclass(childTypes[1], ListOrTarget) and childTypes[2] == Block):
                    return node.__class__
            raise Exception("Foreach statement does not have proper types.")
        if(issubclass(childTypes[0], LogicalExpr)):
            return node.__class__
        else:
            raise Exception("Non-boolean expression used as condition for " + repr(node.__class__))

    # Method calls and Attribute access
    elif (node.__class__ == FunctionCallExpr): # Method/Function call --> return procedure return type
        if (node.name.value not in procedureReturns):
            raise Exception("Unknown method/function : " + repr(node.name.value))
        # Look up return type
        if (procedureReturns[node.name.value] != None): 
            return procedureReturns[node.name.value]
        elif(node.name.value in ["append", "appendAll", "insert"]): # Else, return node.__class__ 
            return childTypes[1]
    elif (node.__class__ == MethodCallExpr):
        if(node.functionCallExpr.name.value not in procedureReturns): # Unknown method
            raise Exception("Unknown method/function : " + repr(node.functionCallExpr.name.value))
        if(node.functionCallExpr.name.value in ["append", "appendAll", "insert"] and node.target.name.value + "[]" in typeEnv): # Initializing empty list
            if(typeEnv[node.target.name.value + "[]"] == None): # Method is valid, list member is in typEnv but has no type.
                if(issubclass(node.functionCallExpr.children[1].__class__, LogicalExpr)):
                    typeEnv[node.target.name.value + "[]"] = LogicalExpr
                else:
                    typeEnv[node.target.name.value + "[]"] = node.functionCallExpr.children[1].__class__ 
            elif(typeEnv[node.target.name.value + "[]"] != childTypes[1]):
                raise Exception("Can't change list element to a different type")
        # Look up return type
        if (procedureReturns[node.functionCallExpr.name.value] != None): # Else, return node class 
            return procedureReturns[node.functionCallExpr.name.value]
    elif (issubclass(node.__class__, Attribute)): # Attribute access --> return attribute type
        if (node.name.value not in attributeReturns):
            raise Exception("Unknown attribute : " + repr(node.name.value))
        if (attributeReturns[node.name.value] != None): # Else, return node class 
            return attributeReturns[node.name.value]
    elif(node.__class__ == UnaryMinus):
        if (childTypes[0] == Numeral):
            return Numeral
    return node.__class__