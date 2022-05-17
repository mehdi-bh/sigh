package norswap.sigh.types;

import norswap.sigh.ast.TupleLiteralNode;
import norswap.sigh.ast.TupleTypeNode;
import java.util.ArrayList;
import java.util.List;

public final class TupleType extends Type
{
    public static final TupleType INSTANCE = new TupleType();

    public TupleType () {
    }

    @Override public String name() {
        return "()";
    }
}
