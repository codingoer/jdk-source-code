package com.sun.tools.internal.ws.processor.model.jaxb;

import com.sun.tools.internal.ws.processor.model.java.JavaStructureMember;
import java.util.Iterator;
import javax.xml.namespace.QName;

public class JAXBElementMember {
   private QName _name;
   private JAXBType _type;
   private JavaStructureMember _javaStructureMember;
   private boolean _repeated;
   private boolean isInherited;
   private JAXBProperty _prop;
   private static final String JAXB_UNIQUE_PARRAM = "__jaxbUniqueParam_";

   public JAXBElementMember() {
      this.isInherited = false;
   }

   public JAXBElementMember(QName name, JAXBType type) {
      this(name, type, (JavaStructureMember)null);
   }

   public JAXBElementMember(QName name, JAXBType type, JavaStructureMember javaStructureMember) {
      this.isInherited = false;
      this._name = name;
      this._type = type;
      this._javaStructureMember = javaStructureMember;
   }

   public QName getName() {
      return this._name;
   }

   public void setName(QName n) {
      this._name = n;
   }

   public JAXBType getType() {
      return this._type;
   }

   public void setType(JAXBType t) {
      this._type = t;
   }

   public boolean isRepeated() {
      return this._repeated;
   }

   public void setRepeated(boolean b) {
      this._repeated = b;
   }

   public JavaStructureMember getJavaStructureMember() {
      return this._javaStructureMember;
   }

   public void setJavaStructureMember(JavaStructureMember javaStructureMember) {
      this._javaStructureMember = javaStructureMember;
   }

   public boolean isInherited() {
      return this.isInherited;
   }

   public void setInherited(boolean b) {
      this.isInherited = b;
   }

   public JAXBProperty getProperty() {
      if (this._prop == null && this._type != null) {
         Iterator var1 = this._type.getWrapperChildren().iterator();

         while(var1.hasNext()) {
            JAXBProperty prop = (JAXBProperty)var1.next();
            if (prop.getElementName().equals(this._name)) {
               this.setProperty(prop);
            }
         }
      }

      return this._prop;
   }

   public void setProperty(JAXBProperty prop) {
      this._prop = prop;
   }
}
