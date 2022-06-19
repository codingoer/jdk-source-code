package com.sun.tools.internal.xjc.reader.internalizer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

final class ContentHandlerNamespacePrefixAdapter extends XMLFilterImpl {
   private boolean namespacePrefixes = false;
   private String[] nsBinding = new String[8];
   private int len;
   private final AttributesImpl atts = new AttributesImpl();
   private static final String PREFIX_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
   private static final String NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces";

   public ContentHandlerNamespacePrefixAdapter() {
   }

   public ContentHandlerNamespacePrefixAdapter(XMLReader parent) {
      this.setParent(parent);
   }

   public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
      return name.equals("http://xml.org/sax/features/namespace-prefixes") ? this.namespacePrefixes : super.getFeature(name);
   }

   public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
      if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
         this.namespacePrefixes = value;
      } else if (!name.equals("http://xml.org/sax/features/namespaces") || !value) {
         super.setFeature(name, value);
      }
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      if (!"http://www.w3.org/XML/1998/namespace".equals(uri)) {
         if (this.len == this.nsBinding.length) {
            String[] buf = new String[this.nsBinding.length * 2];
            System.arraycopy(this.nsBinding, 0, buf, 0, this.nsBinding.length);
            this.nsBinding = buf;
         }

         this.nsBinding[this.len++] = prefix;
         this.nsBinding[this.len++] = uri;
         super.startPrefixMapping(prefix, uri);
      }
   }

   public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      if (this.namespacePrefixes) {
         this.atts.setAttributes((Attributes)atts);

         for(int i = 0; i < this.len; i += 2) {
            String prefix = this.nsBinding[i];
            if (prefix.length() == 0) {
               this.atts.addAttribute("http://www.w3.org/XML/1998/namespace", "xmlns", "xmlns", "CDATA", this.nsBinding[i + 1]);
            } else {
               this.atts.addAttribute("http://www.w3.org/XML/1998/namespace", prefix, "xmlns:" + prefix, "CDATA", this.nsBinding[i + 1]);
            }
         }

         atts = this.atts;
      }

      this.len = 0;
      super.startElement(uri, localName, qName, (Attributes)atts);
   }
}
