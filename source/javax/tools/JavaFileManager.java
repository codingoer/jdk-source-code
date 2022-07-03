/*
 * Copyright (c) 2005, 2006, Oracle and/or its affiliates. All rights reserved.
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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import static javax.tools.JavaFileObject.Kind;

/**
 * File manager for tools operating on Java&trade; programming language source and class files.
 * In this context, <em>file</em> means an abstraction of regular files and other sources of data.
 * <br/>Java&trade工具的文件管理器; 编程语言源代码和类文件。在此上下文中，<em>file</em>表示常规文件和其他数据源的抽象。
 *
 * <p>When constructing new JavaFileObjects, the file manager must determine where to create them.
 * For example, if a file manager manages regular files on a file system,
 * it would most likely have a current/working directory to use as default location when creating or finding files.
 * <br/>在构造新的JavaFileObjects时，文件管理器必须确定在哪里创建它们。
 * <br/>例如，如果文件管理器管理文件系统上的常规文件，那么在创建或查找文件时，它很可能有一个当前/工作目录作为默认位置。
 * A number of hints can be provided to a file manager as to where to create files.  Any file manager might choose to ignore these hints.
 * <br/>可以向文件管理器提供许多关于在何处创建文件的提示。任何文件管理器都可能选择忽略这些提示。
 *
 * <p>Some methods in this interface use class names.
 * Such class names must be given in the Java Virtual Machine internal form of fully qualified class and interface names.
 * <br/>这个接口中的一些方法使用类名。这样的类名必须以Java虚拟机内部的完全限定类名和接口名的形式给出。
 * For convenience '.' and '/' are interchangeable.  The internal form is defined in chapter four of
 * <cite>The Java&trade; Virtual Machine Specification</cite>.
 * <br/>为了方便'.' and '/'是可以互换的。内部形式在Java虚拟机规范的第四章中定义

 * <blockquote><p>
 *   <i>Discussion:</i> this means that the names
 *   "java/lang.package-info", "java/lang/package-info",
 *   "java.lang.package-info", are valid and equivalent.
 *   Compare to binary name as defined in
 *   <cite>The Java&trade; Language Specification</cite>,
 *   section 13.1 "The Form of a Binary".
 * </p></blockquote>
 *
 * <p>The case of names is significant.  All names should be treated as case-sensitive.
 * For example, some file systems have case-insensitive, case-aware file names.
 * <br/>名称的情况很重要。所有名称都应该区分大小写。例如，一些文件系统具有区分大小写的文件名。
 * File objects representing such files should take care to preserve case by using {@link java.io.File#getCanonicalFile} or similar means.
 * <br/>表示此类文件的文件对象应注意使用{@link java.io.File#getCanonicalFile}或类似的方法来保留大小写。
 * If the system is not case-aware, file objects must use other means to preserve case.
 * <br/>如果系统不区分大小写，文件对象必须使用其他方法来保留大小写。
 * <p><em><a name="relative_name">Relative names</a>:</em>
 * some methods in this interface use relative names.  A relative name is a
 * non-null, non-empty sequence of path segments separated by '/'.
 * '.' or '..'  are invalid path segments.
 * A valid relative name must match the "path-rootless" rule of <a
 * href="http://www.ietf.org/rfc/rfc3986.txt">RFC&nbsp;3986</a>,
 * section&nbsp;3.3.  Informally, this should be true:
 *
 * <!-- URI.create(relativeName).normalize().getPath().equals(relativeName) -->
 * <pre>  URI.{@linkplain java.net.URI#create create}(relativeName).{@linkplain java.net.URI#normalize normalize}().{@linkplain java.net.URI#getPath getPath}().equals(relativeName)</pre>
 *
 * <p>All methods in this interface might throw a SecurityException.
 *
 * <p>An object of this interface is not required to support multi-threaded access,
 * that is, be synchronized.  However, it must
 * support concurrent access to different file objects created by this object.
 * <br/>该接口的对象不需要支持多线程访问，也就是说，要同步。但是，它必须支持对该对象创建的不同文件对象的并发访问。
 *
 * <p><em>Implementation note:</em> a consequence of this requirement
 * is that a trivial implementation of output to a {@linkplain java.util.jar.JarOutputStream} is not a sufficient implementation.
 * That is, rather than creating a JavaFileObject that returns the JarOutputStream directly,
 * the contents must be cached until closed and then written to the JarOutputStream.
 * <br/>这种需求的结果是，向JarOutputStream输出的简单实现并不是一个充分的实现。
 * <br/>也就是说，与直接创建返回JarOutputStream的JavaFileObject不同，必须缓存内容直到关闭，然后写入到JarOutputStream。
 *
 * <p>Unless explicitly allowed, all methods in this interface might
 * throw a NullPointerException if given a {@code null} argument.
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @see JavaFileObject
 * @see FileObject
 * @since 1.6
 */
