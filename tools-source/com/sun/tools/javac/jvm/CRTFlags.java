package com.sun.tools.javac.jvm;

public interface CRTFlags {
   int CRT_STATEMENT = 1;
   int CRT_BLOCK = 2;
   int CRT_ASSIGNMENT = 4;
   int CRT_FLOW_CONTROLLER = 8;
   int CRT_FLOW_TARGET = 16;
   int CRT_INVOKE = 32;
   int CRT_CREATE = 64;
   int CRT_BRANCH_TRUE = 128;
   int CRT_BRANCH_FALSE = 256;
   int CRT_VALID_FLAGS = 511;
}
