package norswap.sigh.ast;

import norswap.autumn.positions.Span;

import static norswap.utils.Util.cast;

public class CaseNode extends StatementNode{

    public final ExpressionNode pattern;
    public final BlockNode body;

    public CaseNode (Span span, Object pattern, Object body) {
        super(span);
        this.pattern = cast(pattern, ExpressionNode.class);
        this.body = cast(body, BlockNode.class);
    }

    public String contents () {
        return "case (" + pattern.contents() + ")" +
            body.contents();
    }
}
