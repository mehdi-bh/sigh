package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;

public abstract class AbstractParameterNode extends DeclarationNode{
    public final String name;
    public final TypeNode type;

    public AbstractParameterNode (Span span, Object name, Object type) {
        super(span);
        this.name = Util.cast(name, String.class);
        this.type = Util.cast(type, TypeNode.class);
    }
}