public interface JavaFileManager extends Closeable, Flushable, OptionChecker {

    /**
     * Interface for locations of file objects.  Used by file managers to determine where to place or search for file objects.
     * <br/>用于文件对象位置的接口。由文件管理器使用，用于确定放置或搜索文件对象的位置。
     */
    interface Location {
        /**
         * Gets the name of this location.
         *
         * @return a name
         */
        String getName();

        /**
         * Determines if this is an output location.
         * An output location is a location that is conventionally used for output.
         * <br/>确定这是否是一个输出位置。输出位置是通常用于输出的位置。
         *
         * @return true if this is an output location, false otherwise
         */
        boolean isOutputLocation();
    }

    /**
     * Gets a class loader for loading plug-ins from the given location.
     * For example, to load annotation processors,
     * a compiler will request a class loader for the {@link StandardLocation#ANNOTATION_PROCESSOR_PATH ANNOTATION_PROCESSOR_PATH} location.
     * <br/>获取用于从给定位置装入插件的类加载器。
     * <br/>例如，要加载注释处理器，编译器会请求一个类加载器来获取位置{@link StandardLocation#ANNOTATION_PROCESSOR_PATH}。
     *
     * @param location a location
     * @return a class loader for the given location; or {@code null}
     * if loading plug-ins from the given location is disabled or if
     * the location is not known
     * @throws SecurityException if a class loader can not be created
     * in the current security context
     * @throws IllegalStateException if {@link #close} has been called
     * and this file manager cannot be reopened
     */
    ClassLoader getClassLoader(Location location);

    /**
     * Lists all file objects matching the given criteria in the given location.
     * List file objects in "subpackages" if recurse is true.
     * <br/>列出给定位置中匹配给定条件的所有文件对象。如果递归为真，列出“subpackages”中的文件对象。
     *
     * <p>Note: even if the given location is unknown to this file
     * manager, it may not return {@code null}.  Also, an unknown
     * location may not cause an exception.
     *
     * @param location     a location
     * @param packageName  a package name
     * @param kinds        return objects only of these kinds
     * @param recurse      if true include "subpackages"
     * @return an Iterable of file objects matching the given criteria
     * @throws IOException if an I/O error occurred, or if {@link
     * #close} has been called and this file manager cannot be
     * reopened
     * @throws IllegalStateException if {@link #close} has been called
     * and this file manager cannot be reopened
     */
    Iterable<JavaFileObject> list(Location location,
                                  String packageName,
                                  Set<Kind> kinds,
                                  boolean recurse)
        throws IOException;

    /**
     * Infers a binary name of a file object based on a location.
     * The binary name returned might not be a valid binary name according to <cite>The Java&trade; Language Specification</cite>.
     * 根据位置推断文件对象的二进制名称。根据Java语言规范，返回的二进制名称可能不是有效的二进制名称
     *
     * @param location a location
     * @param file a file object
     * @return a binary name or {@code null} the file object is not
     * found in the given location
     * @throws IllegalStateException if {@link #close} has been called
     * and this file manager cannot be reopened
     */
    String inferBinaryName(Location location, JavaFileObject file);

    /**
     * Compares two file objects and return true if they represent the same underlying object.
     *
     *
     * @param a a file object
     * @param b a file object
     * @return true if the given file objects represent the same
     * underlying object
     *
     * @throws IllegalArgumentException if either of the arguments
     * were created with another file manager and this file manager
     * does not support foreign file objects
     */
    boolean isSameFile(FileObject a, FileObject b);

