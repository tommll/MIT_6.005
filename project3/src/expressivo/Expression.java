/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import expressivo.parser.ExpressionLexer;
import expressivo.parser.ExpressionListener;
import expressivo.parser.ExpressionParser;
import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Stack;

/**
 * An immutable data type representing a polynomial expression of:
 *   + and *
 *   nonnegative integers and floating-point numbers
 *   variables (case-sensitive nonempty strings of letters)
 * 
 * <p>PS3 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 * Declare concrete variants of Expression in their own Java source files.
 */
public interface Expression {
    
    // Datatype definition
    //   TODO
    // Exp<E> = Empty + Number(value: E) + Var(name: N, value: E)
    //                 + Add(left: Exp<E>, right: Exp<E>) + Multiply(left: Exp<E>, right: Exp<E>)
    
    /**
     * Parse an expression.
     * @param input expression to parse, as defined in the PS3 handout.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) {
        CharStream stream = new ANTLRInputStream(input);

        ExpressionParser parser = makeParser(stream);
        ParseTree tree = parser.root();


        // *** Debugging option #1: print the tree to the console
        //System.err.println(tree.toStringTree(parser));

        // *** Debugging option #2: show the tree in a window
        //Trees.inspect(tree, parser);

        // *** Debugging option #3: walk the tree with a listener
        //new ParseTreeWalker().walk(new PrintEverything(), tree);

        ExpressionListener exprListener = new ExpressionListener();
        new ParseTreeWalker().walk(exprListener, tree);
        return exprListener.getExpression();
    }

    /**
     * Make a parser that is ready to parse a stream of characters.
     * To start parsing, the client should call a method on the returned parser
     * corresponding to the start rule of the grammar, e.g. parser.root() or
     * whatever it happens to be.
     * During parsing, if the parser encounters a syntax error, it will throw a
     * ParseCancellationException.
     * @param stream stream of characters
     * @return a parser that is ready to parse the stream
     */
    public static ExpressionParser makeParser(CharStream stream){
        // Make a lexer. This converts the stream of characters into a
        // stream of tokens. Note that this doesn't start reading the character stream yet,
        // it just sets up the lexer to read it.
        ExpressionLexer lexer = new ExpressionLexer(stream);
        lexer.reportErrorsAsExceptions();
        TokenStream tokens = new CommonTokenStream(lexer);

        // Make a new parser whose input comes from token stream
        ExpressionParser parser = new ExpressionParser(tokens);
        parser.reportErrorsAsExceptions();

        return parser;
    }


    /** Print out parse tree nodes to System.err as they are visited.
     * (this is a listener in the Visitor pattern) */
    class PrintEverything implements expressivo.parser.ExpressionListener {

        @Override public void enterRoot(ExpressionParser.RootContext context) {
            System.err.println("entering root");
        }
        @Override public void exitRoot(ExpressionParser.RootContext context) {
            System.err.println("exiting root");
        }

        @Override public void enterExpression(ExpressionParser.ExpressionContext context) {
            System.err.println("entering Expression");
        }
        @Override public void exitExpression(ExpressionParser.ExpressionContext context) {
            System.err.println("exiting Expression");
        }

        @Override public void visitTerminal(TerminalNode terminal) {
            System.err.println("terminal " + terminal.getText());
        }

        // don't need these here, so just make them empty implementations
        @Override public void enterEveryRule(ParserRuleContext context) { }
        @Override public void exitEveryRule(ParserRuleContext context) { }
        @Override public void visitErrorNode(ErrorNode node) { }
    }

    /**
     *  Basic listener to traverse the Parse tree in pre-order
     *  (this is a listener in the Visitor pattern)
     */
    final class ExpressionListener implements expressivo.parser.ExpressionListener{

        private Stack<Expression> stack = new Stack<>();

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
                stack.push(new Var(varName, 0));
            }
            // handle expression op='+' expression case
            else if (context.op != null && context.op.getType() == ExpressionParser.ADD){
                Expression right = stack.pop();
                Expression left = stack.pop();

                stack.push(new Add(left, right));
            }
            // handle expression op='*' expression case
            else if (context.op != null && context.op.getType() == ExpressionParser.MUL){
                Expression right = stack.pop();
                Expression left = stack.pop();

                stack.push(new Multiply(left, right));
            }
            // handle '(' expression ')' case
            else{
                // do nothing
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
     * @return a parsable representation of this expression, such that
     * for all e:Expression, e.equals(Expression.parse(e.toString())).
     */
    @Override 
    public String toString();

    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are structurally-equal
     * Expressions, as defined in the PS3 handout.
     */
    @Override
    public boolean equals(Object thatObject);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();
    
    // TODO more instance methods


    public int value();
}
