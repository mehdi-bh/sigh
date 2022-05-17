package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;
import java.util.ArrayList;
import java.util.List;

public final class TupleTypeNode extends TypeNode
{

    public TupleTypeNode (Span span) {
        super(span);
    }

    @Override public String contents() {
        return "tuple()";
    }
}
