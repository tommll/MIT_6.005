/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import expressivo.parser.ExpressionListener;
import expressivo.parser.ExpressionParser;
import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * String-based commands provided by the expression system.
 * 
 * <p>PS3 instructions: this is a required class.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You MUST NOT add fields, constructors, or instance methods.
 * You may, however, add additional static methods, or strengthen the specs of existing methods.
 */
public class Commands {
    
    /**
     * Differentiate an expression with respect to a variable.
     * @param expression the expression to differentiate
     * @param variable the variable to differentiate by, a case-sensitive nonempty string of letters.
     * @return expression's derivative with respect to variable.  Must be a valid expression equal
     *         to the derivative, but doesn't need to be in simplest or canonical form.
     * @throws IllegalArgumentException if the expression or variable is invalid
     */
    public static String differentiate(String expression, String variable) {
        CharStream stream = new ANTLRInputStream(expression);

        ExpressionParser parser = Expression.makeParser(stream);
        ParseTree tree = parser.root();

        // *** Debugging option #1: print the tree to the console
        //System.err.println(tree.toStringTree(parser));

        // *** Debugging option #2: show the tree in a window
        //Trees.inspect(tree, parser);

        // *** Debugging option #3: walk the tree with a listener
        //new ParseTreeWalker().walk(new Expression.PrintEverything(), tree);

        DifferentiatedExpressionListener exprListener = new DifferentiatedExpressionListener(variable);
        new ParseTreeWalker().walk(exprListener, tree);
        return exprListener.getExpression().toString();
    }

    /**
    A listener class that traverse the expression parse tree to evaluate to
     a mathematically differentiated version of the current expression with respect to a variable.
     (this is a listener in the Visitor pattern)

     All grammar rules that this class obey can be found in parser\Expression.g4
     */
    final static class DifferentiatedExpressionListener implements ExpressionListener {

        private Stack<Expression> stack = new Stack<>();
        private String variable = "";

        public DifferentiatedExpressionListener(String variable){
            this.variable = variable;
        }

        public Expression getExpression(){ return stack.get(1);}

        @Override
        public void exitRoot(ExpressionParser.RootContext context) {}

        @Override
        public void exitExpression(ExpressionParser.ExpressionContext context){
            // handle NUMBER case
            if (context.NUMBER() != null){
                stack.push(new Number(Integer.valueOf(context.NUMBER().getText())));
                stack.push(new Number(0));
            }
            // handle VARIABLE case
            else if (context.VARIABLE() != null){
                String varName = context.VARIABLE().getText();

                if (varName.equals(variable)) {
                    stack.push(new Var(varName, 0));
                    stack.push(new Number(1));
                }
                else{
                    stack.push(new Var(varName, 0));
                    stack.push(new Number( 0));
                }
            }
            // handle expression op='+' expression case
            else if (context.op != null && context.op.getType() == ExpressionParser.ADD){
                List<ExpressionParser.ExpressionContext> addends = context.expression();

                Expression derivativeOfRight = stack.pop();
                Expression right = stack.pop();
                Expression derivativeOfLeft = stack.pop();
                Expression left = stack.pop();

                stack.push(new Add(left, right));
                stack.push(new Add(derivativeOfLeft, derivativeOfRight));
            }
            // handle expression op='*' expression case
            else if (context.op != null && context.op.getType() == ExpressionParser.MUL){
                List<ExpressionParser.ExpressionContext> addends = context.expression();

                Expression derivativeOfRight = stack.pop();
                Expression right = stack.pop();
                Expression derivativeOfLeft = stack.pop();
                Expression left = stack.pop();

                stack.push(new Multiply(left, right));
                stack.push(new Add(new Multiply(left, derivativeOfRight), new Multiply(right, derivativeOfLeft)));
            }
            else{
                // '(' expression case: DO NOTHING
            }
        }

        @Override
        public void enterRoot(ExpressionParser.RootContext context) {}

