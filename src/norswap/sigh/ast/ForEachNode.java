package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;

public final class ForEachNode extends StatementNode
{
    public final VarDeclarationNode var_decl;
    public final ReferenceNode iterable;
    public final StatementNode body;

    public ForEachNode (Span span, Object field_decl, Object iterable, Object body) {
        super(span);
        FieldDeclarationNode field = Util.cast(field_decl, FieldDeclarationNode.class);
        this.var_decl = new VarDeclarationNode(null, field.name, field.type, new IntLiteralNode(null, 0));
        this.iterable = Util.cast(iterable, ReferenceNode.class);
        this.body = Util.cast(body, StatementNode.class);
    }

    @Override
    public String contents () {
        return null;
    }
}
