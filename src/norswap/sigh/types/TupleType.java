package norswap.sigh.types;

import norswap.sigh.ast.TupleLiteralNode;

public final class TupleType extends Type
{
    public final Type componentType;
//    public final TupleLiteralNode componentType;

    public TupleType (Type componentType) {
        this.componentType = componentType;
    }
//    public TupleType (TupleLiteralNode componentType) {
//        this.componentType = componentType;
//    }

    @Override public String name() {
        return "()";
//        return componentType.toString() + "()";
    }

    @Override public boolean equals (Object o) {
        return this == o || o instanceof TupleType
//            && componentType.equals(o)
            ;
    }

    @Override public int hashCode () {
        return componentType.hashCode();
    }
}
