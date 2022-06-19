package com.sun.tools.internal.xjc.util;

import com.sun.codemodel.internal.ClassType;
import com.sun.codemodel.internal.JClassAlreadyExistsException;
import com.sun.codemodel.internal.JClassContainer;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JJavaName;
import com.sun.tools.internal.xjc.ErrorReceiver;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public final class CodeModelClassFactory {
   private ErrorReceiver errorReceiver;
   private int ticketMaster = 0;

   public CodeModelClassFactory(ErrorReceiver _errorReceiver) {
      this.errorReceiver = _errorReceiver;
   }

   public JDefinedClass createClass(JClassContainer parent, String name, Locator source) {
      return this.createClass(parent, 1, name, source);
   }

   public JDefinedClass createClass(JClassContainer parent, int mod, String name, Locator source) {
      return this.createClass(parent, mod, name, source, ClassType.CLASS);
   }

   public JDefinedClass createInterface(JClassContainer parent, String name, Locator source) {
      return this.createInterface(parent, 1, name, source);
   }

   public JDefinedClass createInterface(JClassContainer parent, int mod, String name, Locator source) {
      return this.createClass(parent, mod, name, source, ClassType.INTERFACE);
   }

   public JDefinedClass createClass(JClassContainer parent, String name, Locator source, ClassType kind) {
      return this.createClass(parent, 1, name, source, kind);
   }

   public JDefinedClass createClass(JClassContainer parent, int mod, String name, Locator source, ClassType kind) {
      if (!JJavaName.isJavaIdentifier(name)) {
         this.errorReceiver.error(new SAXParseException(Messages.format("ERR_INVALID_CLASSNAME", name), source));
         return this.createDummyClass(parent);
      } else {
         try {
            if (parent.isClass() && kind == ClassType.CLASS) {
               mod |= 16;
            }

            JDefinedClass r = parent._class(mod, name, kind);
            r.metadata = source;
            return r;
         } catch (JClassAlreadyExistsException var8) {
            JDefinedClass cls = var8.getExistingClass();
            this.errorReceiver.error(new SAXParseException(Messages.format("CodeModelClassFactory.ClassNameCollision", cls.fullName()), (Locator)cls.metadata));
            this.errorReceiver.error(new SAXParseException(Messages.format("CodeModelClassFactory.ClassNameCollision.Source", name), source));
            if (!name.equals(cls.name())) {
               this.errorReceiver.error(new SAXParseException(Messages.format("CodeModelClassFactory.CaseSensitivityCollision", name, cls.name()), (Locator)null));
            }

            if (Util.equals((Locator)cls.metadata, source)) {
               this.errorReceiver.error(new SAXParseException(Messages.format("ERR_CHAMELEON_SCHEMA_GONE_WILD"), source));
            }

            return this.createDummyClass(parent);
         }
      }
   }

   private JDefinedClass createDummyClass(JClassContainer parent) {
      try {
         return parent._class("$$$garbage$$$" + this.ticketMaster++);
      } catch (JClassAlreadyExistsException var3) {
         return var3.getExistingClass();
      }
   }
}
