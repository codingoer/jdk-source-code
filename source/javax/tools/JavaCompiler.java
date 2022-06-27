/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package javax.tools;

import java.io.File;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Callable;
import javax.annotation.processing.Processor;

/**
 * Interface to invoke Java&trade; programming language compilers from programs.
 * <br/>调用Java&trade的接口;程序中的编程语言编译器。
 *
 * <p>The compiler might generate diagnostics during compilation (for example, error messages).
 * If a diagnostic listener is provided, the diagnostics will be supplied to the listener.
 * <br/>编译器可能在编译期间生成诊断信息(例如，错误消息)。如果提供了诊断侦听器，则将向该侦听器提供诊断信息。
 * If no listener is provided, the diagnostics will be formatted in an unspecified format and written to the default output,
 * which is {@code System.err} unless otherwise specified.
 * <br/>如果没有提供监听器，诊断将以未指定的格式格式化并写入默认输出，该输出为{@code System.err}, 除非另有说明。
 * Even if a diagnostic listener is supplied, some diagnostics might not fit in a {@code Diagnostic} and will be written to the default output.
 * <br/>即使提供了诊断侦听器，一些诊断可能不适合{@code diagnostic}，并将被写入默认输出。
 *
 * <p>A compiler tool has an associated standard file manager, which is the file manager that is native to the tool (or built-in).
 * The standard file manager can be obtained by calling {@linkplain #getStandardFileManager getStandardFileManager}.
 * <br/>编译器工具有一个相关的标准文件管理器，它是该工具本地(或内置)的文件管理器。
 * <br/>标准文件管理器可以通过调用{@linkplain #getStandardFileManager getStandardFileManager}获得。
 *
 * <p>A compiler tool must function with any file manager as long as
 * any additional requirements as detailed in the methods below are met.
 * If no file manager is provided, the compiler tool will use a standard file manager such as the one returned by {@linkplain #getStandardFileManager getStandardFileManager}.
 * <br/>只要满足下列方法中详细说明的任何附加要求，编译器工具就必须与任何文件管理器一起工作。
 * <br/>如果没有提供文件管理器，编译器工具将使用标准的文件管理器，例如{@linkplain #getStandardFileManager getStandardFileManager}返回的文件管理器。
 *
 * <p>An instance implementing this interface must conform to
 * <cite>The Java&trade; Language Specification</cite> and generate class files conforming to
 * <cite>The Java&trade; Virtual Machine Specification</cite>.
 * The versions of these specifications are defined in the {@linkplain Tool} interface.
 * <br/>实现此接口的实例必须符合Java语法规范，生成符合Java虚拟机规范的class文件。
 * <br/>这些规范的版本在{@linkplain Tool}接口中定义。
 *
 * Additionally, an instance of this interface supporting {@link javax.lang.model.SourceVersion#RELEASE_6 SourceVersion.RELEASE_6}
 * <br/>另外，该接口的一个实例支持{@link javax.lang.model。SourceVersion # RELEASE_6 SourceVersion。RELEASE_6}
 * or higher must also support {@linkplain javax.annotation.processing annotation processing}.
 * <br/>或更高版本也必须支持{@linkplain javax.annotation.processing}。
 *
 * <p>The compiler relies on two services: {@linkplain DiagnosticListener diagnostic listener} and {@linkplain JavaFileManager file manager}.
 * <br/>编译器依赖于两个服务:{@linkplain DiagnosticListener diagnostic listener}和{@linkplain JavaFileManager file manager}。
 * Although most classes and interfaces in this package defines an API for compilers (and tools in general)
 * the interfaces {@linkplain DiagnosticListener},{@linkplain JavaFileManager}, {@linkplain FileObject}, and {@linkplain JavaFileObject}
 * are not intended to be used in applications.
 * <br/>尽管这个包中的大多数类和接口为编译器(以及一般的工具)定义了一个API
 * <br/>接口{@linkplain DiagnosticListener}、{@linkplain JavaFileManager}、{@linkplain FileObject}和{@linkplain JavaFileObject}不打算用于应用程序。
 * Instead these interfaces are intended to be implemented and used to provide customized services for a
 * compiler and thus defines an SPI for compilers.
 * <br/>相反，这些接口旨在实现并用于为编译器提供定制服务，从而为编译器定义SPI。
 *
 * <p>There are a number of classes and interfaces in this package
 * which are designed to ease the implementation of the SPI to customize the behavior of a compiler:
 * <br/>这个包中有许多类和接口，它们被设计用来简化SPI的实现，以定制编译器的行为:
 *
 * <dl>
 *   <dt>{@link StandardJavaFileManager}</dt>
 *   <dd>
 *
 *     Every compiler which implements this interface provides a
 *     standard file manager for operating on regular {@linkplain java.io.File files}.
 *     The StandardJavaFileManager interface defines additional methods for creating file objects from regular files.
 *     每个实现这个接口的编译器都提供了一个标准的文件管理器，用于操作常规的{@linkplain java.io.File}。
 *     StandardJavaFileManager接口定义了用于从常规文件创建文件对象的其他方法。
 *
 *     <p>The standard file manager serves two purposes:
 *
 *     <ul>
 *       <li>basic building block for customizing how a compiler reads and writes files</li>
 *       <li>定制编译器读写文件方式的基本构建块、多个编译任务之间共享</li>
 *       <li>sharing between multiple compilation tasks</li>
 *     </ul>
 *
 *     <p>Reusing a file manager can potentially reduce overhead of scanning the file system and reading jar files.
 *     Although there might be no reduction in overhead,
 *     a standard file manager must work with multiple sequential compilations making the following example a recommended coding pattern:
 *     重用文件管理器可以潜在地减少扫描文件系统和读取jar文件的开销。
 *     虽然开销可能不会减少，一个标准的文件管理器必须使用多个顺序编译，使以下示例成为推荐的编码模式:
 *
 *     <pre>
 *       File[] files1 = ... ; // input for first compilation task
 *       File[] files2 = ... ; // input for second compilation task
 *
 *       JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 *       StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
 *
 *       {@code Iterable<? extends JavaFileObject>} compilationUnits1 =
 *           fileManager.getJavaFileObjectsFromFiles({@linkplain java.util.Arrays#asList Arrays.asList}(files1));
 *       compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call();
 *
 *       {@code Iterable<? extends JavaFileObject>} compilationUnits2 =
 *           fileManager.getJavaFileObjects(files2); // use alternative method
 *       // reuse the same file manager to allow caching of jar files
 *       compiler.getTask(null, fileManager, null, null, null, compilationUnits2).call();
 *
 *       fileManager.close();</pre>
 *
 *   </dd>
 *
 *   <dt>{@link DiagnosticCollector}</dt>
 *   <dd>
 *     Used to collect diagnostics in a list, for example:
 *     <pre>
 *       {@code Iterable<? extends JavaFileObject>} compilationUnits = ...;
 *       JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 *       {@code DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();}
 *       StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
 *       compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();
 *
 *       for ({@code Diagnostic<? extends JavaFileObject>} diagnostic : diagnostics.getDiagnostics())
 *           System.out.format("Error on line %d in %s%n",
 *                             diagnostic.getLineNumber(),
 *                             diagnostic.getSource().toUri());
 *
 *       fileManager.close();</pre>
 *   </dd>
 *
 *   <dt>
 *     {@link ForwardingJavaFileManager}, {@link ForwardingFileObject}, and
 *     {@link ForwardingJavaFileObject}
 *   </dt>
 *   <dd>
 *
 *     Subclassing is not available for overriding the behavior of a standard file manager
 *     as it is created by calling a method on a compiler, not by invoking a constructor.
 *     子类化不能用于覆盖标准文件管理器的行为. 因为它是通过调用编译器上的方法而不是调用构造函数创建的。
 *     Instead forwarding (or delegation) should be used.  These classes makes it easy to
 *     forward most calls to a given file manager or file object while allowing customizing behavior.
 *     相反，应该使用转发(或委托)。这些类使得将大多数调用转发到给定的文件管理器或文件对象很容易，同时允许自定义行为。
 *     For example, consider how to log all calls to {@linkplain JavaFileManager#flush}:
 *
 *     <pre>
 *       final {@linkplain java.util.logging.Logger Logger} logger = ...;
 *       {@code Iterable<? extends JavaFileObject>} compilationUnits = ...;
 *       JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 *       StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, null, null);
 *       JavaFileManager fileManager = new ForwardingJavaFileManager(stdFileManager) {
 *           public void flush() throws IOException {
 *               logger.entering(StandardJavaFileManager.class.getName(), "flush");
 *               super.flush();
 *               logger.exiting(StandardJavaFileManager.class.getName(), "flush");
 *           }
 *       };
 *       compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();</pre>
 *   </dd>
 *
 *   <dt>{@link SimpleJavaFileObject}</dt>
 *   <dd>
 *
 *     This class provides a basic file object implementation which
 *     can be used as building block for creating file objects.
 *     For example, here is how to define a file object which represent source code stored in a string:
 *     这个类提供了一个基本的文件对象实现，它可以用作创建文件对象的构建块。例如，下面是如何定义一个文件对象，它表示存储在字符串中的源代码:
 *
 *     <pre>
 *       /**
 *        * A file object used to represent source coming from a string.
 *        {@code *}/
 *       public class JavaSourceFromString extends SimpleJavaFileObject {
 *           /**
 *            * The source code of this "file".
 *            {@code *}/
 *           final String code;
 *
 *           /**
 *            * Constructs a new JavaSourceFromString.
 *            * {@code @}param name the name of the compilation unit represented by this file object
 *            * {@code @}param code the source code for the compilation unit represented by this file object
 *            {@code *}/
 *           JavaSourceFromString(String name, String code) {
 *               super({@linkplain java.net.URI#create URI.create}("string:///" + name.replace('.','/') + Kind.SOURCE.extension),
 *                     Kind.SOURCE);
 *               this.code = code;
 *           }
 *
 *           {@code @}Override
 *           public CharSequence getCharContent(boolean ignoreEncodingErrors) {
 *               return code;
 *           }
 *       }</pre>
 *   </dd>
 * </dl>
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @see DiagnosticListener
 * @see Diagnostic
 * @see JavaFileManager
 * @since 1.6
 */
