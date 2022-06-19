package com.sun.tools.internal.ws.wsdl.framework;

import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import org.xml.sax.Locator;

public abstract class GlobalEntity extends Entity implements GloballyKnown {
   private Defining _defining;
   private String _name;

   public GlobalEntity(Defining defining, Locator locator, ErrorReceiver errorReceiver) {
      super(locator);
      this._defining = defining;
      this.errorReceiver = errorReceiver;
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public abstract Kind getKind();

   public Defining getDefining() {
      return this._defining;
   }
}
