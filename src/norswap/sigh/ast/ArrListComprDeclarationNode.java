package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import norswap.utils.Util;

public final class ArrListComprDeclarationNode extends DeclarationNode
{
    public final String name;
    public final TypeNode type;
    public final ListComprNode listComprNode;

    public ArrListComprDeclarationNode (Span span, Object name, Object type, Object listComprNode /*,Object initializer, Object forEachNode*/) {
        super(span);
        this.name = Util.cast(name, String.class);
        this.type = Util.cast(type, TypeNode.class);
        this.listComprNode = Util.cast(listComprNode, ListComprNode.class);
    }

    @Override public String name () {
        return name;
    }

    @Override public String contents () {
        return "var " + name;
    }

    @Override public String declaredThing () {
        return "variable";
    }
}
