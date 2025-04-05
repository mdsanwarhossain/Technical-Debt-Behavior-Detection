package Designite.smells.implementationSmells;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.*;

import Designite.SourceModel.SM_Field;
import Designite.SourceModel.SM_LocalVar;
import Designite.SourceModel.SM_Method;
import Designite.SourceModel.SM_Parameter;
import Designite.SourceModel.SourceItemInfo;
import Designite.metrics.MethodMetrics;
import Designite.smells.ThresholdsDTO;
import Designite.smells.models.ImplementationCodeSmell;
import Designite.utils.Logger;
import Designite.visitors.MethodControlFlowVisitor;
import Designite.visitors.NumberLiteralVisitor;
import Designite.visitors.ReturnStatementsVisitor;
import Designite.visitors.VariableDeclarationVisitor;

public class ImplementationSmellDetector {

	private List<ImplementationCodeSmell> smells;

	private MethodMetrics methodMetrics;
	private SourceItemInfo info;
	private ThresholdsDTO thresholdsDTO;

	private static final String ABST_FUNC_CALL_FRM_CTOR = "Abstract Function Call From Constructor";
	private static final String COMPLEX_CONDITIONAL = "Complex Conditional";
	private static final String COMPLEX_METHOD = "Complex Method";
	private static final String EMPTY_CATCH_CLAUSE = "Empty catch clause";
	private static final String LONG_IDENTIFIER = "Long Identifier";
	private static final String LONG_METHOD = "Long Method";
	private static final String LONG_PARAMETER_LIST = "Long Parameter List";
	private static final String LONG_STATEMENT = "Long Statement";
	private static final String MAGIC_NUMBER = "Magic Number";
	private static final String MISSING_DEFAULT = "Missing default";

	// New implementation smells
	private static final String MULTIPLE_RETURN_STATEMENTS = "Multiple Return Statements";
	private static final String NESTED_TRY = "Nested Try";
	private static final String DEEPLY_NESTED_BLOCK = "Deeply Nested Block";
	private static final String REDUNDANT_VARIABLE = "Redundant Variable";
	private static final String UNUSED_PARAMETER = "Unused Parameter";
	private static final String EXCESSIVE_COMMENTS = "Excessive Comments";
	private static final String DUPLICATE_CODE_FRAGMENT = "Duplicate Code Fragment";

	private static final String AND_OPERATOR_REGEX = "\\&\\&";
	private static final String OR_OPERATOR_REGEX = "\\|\\|";
	private static final Pattern EMPTY_BODY_PATTERN = Pattern.compile("^\\{\\s*\\}\\s*$");

	public ImplementationSmellDetector(MethodMetrics methodMetrics, SourceItemInfo info) {
		this.methodMetrics = methodMetrics;
		this.info = info;

		thresholdsDTO = new ThresholdsDTO();
		smells = new ArrayList<>();
	}

	public List<ImplementationCodeSmell> detectCodeSmells() {
		detectAbstractFunctionCallFromConstructor();
		detectComplexConditional();
		detectComplexMethod();
		detectEmptyCatchBlock();
		detectLongIdentifier();
		detectLongMethod();
		detectLongParameterList();
		detectLongStatement();
		detectMagicNumber();
		detectMissingDefault();

		// New smell detection methods
		detectMultipleReturnStatements();
		detectNestedTry();
		detectDeeplyNestedBlock();
		detectRedundantVariable();
		detectUnusedParameter();
		detectExcessiveComments();
		detectDuplicateCodeFragment();

		return smells;
	}

	public List<ImplementationCodeSmell> detectAbstractFunctionCallFromConstructor() {
		if (hasAbstractFunctionCallFromConstructor()) {
			addToSmells(initializeCodeSmell(ABST_FUNC_CALL_FRM_CTOR));
		}
		return smells;
	}

	private boolean hasAbstractFunctionCallFromConstructor() {
		SM_Method method = methodMetrics.getMethod();
		if (method.isConstructor()) {
			for (SM_Method calledMethod : method.getCalledMethods()) {
				if (calledMethod.isAbstract()) {
					return true;
				}
			}
		}
		return false;
	}

