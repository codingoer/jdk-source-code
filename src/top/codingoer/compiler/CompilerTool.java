package top.codingoer.compiler;

import javax.lang.model.SourceVersion;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.Tool;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.ServiceLoader;

public class CompilerTool {

    public static void main(String[] args) {
        compilerWithOptions();
        //serviceLoader();
    }

    public static void compilerWithOptions() {
        final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        for( final SourceVersion version: javac.getSourceVersions() ) {
            System.out.println( version );
        }

        File file = new File("/Users/lionel/IdeaProjects/codingoer/jdk8-source-code/src/top/codingoer/compiler/HelloWorld.java");

        StandardJavaFileManager standardFileManager = javac.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javaFileObjects = standardFileManager.getJavaFileObjects(file);
        JavaCompiler.CompilationTask task = javac.getTask(null, standardFileManager, null,
                null, null, javaFileObjects);
        Boolean call = task.call();
    }

    public static void serviceLoader() {
        ServiceLoader<Tool> tools = ServiceLoader.loadInstalled(Tool.class);
        for (Tool tool : tools) {
            System.out.println("Found tool: " + tool);
        }
    }
}
