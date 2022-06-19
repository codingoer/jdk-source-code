package com.sun.tools.internal.ws.processor.util;

import com.sun.tools.internal.ws.processor.model.AbstractType;
import com.sun.tools.internal.ws.processor.model.Block;
import com.sun.tools.internal.ws.processor.model.ExtendedModelVisitor;
import com.sun.tools.internal.ws.processor.model.Fault;
import com.sun.tools.internal.ws.processor.model.Model;
import com.sun.tools.internal.ws.processor.model.Parameter;
import com.sun.tools.internal.ws.processor.model.Port;
import com.sun.tools.internal.ws.processor.model.Service;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBType;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBTypeVisitor;
import com.sun.tools.internal.ws.processor.model.jaxb.RpcLitStructure;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;

public class ClassNameCollector extends ExtendedModelVisitor implements JAXBTypeVisitor {
   private Set _seiClassNames;
   private Set _jaxbGeneratedClassNames;
   private Set _exceptionClassNames;
   boolean doneVisitingJAXBModel = false;
   private Set _allClassNames;
   private Set _exceptions;
   private Set _wsdlBindingNames;
   private Set _conflictingClassNames;
   private Set _portTypeNames;

   public void process(Model model) {
      try {
         this._allClassNames = new HashSet();
         this._exceptions = new HashSet();
         this._wsdlBindingNames = new HashSet();
         this._conflictingClassNames = new HashSet();
         this._seiClassNames = new HashSet();
         this._jaxbGeneratedClassNames = new HashSet();
         this._exceptionClassNames = new HashSet();
         this._portTypeNames = new HashSet();
         this.visit((Model)model);
      } catch (Exception var6) {
         var6.printStackTrace();
      } finally {
         this._allClassNames = null;
         this._exceptions = null;
      }

   }

   public Set getConflictingClassNames() {
      return this._conflictingClassNames;
   }

   protected void postVisit(Model model) throws Exception {
      Iterator iter = model.getExtraTypes();

      while(iter.hasNext()) {
         this.visitType((AbstractType)iter.next());
      }

   }

   protected void preVisit(Service service) throws Exception {
      this.registerClassName(service.getJavaInterface().getName());
   }

   protected void processPort11x(Port port) {
      QName wsdlBindingName = (QName)port.getProperty("com.sun.xml.internal.ws.processor.model.WSDLBindingName");
      if (!this._wsdlBindingNames.contains(wsdlBindingName)) {
         this.registerClassName(port.getJavaInterface().getName());
      }

      this.registerClassName((String)port.getProperty("com.sun.xml.internal.ws.processor.model.StubClassName"));
      this.registerClassName((String)port.getProperty("com.sun.xml.internal.ws.processor.model.TieClassName"));
   }

   protected void preVisit(Port port) throws Exception {
      QName portTypeName = (QName)port.getProperty("com.sun.xml.internal.ws.processor.model.WSDLPortTypeName");
      if (!this._portTypeNames.contains(portTypeName)) {
         this.addSEIClassName(port.getJavaInterface().getName());
      }
   }

   private void addSEIClassName(String s) {
      this._seiClassNames.add(s);
      this.registerClassName(s);
   }

   protected void postVisit(Port port) throws Exception {
      QName wsdlBindingName = (QName)port.getProperty("com.sun.xml.internal.ws.processor.model.WSDLBindingName");
      if (!this._wsdlBindingNames.contains(wsdlBindingName)) {
         this._wsdlBindingNames.add(wsdlBindingName);
      }

      QName portTypeName = (QName)port.getProperty("com.sun.xml.internal.ws.processor.model.WSDLPortTypeName");
      if (!this._portTypeNames.contains(portTypeName)) {
         this._portTypeNames.add(portTypeName);
      }

   }

   protected boolean shouldVisit(Port port) {
      QName wsdlBindingName = (QName)port.getProperty("com.sun.xml.internal.ws.processor.model.WSDLBindingName");
      return !this._wsdlBindingNames.contains(wsdlBindingName);
   }

   protected void preVisit(Fault fault) throws Exception {
      if (!this._exceptions.contains(fault.getJavaException())) {
         this._exceptions.add(fault.getJavaException());
         this.addExceptionClassName(fault.getJavaException().getName());
         Iterator iter = fault.getSubfaults();

         while(iter != null && iter.hasNext()) {
            Fault subfault = (Fault)iter.next();
            this.preVisit(subfault);
         }
      }

   }

   private void addExceptionClassName(String name) {
      if (this._allClassNames.contains(name)) {
         this._exceptionClassNames.add(name);
      }

      this.registerClassName(name);
   }

   protected void visitBodyBlock(Block block) throws Exception {
      this.visitBlock(block);
   }

   protected void visitHeaderBlock(Block block) throws Exception {
      this.visitBlock(block);
   }

   protected void visitFaultBlock(Block block) throws Exception {
   }

   protected void visitBlock(Block block) throws Exception {
      this.visitType(block.getType());
   }

   protected void visit(Parameter parameter) throws Exception {
      this.visitType(parameter.getType());
   }

   private void visitType(AbstractType type) throws Exception {
      if (type != null) {
         if (type instanceof JAXBType) {
            this.visitType((JAXBType)type);
         } else if (type instanceof RpcLitStructure) {
            this.visitType((RpcLitStructure)type);
         }
      }

   }

   private void visitType(JAXBType type) throws Exception {
      type.accept(this);
   }

   private void visitType(RpcLitStructure type) throws Exception {
      type.accept(this);
   }

   private void registerClassName(String name) {
      if (name != null && !name.equals("")) {
         if (this._allClassNames.contains(name)) {
            this._conflictingClassNames.add(name);
         } else {
            this._allClassNames.add(name);
         }

      }
   }

   public Set getSeiClassNames() {
      return this._seiClassNames;
   }

   public Set getJaxbGeneratedClassNames() {
      return this._jaxbGeneratedClassNames;
   }

   public Set getExceptionClassNames() {
      return this._exceptionClassNames;
   }

   public void visit(JAXBType type) throws Exception {
      if (!this.doneVisitingJAXBModel && type.getJaxbModel() != null) {
         Set classNames = type.getJaxbModel().getGeneratedClassNames();
         Iterator var3 = classNames.iterator();

         while(var3.hasNext()) {
            String className = (String)var3.next();
            this.addJAXBGeneratedClassName(className);
         }

         this.doneVisitingJAXBModel = true;
      }

   }

   public void visit(RpcLitStructure type) throws Exception {
      if (!this.doneVisitingJAXBModel) {
         Set classNames = type.getJaxbModel().getGeneratedClassNames();
         Iterator var3 = classNames.iterator();

         while(var3.hasNext()) {
            String className = (String)var3.next();
            this.addJAXBGeneratedClassName(className);
         }

         this.doneVisitingJAXBModel = true;
      }

   }

   private void addJAXBGeneratedClassName(String name) {
      this._jaxbGeneratedClassNames.add(name);
      this.registerClassName(name);
   }
}