	public List<ImplementationCodeSmell> detectComplexConditional() {
		hasComplexConditional();
		return smells;
	}

	private void hasComplexConditional() {
		MethodControlFlowVisitor visitor = new MethodControlFlowVisitor();
		methodMetrics.getMethod().getMethodDeclaration().accept(visitor);
		for (IfStatement ifStatement : visitor.getIfStatements()) {
			if (numOfBooleanSubExpressions(ifStatement) >=  thresholdsDTO.getComplexCondition()) {
				addToSmells(initializeCodeSmell(COMPLEX_CONDITIONAL));
			}
		}
	}

	private String getBooleaRegex() {
		return AND_OPERATOR_REGEX + "|" + OR_OPERATOR_REGEX;
	}

	private int numOfBooleanSubExpressions(IfStatement ifStatement) {
		return ifStatement.getExpression().toString().split(getBooleaRegex()).length;
	}

	public List<ImplementationCodeSmell> detectComplexMethod() {
		if (hasComplexMethod()) {
			addToSmells(initializeCodeSmell(COMPLEX_METHOD));
		}
		return smells;
	}

	private boolean hasComplexMethod() {
		return methodMetrics.getCyclomaticComplexity() >= thresholdsDTO.getComplexMethod();
	}

	public List<ImplementationCodeSmell> detectEmptyCatchBlock() {
		MethodControlFlowVisitor visitor = new MethodControlFlowVisitor();
		methodMetrics.getMethod().getMethodDeclaration().accept(visitor);
		for (TryStatement tryStatement : visitor.getTryStatements()) {
			for (Object catchClause : tryStatement.catchClauses()) {
				if (!hasBody((CatchClause) catchClause)) {
					addToSmells(initializeCodeSmell(EMPTY_CATCH_CLAUSE));
				}
			}
		}
		return smells;
	}

	public boolean hasBody(CatchClause catchClause) {
		String body = catchClause.getBody().toString();
		return !EMPTY_BODY_PATTERN.matcher(body).matches();
	}

	public List<ImplementationCodeSmell> detectLongIdentifier() {
		if (hasLongIdentifier()) {
			addToSmells(initializeCodeSmell(LONG_IDENTIFIER));
		}
		return smells;
	}

	private boolean hasLongIdentifier() {
		return hasLongParameter() || hasLongLocalVar() || hasLongFieldAccess();
	}

	private boolean hasLongParameter() {
		for (SM_Parameter parameter : methodMetrics.getMethod().getParameterList()) {
			if (parameter.getName().length() >= thresholdsDTO.getLongIdentifier()) {
				return true;
			}
		}
		return false;
	}

	private boolean hasLongLocalVar() {
		for (SM_LocalVar var : methodMetrics.getMethod().getLocalVarList()) {
			if (var.getName().length() >= thresholdsDTO.getLongIdentifier()) {
				return true;
			}
		}
		return false;
	}

	private boolean hasLongFieldAccess() {
		for (SM_Field field : methodMetrics.getMethod().getDirectFieldAccesses()) {
			if (field.getName().length() >= thresholdsDTO.getLongIdentifier()) {
				return true;
			}
		}
		return false;
	}

	public List<ImplementationCodeSmell> detectLongMethod() {
		if (hasLongMethod()) {
			addToSmells(initializeCodeSmell(LONG_METHOD));
		}
		return smells;
	}

	private boolean hasLongMethod() {
		return methodMetrics.getNumOfLines() >= thresholdsDTO.getLongMethod();
	}

	public List<ImplementationCodeSmell> detectLongParameterList() {
		if (hasLongParameterList()) {
			addToSmells(initializeCodeSmell(LONG_PARAMETER_LIST));
		}
		return smells;
	}

	private boolean hasLongParameterList() {
		return methodMetrics.getNumOfParameters() >= thresholdsDTO.getLongParameterList();
	}

	public List<ImplementationCodeSmell> detectLongStatement() {
		SM_Method currentMethod = methodMetrics.getMethod();
		if(currentMethod.hasBody()) {
			String methodBody = currentMethod.getMethodBody();
			hasLongStatement(methodBody);
		}

		return smells;
	}

