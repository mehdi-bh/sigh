package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.sigh.types.FloatType;
import norswap.sigh.types.IntType;
import norswap.sigh.types.StringType;
import norswap.utils.Util;

public final class ForEachNode extends StatementNode
{
    public final VarDeclarationNode var_decl;
    public final ReferenceNode iterable;
    public final StatementNode body;

    public ForEachNode (Span span, Object field_decl, Object iterable, Object body) {
        super(span);
        FieldDeclarationNode field = Util.cast(field_decl, FieldDeclarationNode.class);
        String type = field.type.contents();

        if (type.equals(StringType.INSTANCE.name())){
            this.var_decl = new VarDeclarationNode(null, field.name, field.type, new StringLiteralNode(null, ""));
        }
        else {
            this.var_decl = new VarDeclarationNode(null, field.name, field.type, new IntLiteralNode(null, 0));
        }

        this.iterable = Util.cast(iterable, ReferenceNode.class);
        this.body = Util.cast(body, StatementNode.class);
    }

    @Override
    public String contents () {
        return null;
    }
}
