package com.sun.tools.internal.ws.processor.model;

import com.sun.tools.internal.ws.processor.model.java.JavaParameter;
import com.sun.tools.internal.ws.wsdl.document.MessagePart;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import java.util.ArrayList;
import java.util.List;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;

public class Parameter extends ModelObject {
   private final String entityName;
   private String name;
   private JavaParameter javaParameter;
   private AbstractType type;
   private Block block;
   private Parameter link;
   private boolean embedded;
   private String typeName;
   private String customName;
   private WebParam.Mode mode;
   private int parameterOrderPosition;
   private List annotations = new ArrayList();

   public Parameter(String name, Entity entity) {
      super(entity);
      this.name = name;
      if (entity instanceof com.sun.tools.internal.ws.wsdl.document.Message) {
         this.entityName = ((com.sun.tools.internal.ws.wsdl.document.Message)entity).getName();
      } else if (entity instanceof MessagePart) {
         this.entityName = ((MessagePart)entity).getName();
      } else {
         this.entityName = name;
      }

   }

   public String getEntityName() {
      return this.entityName;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String s) {
      this.name = s;
   }

   public JavaParameter getJavaParameter() {
      return this.javaParameter;
   }

   public void setJavaParameter(JavaParameter p) {
      this.javaParameter = p;
   }

   public AbstractType getType() {
      return this.type;
   }

   public void setType(AbstractType t) {
      this.type = t;
   }

   public String getTypeName() {
      return this.typeName;
   }

   public void setTypeName(String t) {
      this.typeName = t;
   }

   public Block getBlock() {
      return this.block;
   }

   public void setBlock(Block d) {
      this.block = d;
   }

   public Parameter getLinkedParameter() {
      return this.link;
   }

   public void setLinkedParameter(Parameter p) {
      this.link = p;
   }

   public boolean isEmbedded() {
      return this.embedded;
   }

   public void setEmbedded(boolean b) {
      this.embedded = b;
   }

   public void accept(ModelVisitor visitor) throws Exception {
      visitor.visit(this);
   }

   public int getParameterIndex() {
      return this.parameterOrderPosition;
   }

   public void setParameterIndex(int parameterOrderPosition) {
      this.parameterOrderPosition = parameterOrderPosition;
   }

   public boolean isReturn() {
      return this.parameterOrderPosition == -1;
   }

   public String getCustomName() {
      return this.customName;
   }

   public void setCustomName(String customName) {
      this.customName = customName;
   }

   public List getAnnotations() {
      return this.annotations;
   }

   public void setAnnotations(List annotations) {
      this.annotations = annotations;
   }

   public void setMode(WebParam.Mode mode) {
      this.mode = mode;
   }

   public boolean isIN() {
      return this.mode == Mode.IN;
   }

   public boolean isOUT() {
      return this.mode == Mode.OUT;
   }

   public boolean isINOUT() {
      return this.mode == Mode.INOUT;
   }
}