	private void hasLongStatement(String methodBody) {
		//FIXME is there another non-hard-coded to replace the "\n"
		String[] methodStatements = methodBody.split("\n");

		for(String singleMethodStatement : methodStatements) {
			singleMethodStatement = singleMethodStatement.trim().replaceAll("\\s+", " ");
			if(isLongStatement(singleMethodStatement)) {
				addToSmells(initializeCodeSmell(LONG_STATEMENT));
			}
		}
	}

	private boolean isLongStatement(String statement) {
		return statement.length() > this.thresholdsDTO.getLongStatement();
	}

	public List<ImplementationCodeSmell> detectMagicNumber() {
		hasMagicNumbers();
		return smells;
	}

	private void hasMagicNumbers() {
		NumberLiteralVisitor visitor = new NumberLiteralVisitor();
		methodMetrics.getMethod().getMethodDeclaration().accept(visitor);
		List<NumberLiteral> literals = visitor.getNumberLiteralsExpressions();

		if( literals.size() < 1 ) {
			return;
		}

		for(NumberLiteral singleNumberLiteral : literals) {
			if( isLiteralValid(singleNumberLiteral) ) {
				addToSmells(initializeCodeSmell(MAGIC_NUMBER));
			}
		}
	}

	private boolean isLiteralValid(NumberLiteral singleNumberLiteral) {
		boolean isValid = isNotZeroOrOne(singleNumberLiteral) && isNotArrayInitialization(singleNumberLiteral);
		return isValid;
	}

	// 0s and 1s are not considered as Magic Numbers
	private boolean isNotZeroOrOne(NumberLiteral singleNumberLiteral) {
		String numberToString = singleNumberLiteral.toString().toLowerCase().replaceAll("_", "");
		double literalValue = 0.0;
		try {
			// hex case
			if(numberToString.startsWith("0x")) {
				literalValue = (double)(Long.parseLong(numberToString.replaceAll("0x", "").replaceAll("l", ""),16));
				// long case
			} else if(numberToString.endsWith("l")) {
				literalValue = (double)(Long.parseLong(numberToString.replaceAll("l", "")));
				// float case
			} else if(numberToString.endsWith("f")) {
				literalValue = Float.parseFloat(numberToString.replaceAll("f", ""));
			}
			// double case
			else {
				literalValue = Double.parseDouble(numberToString);
			}
		} catch (NumberFormatException ex) {
			String logMessage = "Exception while parsing literal number (during Magic Number detection). " + ex.getMessage();
			Logger.log(logMessage);
			literalValue = 0.0;
		}
		return literalValue != 0.0 && literalValue != 1.0;
	}


	// Literals in array initializations (such as int[] arr = {0,1};) are not considered as Magic Numbers
	private boolean isNotArrayInitialization(NumberLiteral singleNumberLiteral) {
		return singleNumberLiteral.getParent().getNodeType() != ASTNode.ARRAY_INITIALIZER;
	}

	public List<ImplementationCodeSmell> detectMissingDefault() {
		hasMissingDefaults();
		return smells;
	}

	private void hasMissingDefaults() {
		MethodControlFlowVisitor visitor = new MethodControlFlowVisitor();
		methodMetrics.getMethod().getMethodDeclaration().accept(visitor);
		List<SwitchStatement> switchStatements = visitor.getSwitchStatements();
		for(SwitchStatement singleSwitchStatement : switchStatements) {
			if(switchIsMissingDefault(singleSwitchStatement)) {
				addToSmells(initializeCodeSmell(MISSING_DEFAULT));
			}
		}
	}

	private boolean switchIsMissingDefault(SwitchStatement switchStatement) {
		List<Statement> statetmentsOfSwitch = switchStatement.statements();
		for(Statement stm : statetmentsOfSwitch) {
			if ((stm instanceof SwitchCase) && ((SwitchCase)stm).isDefault()) {
				return false;
			}
		}
		return true;
	}

	// New implementation smell detection methods

