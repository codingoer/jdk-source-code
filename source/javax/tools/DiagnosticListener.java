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

/**
 * Interface for receiving diagnostics from tools. 接收工具诊断的接口。
 *
 * @param <S> the type of source objects used by diagnostics received by this listener
 * 这个泛型由此侦听器接收的诊断程序使用。
 *
 * @author Jonathan Gibbons
 * @author Peter von der Ah&eacute;
 * @since 1.6
 */
public interface DiagnosticListener<S> {
    /**
     * Invoked when a problem is found. 发现问题时调用。
     *
     * @param diagnostic a diagnostic representing the problem that was found
     *
     * @throws NullPointerException if the diagnostic argument is
     * {@code null} and the implementation cannot handle {@code null} arguments
     *
     */
    void report(Diagnostic<? extends S> diagnostic);
}
