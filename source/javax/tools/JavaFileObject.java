/*
 * Copyright (c) 2005, 2012, Oracle and/or its affiliates. All rights reserved.
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

import javax.lang.model.element.NestingKind;
import javax.lang.model.element.Modifier;

/**
 * File abstraction for tools operating on Java&trade; programming language source and class files.
 * <br/>在Java&trade上操作工具的文件抽象; 编程语言源代码和类文件。
 *
 * <p>All methods in this interface might throw a SecurityException if a security exception occurs.
 * <br/>如果发生安全异常，此接口中的所有方法都可能抛出SecurityException。
 *
 * <p>Unless explicitly allowed, all methods in this interface might throw a NullPointerException if given a {@code null} argument.
 * <br/>除非明确允许，否则如果给出一个{@code null}参数，该接口中的所有方法都可能抛出NullPointerException。
 *
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @see JavaFileManager
 * @since 1.6
 */
public interface JavaFileObject extends FileObject {

    /**
     * Kinds of JavaFileObjects.
     */
    enum Kind {
        /**
         * Source files written in the Java programming language.  For
         * example, regular files ending with {@code .java}.
         */
        SOURCE(".java"),

        /**
         * Class files for the Java Virtual Machine.  For example,
         * regular files ending with {@code .class}.
         */
        CLASS(".class"),

        /**
         * HTML files.  For example, regular files ending with {@code
         * .html}.
         */
        HTML(".html"),

        /**
         * Any other kind.
         */
        OTHER("");
        /**
         * The extension which (by convention) is normally used for
         * this kind of file object.  If no convention exists, the
         * empty string ({@code ""}) is used.
         */
        public final String extension;
        private Kind(String extension) {
            extension.getClass(); // null check
            this.extension = extension;
        }
    };

    /**
     * Gets the kind of this file object.
     *
     * @return the kind
     */
    Kind getKind();

    /**
     * Checks if this file object is compatible with the specified
     * simple name and kind.  A simple name is a single identifier
     * (not qualified) as defined in
     * <cite>The Java&trade; Language Specification</cite>,
     * section 6.2 "Names and Identifiers".
     *
     * @param simpleName a simple name of a class
     * @param kind a kind
     * @return {@code true} if this file object is compatible; false
     * otherwise
     */
    boolean isNameCompatible(String simpleName, Kind kind);

    /**
     * Provides a hint about the nesting level of the class
     * represented by this file object.  This method may return
     * {@link NestingKind#MEMBER} to mean
     * {@link NestingKind#LOCAL} or {@link NestingKind#ANONYMOUS}.
     * If the nesting level is not known or this file object does not
     * represent a class file this method returns {@code null}.
     *
     * @return the nesting kind, or {@code null} if the nesting kind
     * is not known
     */
    NestingKind getNestingKind();

    /**
     * Provides a hint about the access level of the class represented
     * by this file object.  If the access level is not known or if
     * this file object does not represent a class file this method
     * returns {@code null}.
     *
     * @return the access level
     */
    Modifier getAccessLevel();

}