	/**
	 * Detects if a method has multiple return statements, which can make the code harder to follow
	 * and maintain. Methods with a single entry and exit point are generally easier to understand.
	 */
	public List<ImplementationCodeSmell> detectMultipleReturnStatements() {
		SM_Method method = methodMetrics.getMethod();
		if (method.hasBody()) {
			ReturnStatementsVisitor visitor = new ReturnStatementsVisitor();
			method.getMethodDeclaration().accept(visitor);

			// Consider it a smell if there are more than 1 return statements
			if (visitor.getReturnStatements().size() > 1) {
				addToSmells(initializeCodeSmell(MULTIPLE_RETURN_STATEMENTS));
			}
		}
		return smells;
	}

	/**
	 * Detects nested try blocks, which can make error handling complex and difficult to understand.
	 */
	public List<ImplementationCodeSmell> detectNestedTry() {
		SM_Method method = methodMetrics.getMethod();
		if (method.hasBody()) {
			MethodControlFlowVisitor visitor = new MethodControlFlowVisitor();
			method.getMethodDeclaration().accept(visitor);

			for (TryStatement tryStatement : visitor.getTryStatements()) {
				if (hasNestedTry(tryStatement)) {
					addToSmells(initializeCodeSmell(NESTED_TRY));
					break; // One detection is enough
				}
			}
		}
		return smells;
	}

	private boolean hasNestedTry(TryStatement tryStatement) {
		// Create a visitor to find nested try statements
		class NestedTryVisitor extends ASTVisitor {
			private boolean hasNestedTry = false;

			@Override
			public boolean visit(TryStatement node) {
				// Skip the root try statement
				if (node != tryStatement) {
					hasNestedTry = true;
					return false; // No need to go deeper once found
				}
				return true;
			}

			public boolean hasNestedTry() {
				return hasNestedTry;
			}
		}

		NestedTryVisitor nestedVisitor = new NestedTryVisitor();
		tryStatement.getBody().accept(nestedVisitor);

		return nestedVisitor.hasNestedTry();
	}

	/**
	 * Detects deeply nested blocks (if, for, while, etc.) which can make code hard to read and maintain.
	 * Deep nesting is a sign that the method might be doing too much or could be refactored.
	 */
	public List<ImplementationCodeSmell> detectDeeplyNestedBlock() {
		SM_Method method = methodMetrics.getMethod();
		if (method.hasBody()) {
			// Define a threshold for deep nesting (e.g., more than 3 levels)
			final int DEEP_NESTING_THRESHOLD = 3;

			// Create a visitor to calculate nesting depth
			class NestingDepthVisitor extends ASTVisitor {
				private int currentDepth = 0;
				private int maxDepth = 0;

				private void increaseDepth() {
					currentDepth++;
					maxDepth = Math.max(maxDepth, currentDepth);
				}

				private void decreaseDepth() {
					currentDepth--;
				}

				@Override
				public boolean visit(Block node) {
					// Only count blocks that are part of control structures
					if (isControlStructureBlock(node)) {
						increaseDepth();
					}
					return true;
				}

				@Override
				public void endVisit(Block node) {
					if (isControlStructureBlock(node)) {
						decreaseDepth();
					}
				}

				private boolean isControlStructureBlock(Block node) {
					ASTNode parent = node.getParent();
					return parent instanceof IfStatement ||
							parent instanceof ForStatement ||
							parent instanceof WhileStatement ||
							parent instanceof DoStatement ||
							parent instanceof EnhancedForStatement;
				}

				public int getMaxDepth() {
					return maxDepth;
				}
			}

			NestingDepthVisitor depthVisitor = new NestingDepthVisitor();
			method.getMethodDeclaration().accept(depthVisitor);

			if (depthVisitor.getMaxDepth() > DEEP_NESTING_THRESHOLD) {
				addToSmells(initializeCodeSmell(DEEPLY_NESTED_BLOCK));
			}
		}
		return smells;
	}

