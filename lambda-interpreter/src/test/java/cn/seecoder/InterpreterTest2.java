package cn.seecoder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class InterpreterTest2 {
    PrintStream console = null;
    ByteArrayOutputStream bytes = null;
    String lineBreak;

    Interpreter interpreter;
    String initInfo;
    String steps;

    static String ZERO = "(\\f.\\x.x)";
    static String SUCC = "(\\n.\\f.\\x.f (n f x))";
    static String ONE = app(SUCC, ZERO);
    static String TWO = app(SUCC, ONE);
    static String THREE = app(SUCC, TWO);
    static String FOUR = app(SUCC, THREE);
    static String FIVE = app(SUCC, FOUR);
    static String PLUS = "(\\m.\\n.((m " + SUCC + ") n))";
    static String POW = "(\\b.\\e.e b)";       // POW not ready
    static String PRED = "(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))";
    static String SUB = "(\\m.\\n.n" + PRED + "m)";
    static String TRUE = "(\\x.\\y.x)";
    static String FALSE = "(\\x.\\y.y)";
    static String AND = "(\\p.\\q.p q p)";
    static String OR = "(\\p.\\q.p p q)";
    static String NOT = "(\\p.\\a.\\b.p b a)";
    static String IF = "(\\p.\\a.\\b.p a b)";
    static String ISZERO = "(\\n.n(\\x." + FALSE + ")" + TRUE + ")";
    static String LEQ = "(\\m.\\n." + ISZERO + "(" + SUB + "m n))";
    static String EQ = "(\\m.\\n." + AND + "(" + LEQ + "m n)(" + LEQ + "n m))";
    static String MAX = "(\\m.\\n." + IF + "(" + LEQ + " m n)n m)";
    static String MIN = "(\\m.\\n." + IF + "(" + LEQ + " m n)m n)";

    static String app(String func, String x) {
        return "(" + func + x + ")";
    }
    static String app(String func, String x, String y) {
        return "(" + "(" + func + x + ")" + y + ")";
    }
    static String app(String func, String cond, String x, String y) {
        return "(" + func + cond + x + y + ")";
    }

    String[] sources = {
            ZERO,//0
            ONE,//1
            TWO,//2
            THREE,//3
            app(PLUS, ZERO, ONE),//4
            app(PLUS, TWO, THREE),//5
            app(POW, TWO, TWO),//6
            app(PRED, ONE),//7
            app(PRED, TWO),//8
            app(SUB, FOUR, TWO),//9
            app(AND, TRUE, TRUE),//10
            app(AND, TRUE, FALSE),//11
            app(AND, FALSE, FALSE),//12
            app(OR, TRUE, TRUE),//13
            app(OR, TRUE, FALSE),//14
            app(OR, FALSE, FALSE),//15
            app(NOT, TRUE),//16
            app(NOT, FALSE),//17
            app(IF, TRUE, TRUE, FALSE),//18
            app(IF, FALSE, TRUE, FALSE),//19
            app(IF, app(OR, TRUE, FALSE), ONE, ZERO),//20
            app(IF, app(AND, TRUE, FALSE), FOUR, THREE),//21
            app(ISZERO, ZERO),//22
            app(ISZERO, ONE),//23
            app(LEQ, THREE, TWO),//24
            app(LEQ, TWO, THREE),//25
            app(EQ, TWO, FOUR),//26
            app(EQ, FIVE, FIVE),//27
            app(MAX, ONE, TWO),//28
            app(MAX, FOUR, TWO),//29
            app(MIN, ONE, TWO),//30
            app(MIN, FOUR, TWO),//31
    };

    public void testLexer(int n) {
        Lexer lexer = new Lexer(sources[n]);
        Parser parser = new Parser(lexer);
        parser.parse();
    }

    public AST testParser(int n) {
        Lexer lexer = new Lexer(sources[n]);
        Parser parser = new Parser(lexer);
        return parser.parse();
    }

    public AST testInterpreter(int n) {
        Lexer lexer = new Lexer(sources[n]);
        Parser parser = new Parser(lexer);
        interpreter = new Interpreter(parser);
        return interpreter.eval();
    }

    @Before
    public void setUp() {
        lineBreak = System.getProperty("line.separator");
        console = System.out;
        bytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bytes));
    }

    @After
    public void tearDown() {
        System.setOut(console);
    }
    
    
}