public interface JavaCompiler extends Tool, OptionChecker {

    /**
     * Creates a future for a compilation task with the given components and arguments.
     * The compilation might not have completed as described in the CompilationTask interface.
     * <br/>使用给定的组件和参数为编译任务创建一个future。编译可能没有像CompilationTask接口中描述的那样完成。
     *
     * <p>If a file manager is provided, it must be able to handle all locations defined in {@link StandardLocation}.
     * <br/>如果提供了一个文件管理器，它必须能够处理在{@link StandardLocation}中定义的所有位置。
     *
     * <p>Note that annotation processing can process both the compilation units of source code to be compiled,
     * passed with the {@code compilationUnits} parameter, as well as class files,
     * whose names are passed with the {@code classes} parameter.
     * <br/>注意，注解处理既可以处理要编译的源代码的编译单元，
     * <br/>(通过{@code compilationUnits}参数传递)，也可以处理类文件(通过{@code classes}参数传递其名称)。
     *
     * @param out a Writer for additional output from the compiler;
     * use {@code System.err} if {@code null}
     * @param fileManager a file manager; if {@code null} use the
     * compiler's standard filemanager
     * @param diagnosticListener a diagnostic listener; if {@code
     * null} use the compiler's default method for reporting
     * diagnostics
     * @param options compiler options, {@code null} means no options
     * @param classes names of classes to be processed by annotation
     * processing, {@code null} means no class names
     * @param compilationUnits the compilation units to compile, {@code
     * null} means no compilation units
     * @return an object representing the compilation
     * @throws RuntimeException if an unrecoverable error
     * occurred in a user supplied component.  The
     * {@linkplain Throwable#getCause() cause} will be the error in
     * user code.
     * @throws IllegalArgumentException if any of the options are invalid,
     * or if any of the given compilation units are of other kind than
     * {@linkplain JavaFileObject.Kind#SOURCE source}
     */
    CompilationTask getTask(Writer out,
                            JavaFileManager fileManager,
                            DiagnosticListener<? super JavaFileObject> diagnosticListener,
                            Iterable<String> options,
                            Iterable<String> classes,
                            Iterable<? extends JavaFileObject> compilationUnits);

