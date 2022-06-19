package com.sun.tools.internal.ws.wsdl.document;

import com.sun.tools.internal.ws.wsdl.framework.ExtensionVisitor;

public interface WSDLDocumentVisitor extends ExtensionVisitor {
   void preVisit(Definitions var1) throws Exception;

   void postVisit(Definitions var1) throws Exception;

   void visit(Import var1) throws Exception;

   void preVisit(Types var1) throws Exception;

   void postVisit(Types var1) throws Exception;

   void preVisit(Message var1) throws Exception;

   void postVisit(Message var1) throws Exception;

   void visit(MessagePart var1) throws Exception;

   void preVisit(PortType var1) throws Exception;

   void postVisit(PortType var1) throws Exception;

   void preVisit(Operation var1) throws Exception;

   void postVisit(Operation var1) throws Exception;

   void preVisit(Input var1) throws Exception;

   void postVisit(Input var1) throws Exception;

   void preVisit(Output var1) throws Exception;

   void postVisit(Output var1) throws Exception;

   void preVisit(Fault var1) throws Exception;

   void postVisit(Fault var1) throws Exception;

   void preVisit(Binding var1) throws Exception;

   void postVisit(Binding var1) throws Exception;

   void preVisit(BindingOperation var1) throws Exception;

   void postVisit(BindingOperation var1) throws Exception;

   void preVisit(BindingInput var1) throws Exception;

   void postVisit(BindingInput var1) throws Exception;

   void preVisit(BindingOutput var1) throws Exception;

   void postVisit(BindingOutput var1) throws Exception;

   void preVisit(BindingFault var1) throws Exception;

   void postVisit(BindingFault var1) throws Exception;

   void preVisit(Service var1) throws Exception;

   void postVisit(Service var1) throws Exception;

   void preVisit(Port var1) throws Exception;

   void postVisit(Port var1) throws Exception;

   void visit(Documentation var1) throws Exception;
}
