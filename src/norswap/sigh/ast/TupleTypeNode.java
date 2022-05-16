package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;

public final class TupleTypeNode extends TypeNode
{
//    public final TypeNode componentType;

    public TupleTypeNode (Span span, Object componentType) {
        super(span);
        System.out.println(String.valueOf(span));
//        this.componentType = Util.cast(componentType, TypeNode.class);
//        this.componentType = null;
    }

    @Override public String contents() {
        return "()";
//        return componentType.contents() + "()";

    }
}
