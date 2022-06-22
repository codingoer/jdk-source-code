package top.codingoer.compiler;

import javax.lang.model.SourceVersion;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.Tool;
import javax.tools.ToolProvider;
import java.util.ServiceLoader;

public class CompilerTool {

    public static void main(String[] args) {
        //compilerWithOptions();
        serviceLoader();
    }

    public static void compilerWithOptions() {
        final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        for( final SourceVersion version: javac.getSourceVersions() ) {
            System.out.println( version );
        }

        //StandardJavaFileManager standardFileManager = javac.getStandardFileManager(null, null, null);
        //standardFileManager.getJavaFileForInput();
    }

    public static void serviceLoader() {
        ServiceLoader<Tool> tools = ServiceLoader.loadInstalled(Tool.class);
        for (Tool tool : tools) {
            System.out.println("Found tool: " + tool);
        }
    }
}