    /**
     * Handles one option.  If {@code current} is an option to this
     * file manager it will consume any arguments to that option from
     * {@code remaining} and return true, otherwise return false.
     *
     * @param current current option
     * @param remaining remaining options
     * @return true if this option was handled by this file manager,
     * false otherwise
     * @throws IllegalArgumentException if this option to this file
     * manager is used incorrectly
     * @throws IllegalStateException if {@link #close} has been called
     * and this file manager cannot be reopened
     */
    boolean handleOption(String current, Iterator<String> remaining);

    /**
     * Determines if a location is known to this file manager.
     *
     * @param location a location
     * @return true if the location is known
     */
    boolean hasLocation(Location location);

    /**
     * Gets a {@linkplain JavaFileObject file object} for input
     * representing the specified class of the specified kind in the given location.
     * <br/>获取输入的{@linkplain JavaFileObject 文件对象}，表示给定位置中指定类型的指定类。
     *
     * @param location a location
     * @param className the name of a class
     * @param kind the kind of file, must be one of {@link
     * JavaFileObject.Kind#SOURCE SOURCE} or {@link
     * JavaFileObject.Kind#CLASS CLASS}
     * @return a file object, might return {@code null} if the
     * file does not exist
     * @throws IllegalArgumentException if the location is not known
     * to this file manager and the file manager does not support
     * unknown locations, or if the kind is not valid
     * @throws IOException if an I/O error occurred, or if {@link
     * #close} has been called and this file manager cannot be
     * reopened
     * @throws IllegalStateException if {@link #close} has been called
     * and this file manager cannot be reopened
     */
    JavaFileObject getJavaFileForInput(Location location,
                                       String className,
                                       Kind kind)
        throws IOException;

    /**
     * Gets a {@linkplain JavaFileObject file object} for output
     * representing the specified class of the specified kind in the given location.
     * <br/>获取输出的{@linkplain JavaFileObject文件对象}，表示给定位置中指定类型的指定类。
     *
     * <p>Optionally, this file manager might consider the sibling as
     * a hint for where to place the output.  The exact semantics of this hint is unspecified.
     * <br/>可选地，这个文件管理器可以将同级作为放置输出的提示。这个提示的确切语义是不确定的。
     * The JDK compiler, javac, for example, will place class files in the same directories as
     * originating source files unless a class file output directory is provided.
     * <br/>例如，JDK编译器javac会将类文件放在与原始源文件相同的目录中，除非提供了类文件输出目录。
     * To facilitate this behavior, javac might provide the originating source file as sibling when calling this method.
     * <br/>为了方便这种行为，在调用此方法时，javac可能会将原始源文件作为兄弟文件提供。
     *
     * @param location a location
     * @param className the name of a class
     * @param kind the kind of file, must be one of {@link
     * JavaFileObject.Kind#SOURCE SOURCE} or {@link
     * JavaFileObject.Kind#CLASS CLASS}
     * @param sibling a file object to be used as hint for placement;
     * might be {@code null}
     * @return a file object for output
     * @throws IllegalArgumentException if sibling is not known to
     * this file manager, or if the location is not known to this file
     * manager and the file manager does not support unknown
     * locations, or if the kind is not valid
     * @throws IOException if an I/O error occurred, or if {@link
     * #close} has been called and this file manager cannot be
     * reopened
     * @throws IllegalStateException {@link #close} has been called
     * and this file manager cannot be reopened
     */
    JavaFileObject getJavaFileForOutput(Location location,
                                        String className,
                                        Kind kind,
                                        FileObject sibling)
        throws IOException;

