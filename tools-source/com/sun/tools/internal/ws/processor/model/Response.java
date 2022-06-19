package com.sun.tools.internal.ws.processor.model;

import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Response extends Message {
   private Map _faultBlocks = new HashMap();

   public Response(com.sun.tools.internal.ws.wsdl.document.Message entity, ErrorReceiver receiver) {
      super(entity, receiver);
   }

   public void addFaultBlock(Block b) {
      if (this._faultBlocks.containsKey(b.getName())) {
         throw new ModelException("model.uniqueness", new Object[0]);
      } else {
         this._faultBlocks.put(b.getName(), b);
      }
   }

   public Iterator getFaultBlocks() {
      return this._faultBlocks.values().iterator();
   }

   public int getFaultBlockCount() {
      return this._faultBlocks.size();
   }

   public Map getFaultBlocksMap() {
      return this._faultBlocks;
   }

   public void setFaultBlocksMap(Map m) {
      this._faultBlocks = m;
   }

   public void accept(ModelVisitor visitor) throws Exception {
      visitor.visit(this);
   }
}
