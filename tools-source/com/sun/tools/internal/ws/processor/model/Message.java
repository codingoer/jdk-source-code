package com.sun.tools.internal.ws.processor.model;

import com.sun.tools.internal.ws.resources.ModelMessages;
import com.sun.tools.internal.ws.wscompile.AbortException;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Message extends ModelObject {
   private Map _attachmentBlocks = new HashMap();
   private Map _bodyBlocks = new HashMap();
   private Map _headerBlocks = new HashMap();
   private Map _unboundBlocks = new HashMap();
   private List _parameters = new ArrayList();
   private Map _parametersByName = new HashMap();

   protected Message(com.sun.tools.internal.ws.wsdl.document.Message entity, ErrorReceiver receiver) {
      super(entity);
      this.setErrorReceiver(receiver);
   }

   public void addBodyBlock(Block b) {
      if (this._bodyBlocks.containsKey(b.getName())) {
         this.errorReceiver.error(this.getEntity().getLocator(), ModelMessages.MODEL_PART_NOT_UNIQUE(((com.sun.tools.internal.ws.wsdl.document.Message)this.getEntity()).getName(), b.getName()));
         throw new AbortException();
      } else {
         this._bodyBlocks.put(b.getName(), b);
         b.setLocation(1);
      }
   }

   public Iterator getBodyBlocks() {
      return this._bodyBlocks.values().iterator();
   }

   public int getBodyBlockCount() {
      return this._bodyBlocks.size();
   }

   public Map getBodyBlocksMap() {
      return this._bodyBlocks;
   }

   public void setBodyBlocksMap(Map m) {
      this._bodyBlocks = m;
   }

   public boolean isBodyEmpty() {
      return this.getBodyBlocks().hasNext();
   }

   public boolean isBodyEncoded() {
      boolean isEncoded = false;
      Iterator iter = this.getBodyBlocks();

      while(iter.hasNext()) {
         Block bodyBlock = (Block)iter.next();
         if (bodyBlock.getType().isSOAPType()) {
            isEncoded = true;
         }
      }

      return isEncoded;
   }

   public void addHeaderBlock(Block b) {
      if (this._headerBlocks.containsKey(b.getName())) {
         this.errorReceiver.error(this.getEntity().getLocator(), ModelMessages.MODEL_PART_NOT_UNIQUE(((com.sun.tools.internal.ws.wsdl.document.Message)this.getEntity()).getName(), b.getName()));
         throw new AbortException();
      } else {
         this._headerBlocks.put(b.getName(), b);
         b.setLocation(2);
      }
   }

   public Iterator getHeaderBlocks() {
      return this._headerBlocks.values().iterator();
   }

   public Collection getHeaderBlockCollection() {
      return this._headerBlocks.values();
   }

   public int getHeaderBlockCount() {
      return this._headerBlocks.size();
   }

   public Map getHeaderBlocksMap() {
      return this._headerBlocks;
   }

   public void setHeaderBlocksMap(Map m) {
      this._headerBlocks = m;
   }

   public void addAttachmentBlock(Block b) {
      if (this._attachmentBlocks.containsKey(b.getName())) {
         this.errorReceiver.error(this.getEntity().getLocator(), ModelMessages.MODEL_PART_NOT_UNIQUE(((com.sun.tools.internal.ws.wsdl.document.Message)this.getEntity()).getName(), b.getName()));
         throw new AbortException();
      } else {
         this._attachmentBlocks.put(b.getName(), b);
         b.setLocation(3);
      }
   }

   public void addUnboundBlock(Block b) {
      if (!this._unboundBlocks.containsKey(b.getName())) {
         this._unboundBlocks.put(b.getName(), b);
         b.setLocation(0);
      }
   }

   public Iterator getUnboundBlocks() {
      return this._unboundBlocks.values().iterator();
   }

   public Map getUnboundBlocksMap() {
      return this._unboundBlocks;
   }

   public int getUnboundBlocksCount() {
      return this._unboundBlocks.size();
   }

   public void setUnboundBlocksMap(Map m) {
      this._unboundBlocks = m;
   }

   public Iterator getAttachmentBlocks() {
      return this._attachmentBlocks.values().iterator();
   }

   public int getAttachmentBlockCount() {
      return this._attachmentBlocks.size();
   }

   public Map getAttachmentBlocksMap() {
      return this._attachmentBlocks;
   }

   public void setAttachmentBlocksMap(Map m) {
      this._attachmentBlocks = m;
   }

   public void addParameter(Parameter p) {
      if (this._parametersByName.containsKey(p.getName())) {
         this.errorReceiver.error(this.getEntity().getLocator(), ModelMessages.MODEL_PARAMETER_NOTUNIQUE(p.getName(), p.getName()));
         throw new AbortException();
      } else {
         this._parameters.add(p);
         String name = p.getCustomName() != null ? p.getCustomName() : p.getName();
         this._parametersByName.put(name, p);
      }
   }

   public Parameter getParameterByName(String name) {
      if (this._parametersByName.size() != this._parameters.size()) {
         this.initializeParametersByName();
      }

      return (Parameter)this._parametersByName.get(name);
   }

   public Iterator getParameters() {
      return this._parameters.iterator();
   }

   public List getParametersList() {
      return this._parameters;
   }

   public void setParametersList(List l) {
      this._parameters = l;
   }

   private void initializeParametersByName() {
      this._parametersByName = new HashMap();
      if (this._parameters != null) {
         Iterator iter = this._parameters.iterator();

         while(iter.hasNext()) {
            Parameter param = (Parameter)iter.next();
            if (param.getName() != null && this._parametersByName.containsKey(param.getName())) {
               this.errorReceiver.error(this.getEntity().getLocator(), ModelMessages.MODEL_PARAMETER_NOTUNIQUE(param.getName(), param.getName()));
               throw new AbortException();
            }

            this._parametersByName.put(param.getName(), param);
         }
      }

   }

   public Set getAllBlocks() {
      Set blocks = new HashSet();
      blocks.addAll(this._bodyBlocks.values());
      blocks.addAll(this._headerBlocks.values());
      blocks.addAll(this._attachmentBlocks.values());
      return blocks;
   }
}
