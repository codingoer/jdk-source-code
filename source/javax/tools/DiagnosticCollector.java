/*
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides an easy way to collect diagnostics in a list.
 * <br/>提供在列表中收集诊断信息的简单方法。
 * @param <S> the type of source objects used by diagnostics received by this object
 * <br/>泛型是此对象接收到的诊断所使用的源对象类型
 *
 * @author Peter von der Ah&eacute;
 * @since 1.6
 */
public final class DiagnosticCollector<S> implements DiagnosticListener<S> {
    private List<Diagnostic<? extends S>> diagnostics =
        Collections.synchronizedList(new ArrayList<Diagnostic<? extends S>>());

    public void report(Diagnostic<? extends S> diagnostic) {
        diagnostic.getClass(); // null check
        diagnostics.add(diagnostic);
    }

    /**
     * Gets a list view of diagnostics collected by this object.
     *
     * @return a list view of diagnostics
     */
    public List<Diagnostic<? extends S>> getDiagnostics() {
        return Collections.unmodifiableList(diagnostics);
    }
}
