package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.BGMBuilder;
import com.sun.xml.internal.bind.annotation.XmlLocation;
import com.sun.xml.internal.xsom.XSComponent;
import java.util.Collection;
import java.util.Collections;
import org.xml.sax.Locator;

abstract class AbstractDeclarationImpl implements BIDeclaration {
   @XmlLocation
   Locator loc;
   protected BindInfo parent;
   private boolean isAcknowledged = false;

   /** @deprecated */
   @Deprecated
   protected AbstractDeclarationImpl(Locator loc) {
      this.loc = loc;
   }

   protected AbstractDeclarationImpl() {
   }

   public Locator getLocation() {
      return this.loc;
   }

   public void setParent(BindInfo p) {
      this.parent = p;
   }

   protected final XSComponent getOwner() {
      return this.parent.getOwner();
   }

   protected final BGMBuilder getBuilder() {
      return this.parent.getBuilder();
   }

   protected final JCodeModel getCodeModel() {
      return (JCodeModel)Ring.get(JCodeModel.class);
   }

   public final boolean isAcknowledged() {
      return this.isAcknowledged;
   }

   public void onSetOwner() {
   }

   public Collection getChildren() {
      return Collections.emptyList();
   }

   public void markAsAcknowledged() {
      this.isAcknowledged = true;
   }
}
