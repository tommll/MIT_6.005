package expressivo;

import java.util.Objects;

public class Var implements Expression{
    /*
        Abstract function:
            Represent a number: int
        Rep invariant:
            value must not be null
        Rep exposure:
            All fields are private and final, primitive type are immutable by default
     */

    private final String name;
    private final int value;

    public Var(String name, int value) {
        this.name = name;
        this.value = value;
        checkRep();
    }

    private void checkRep(){
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
    }

    /** Return a string represent the Variable, which is its name
     *
     * @return
     */
    @Override
    public String toString() {
        return name;
    }

    /** Return integer value which the current variable holds
     *
     * @return
     */
    @Override
    public int value(){
        return value;
    }

    /** 2 Var objects are equal if:
     *      their values are equals
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Var var = (Var) o;
        return name.equals(var.name) && value == var.value;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result += 31 * result + Integer.hashCode(value);

        return result;
    }

}
