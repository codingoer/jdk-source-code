package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSAnnotation;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class AnnotationImpl implements XSAnnotation {
   private Object annotation;
   private final Locator locator;
   private static final LocatorImplUnmodifiable NULL_LOCATION = new LocatorImplUnmodifiable();

   public Object getAnnotation() {
      return this.annotation;
   }

   public Object setAnnotation(Object o) {
      Object r = this.annotation;
      this.annotation = o;
      return r;
   }

   public Locator getLocator() {
      return this.locator;
   }

   public AnnotationImpl(Object o, Locator _loc) {
      this.annotation = o;
      this.locator = _loc;
   }

   public AnnotationImpl() {
      this.locator = NULL_LOCATION;
   }

   private static class LocatorImplUnmodifiable extends LocatorImpl {
      private LocatorImplUnmodifiable() {
      }

      public void setColumnNumber(int columnNumber) {
      }

      public void setPublicId(String publicId) {
      }

      public void setSystemId(String systemId) {
      }

      public void setLineNumber(int lineNumber) {
      }

      // $FF: synthetic method
      LocatorImplUnmodifiable(Object x0) {
         this();
      }
   }
}
