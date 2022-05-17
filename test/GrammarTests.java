import norswap.autumn.AutumnTestFixture;
import norswap.sigh.SighGrammar;
import norswap.sigh.ast.*;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

import static java.util.Arrays.asList;
import static norswap.sigh.ast.BinaryOperator.*;

public class GrammarTests extends AutumnTestFixture {
    // ---------------------------------------------------------------------------------------------

    private final SighGrammar grammar = new SighGrammar();
    private final Class<?> grammarClass = grammar.getClass();

    // ---------------------------------------------------------------------------------------------

    private static StringLiteralNode stringlit (String s) {
        return new StringLiteralNode(null, s);
    }

    private static IntLiteralNode intlit (long i) {
        return new IntLiteralNode(null, i);
    }

    private static FloatLiteralNode floatlit (double d) {
        return new FloatLiteralNode(null, d);
    }

    private static SimpleTypeNode simpleType (String name){ return new SimpleTypeNode(null,name);}

    private static ParameterDefaultNode param(String name, String type, ExpressionNode lit){ return new ParameterDefaultNode(null,name, simpleType(type),lit);}


    @Test
    public void testParameterDefault () {
        rule = grammar.statement;

        BlockNode bn = new BlockNode(null, asList(new ReturnNode(null, intlit(1))));

        successExpect("fun f (x: Int): Int { return 1 }",
            new FunDeclarationNode(null, "f",
                asList(param("x","Int",null)),
                simpleType("Int"),
                bn));

        successExpect("fun f (x: Int = 5): Int { return 1 }",
            new FunDeclarationNode(null, "f",
                asList(param("x","Int",intlit(5))),
                simpleType("Int"),
                bn));

        successExpect("fun f (x: Int = 5, y: Int = 2, z: String = \"oui monsieur\"): Int { return 1 }",
            new FunDeclarationNode(null, "f", asList(
                param("x","Int",intlit(5)),
                param("y","Int",intlit(2)),
                param("z","String",stringlit("oui monsieur"))
            ),
                simpleType("Int"),
                bn));

        successExpect("fun f (x: Int, y: Int, z: String = \"oui monsieur\"): Int { return 1 }",
            new FunDeclarationNode(null, "f", asList(
                param("x","Int",null),
                param("y","Int",null),
                param("z","String",stringlit("oui monsieur"))
            ),
                simpleType("Int"),
                bn));

        //This test case is refused in the SementicAnalysis
        successExpect("fun f (x: Int = 5, y: Int, z: String = \"non monsieur\"): Int { return 1 }",
            new FunDeclarationNode(null, "f", asList(
                param("x","Int",intlit(5)),
                param("y","Int",null),
                param("z","String",stringlit("non monsieur"))
            ),
                simpleType("Int"),
                bn));
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testAny () {
        rule = grammar.statement;

        successExpect("var x: Any = 1", new VarDeclarationNode(null,
            "x", new SimpleTypeNode(null, "Any"), intlit(1)));

        successExpect("struct P { var x: Any; var y: Any }",
            new StructDeclarationNode(null, "P", asList(
                new FieldDeclarationNode(null, "x", new SimpleTypeNode(null, "Any")),
                new FieldDeclarationNode(null, "y", new SimpleTypeNode(null, "Any")))));

        successExpect("fun f (x: Any): Any { return 1 }",
            new FunDeclarationNode(null, "f",
                asList(new ParameterDefaultNode(null, "x", new SimpleTypeNode(null, "Any"),null)),
                new SimpleTypeNode(null, "Any"),
                new BlockNode(null, asList(new ReturnNode(null, intlit(1))))));

        successExpect("fun f (x: Any = 5): Any {}",
            new FunDeclarationNode(null, "f",
                asList(new ParameterDefaultNode(null, "x", new SimpleTypeNode(null, "Any"),intlit(5))),
                new SimpleTypeNode(null, "Any"),
                new BlockNode(null, new ArrayList<>())));
    }

    // ---------------------------------------------------------------------------------------------

    // @Test
    public void testForStatement () {
        rule = grammar.statement;

        successExpect("for (var i: Int = 0 # i < 5 # i = i + 1) {}",
            new ForNode(null,
                new VarDeclarationNode(null, "i", simpleType("Int"), intlit(0)),
                new BinaryExpressionNode(null, new ReferenceNode(null, "i"), GREATER_EQUAL, intlit(5)),
                new AssignmentNode(null, new ReferenceNode(null, "i"), new BinaryExpressionNode(null, new ReferenceNode(null, "i"), ADD, intlit(1))),
                new BlockNode(null, new ArrayList<>())
                ));
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testLiteralsAndUnary () {
        rule = grammar.expression;

        successExpect("42", intlit(42));
        successExpect("42.0", floatlit(42d));
        successExpect("\"hello\"", new StringLiteralNode(null, "hello"));
        successExpect("(42)", new ParenthesizedNode(null, intlit(42)));
        successExpect("[1, 2, 3]", new ArrayLiteralNode(null, asList(intlit(1), intlit(2), intlit(3))));

        successExpect("(1, 2, 3)", new TupleLiteralNode(null, asList(intlit(1), intlit(2), intlit(3))));
        successExpect("(1, 2, [1,2,3])", new TupleLiteralNode(null, asList(intlit(1), intlit(2),
                                                    new ArrayLiteralNode(null, asList(intlit(1), intlit(2), intlit(3))))));
        successExpect("(1, (2,\"str\"), (3, 5.55))", new TupleLiteralNode(null, asList(intlit(1),
                                                            new TupleLiteralNode(null, asList(intlit(2),stringlit("str"))),
                                                            new TupleLiteralNode(null,asList(intlit(3),floatlit(5.55))))));

        successExpect("true", new ReferenceNode(null, "true"));
        successExpect("false", new ReferenceNode(null, "false"));
        successExpect("null", new ReferenceNode(null, "null"));
        successExpect("!false", new UnaryExpressionNode(null, UnaryOperator.NOT, new ReferenceNode(null, "false")));
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testNumericBinary () {
        successExpect("1 + 2", new BinaryExpressionNode(null, intlit(1), ADD, intlit(2)));
        successExpect("2 - 1", new BinaryExpressionNode(null, intlit(2), SUBTRACT,  intlit(1)));
        successExpect("2 * 3", new BinaryExpressionNode(null, intlit(2), MULTIPLY, intlit(3)));
        successExpect("2 / 3", new BinaryExpressionNode(null, intlit(2), DIVIDE, intlit(3)));
        successExpect("2 % 3", new BinaryExpressionNode(null, intlit(2), REMAINDER, intlit(3)));

        successExpect("1.0 + 2.0", new BinaryExpressionNode(null, floatlit(1), ADD, floatlit(2)));
        successExpect("2.0 - 1.0", new BinaryExpressionNode(null, floatlit(2), SUBTRACT, floatlit(1)));
        successExpect("2.0 * 3.0", new BinaryExpressionNode(null, floatlit(2), MULTIPLY, floatlit(3)));
        successExpect("2.0 / 3.0", new BinaryExpressionNode(null, floatlit(2), DIVIDE, floatlit(3)));
        successExpect("2.0 % 3.0", new BinaryExpressionNode(null, floatlit(2), REMAINDER, floatlit(3)));

        successExpect("2 * (4-1) * 4.0 / 6 % (2+1)", new BinaryExpressionNode(null,
            new BinaryExpressionNode(null,
                new BinaryExpressionNode(null,
                    new BinaryExpressionNode(null,
                        intlit(2),
                        MULTIPLY,
                        new ParenthesizedNode(null, new BinaryExpressionNode(null,
                            intlit(4),
                            SUBTRACT,
                            intlit(1)))),
                    MULTIPLY,
                    floatlit(4d)),
                DIVIDE,
                intlit(6)),
            REMAINDER,
            new ParenthesizedNode(null, new BinaryExpressionNode(null,
                intlit(2),
                ADD,
                intlit(1)))));
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testArrayStructAccess () {
        rule = grammar.expression;
        successExpect("[1][0]", new ArrayAccessNode(null,
            new ArrayLiteralNode(null, asList(intlit(1))), intlit(0)));
        successExpect("[1].length", new FieldAccessNode(null,
            new ArrayLiteralNode(null, asList(intlit(1))), "length"));
        successExpect("p.x", new FieldAccessNode(null, new ReferenceNode(null, "p"), "x"));


        //todo tuple.length

        successExpect("(1,2)[1]", new ArrayAccessNode(null,
            new TupleLiteralNode(null,asList(intlit(1),intlit(2))),
            intlit(1)));

        successExpect("(1, (2,\"str\"), (3, 5.55))[1][1]", new ArrayAccessNode(null,
            new ArrayAccessNode(null,
                new TupleLiteralNode(null, asList(intlit(1),
                    new TupleLiteralNode(null, asList(intlit(2),stringlit("str"))),
                    new TupleLiteralNode(null,asList(intlit(3),floatlit(5.55))))
                ),
                intlit(1)),
            intlit(1)));

    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testDeclarations() {
        rule = grammar.statement;

        successExpect("var x: Int = 1", new VarDeclarationNode(null,
            "x", new SimpleTypeNode(null, "Int"), intlit(1)));

        successExpect("var t: () = ()", new VarDeclarationNode(null,
            "t", new TupleTypeNode(null), new TupleLiteralNode(null, Collections.emptyList())));

        successExpect("var t: () = (1,2)", new VarDeclarationNode(null,
            "t", new TupleTypeNode(null), new TupleLiteralNode(null,asList(intlit(1),intlit(2)))));

        successExpect("var t: () = ( (1,2), 2, (\"a\", -2.1) )", new VarDeclarationNode(null,
            "t", new TupleTypeNode(null),
                        new TupleLiteralNode(null,asList(
                            new TupleLiteralNode(null, asList(intlit(1),intlit(2))),
                            intlit(2),
                            new TupleLiteralNode(null, asList(stringlit("a"),floatlit(-2.1)))
                        ))));


        successExpect("struct P {}", new StructDeclarationNode(null, "P", asList()));

        successExpect("struct P { var x: Int; var y: Int }",
            new StructDeclarationNode(null, "P", asList(
                new FieldDeclarationNode(null, "x", new SimpleTypeNode(null, "Int")),
                new FieldDeclarationNode(null, "y", new SimpleTypeNode(null, "Int")))));

        successExpect("fun f (x: Int): Int { return 1 }",
            new FunDeclarationNode(null, "f",
                asList(new ParameterDefaultNode(null, "x", new SimpleTypeNode(null, "Int"),null)),
                new SimpleTypeNode(null, "Int"),
                new BlockNode(null, asList(new ReturnNode(null, intlit(1))))));
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testStatements() {
        rule = grammar.statement;

        successExpect("return", new ReturnNode(null, null));
        successExpect("return 1", new ReturnNode(null, intlit(1)));
        successExpect("print(1)", new ExpressionStatementNode(null,
            new FunCallNode(null, new ReferenceNode(null, "print"), asList(intlit(1)))));
        successExpect("{ return }", new BlockNode(null, asList(new ReturnNode(null, null))));


        successExpect("if true return 1 else return 2", new IfNode(null, new ReferenceNode(null, "true"),
            new ReturnNode(null, intlit(1)),
            new ReturnNode(null, intlit(2))));

        successExpect("if false return 1 else if true return 2 else return 3 ",
            new IfNode(null, new ReferenceNode(null, "false"),
                new ReturnNode(null, intlit(1)),
                new IfNode(null, new ReferenceNode(null, "true"),
                    new ReturnNode(null, intlit(2)),
                    new ReturnNode(null, intlit(3)))));

        successExpect("while 1 < 2 { return } ", new WhileNode(null,
            new BinaryExpressionNode(null, intlit(1), LOWER, intlit(2)),
            new BlockNode(null, asList(new ReturnNode(null, null)))));
    }

    // ---------------------------------------------------------------------------------------------
}
