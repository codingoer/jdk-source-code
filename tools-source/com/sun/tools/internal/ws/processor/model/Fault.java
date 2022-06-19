package com.sun.tools.internal.ws.processor.model;

import com.sun.codemodel.internal.JClass;
import com.sun.tools.internal.ws.processor.model.java.JavaException;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;

public class Fault extends ModelObject {
   private boolean wsdlException = true;
   private String name;
   private Block block;
   private JavaException javaException;
   private Set subfaults = new HashSet();
   private QName elementName = null;
   private String javaMemberName = null;
   private JClass exceptionClass;
   private String wsdlFaultName;

   public Fault(Entity entity) {
      super(entity);
   }

   public Fault(String name, Entity entity) {
      super(entity);
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String s) {
      this.name = s;
   }

   public Block getBlock() {
      return this.block;
   }

   public void setBlock(Block b) {
      this.block = b;
   }

   public JavaException getJavaException() {
      return this.javaException;
   }

   public void setJavaException(JavaException e) {
      this.javaException = e;
   }

   public void accept(ModelVisitor visitor) throws Exception {
      visitor.visit(this);
   }

   public Iterator getSubfaults() {
      return this.subfaults.isEmpty() ? null : this.subfaults.iterator();
   }

   public Set getSubfaultsSet() {
      return this.subfaults;
   }

   public void setSubfaultsSet(Set s) {
      this.subfaults = s;
   }

   public Iterator getAllFaults() {
      Set allFaults = this.getAllFaultsSet();
      return allFaults.isEmpty() ? null : allFaults.iterator();
   }

   public Set getAllFaultsSet() {
      Set transSet = new HashSet();
      Iterator iter = this.subfaults.iterator();

      while(iter.hasNext()) {
         transSet.addAll(((Fault)iter.next()).getAllFaultsSet());
      }

      transSet.addAll(this.subfaults);
      return transSet;
   }

   public QName getElementName() {
      return this.elementName;
   }

   public void setElementName(QName elementName) {
      this.elementName = elementName;
   }

   public String getJavaMemberName() {
      return this.javaMemberName;
   }

   public void setJavaMemberName(String javaMemberName) {
      this.javaMemberName = javaMemberName;
   }

   public boolean isWsdlException() {
      return this.wsdlException;
   }

   public void setWsdlException(boolean wsdlFault) {
      this.wsdlException = wsdlFault;
   }

   public void setExceptionClass(JClass ex) {
      this.exceptionClass = ex;
   }

   public JClass getExceptionClass() {
      return this.exceptionClass;
   }

   public String getWsdlFaultName() {
      return this.wsdlFaultName;
   }

   public void setWsdlFaultName(String wsdlFaultName) {
      this.wsdlFaultName = wsdlFaultName;
   }
}
