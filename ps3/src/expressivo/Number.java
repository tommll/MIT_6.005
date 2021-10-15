package expressivo;

import java.util.Objects;

public class Number implements Expression{
    /*
        Abstract function:
            Represent a number: int
        Rep invariant:
            value must not be null
        Rep exposure:
            All fields are private and final, primitive type are immutable by default
     */
    private final int value;

    public Number(int value){
        this.value = value;
        checkRep();
    }

    private void checkRep(){
        Objects.requireNonNull(value);
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**  2 Num objects are equals if:
     *      Both can be converted to the same absolute value (Ex: 1 == 1.00)
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Number num = (Number) o;
        return value == num.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
