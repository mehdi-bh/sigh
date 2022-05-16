package norswap.sigh.ast;

import norswap.autumn.positions.Span;
import java.util.List;

import static norswap.utils.Util.cast;

public class SwitchNode extends StatementNode{

    public ExpressionNode item;
    public List<CaseNode> body;
    public BlockNode defaultStmt;

    @SuppressWarnings("unchecked")
    public SwitchNode (Span span, Object item, Object body, Object defaultStmt) {
        super(span);
        this.item = cast(item, ExpressionNode.class);
        this.body = cast(body, List.class);
        this.defaultStmt = cast(defaultStmt, BlockNode.class);
    }

    public String contents () {
        StringBuilder bodyStr = new StringBuilder();
        for (CaseNode node : this.body) {
            bodyStr.append(node.contents()).append("\n");
        }
        return
            "switch (" + item.contents() + ") {\n" +
                bodyStr.toString() +
                "case (default)" +
                defaultStmt.contents();
    }
}
