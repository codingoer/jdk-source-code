package top.codingoer.compiler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class CompilerToolTest {

    public static void main(String[] args) {
        test1();
    }

    public static void test1() {
        // The tool provider is used to get a compiler instance
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

        javac.run(null, null, null, "C:\\Users\\enmonster\\jdk8-source-code\\src\\top\\codingoer\\compiler\\HelloWorld.java");
    }

    public static void test2() {

    }
}