    /**
     * Gets a new instance of the standard file manager implementation for this tool.
     * The file manager will use the given diagnostic listener for producing any non-fatal diagnostics.
     * Fatal errors will be signaled with the appropriate exceptions.
     * <br/>获取此工具的标准文件管理器实现的新实例。文件管理器将使用给定的诊断侦听器生成任何非致命诊断。致命错误将以适当的异常表示。
     *
     * <p>The standard file manager will be automatically reopened if
     * it is accessed after calls to {@code flush} or {@code close}.
     * The standard file manager must be usable with other tools.
     *
     * @param diagnosticListener a diagnostic listener for non-fatal
     * diagnostics; if {@code null} use the compiler's default method
     * for reporting diagnostics
     * @param locale the locale to apply when formatting diagnostics;
     * {@code null} means the {@linkplain Locale#getDefault() default locale}.
     * @param charset the character set used for decoding bytes; if
     * {@code null} use the platform default
     * @return the standard file manager
     */
    StandardJavaFileManager getStandardFileManager(
        DiagnosticListener<? super JavaFileObject> diagnosticListener,
        Locale locale,
        Charset charset);

    /**
     * Interface representing a future for a compilation task.  The compilation task has not yet started.
     * To start the task, call the {@linkplain #call call} method.
     * <br/>接口，表示编译任务的未来。编译任务尚未启动。要启动该任务，请调用{@linkplain #call call}方法。
     *
     * <p>Before calling the call method, additional aspects of the task can be configured,
     * for example, by calling the {@linkplain #setProcessors setProcessors} method.
     * <br/>在调用call方法之前，可以配置任务的其他方面，例如，通过调用{@linkplain #setProcessors setProcessors}方法。
     */
    interface CompilationTask extends Callable<Boolean> {

        /**
         * Sets processors (for annotation processing).  This will
         * bypass the normal discovery mechanism.
         *
         * @param processors processors (for annotation processing)
         * @throws IllegalStateException if the task has started
         */
        void setProcessors(Iterable<? extends Processor> processors);

        /**
         * Set the locale to be applied when formatting diagnostics and
         * other localized data.
         *
         * @param locale the locale to apply; {@code null} means apply no
         * locale
         * @throws IllegalStateException if the task has started
         */
        void setLocale(Locale locale);

        /**
         * Performs this compilation task.  The compilation may only be performed once.
         * Subsequent calls to this method throw IllegalStateException.
         * <br/>执行编译任务。编译只能执行一次。对该方法的后续调用将抛出IllegalStateException。
         *
         * @return true if and only all the files compiled without errors;
         * false otherwise
         *
         * @throws RuntimeException if an unrecoverable error occurred
         * in a user-supplied component.  The
         * {@linkplain Throwable#getCause() cause} will be the error
         * in user code.
         * @throws IllegalStateException if called more than once
         */
        Boolean call();
    }
}
