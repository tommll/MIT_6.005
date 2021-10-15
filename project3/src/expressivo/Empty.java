package expressivo;

public class Empty implements Expression{
    public Empty(){
    }

    public int value(){throw new IllegalArgumentException();}

    public boolean equals(Object o){
        if (o == null || getClass() != o.getClass()) return false;
        return true;
    }

    public String toString(){
        return "";
    }

    public int hashCode(){
        return 0;
    }


}