    /**
     * Gets a {@linkplain FileObject file object} for input
     * representing the specified <a href="JavaFileManager.html#relative_name">relative
     * name</a> in the specified package in the given location.
     *
     * <p>If the returned object represents a {@linkplain
     * JavaFileObject.Kind#SOURCE source} or {@linkplain
     * JavaFileObject.Kind#CLASS class} file, it must be an instance
     * of {@link JavaFileObject}.
     *
     * <p>Informally, the file object returned by this method is
     * located in the concatenation of the location, package name, and
     * relative name.  For example, to locate the properties file
     * "resources/compiler.properties" in the package
     * "com.sun.tools.javac" in the {@linkplain
     * StandardLocation#SOURCE_PATH SOURCE_PATH} location, this method
     * might be called like so:
     *
     * <pre>getFileForInput(SOURCE_PATH, "com.sun.tools.javac", "resources/compiler.properties");</pre>
     *
     * <p>If the call was executed on Windows, with SOURCE_PATH set to
     * <code>"C:\Documents&nbsp;and&nbsp;Settings\UncleBob\src\share\classes"</code>,
     * a valid result would be a file object representing the file
     * <code>"C:\Documents&nbsp;and&nbsp;Settings\UncleBob\src\share\classes\com\sun\tools\javac\resources\compiler.properties"</code>.
     *
     * @param location a location
     * @param packageName a package name
     * @param relativeName a relative name
     * @return a file object, might return {@code null} if the file
     * does not exist
     * @throws IllegalArgumentException if the location is not known
     * to this file manager and the file manager does not support
     * unknown locations, or if {@code relativeName} is not valid
     * @throws IOException if an I/O error occurred, or if {@link
     * #close} has been called and this file manager cannot be
     * reopened
     * @throws IllegalStateException if {@link #close} has been called
     * and this file manager cannot be reopened
     */
    FileObject getFileForInput(Location location,
                               String packageName,
                               String relativeName)
        throws IOException;

    /**
     * Gets a {@linkplain FileObject file object} for output
     * representing the specified <a href="JavaFileManager.html#relative_name">relative
     * name</a> in the specified package in the given location.
     *
     * <p>Optionally, this file manager might consider the sibling as
     * a hint for where to place the output.  The exact semantics of
     * this hint is unspecified.  The JDK compiler, javac, for
     * example, will place class files in the same directories as
     * originating source files unless a class file output directory
     * is provided.  To facilitate this behavior, javac might provide
     * the originating source file as sibling when calling this
     * method.
     *
     * <p>If the returned object represents a {@linkplain
     * JavaFileObject.Kind#SOURCE source} or {@linkplain
     * JavaFileObject.Kind#CLASS class} file, it must be an instance
     * of {@link JavaFileObject}.
     *
     * <p>Informally, the file object returned by this method is
     * located in the concatenation of the location, package name, and
     * relative name or next to the sibling argument.  See {@link
     * #getFileForInput getFileForInput} for an example.
     *
     * @param location a location
     * @param packageName a package name
     * @param relativeName a relative name
     * @param sibling a file object to be used as hint for placement;
     * might be {@code null}
     * @return a file object
     * @throws IllegalArgumentException if sibling is not known to
     * this file manager, or if the location is not known to this file
     * manager and the file manager does not support unknown
     * locations, or if {@code relativeName} is not valid
     * @throws IOException if an I/O error occurred, or if {@link
     * #close} has been called and this file manager cannot be
     * reopened
     * @throws IllegalStateException if {@link #close} has been called
     * and this file manager cannot be reopened
     */
    FileObject getFileForOutput(Location location,
                                String packageName,
                                String relativeName,
                                FileObject sibling)
        throws IOException;

    /**
     * Flushes any resources opened for output by this file manager directly or indirectly.
     * Flushing a closed file manager has no effect.
     * <br/>直接或间接刷新此文件管理器为输出而打开的任何资源。刷新已关闭的文件管理器无效。
     *
     * @throws IOException if an I/O error occurred
     * @see #close
     */
    void flush() throws IOException;

    /**
     * Releases any resources opened by this file manager directly or indirectly.
     * This might render this file manager useless and
     * the effect of subsequent calls to methods on this object or any
     * objects obtained through this object is undefined unless explicitly allowed.
     * However, closing a file manager which has already been closed has no effect.
     *
     *
     * @throws IOException if an I/O error occurred
     * @see #flush
     */
    void close() throws IOException;
}
