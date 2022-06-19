package com.sun.xml.internal.xsom;

import com.sun.xml.internal.xsom.parser.SchemaDocument;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import java.util.Collection;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Locator;

public interface XSComponent {
   XSAnnotation getAnnotation();

   XSAnnotation getAnnotation(boolean var1);

   List getForeignAttributes();

   String getForeignAttribute(String var1, String var2);

   Locator getLocator();

   XSSchema getOwnerSchema();

   XSSchemaSet getRoot();

   SchemaDocument getSourceDocument();

   Collection select(String var1, NamespaceContext var2);

   XSComponent selectSingle(String var1, NamespaceContext var2);

   void visit(XSVisitor var1);

   Object apply(XSFunction var1);
}
