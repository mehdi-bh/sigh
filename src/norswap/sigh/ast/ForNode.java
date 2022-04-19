package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;

public final class ForNode extends StatementNode
{
    public final VarDeclarationNode var_decl;
    public final ExpressionNode condition;
    public final AssignmentNode increment;
    public final StatementNode body;

    public ForNode (Span span, Object var_decl, Object condition, Object increment, Object body) {
        super(span);
        this.var_decl = Util.cast(var_decl, VarDeclarationNode.class);
        this.condition = Util.cast(condition, ExpressionNode.class);
        this.increment = Util.cast(increment, AssignmentNode.class);
        this.body = Util.cast(body, StatementNode.class);
    }

    @Override
    public String contents () {
        return "";
    }
}
