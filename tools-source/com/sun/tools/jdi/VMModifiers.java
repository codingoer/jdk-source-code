package com.sun.tools.jdi;

public interface VMModifiers {
   int PUBLIC = 1;
   int PRIVATE = 2;
   int PROTECTED = 4;
   int STATIC = 8;
   int FINAL = 16;
   int SYNCHRONIZED = 32;
   int VOLATILE = 64;
   int BRIDGE = 64;
   int TRANSIENT = 128;
   int VARARGS = 128;
   int NATIVE = 256;
   int INTERFACE = 512;
   int ABSTRACT = 1024;
   int ENUM_CONSTANT = 16384;
   int SYNTHETIC = -268435456;
}
