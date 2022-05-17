package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;
import java.util.ArrayList;
import java.util.List;

public final class TupleTypeNode extends TypeNode
{
//    public final TypeNode componentType;
    public final List<TypeNode> componentsType;

    public TupleTypeNode (Span span) {
        super(span);
//        this.componentType = Util.cast(componentType, TypeNode.class);
//        this.componentType = null;
        this.componentsType = new ArrayList<>();
    }

    @Override public String contents() {
        return "tuple()";
//        return componentType.contents() + "()";

    }
}
