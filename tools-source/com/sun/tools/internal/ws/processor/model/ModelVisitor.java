package com.sun.tools.internal.ws.processor.model;

public interface ModelVisitor {
   void visit(Model var1) throws Exception;

   void visit(Service var1) throws Exception;

   void visit(Port var1) throws Exception;

   void visit(Operation var1) throws Exception;

   void visit(Request var1) throws Exception;

   void visit(Response var1) throws Exception;

   void visit(Fault var1) throws Exception;

   void visit(Block var1) throws Exception;

   void visit(Parameter var1) throws Exception;
}
