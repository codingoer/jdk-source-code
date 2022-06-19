package com.sun.xml.internal.dtdparser;

final class InternalEntity extends EntityDecl {
   char[] buf;

   InternalEntity(String name, char[] value) {
      this.name = name;
      this.buf = value;
   }
}
