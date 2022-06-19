package com.sun.tools.internal.ws.processor.model.java;

public class JavaStructureMember {
   private String name;
   private JavaType type;
   private boolean isPublic;
   private boolean isInherited;
   private String readMethod;
   private String writeMethod;
   private String declaringClass;
   private Object owner;
   private int constructorPos;

   public JavaStructureMember() {
      this.isPublic = false;
      this.isInherited = false;
   }

   public JavaStructureMember(String name, JavaType type, Object owner) {
      this(name, type, owner, false);
   }

   public JavaStructureMember(String name, JavaType type, Object owner, boolean isPublic) {
      this.isPublic = false;
      this.isInherited = false;
      this.name = name;
      this.type = type;
      this.owner = owner;
      this.isPublic = isPublic;
      this.constructorPos = -1;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String s) {
      this.name = s;
   }

   public JavaType getType() {
      return this.type;
   }

   public void setType(JavaType t) {
      this.type = t;
   }

   public boolean isPublic() {
      return this.isPublic;
   }

   public void setPublic(boolean b) {
      this.isPublic = b;
   }

   public boolean isInherited() {
      return this.isInherited;
   }

   public void setInherited(boolean b) {
      this.isInherited = b;
   }

   public String getReadMethod() {
      return this.readMethod;
   }

   public void setReadMethod(String readMethod) {
      this.readMethod = readMethod;
   }

   public String getWriteMethod() {
      return this.writeMethod;
   }

   public void setWriteMethod(String writeMethod) {
      this.writeMethod = writeMethod;
   }

   public String getDeclaringClass() {
      return this.declaringClass;
   }

   public void setDeclaringClass(String declaringClass) {
      this.declaringClass = declaringClass;
   }

   public Object getOwner() {
      return this.owner;
   }

   public void setOwner(Object owner) {
      this.owner = owner;
   }

   public int getConstructorPos() {
      return this.constructorPos;
   }

   public void setConstructorPos(int idx) {
      this.constructorPos = idx;
   }
}
