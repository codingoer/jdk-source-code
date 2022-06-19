package com.sun.tools.internal.ws.processor.model.java;

import com.sun.tools.internal.ws.processor.model.ModelException;
import com.sun.tools.internal.ws.util.ClassNameInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JavaInterface {
   private String javadoc;
   private String name;
   private String realName;
   private String impl;
   private List methods;
   private List interfaces;

   public JavaInterface() {
      this.methods = new ArrayList();
      this.interfaces = new ArrayList();
   }

   public JavaInterface(String name) {
      this(name, (String)null);
   }

   public JavaInterface(String name, String impl) {
      this.methods = new ArrayList();
      this.interfaces = new ArrayList();
      this.realName = name;
      this.name = name.replace('$', '.');
      this.impl = impl;
   }

   public String getName() {
      return this.name;
   }

   public String getFormalName() {
      return this.name;
   }

   public void setFormalName(String s) {
      this.name = s;
   }

   public String getRealName() {
      return this.realName;
   }

   public void setRealName(String s) {
      this.realName = s;
   }

   public String getImpl() {
      return this.impl;
   }

   public void setImpl(String s) {
      this.impl = s;
   }

   public Iterator getMethods() {
      return this.methods.iterator();
   }

   public boolean hasMethod(JavaMethod method) {
      for(int i = 0; i < this.methods.size(); ++i) {
         if (method.equals((JavaMethod)this.methods.get(i))) {
            return true;
         }
      }

      return false;
   }

   public void addMethod(JavaMethod method) {
      if (this.hasMethod(method)) {
         throw new ModelException("model.uniqueness", new Object[0]);
      } else {
         this.methods.add(method);
      }
   }

   public List getMethodsList() {
      return this.methods;
   }

   public void setMethodsList(List l) {
      this.methods = l;
   }

   public boolean hasInterface(String interfaceName) {
      for(int i = 0; i < this.interfaces.size(); ++i) {
         if (interfaceName.equals((String)this.interfaces.get(i))) {
            return true;
         }
      }

      return false;
   }

   public void addInterface(String interfaceName) {
      if (!this.hasInterface(interfaceName)) {
         this.interfaces.add(interfaceName);
      }
   }

   public Iterator getInterfaces() {
      return this.interfaces.iterator();
   }

   public List getInterfacesList() {
      return this.interfaces;
   }

   public void setInterfacesList(List l) {
      this.interfaces = l;
   }

   public String getSimpleName() {
      return ClassNameInfo.getName(this.name);
   }

   public String getJavaDoc() {
      return this.javadoc;
   }

   public void setJavaDoc(String javadoc) {
      this.javadoc = javadoc;
   }
}
