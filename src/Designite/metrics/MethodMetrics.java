package Designite.metrics;

import java.util.List;

import Designite.SourceModel.SM_Field;
import Designite.SourceModel.SM_Method;
import Designite.SourceModel.SM_Type;
//import java.util.HashSet;
//import java.util.Set;
//
//import org.eclipse.jdt.core.dom.ASTVisitor;
//import org.eclipse.jdt.core.dom.CompilationUnit;
//import org.eclipse.jdt.core.dom.FieldAccess;
//import org.eclipse.jdt.core.dom.IfStatement;
//import org.eclipse.jdt.core.dom.MethodDeclaration;
//import org.eclipse.jdt.core.dom.MethodInvocation;
//import org.eclipse.jdt.core.dom.SimpleName;
//import org.eclipse.jdt.core.dom.SwitchCase;
//import org.eclipse.jdt.core.dom.TypeDeclaration;
//import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
//import org.eclipse.jdt.core.dom.WhileStatement;
//import org.eclipse.jdt.core.dom.DoStatement;
//import org.eclipse.jdt.core.dom.EnhancedForStatement;
//import org.eclipse.jdt.core.dom.ForStatement;
//import org.eclipse.jdt.core.dom.CatchClause;
//
//import Designite.visitors.FieldAccessVisitor;
//import Designite.visitors.MethodInvocationVisitor;
//import Designite.visitors.VariableDeclarationVisitor;

public class MethodMetrics extends Metrics {

	private int numOfParameters;
	private int cyclomaticComplexity;
	private int numOfLines;
	private SM_Method method;

	public int getNumOfParameters() {
		return numOfParameters;
	}

	public int getCyclomaticComplexity() {
		return cyclomaticComplexity;
	}

	public int getNumOfLines() {
		return numOfLines;
	}

	public void setNumOfParameters(int numOfParameters) {
		this.numOfParameters = numOfParameters;
	}

	public void setCyclomaticComplexity(int cyclomaticComplexity) {
		this.cyclomaticComplexity = cyclomaticComplexity;
	}

	public void setNumOfLines(int numOfLines) {
		this.numOfLines = numOfLines;
	}

	public void setMethod(SM_Method method){
		this.method = method;
	}

	public SM_Method getMethod() {
		return method;
	}

	public List<SM_Field> getDirectFieldAccesses() {
		return method.getDirectFieldAccesses();
	}

	public List<SM_Type> getSMTypesInInstanceOf() {
		return method.getSMTypesInInstanceOf();
	}

//	private MethodDeclaration methodDeclaration;
//	private TypeDeclaration typeDeclaration;
//	private CompilationUnit compilationUnit;
//
//	private int loc;
//	private int cyclomaticComplexity;
//	private int parameterCount;
//	private int localVariableCount;
//	private int externalMethodCalls;
//	private int externalFieldAccesses;
//
//	public MethodMetrics(MethodDeclaration methodDeclaration, TypeDeclaration typeDeclaration, CompilationUnit compilationUnit) {
//		this.methodDeclaration = methodDeclaration;
//		this.typeDeclaration = typeDeclaration;
//		this.compilationUnit = compilationUnit;
//	}
//
//	public void calculate() {
//		calculateLOC();
//		calculateCyclomaticComplexity();
//		calculateParameterCount();
//		calculateLocalVariableCount();
//		calculateExternalMethodCalls();
//		calculateExternalFieldAccesses();
//	}
//
//	private void calculateLOC() {
//		// Get start and end positions from the AST
//		int startPosition = methodDeclaration.getStartPosition();
//		int endPosition = startPosition + methodDeclaration.getLength();
//
//		// Get the source code as a string
//		String source = compilationUnit.toString();
//
//		// Count the number of newlines in the method declaration
//		int count = 0;
//		for (int i = startPosition; i < endPosition; i++) {
//			if (source.charAt(i) == '\n') {
//				count++;
//			}
//		}
//
//		this.loc = count + 1; // +1 for the last line
//	}
//
//	private void calculateCyclomaticComplexity() {
//		// Start with 1 (for the method itself)
//		final int[] complexity = {1};
//
//		methodDeclaration.accept(new ASTVisitor() {
//			@Override
//			public boolean visit(IfStatement node) {
//				complexity[0]++;
//				return true;
//			}
//
//			@Override
//			public boolean visit(SwitchCase node) {
//				complexity[0]++;
//				return true;
//			}
//
//			@Override
//			public boolean visit(ForStatement node) {
//				complexity[0]++;
//				return true;
//			}
//
//			@Override
//			public boolean visit(EnhancedForStatement node) {
//				complexity[0]++;
//				return true;
//			}
//
//			@Override
//			public boolean visit(WhileStatement node) {
//				complexity[0]++;
//				return true;
//			}
//
//			@Override
//			public boolean visit(DoStatement node) {
//				complexity[0]++;
//				return true;
//			}
//
//			@Override
//			public boolean visit(CatchClause node) {
//				complexity[0]++;
//				return true;
//			}
//		});
//
//		this.cyclomaticComplexity = complexity[0];
//	}
//
//	private void calculateParameterCount() {
//		this.parameterCount = methodDeclaration.parameters().size();
//	}
//
//	private void calculateLocalVariableCount() {
//		VariableDeclarationVisitor visitor = new VariableDeclarationVisitor();
//		methodDeclaration.accept(visitor);
//		this.localVariableCount = visitor.getVariableDeclarations().size();
//	}
//
//	private void calculateExternalMethodCalls() {
//		MethodInvocationVisitor visitor = new MethodInvocationVisitor();
//		methodDeclaration.accept(visitor);
//
//		// Count method calls to other classes
//		int externalCalls = 0;
//		String thisClassName = typeDeclaration.getName().getIdentifier();
//
//		for (MethodInvocation invocation : visitor.getMethodInvocations()) {
//			if (invocation.getExpression() != null) {
//				// This is a method call on an object
//				// If it's not "this", count it as external
//				if (!(invocation.getExpression() instanceof SimpleName
//						&& ((SimpleName) invocation.getExpression()).getIdentifier().equals("this"))) {
//					externalCalls++;
//				}
//			}
//		}
//
//		this.externalMethodCalls = externalCalls;
//	}
//
//	private void calculateExternalFieldAccesses() {
//		FieldAccessVisitor visitor = new FieldAccessVisitor();
//		methodDeclaration.accept(visitor);
//
//		// Count field accesses to other classes
//		int externalAccesses = 0;
//
//		for (FieldAccess access : visitor.getFieldAccesses()) {
//			if (access.getExpression() != null) {
//				// This is a field access on an object
//				// If it's not "this", count it as external
//				if (!(access.getExpression() instanceof SimpleName
//						&& ((SimpleName) access.getExpression()).getIdentifier().equals("this"))) {
//					externalAccesses++;
//				}
//			}
//		}
//
//		this.externalFieldAccesses = externalAccesses;
//	}
//
//	public int getLOC() {
//		return loc;
//	}
//
//	public int getCyclomaticComplexity() {
//		return cyclomaticComplexity;
//	}
//
//	public int getParameterCount() {
//		return parameterCount;
//	}
//
//	public int getLocalVariableCount() {
//		return localVariableCount;
//	}
//
//	public int getExternalMethodCalls() {
//		return externalMethodCalls;
//	}
//
//	public int getExternalFieldAccesses() {
//		return externalFieldAccesses;
//	}

}
