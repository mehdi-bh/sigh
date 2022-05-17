import norswap.autumn.AutumnTestFixture;
import norswap.autumn.Grammar;
import norswap.autumn.Grammar.rule;
import norswap.autumn.ParseResult;
import norswap.autumn.positions.LineMapString;
import norswap.sigh.SemanticAnalysis;
import norswap.sigh.SighGrammar;
import norswap.sigh.ast.SighNode;
import norswap.sigh.errors.ImutableModificationException;
import norswap.sigh.interpreter.Interpreter;
import norswap.sigh.interpreter.Null;
import norswap.uranium.Reactor;
import norswap.uranium.SemanticError;
import norswap.utils.IO;
import norswap.utils.TestFixture;
import norswap.utils.data.wrappers.Pair;
import norswap.utils.visitors.Walker;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Set;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertThrows;

public final class InterpreterTests extends TestFixture {

    // TODO peeling

    // ---------------------------------------------------------------------------------------------

    private final SighGrammar grammar = new SighGrammar();
    private final AutumnTestFixture autumnFixture = new AutumnTestFixture();

    {
        autumnFixture.runTwice = false;
        autumnFixture.bottomClass = this.getClass();
    }

    // ---------------------------------------------------------------------------------------------

    private Grammar.rule rule;

    // ---------------------------------------------------------------------------------------------

    private void check (String input, Object expectedReturn) {
        assertNotNull(rule, "You forgot to initialize the rule field.");
        check(rule, input, expectedReturn, null);
    }

    // ---------------------------------------------------------------------------------------------

    private void check (String input, Object expectedReturn, String expectedOutput) {
        assertNotNull(rule, "You forgot to initialize the rule field.");
        check(rule, input, expectedReturn, expectedOutput);
    }

    // ---------------------------------------------------------------------------------------------

    private void check (rule rule, String input, Object expectedReturn, String expectedOutput) {
        // TODO
        // (1) write proper parsing tests
        // (2) write some kind of automated runner, and use it here

        autumnFixture.rule = rule;
        ParseResult parseResult = autumnFixture.success(input);
        SighNode root = parseResult.topValue();

        Reactor reactor = new Reactor();
        Walker<SighNode> walker = SemanticAnalysis.createWalker(reactor);
        Interpreter interpreter = new Interpreter(reactor);
        walker.walk(root);
        reactor.run();
        Set<SemanticError> errors = reactor.errors();

        if (!errors.isEmpty()) {
            LineMapString map = new LineMapString("<test>", input);
            String report = reactor.reportErrors(it ->
                it.toString() + " (" + ((SighNode) it).span.startString(map) + ")");
            //            String tree = AttributeTreeFormatter.format(root, reactor,
            //                    new ReflectiveFieldWalker<>(SighNode.class, PRE_VISIT, POST_VISIT));
            //            System.err.println(tree);
            throw new AssertionError(report);
        }

        Pair<String, Object> result = IO.captureStdout(() -> interpreter.interpret(root));
        assertEquals(result.b, expectedReturn);
        if (expectedOutput != null) assertEquals(result.a, expectedOutput);
    }

    // ---------------------------------------------------------------------------------------------

    private void checkExpr (String input, Object expectedReturn, String expectedOutput) {
        rule = grammar.root;
        check("return " + input, expectedReturn, expectedOutput);
    }

    // ---------------------------------------------------------------------------------------------

    private void checkExpr (String input, Object expectedReturn) {
        rule = grammar.root;
        check("return " + input, expectedReturn);
    }

    // ---------------------------------------------------------------------------------------------

    private void checkThrows (String input, Class<? extends Throwable> expected) {
        assertThrows(expected, () -> check(input, null));
    }


