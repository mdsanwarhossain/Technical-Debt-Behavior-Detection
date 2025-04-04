//package visitors;

package Designite.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ReturnStatement;

public class ReturnStatementsVisitor extends ASTVisitor {
    private List<ReturnStatement> returnStatements = new ArrayList<>();

    @Override
    public boolean visit(ReturnStatement node) {
        returnStatements.add(node);
        return true;
    }

    public List<ReturnStatement> getReturnStatements() {
        return returnStatements;
    }
}
