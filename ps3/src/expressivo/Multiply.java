package expressivo;

import java.util.Objects;

public class Multiply implements Expression{
    /*
        Abstract function:
            Represent a multiply expression
        Rep invariant:
            value must not be null
        Rep exposure:
            All fields are private and final, primitive type are immutable by default
     */
    private final Expression left;
    private final Expression right;

    public Multiply(Expression left, Expression right) {
        this.left = left;
        this.right = right;
        checkRep();
    }

    /** Preserve the rep-invariant
     *
     */
    private void checkRep(){
        Objects.requireNonNull(left);
        Objects.requireNonNull(right);
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
        Multiply multExp = (Multiply) o;
        return left.equals(multExp.left) && right.equals(multExp.right);
    }

    /** Return the integer result of the multiplication
     *
     * @return
     */
    @Override
    public int value(){
        return left.value() * right.value();
    }

    /** Return a string represent the multiply operation
     *  Don't required any brackets because left and right operands
     *  can be automatically identified
     *
     * @return
     */
    @Override
    public String toString() {
        return left + "*" + right;
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
