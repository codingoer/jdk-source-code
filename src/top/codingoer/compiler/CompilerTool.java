package top.codingoer.compiler;

import javax.lang.model.SourceVersion;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class CompilerTool {

    public static void main(String[] args) {
        compilerWithOptions();
    }

    public static void compilerWithOptions() {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        for( final SourceVersion version: compiler.getSourceVersions() ) {
            System.out.println( version );
        }
    }

}
