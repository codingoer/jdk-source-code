package com.sun.tools.internal.ws.processor.model;

import java.util.Iterator;

public class ExtendedModelVisitor {
   public void visit(Model model) throws Exception {
      this.preVisit(model);
      Iterator var2 = model.getServices().iterator();

      while(var2.hasNext()) {
         Service service = (Service)var2.next();
         this.preVisit(service);

         Port port;
         for(Iterator var4 = service.getPorts().iterator(); var4.hasNext(); this.postVisit(port)) {
            port = (Port)var4.next();
            this.preVisit(port);
            if (this.shouldVisit(port)) {
               Iterator var6 = port.getOperations().iterator();

               while(var6.hasNext()) {
                  Operation operation = (Operation)var6.next();
                  this.preVisit(operation);
                  Request request = operation.getRequest();
                  if (request != null) {
                     this.preVisit(request);
                     Iterator iter4 = request.getHeaderBlocks();

                     Block block;
                     while(iter4.hasNext()) {
                        block = (Block)iter4.next();
                        this.visitHeaderBlock(block);
                     }

                     iter4 = request.getBodyBlocks();

                     while(iter4.hasNext()) {
                        block = (Block)iter4.next();
                        this.visitBodyBlock(block);
                     }

                     iter4 = request.getParameters();

                     while(iter4.hasNext()) {
                        Parameter parameter = (Parameter)iter4.next();
                        this.visit(parameter);
                     }

                     this.postVisit(request);
                  }

                  Response response = operation.getResponse();
                  Iterator iter4;
                  if (response != null) {
                     this.preVisit(response);
                     iter4 = response.getHeaderBlocks();

                     Block block;
                     while(iter4.hasNext()) {
                        block = (Block)iter4.next();
                        this.visitHeaderBlock(block);
                     }

                     iter4 = response.getBodyBlocks();

                     while(iter4.hasNext()) {
                        block = (Block)iter4.next();
                        this.visitBodyBlock(block);
                     }

                     iter4 = response.getParameters();

                     while(iter4.hasNext()) {
                        Parameter parameter = (Parameter)iter4.next();
                        this.visit(parameter);
                     }

                     this.postVisit(response);
                  }

                  iter4 = operation.getFaults();

                  while(iter4.hasNext()) {
                     Fault fault = (Fault)iter4.next();
                     this.preVisit(fault);
                     this.visitFaultBlock(fault.getBlock());
                     this.postVisit(fault);
                  }

                  this.postVisit(operation);
               }
            }
         }

         this.postVisit(service);
      }

      this.postVisit(model);
   }

   protected boolean shouldVisit(Port port) {
      return true;
   }

   protected void preVisit(Model model) throws Exception {
   }

   protected void postVisit(Model model) throws Exception {
   }

   protected void preVisit(Service service) throws Exception {
   }

   protected void postVisit(Service service) throws Exception {
   }

   protected void preVisit(Port port) throws Exception {
   }

   protected void postVisit(Port port) throws Exception {
   }

   protected void preVisit(Operation operation) throws Exception {
   }

   protected void postVisit(Operation operation) throws Exception {
   }

   protected void preVisit(Request request) throws Exception {
   }

   protected void postVisit(Request request) throws Exception {
   }

   protected void preVisit(Response response) throws Exception {
   }

   protected void postVisit(Response response) throws Exception {
   }

   protected void preVisit(Fault fault) throws Exception {
   }

   protected void postVisit(Fault fault) throws Exception {
   }

   protected void visitBodyBlock(Block block) throws Exception {
   }

   protected void visitHeaderBlock(Block block) throws Exception {
   }

   protected void visitFaultBlock(Block block) throws Exception {
   }

   protected void visit(Parameter parameter) throws Exception {
   }
}
