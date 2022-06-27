package top.codingoer.compiler.example;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * JavaCompiler文档中的代码示例
 */
public class JavaCompilerDocExample {

    public static void main(String[] args) {
        DiagnosticCollectorTest();
    }


    public static void standardJavaFileManagerTest() {
        File[] files1 = new File[1];
        files1[0] = new File("/Users/lionel/IdeaProjects/codingoer/jdk8-source-code/src/top/codingoer/compiler/java/HelloWorld.java");

        File[] files2 = new File[1];
        files2[0] = new File("/Users/lionel/IdeaProjects/codingoer/jdk8-source-code/src/top/codingoer/compiler/java/HelloWorld.java");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files1));
        compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call();

        Iterable<? extends JavaFileObject> compilationUnits2 = fileManager.getJavaFileObjects(files2); // use alternative method
        // reuse the same file manager to allow caching of jar files
        compiler.getTask(null, fileManager, null, null, null, compilationUnits2).call();

        try {
            fileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void DiagnosticCollectorTest() {
        File[] files1 = new File[1];
        files1[0] = new File("/Users/lionel/IdeaProjects/codingoer/jdk8-source-code/java/HelloWorld.java");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files1));
        compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits1).call();

        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            String result = String.format("Error on line %d in %s%n", diagnostic.getLineNumber(), diagnostic.getSource().toUri());
            System.out.println(result);
        }

        try {
            fileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
