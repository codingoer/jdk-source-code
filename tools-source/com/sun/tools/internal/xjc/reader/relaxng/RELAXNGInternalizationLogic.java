package com.sun.tools.internal.xjc.reader.relaxng;

import com.sun.tools.internal.xjc.reader.internalizer.AbstractReferenceFinderImpl;
import com.sun.tools.internal.xjc.reader.internalizer.DOMForest;
import com.sun.tools.internal.xjc.reader.internalizer.InternalizationLogic;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.XMLFilterImpl;

public class RELAXNGInternalizationLogic implements InternalizationLogic {
   public XMLFilterImpl createExternalReferenceFinder(DOMForest parent) {
      return new ReferenceFinder(parent);
   }

   public boolean checkIfValidTargetNode(DOMForest parent, Element bindings, Element target) {
      return "http://relaxng.org/ns/structure/1.0".equals(target.getNamespaceURI());
   }

   public Element refineTarget(Element target) {
      return target;
   }

   private static final class ReferenceFinder extends AbstractReferenceFinderImpl {
      ReferenceFinder(DOMForest parent) {
         super(parent);
      }

      protected String findExternalResource(String nsURI, String localName, Attributes atts) {
         return !"http://relaxng.org/ns/structure/1.0".equals(nsURI) || !"include".equals(localName) && !"externalRef".equals(localName) ? null : atts.getValue("href");
      }
   }
}
