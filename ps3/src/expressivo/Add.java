package expressivo;

import java.util.Objects;

public class Add implements Expression{
    /*
        Abstract function:
            Represent a number: int or double
        Rep invariant:
            left and right must not be null
        Rep exposure:
            All fields are private and final, primitive type are immutable by default
     */
    private final Expression left;
    private final Expression right;

    public Add(Expression left, Expression right) {
        this.left = left;
        this.right = right;
        checkRep();
    }

    /**
     *  Check the rep-invariant
     */
    private void checkRep(){
        Objects.requireNonNull(left);
        Objects.requireNonNull(right);
    }

    @Override
    public int value(){
        return left.value() + right.value();
    }

    /** Return a string represent an adding operation
     *  The double brackets are necessary to identify
     *  the location of left and right operands
     *
     * @return
     */
    @Override
    public String toString() {
        return "(" + left + " + " + right + ")";
    }

    /** Two expressions to be equal if:
     *      the expressions contain the same variables, numbers, and operators;
     *      those variables, numbers, and operators are in the same order, read left-to-right;
     *      and they are grouped in the same way.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Add plusExp = (Add) o;
        return left.equals(plusExp.left) && right.equals(plusExp.right);
    }

    @Override
    public int hashCode() {
        int result = left.hashCode();
        result += 31 * result + right.hashCode();

        return result;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }
}

