package norswap.sigh.errors;

public class ImutableModificationException extends Error{
    public ImutableModificationException(){super();}
    public ImutableModificationException(String s){super(s);}
    public ImutableModificationException(Object o){
        super("Trying to modify immutable object : " + o);
    }
}
