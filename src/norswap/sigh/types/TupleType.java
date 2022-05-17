package norswap.sigh.types;

import norswap.sigh.ast.TupleLiteralNode;
import norswap.sigh.ast.TupleTypeNode;
import java.util.ArrayList;
import java.util.List;

public final class TupleType extends Type
{
    public static final TupleType INSTANCE = new TupleType();
//    public static List<Type> componentTypes = new ArrayList<>();
//    public final Type componentType;

    public TupleType () {
//        this.componentType = componentType;
//        this.componentTypes = new ArrayList<>();
    }
//    public TupleType (TupleLiteralNode componentType) {
//        this.componentType = componentType;
//    }

    @Override public String name() {
        return "()";
//        return componentType.toString() + "()";
    }

//    @Override public boolean equals (Object o) {
//        return this == o || o instanceof TupleType
////            && componentType.equals(o)
//            ;
//    }
//
//    @Override public int hashCode () {
//        return componentType.hashCode();
//    }
}
