package ASTNode;

import ASTNode.Statement.Statement;

import java.util.List;

public class Program extends ASTNode {
    public List<Statement> statements;

    /**
     * creates a new program node
     */
    public Program(List<Statement> statements) {
        this.statements = statements;
    }
}
