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

import java.util.Set;
import java.io.InputStream;
import java.io.OutputStream;
import javax.lang.model.SourceVersion;

/**
 * Common interface for tools that can be invoked from a program.
 * A tool is traditionally a command line program such as a compiler.
 * The set of tools available with a platform is defined by the vendor.
 * <br/>可以从程序中调用的工具的通用接口。工具通常是命令行程序，比如编译器。平台中可用的工具集由供应商定义。
 *
 * <p>Tools can be located using {@link java.util.ServiceLoader#load(Class)}.
 * <br/>可以使用{@link java.util.ServiceLoader#load(Class)}定位工具。
 *
 * @author Neal M Gafter
 * @author Peter von der Ah&eacute;
 * @author Jonathan Gibbons
 * @since 1.6
 */
public interface Tool {

    /**
     * Run the tool with the given I/O channels and arguments. By convention a tool returns 0 for success and nonzero for errors.
     * <br/>使用给定的I/O通道和参数运行该工具。按照惯例，工具返回0表示成功，返回非0表示错误。
     * Any diagnostics generated will be written to either {@code out} or {@code err} in some unspecified format.
     * <br/>生成的任何诊断将以某种未指定的格式写入{@code out}或{@code err}。
     *
     * @param in "standard" input; use System.in if null
     * @param out "standard" output; use System.out if null
     * @param err "standard" error; use System.err if null
     * @param arguments arguments to pass to the tool
     * @return 0 for success; nonzero otherwise
     * @throws NullPointerException if the array of arguments contains
     * any {@code null} elements.
     */
    int run(InputStream in, OutputStream out, OutputStream err, String... arguments);

    /**
     * Gets the source versions of the Java&trade; programming language supported by this tool.
     * <br/>获取Java&trade的源版本;该工具支持的编程语言。
     * @return a set of supported source versions
     */
    Set<SourceVersion> getSourceVersions();

}
