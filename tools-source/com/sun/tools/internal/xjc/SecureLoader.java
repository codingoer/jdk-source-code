package com.sun.tools.internal.xjc;

import java.security.AccessController;
import java.security.PrivilegedAction;

class SecureLoader {
   static ClassLoader getContextClassLoader() {
      return System.getSecurityManager() == null ? Thread.currentThread().getContextClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
   }

   static ClassLoader getClassClassLoader(final Class c) {
      return System.getSecurityManager() == null ? c.getClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public ClassLoader run() {
            return c.getClassLoader();
         }
      });
   }

   static ClassLoader getSystemClassLoader() {
      return System.getSecurityManager() == null ? ClassLoader.getSystemClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public ClassLoader run() {
            return ClassLoader.getSystemClassLoader();
         }
      });
   }

   static void setContextClassLoader(final ClassLoader cl) {
      if (System.getSecurityManager() == null) {
         Thread.currentThread().setContextClassLoader(cl);
      } else {
         AccessController.doPrivileged(new PrivilegedAction() {
            public ClassLoader run() {
               Thread.currentThread().setContextClassLoader(cl);
               return null;
            }
         });
      }

   }

   static ClassLoader getParentClassLoader(final ClassLoader cl) {
      return System.getSecurityManager() == null ? cl.getParent() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public ClassLoader run() {
            return cl.getParent();
         }
      });
   }
}