	/**
	 * Detects redundant variables that are used only once immediately after declaration.
	 * These variables don't add value and can be replaced with direct expressions.
	 */
	public List<ImplementationCodeSmell> detectRedundantVariable() {
		SM_Method method = methodMetrics.getMethod();
		if (method.hasBody()) {
			VariableDeclarationVisitor visitor = new VariableDeclarationVisitor();
			method.getMethodDeclaration().accept(visitor);

			for (VariableDeclarationFragment varDecl : visitor.getVariableDeclarations()) {
				if (isRedundantVariable(varDecl, method.getMethodDeclaration())) {
					addToSmells(initializeCodeSmell(REDUNDANT_VARIABLE));
					break; // One detection is enough
				}
			}
		}
		return smells;
	}

	private boolean isRedundantVariable(VariableDeclarationFragment varDecl, MethodDeclaration methodDecl) {
		// A variable is considered redundant if:
		// 1. It's initialized with a value (not just declared)
		// 2. It's used exactly once
		// 3. It's used in the next statement after declaration

		if (varDecl.getInitializer() == null) {
			return false; // Not initialized
		}

		final String varName = varDecl.getName().getIdentifier();
		final int[] useCount = {0};
		final boolean[] isRedundant = {false};

		// Find the statement containing the variable declaration
		final Statement[] declarationStmt = {null};
		methodDecl.accept(new ASTVisitor() {
			@Override
			public boolean visit(VariableDeclarationStatement node) {
				for (Object fragment : node.fragments()) {
					if (fragment == varDecl) {
						declarationStmt[0] = node;
						return false;
					}
				}
				return true;
			}
		});

		if (declarationStmt[0] == null) {
			return false; // Declaration statement not found
		}

		// Find the parent block to check statement order
		Block parentBlock = null;
		ASTNode parent = declarationStmt[0].getParent();
		while (parent != null) {
			if (parent instanceof Block) {
				parentBlock = (Block) parent;
				break;
			}
			parent = parent.getParent();
		}

		if (parentBlock == null) {
			return false; // Parent block not found
		}

		// Get the list of statements in the block
		List<Statement> statements = parentBlock.statements();
		int declIndex = statements.indexOf(declarationStmt[0]);

		if (declIndex == -1 || declIndex >= statements.size() - 1) {
			return false; // Declaration not found or is the last statement
		}

		// Check the next statement for variable usage
		Statement nextStmt = statements.get(declIndex + 1);

		// Count variable uses in the next statement
		nextStmt.accept(new ASTVisitor() {
			@Override
			public boolean visit(SimpleName node) {
				if (node.getIdentifier().equals(varName)) {
					useCount[0]++;
				}
				return true;
			}
		});

		// Check if the variable is used only in the next statement and nowhere else
		methodDecl.accept(new ASTVisitor() {
			@Override
			public boolean visit(SimpleName node) {
				if (node.getIdentifier().equals(varName) &&
						!isInSubtree(node, nextStmt) &&
						!isInSubtree(node, declarationStmt[0])) {
					useCount[0]++;
				}
				return true;
			}

			private boolean isInSubtree(ASTNode node, ASTNode root) {
				ASTNode parent = node;
				while (parent != null) {
					if (parent == root) {
						return true;
					}
					parent = parent.getParent();
				}
				return false;
			}
		});

		// Variable is redundant if it's used exactly once in the next statement
		return useCount[0] == 1;
	}

	/**
	 * Detects unused parameters in methods, which can confuse readers and indicate poor design.
	 */
	public List<ImplementationCodeSmell> detectUnusedParameter() {
		SM_Method method = methodMetrics.getMethod();
		if (method.hasBody() && !method.getParameterList().isEmpty()) {
			MethodDeclaration methodDecl = method.getMethodDeclaration();

			// Get all parameter names
			final Set<String> parameterNames = new HashSet<>();
			for (SM_Parameter param : method.getParameterList()) {
				parameterNames.add(param.getName());
			}

			// Check which parameters are used in the method body
			final Set<String> usedParameters = new HashSet<>();
			methodDecl.getBody().accept(new ASTVisitor() {
				@Override
				public boolean visit(SimpleName node) {
					String name = node.getIdentifier();
					if (parameterNames.contains(name)) {
						usedParameters.add(name);
					}
					return true;
				}
			});

			// If any parameter is unused, report the smell
			if (usedParameters.size() < parameterNames.size()) {
				addToSmells(initializeCodeSmell(UNUSED_PARAMETER));
			}
		}
		return smells;
	}

