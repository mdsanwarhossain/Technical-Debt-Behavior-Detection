package Designite.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class VariableDeclarationVisitor extends ASTVisitor {
    private List<VariableDeclarationFragment> variableDeclarations = new ArrayList<>();

    @Override
    public boolean visit(VariableDeclarationFragment node) {
        variableDeclarations.add(node);
        return true;
    }

    public List<VariableDeclarationFragment> getVariableDeclarations() {
        return variableDeclarations;
    }
}