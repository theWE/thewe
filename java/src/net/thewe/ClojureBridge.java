package net.thewe;

import clojure.lang.LispReader;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import java.io.PushbackReader;
import java.io.StringReader;

public class ClojureBridge {
    final static private Symbol CLOJURE_MAIN = Symbol.create("clojure.main");
    final static private Var REQUIRE = Var.intern(RT.CLOJURE_NS, Symbol.create("require"));

    static {
        try {
            REQUIRE.invoke(CLOJURE_MAIN);
        }
        catch (Exception e) {
        }
    }

    public static Object read(String exprStr) throws Exception {
        return LispReader.read(new PushbackReader(new StringReader(exprStr)), false, null, true);
    }

    public static Object eval(Object expr) throws Exception {
        return clojure.lang.Compiler.eval(expr);
    }

    public static Object eval(String exprStr) throws Exception {
        return eval(read(exprStr));
    }

    public static void main(String[] args) throws Exception {
        eval("(defn f [] 2)");
        System.out.println(eval("(f)"));
    }
}
