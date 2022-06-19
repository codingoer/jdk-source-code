package com.sun.codemodel.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JDocComment extends JCommentPart implements JGenerable {
   private static final long serialVersionUID = 1L;
   private final Map atParams = new HashMap();
   private final Map atXdoclets = new HashMap();
   private final Map atThrows = new HashMap();
   private JCommentPart atReturn = null;
   private JCommentPart atDeprecated = null;
   private final JCodeModel owner;
   private static final String INDENT = " *     ";

   public JDocComment(JCodeModel owner) {
      this.owner = owner;
   }

   public JDocComment append(Object o) {
      this.add(o);
      return this;
   }

   public JCommentPart addParam(String param) {
      JCommentPart p = (JCommentPart)this.atParams.get(param);
      if (p == null) {
         this.atParams.put(param, p = new JCommentPart());
      }

      return p;
   }

   public JCommentPart addParam(JVar param) {
      return this.addParam(param.name());
   }

   public JCommentPart addThrows(Class exception) {
      return this.addThrows(this.owner.ref(exception));
   }

   public JCommentPart addThrows(JClass exception) {
      JCommentPart p = (JCommentPart)this.atThrows.get(exception);
      if (p == null) {
         this.atThrows.put(exception, p = new JCommentPart());
      }

      return p;
   }

   public JCommentPart addReturn() {
      if (this.atReturn == null) {
         this.atReturn = new JCommentPart();
      }

      return this.atReturn;
   }

   public JCommentPart addDeprecated() {
      if (this.atDeprecated == null) {
         this.atDeprecated = new JCommentPart();
      }

      return this.atDeprecated;
   }

   public Map addXdoclet(String name) {
      Map p = (Map)this.atXdoclets.get(name);
      if (p == null) {
         this.atXdoclets.put(name, p = new HashMap());
      }

      return (Map)p;
   }

   public Map addXdoclet(String name, Map attributes) {
      Map p = (Map)this.atXdoclets.get(name);
      if (p == null) {
         this.atXdoclets.put(name, p = new HashMap());
      }

      ((Map)p).putAll(attributes);
      return (Map)p;
   }

   public Map addXdoclet(String name, String attribute, String value) {
      Map p = (Map)this.atXdoclets.get(name);
      if (p == null) {
         this.atXdoclets.put(name, p = new HashMap());
      }

      ((Map)p).put(attribute, value);
      return (Map)p;
   }

   public void generate(JFormatter f) {
      f.p("/**").nl();
      this.format(f, " * ");
      f.p(" * ").nl();
      Iterator var2 = this.atParams.entrySet().iterator();

      Map.Entry e;
      while(var2.hasNext()) {
         e = (Map.Entry)var2.next();
         f.p(" * @param ").p((String)e.getKey()).nl();
         ((JCommentPart)e.getValue()).format(f, " *     ");
      }

      if (this.atReturn != null) {
         f.p(" * @return").nl();
         this.atReturn.format(f, " *     ");
      }

      var2 = this.atThrows.entrySet().iterator();

      while(var2.hasNext()) {
         e = (Map.Entry)var2.next();
         f.p(" * @throws ").t((JClass)e.getKey()).nl();
         ((JCommentPart)e.getValue()).format(f, " *     ");
      }

      if (this.atDeprecated != null) {
         f.p(" * @deprecated").nl();
         this.atDeprecated.format(f, " *     ");
      }

      for(var2 = this.atXdoclets.entrySet().iterator(); var2.hasNext(); f.nl()) {
         e = (Map.Entry)var2.next();
         f.p(" * @").p((String)e.getKey());
         if (e.getValue() != null) {
            Iterator var4 = ((Map)e.getValue()).entrySet().iterator();

            while(var4.hasNext()) {
               Map.Entry a = (Map.Entry)var4.next();
               f.p(" ").p((String)a.getKey()).p("= \"").p((String)a.getValue()).p("\"");
            }
         }
      }

      f.p(" */").nl();
   }
}
