package com.sun.tools.corba.se.idl;

import com.sun.tools.corba.se.idl.constExpr.Expression;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

public class SequenceEntry extends SymtabEntry {
   static SequenceGen sequenceGen;
   private Expression _maxSize = null;
   private Vector _contained = new Vector();

   protected SequenceEntry() {
      this.repositoryID(Util.emptyID);
   }

   protected SequenceEntry(SequenceEntry var1) {
      super(var1);
      this._maxSize = var1._maxSize;
   }

   protected SequenceEntry(SymtabEntry var1, IDLID var2) {
      super(var1, var2);
      if (!(var1 instanceof SequenceEntry)) {
         if (this.module().equals("")) {
            this.module(this.name());
         } else if (!this.name().equals("")) {
            this.module(this.module() + "/" + this.name());
         }
      }

      this.repositoryID(Util.emptyID);
   }

   public Object clone() {
      return new SequenceEntry(this);
   }

   public boolean isReferencable() {
      return this.type().isReferencable();
   }

   public void isReferencable(boolean var1) {
   }

   public void generate(Hashtable var1, PrintWriter var2) {
      sequenceGen.generate(var1, this, var2);
   }

   public Generator generator() {
      return sequenceGen;
   }

   public void maxSize(Expression var1) {
      this._maxSize = var1;
   }

   public Expression maxSize() {
      return this._maxSize;
   }

   public void addContained(SymtabEntry var1) {
      this._contained.addElement(var1);
   }

   public Vector contained() {
      return this._contained;
   }
}