        @Override
        public void enterExpression(ExpressionParser.ExpressionContext context) {}

        @Override
        public void visitTerminal(TerminalNode terminalNode) {  }

        @Override
        public void visitErrorNode(ErrorNode errorNode) {  }

        @Override
        public void enterEveryRule(ParserRuleContext parserRuleContext) { }

        @Override
        public void exitEveryRule(ParserRuleContext parserRuleContext) { }
    }


    /**
     * Simplify an expression.
     * @param expression the expression to simplify
     * @param environment maps variables to values.  Variables are required to be case-sensitive nonempty 
     *         strings of letters.  The set of variables in environment is allowed to be different than the 
     *         set of variables actually found in expression.  Values must be nonnegative numbers.
     * @return an expression equal to the input, but after substituting every variable v that appears in both
     *         the expression and the environment with its value, environment.get(v).  If there are no
     *         variables left in this expression after substitution, it must be evaluated to a single number.
     *         Additional simplifications to the expression may be done at the implementor's discretion.
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static String simplify(String expression, Map<String,Integer> environment) {
        CharStream stream = new ANTLRInputStream(expression);

        ExpressionParser parser = Expression.makeParser(stream);
        ParseTree tree = parser.root();

        // *** Debugging option #1: print the tree to the console
        //System.err.println(tree.toStringTree(parser));

        // *** Debugging option #2: show the tree in a window
        //Trees.inspect(tree, parser);

        // *** Debugging option #3: walk the tree with a listener
        //new ParseTreeWalker().walk(new Expression.PrintEverything(), tree);

        SimplifiedExpressionListener exprListener = new SimplifiedExpressionListener(environment);
        new ParseTreeWalker().walk(exprListener, tree);

        return exprListener.getExpression().toString();
    }

    /**
    A listener class that traverse the expression parse tree to evaluate to a simplification
     of the current expression by replacing variables with integer value.
     (this is a listener in the Visitor pattern)

     All grammar rules that this class obey can be found in parser\Expression.g4
     */
    final static class SimplifiedExpressionListener implements ExpressionListener {

        private Stack<Expression> stack = new Stack<>();
        private Map<String,Integer> environment;

        public SimplifiedExpressionListener(Map<String,Integer> environment){
            this.environment = environment;
        }

        public Expression getExpression(){ return stack.get(0);}

        @Override
        public void exitRoot(ExpressionParser.RootContext context) {}

        @Override
        public void exitExpression(ExpressionParser.ExpressionContext context){
            // handle NUMBER case
            if (context.NUMBER() != null){
                stack.push(new Number(Integer.valueOf(context.NUMBER().getText())));
            }
            // handle VARIABLE case
            else if (context.VARIABLE() != null){
                String varName = context.VARIABLE().getText();
                stack.push(new Number(environment.get(varName)));

            }
            // handle expression op='+' expression case
            else if (context.op != null && context.op.getType() == ExpressionParser.ADD){
                Expression right = stack.pop();
                Expression left = stack.pop();

                stack.push(new Number(left.value() + right.value()));
            }
            // handle expression op='*' expression case
            else if (context.op != null && context.op.getType() == ExpressionParser.MUL){
                Expression right = stack.pop();
                Expression left = stack.pop();

                stack.push(new Number(left.value() * right.value()));
            }
            else{
                // '(' expression ')' case: DO NOTHING
            }
        }

        @Override
        public void enterRoot(ExpressionParser.RootContext context) {}

        @Override
        public void enterExpression(ExpressionParser.ExpressionContext context) {}

        @Override
        public void visitTerminal(TerminalNode terminalNode) {  }

        @Override
        public void visitErrorNode(ErrorNode errorNode) {  }

        @Override
        public void enterEveryRule(ParserRuleContext parserRuleContext) { }

        @Override
        public void exitEveryRule(ParserRuleContext parserRuleContext) { }
    }
    
}