	/**
	 * Detects excessive comments in methods, which might indicate overly complex code
	 * that needs to be explained rather than being self-explanatory.
	 */
	public List<ImplementationCodeSmell> detectExcessiveComments() {
		SM_Method method = methodMetrics.getMethod();
		if (method.hasBody()) {
			// Define a threshold for comment-to-code ratio
			final double EXCESSIVE_COMMENT_RATIO = 0.4; // 40% or more comments is excessive

			// Count lines of code and comment lines
			String methodBody = method.getMethodBody();
			int totalLines = 0;
			int commentLines = 0;

			boolean inBlockComment = false;
			for (String line : methodBody.split("\n")) {
				line = line.trim();
				if (line.isEmpty()) {
					continue; // Skip empty lines
				}

				totalLines++;

				if (inBlockComment) {
					commentLines++;
					if (line.contains("*/")) {
						inBlockComment = false;
					}
				} else if (line.startsWith("//")) {
					commentLines++;
				} else if (line.startsWith("/*")) {
					commentLines++;
					if (!line.contains("*/")) {
						inBlockComment = true;
					}
				} else if (line.contains("/*") && !line.contains("*/")) {
					commentLines++;
					inBlockComment = true;
				}
			}

			// Calculate comment ratio
			if (totalLines > 0) {
				double commentRatio = (double) commentLines / totalLines;
				if (commentRatio >= EXCESSIVE_COMMENT_RATIO && commentLines > 3) {
					// Only consider it excessive if there are more than 3 comment lines
					addToSmells(initializeCodeSmell(EXCESSIVE_COMMENTS));
				}
			}
		}
		return smells;
	}

	/**
	 * Detects duplicate code fragments within a method, which indicates a need for refactoring.
	 */
	public List<ImplementationCodeSmell> detectDuplicateCodeFragment() {
		SM_Method method = methodMetrics.getMethod();
		if (method.hasBody()) {
			// Define minimum size for a code fragment to be considered for duplication
			final int MIN_FRAGMENT_SIZE = 3; // Minimum 3 statements

			// Get all statements in the method
			final List<Statement> allStatements = new ArrayList<>();
			method.getMethodDeclaration().accept(new ASTVisitor() {
				@Override
				public boolean visit(ExpressionStatement node) {
					allStatements.add(node);
					return true;
				}

				@Override
				public boolean visit(VariableDeclarationStatement node) {
					allStatements.add(node);
					return true;
				}

				@Override
				public boolean visit(ReturnStatement node) {
					allStatements.add(node);
					return true;
				}
			});

			// Check for duplicate sequences of statements
			if (allStatements.size() >= MIN_FRAGMENT_SIZE * 2) { // Need at least 2 fragments
				for (int i = 0; i <= allStatements.size() - MIN_FRAGMENT_SIZE * 2; i++) {
					for (int j = i + MIN_FRAGMENT_SIZE; j <= allStatements.size() - MIN_FRAGMENT_SIZE; j++) {
						boolean isDuplicate = true;
						for (int k = 0; k < MIN_FRAGMENT_SIZE; k++) {
							Statement s1 = allStatements.get(i + k);
							Statement s2 = allStatements.get(j + k);

							// Compare statements by their string representation
							// This is a simple approach; more sophisticated approaches could be used
							if (!s1.toString().equals(s2.toString())) {
								isDuplicate = false;
								break;
							}
						}

						if (isDuplicate) {
							addToSmells(initializeCodeSmell(DUPLICATE_CODE_FRAGMENT));
							return smells; // One detection is enough
						}
					}
				}
			}
		}
		return smells;
	}

	public List<ImplementationCodeSmell> getSmells() {
		return smells;
	}

	private ImplementationCodeSmell initializeCodeSmell(String smellName) {
		return new ImplementationCodeSmell(info.getProjectName()
				, info.getPackageName()
				, info.getTypeName()
				, info.getMethodName()
				, smellName);
	}

	private void addToSmells(ImplementationCodeSmell smell) {
		smells.add(smell);
	}
}