package norswap.sigh.ast;

import norswap.autumn.positions.Span;

public final class ParameterNode extends AbstractParameterNode
{
    public ParameterNode (Span span, Object name, Object type) {
        super(span, name, type);
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
