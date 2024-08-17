package ASTNode;

import ASTNode.Statement.Statement;

import java.util.List;

public class Block extends ASTNode {
    public List<Statement> statements;

    /**
     * creates a new Block that contains a list of statements
     */
    public Block(List<Statement> statements) {
        this.statements = statements;
    }
}
