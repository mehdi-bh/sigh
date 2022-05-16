package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;

public final class TupleAccessNode extends ExpressionNode
{
    public final ExpressionNode tuple;
    public final ExpressionNode index;

    public TupleAccessNode (Span span, Object tuple, Object index) {
        super(span);
        this.tuple = Util.cast(tuple, ExpressionNode.class);
        this.index = Util.cast(index, ExpressionNode.class);
    }

    @Override public String contents() {
        return String.format("%s.%s", tuple.contents(), index.contents());
    }
}
