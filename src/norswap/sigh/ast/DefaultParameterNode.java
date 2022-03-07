package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;

public final class DefaultParameterNode extends AbstractParameterNode
{
    public final ExpressionNode defaultValue;

    public DefaultParameterNode (Span span, Object name, Object type, Object defaultValue) {
        super(span, name, type);
        this.defaultValue = Util.cast(defaultValue, ExpressionNode.class);
    }

    @Override public String name () {
        return name;
    }

    @Override public String contents () {
        return name;
    }

    @Override public String declaredThing () {
        return "parameter";
    }
}