    // ---------------------------------------------------------------------------------------------
//    @Test
//    public void testFunCal(){
////        rule = grammar.fun_decl;
//
//        check(
//            "fun add (a: Int, b: Int): Int { return a + b } " +
//                "return add(4, 7)",
//            11L);
//
////        check(
////            "fun add (a: Int = 4 , b: Int = 7): Int { return a + b } " +
////                "return add()",
////            11L);
//    }
    // ---------------------------------------------------------------------------------------------

    @Test
    public void testLiteralsAndUnary () {
        checkExpr("42", 42L);
        checkExpr("42.0", 42.0d);
        checkExpr("\"hello\"", "hello");
        checkExpr("(42)", 42L);
        checkExpr("[1, 2, 3]", new Object[]{1L, 2L, 3L});

        checkExpr("(1, 2, 3)", new Object[]{1L, 2L, 3L});
        checkExpr("(1, 2, (1,2))", new Object[]{1L, 2L, new Object[]{1L,2L} });
        checkExpr("(1.55, 2, (1,\"s\"))", new Object[]{1.55D, 2L, new Object[]{1L,"s"} });

        checkExpr("true", true);
        checkExpr("false", false);
        checkExpr("null", Null.INSTANCE);
        checkExpr("!false", true);
        checkExpr("!true", false);
        checkExpr("!!true", true);
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testNumericBinary () {
        checkExpr("1 + 2", 3L);
        checkExpr("2 - 1", 1L);
        checkExpr("2 * 3", 6L);
        checkExpr("2 / 3", 0L);
        checkExpr("3 / 2", 1L);
        checkExpr("2 % 3", 2L);
        checkExpr("3 % 2", 1L);

        checkExpr("1.0 + 2.0", 3.0d);
        checkExpr("2.0 - 1.0", 1.0d);
        checkExpr("2.0 * 3.0", 6.0d);
        checkExpr("2.0 / 3.0", 2d / 3d);
        checkExpr("3.0 / 2.0", 3d / 2d);
        checkExpr("2.0 % 3.0", 2.0d);
        checkExpr("3.0 % 2.0", 1.0d);

        checkExpr("1 + 2.0", 3.0d);
        checkExpr("2 - 1.0", 1.0d);
        checkExpr("2 * 3.0", 6.0d);
        checkExpr("2 / 3.0", 2d / 3d);
        checkExpr("3 / 2.0", 3d / 2d);
        checkExpr("2 % 3.0", 2.0d);
        checkExpr("3 % 2.0", 1.0d);

        checkExpr("1.0 + 2", 3.0d);
        checkExpr("2.0 - 1", 1.0d);
        checkExpr("2.0 * 3", 6.0d);
        checkExpr("2.0 / 3", 2d / 3d);
        checkExpr("3.0 / 2", 3d / 2d);
        checkExpr("2.0 % 3", 2.0d);
        checkExpr("3.0 % 2", 1.0d);

        checkExpr("2 * (4-1) * 4.0 / 6 % (2+1)", 1.0d);

        checkExpr("(10,20)[0] + 15", 25L);
        checkExpr("15 + (10,20)[0]", 25L);
        checkExpr("(10,(20.50,30))[1][0] + 15", 35.50D);
        checkExpr("15 +(10,(20.50,30))[1][0]  ", 35.50D);
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testOtherBinary () {
        checkExpr("true  && true",  true);
        checkExpr("true  || true",  true);
        checkExpr("true  || false", true);
        checkExpr("false || true",  true);
        checkExpr("false && true",  false);
        checkExpr("true  && false", false);
        checkExpr("false && false", false);
        checkExpr("false || false", false);

        checkExpr("1 + \"a\"", "1a");
        checkExpr("\"a\" + 1", "a1");
        checkExpr("\"a\" + true", "atrue");

        checkExpr("1 == 1", true);
        checkExpr("1 == 2", false);
        checkExpr("1.0 == 1.0", true);
        checkExpr("1.0 == 2.0", false);
        checkExpr("true == true", true);
        checkExpr("false == false", true);
        checkExpr("true == false", false);
        checkExpr("1 == 1.0", true);
        checkExpr("[1] == [1]", false);

        checkExpr("1 != 1", false);
        checkExpr("1 != 2", true);
        checkExpr("1.0 != 1.0", false);
        checkExpr("1.0 != 2.0", true);
        checkExpr("true != true", false);
        checkExpr("false != false", false);
        checkExpr("true != false", true);
        checkExpr("1 != 1.0", false);

        checkExpr("\"hi\" != \"hi2\"", true);
        checkExpr("[1] != [1]", true);

        checkExpr(" (1,2) != (1,2)", true);
        checkExpr(" (1,2)[0] == (1,2)[0]", true);

         // test short circuit
        checkExpr("true || print(\"x\") == \"y\"", true, "");
        checkExpr("false && print(\"x\") == \"y\"", false, "");
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testVarDecl () {
        rule = grammar.root;

        check("var x: Int = 1; return x", 1L);
        check("var x: Float = 2.0; return x", 2d);

        check("var x: Int = 0; return x = 3", 3L);
        check("var x: String = \"0\"; return x = \"S\"", "S");

        check("var t: () = (1,2); return t", new Object[]{1L,2L});
        check("var t: () = ((1,2) , (\"a\",\"b\")); return t", new Object[]{new Object[]{1L,2L},new Object[]{"a","b"}});

        check("var t: () = (1,2); return t = (4,5,7)", new Object[]{4L,5L, 7L});

        // implicit conversions
        check("var x: Float = 1; x = 2; return x", 2.0d);
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testRootAndBlock () {
        rule = grammar.root;
        check("return", null);
        check("return 1", 1L);
        check("return 1; return 2", 1L);

        check("print(\"a\")", null, "a\n");
        check("print(\"a\" + 1)", null, "a1\n");
        check("print(\"a\"); print(\"b\")", null, "a\nb\n");

        check("{ print(\"a\"); print(\"b\") }", null, "a\nb\n");

        check(
            "var x: Int = 1;" +
            "{ print(\"\" + x); var x: Int = 2; print(\"\" + x) }" +
            "print(\"\" + x)",
            null, "1\n2\n1\n");
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testCalls () {
        rule = grammar.root;

        check(
            "fun add (a: Int, b: Int): Int { return a + b } " +
                "return add(4, 7)",
            11L);

        check(
            "fun add (a: Int = 4 , b: Int = 7): Int { return a + b } " +
                "return add() + add(3,3)",
            17L);

        check(
            "fun add (a: Int = 4 , b: Int = 7): Int { return a + b } " +
                "return add(100)",
            107L);

        check(
            "fun add (a: String, b: Int = 4 , c: String = \" marto\"): String { return a + b + c } " +
                "return add(\"Number \", 1)",
            "Number 1 marto");


        check(
            "fun addToCoordinates(t: (), x: Any) : () {" +
                "return (t[0] + x, t[1] + x)" +
                "}" +
                "return addToCoordinates( (1,2) , \"10\" )",
            new Object[]{
                "110","210"
            });

        check(
            "fun addToCoordinates(t: (), x: Int) : () {" +
                "return (t[0] + x, t[1] + x)" +
                "}" +
                "return addToCoordinates( (1,2) , 10 )",
            new Object[]{
                11L,12L
            });

        check(
            "fun duplicate(t: ()) : () {" +
                "return (t,t)" +
                "}" +
                "return duplicate( (1,2) )",
            new Object[]{
                new Object[]{1L,2L},
                new Object[]{1L,2L}
            });

        check(
            "fun sumCoordinates(t: ()) : Int {" +
                "return t[0] + t[1]" +
                "}" +
                "return sumCoordinates( (3, -7) )",
            -4L);

        check(
            "fun sumCoordinates(t: () = (1,2) ) : () {" +
                "return t[0] + t[1]" +
                "}" +
                "return sumCoordinates()",3L);

        HashMap<String, Object> point = new HashMap<>();
        point.put("x", 1L);
        point.put("y", 2L);

        check(
            "struct Point { var x: Int; var y: Int }" +
                "return $Point(1, 2)",
            point);

        check("var str: String = null; return print(str + 1)", "null1", "null1\n");
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testArrayStructAccess () {
        checkExpr("[1][0]", 1L);
        checkExpr("[1.0][0]", 1d);
        checkExpr("[1, 2][1]", 2L);

        // TODO check that this fails (& maybe improve so that it generates a better message?)
        // or change to make it legal (introduce a top type, and make it a top type array if thre
        // is no inference context available)
        // checkExpr("[].length", 0L);
        checkExpr("[1].length", 1L);
        checkExpr("[1, 2].length", 2L);

        checkThrows("var array: Int[] = null; return array[0]", NullPointerException.class);
        checkThrows("var array: Int[] = null; return array.length", NullPointerException.class);

        check("var x: Int[] = [0, 1]; x[0] = 3; return x[0]", 3L);
        checkThrows("var x: Int[] = []; x[0] = 3; return x[0]",
            ArrayIndexOutOfBoundsException.class);
        checkThrows("var x: Int[] = null; x[0] = 3",
            NullPointerException.class);

        checkExpr("(1,2)[0]", 1L);
        checkExpr("(1.0,\"s\")[0]", 1d);
        checkExpr("(1.0,\"s\")[1]", "s");
        checkExpr("(1, 2, (1.0,\"s\"))[2][1]", "s");
        checkExpr("(1, 2, (1.0,\"s\", (1,2) ))[2][2][1]", 2L);

        checkExpr("(1,[1,2])[1]", new Object[]{1L,2L});

        //TODO make it works ?
        //        checkExpr("(1,2).length", 2L);

        check("var t: () = (1,[1,2]); t[1][0] = 10; return t[1] ", new Object[]{10L,2L});

        checkThrows("var t: () = null; return t[0]", NullPointerException.class);
        checkThrows("var x: () = (0, 1); x[0] = 3;", ImutableModificationException.class);
        checkThrows("var t: () = (1,[1,2]); t[1] = [100,100];  ", ImutableModificationException.class);

//        checkThrows("var x: () = (0,1); return x[5]",ArrayIndexOutOfBoundsException.class);
        checkThrows("var x: () = null; x[0] = 3",
            ImutableModificationException.class);




        check(
            "struct P { var x: Int; var y: Int }" +
                "return $P(1, 2).y",
            2L);

        checkThrows(
            "struct P { var x: Int; var y: Int }" +
                "var p: P = null;" +
                "return p.y",
            NullPointerException.class);

        check(
            "struct P { var x: Int; var y: Int }" +
                "var p: P = $P(1, 2);" +
                "p.y = 42;" +
                "return p.y",
            42L);

        checkThrows(
            "struct P { var x: Int; var y: Int }" +
                "var p: P = null;" +
                "p.y = 42",
            NullPointerException.class);
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testIfWhile () {
        check("if (true) return 1 else return 2", 1L);
        check("if (false) return 1 else return 2", 2L);
        check("if (false) return 1 else if (true) return 2 else return 3 ", 2L);
        check("if (false) return 1 else if (false) return 2 else return 3 ", 3L);

        check("var i: Int = 0; while (i < 3) { print(\"\" + i); i = i + 1 } ", null, "0\n1\n2\n");
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testSwitch () {
        check("var color : Int = 3" +
            "switch (color) {" +
                "case (0) {}" +
                "case (1) {}" +
                "case (default) {print(\"No match\")}" +
            "}", null, "No match\n");
        check("var color : Int = 0" +
            "switch (color) {" +
                "case (0) {print(\"Wow 0\")}" +
                "case (1) {}" +
                "case (default) {}" +
            "}", null, "Wow 0\n");
        check("var color : String = \"Blue\"" +
            "switch (color) {" +
                "case (\"Blue\") {print(\"Wow blue\")}" +
                "case (\"Red\") {}" +
                "case (default) {}" +
            "}", null, "Wow blue\n");
        check("var color : String = \"Green\"" +
            "switch (color) {" +
            "case (\"Blue\") {print(\"Wow blue\")}" +
            "case (\"Red\") {}" +
            "case (default) {print(\"Oh green\")}" +
            "}", null, "Oh green\n");
        check("var color : String = \"Green\"" +
            "switch (color) {" +
            "case (\"Blue\") {print(\"Wow blue\")}" +
            "case (\"Red\") {}" +
            "case (default) {print(\"Oh green\")}" +
            "}", null, "Oh green\n");
        check("var color : Float = 2.0" +
            "switch (color) {" +
            "case (2.0) {print(\"Wow 2\")}" +
            "case (1.0) {}" +
            "case (default) {print(\"Oh green\")}" +
            "}", null, "Wow 2\n");
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testListcomprehension(){
        rule = grammar.root;

        check("var arr: Float[] = [1,2,3]" +
            "var lc: Float[] = [x + 100 for var x: Float # arr]" +
            "print(\"\"+lc[0])" +
            "print(\"\"+lc[1])" +
            "print(\"\"+lc[2])",null,"101\n102\n103\n");

        //TODO finish tests

    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testFor () {
        check("" +
            "var arr: String[] = [\"a\",\"z\",\"e\"]\n" +
            "for (var i: Int = 0 # i < 3 # i = i + 1) {" +
            "print(arr[i])" +
            "}"
        ,
        null,
        "a\nz\ne\n");
        check("" +
                "var arr: String[] = [\"a\",\"z\",\"e\",\"r\"]\n" +
                "for (var i: Int = 0 # i < 4 # i = i + 2) {" +
                "print(arr[i])" +
                "}"
            ,
            null,
            "a\ne\n");
        check("" +
                "var arr: String[] = [\"a\",\"z\",\"e\",\"r\"]\n" +
                "for (var i: Int = 3 # i >= 0 # i = i - 1) {" +
                "print(arr[i])" +
                "}"
            ,
            null,
            "r\ne\nz\na\n");
        check("" +
                "var arr: String[] = [\"a\",\"z\",\"e\",\"r\"]\n" +
                "for (var i: Int = 3 # i >= 0 # i = i - 2) {" +
                "print(arr[i])" +
                "}"
            ,
            null,
            "r\nz\n");
        check("" +
                "for (var i: Float = 0 # i < 4 # i = i + 1) {" +
                "print(\"\" + i)" +
                "}"
            ,
            null,
            "0.0\n1.0\n2.0\n3.0\n");
    }

    @Test
    public void testForEach () {
        rule = grammar.root;

        check("" +
            "var arr: String[] = [\"a\",\"z\",\"e\"]\n" +
            "foreach (var i: String # arr) {" +
                "print(i)" +
                "}"
            ,
            null,
            "a\nz\ne\n");
        check("" +
                "var arr: Int[] = [1,2,3]\n" +
                "foreach (var i: Int # arr) {" +
                "print(\"\" + i)" +
                "}"
            ,
            null,
            "1\n2\n3\n");
        check("" +
                "var arr: Float[] = [1.0,2.0,3.0]\n" +
                "foreach (var i: Float # arr) {" +
                "print(\"\" + i)" +
                "}"
            ,
            null,
            "1.0\n2.0\n3.0\n");
        check("" +
                "var arr: Float[] = [1.0,2.0,3.0]\n" +
                "foreach (var i: Any # arr) {" +
                "print(\"\" + i)" +
                "}"
            ,
            null,
            "1.0\n2.0\n3.0\n");
        check("" +
                "var arr: Any[] = [1.0,2.0,3.0]\n" +
                "foreach (var i: Float # arr) {" +
                "print(\"\" + i)" +
                "}"
            ,
            null,
            "1.0\n2.0\n3.0\n");
        check("" +
                "var arr: Any[] = [1.0,2.0,3.0]\n" +
                "foreach (var i: Any # arr) {" +
                "print(\"\" + i)" +
                "}"
            ,
            null,
            "1.0\n2.0\n3.0\n");

        check("" +
            "var arr: () = (1,2,3)" +
            "foreach (var i: Any # arr) {" +
            "print(\"\" + i )" +
            "}", null,"1\n2\n3\n");

        check("" +
            "var arr: () = (\"1\",2,3.54, (1,2) )" +
            "foreach (var i: Any # arr) {" +
            "print(\"\" + i )" +
            "}", null,
            "1\n2\n3.54\n[1, 2]\n");
        check("" +
            "var arr: () = ( (1,2), (3,4,5) , (6,7,8,9))" +
            "foreach (var i: Any # arr) {" +
                "foreach (var x: Any # i) {" +
                    "print(\"\" + x )" +
                "}" +
            "}",null,
            "1\n2\n3\n4\n5\n6\n7\n8\n9\n");

        checkThrows("" +
            "var arr: () = (1,2,3)" +
            "foreach (var i: Int # arr) {" +
            "print(\"\" + i )" +
            "}",AssertionError.class);
        checkThrows("" +
            "var arr: () = (\"1\",2,3.54)" +
            "foreach (var i: Int # arr) {" +
            "print(\"\" + i )" +
            "}", AssertionError.class);
        checkThrows("" +
            "var arr: () = (\"1\",2,3.54)" +
            "foreach (var i: String # arr) {" +
            "print(\"\" + i )" +
            "}", AssertionError.class);


    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testAny () {
        rule = grammar.root;

        check("" +
            "fun test1 (a: Any){\n" +
            "    print(a)\n" +
            "}\n" +
            "\n" +
            "test1(\"t1\")\n" +
            "test1(1)"
        ,
            null,
            "t1\n1\n");
        check("" +
                "fun test2 (a: String): Any\n" +
                "{\n" +
                "    return a\n" +
                "}\n" +
                "\n" +
                "var ret: String = test2(\"t2\")\n" +
                "print(ret)"
            ,
            null,
            "t2\n");
        check("" +
                "fun test3 (a: String): Any\n" +
                "{\n" +
                "    print(\"t3\")\n" +
                "}\n" +
                "\n" +
                "test3(\"t3\")"
            ,
            null,
            "t3\n");
        check("" +
                "fun test4 (a: Any) : Any\n" +
                "{\n" +
                "    return a\n" +
                "}\n" +
                "\n" +
                "var t4: Int = test4(5)\n" +
                "print(t4 + \"\")"
            ,
            null,
            "5\n");
        check("" +
                "var i: Any = 4\n" +
                "print(i)"
            ,
            null,
            "4\n");
        check("" +
                "struct Pair {\n" +
                "    var a: Any\n" +
                "    var b: Any\n" +
                "}\n" +
                "\n" +
                "fun sum_pair (pair: Pair): Any {\n" +
                "    return pair.a + pair.b\n" +
                "}\n" +
                "\n" +
                "print(sum_pair($Pair(2, 3)))"
            ,
            null,
            "5\n");
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testInference () {
        check("var array: Int[] = []", null);
        check("var array: String[] = []", null);
        check("fun use_array (array: Int[]) {} ; use_array([])", null);
    }

    // ---------------------------------------------------------------------------------------------

    @Test
    public void testTypeAsValues () {
        check("struct S{} ; return \"\"+ S", "S");
        check("struct S{} ; var type: Type = S ; return \"\"+ type", "S");
    }

    // ---------------------------------------------------------------------------------------------

    @Test public void testUnconditionalReturn()
    {
        check("fun f(): Int { if (true) return 1 else return 2 } ; return f()", 1L);
    }

    // ---------------------------------------------------------------------------------------------

    // NOTE(norswap): Not incredibly complete, but should cover the basics.
}
