package com.sun.tools.internal.xjc.reader.relaxng;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JPackage;
import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CClassInfoParent;
import com.sun.tools.internal.xjc.model.CCustomizations;
import com.sun.tools.internal.xjc.model.CEnumConstant;
import com.sun.tools.internal.xjc.model.CEnumLeafInfo;
import com.sun.tools.internal.xjc.model.CNonElement;
import com.sun.tools.internal.xjc.model.CTypeInfo;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.rngom.digested.DChoicePattern;
import com.sun.xml.internal.rngom.digested.DDefine;
import com.sun.xml.internal.rngom.digested.DElementPattern;
import com.sun.xml.internal.rngom.digested.DPattern;
import com.sun.xml.internal.rngom.digested.DPatternWalker;
import com.sun.xml.internal.rngom.digested.DRefPattern;
import com.sun.xml.internal.rngom.digested.DValuePattern;
import com.sun.xml.internal.rngom.nc.NameClass;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSSchemaSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

public final class RELAXNGCompiler {
   final DPattern grammar;
   final Set defs;
   final Options opts;
   final Model model;
   final JPackage pkg;
   final Map datatypes = new HashMap();
   final Map classes = new HashMap();
   final Map bindQueue = new HashMap();
   final TypeUseBinder typeUseBinder = new TypeUseBinder(this);

   public static Model build(DPattern grammar, JCodeModel codeModel, Options opts) {
      RELAXNGCompiler compiler = new RELAXNGCompiler(grammar, codeModel, opts);
      compiler.compile();
      return compiler.model;
   }

   public RELAXNGCompiler(DPattern grammar, JCodeModel codeModel, Options opts) {
      this.grammar = grammar;
      this.opts = opts;
      this.model = new Model(opts, codeModel, NameConverter.smart, opts.classNameAllocator, (XSSchemaSet)null);
      this.datatypes.put("", DatatypeLib.BUILTIN);
      this.datatypes.put("http://www.w3.org/2001/XMLSchema-datatypes", DatatypeLib.XMLSCHEMA);
      DefineFinder deff = new DefineFinder();
      grammar.accept(deff);
      this.defs = deff.defs;
      if (opts.defaultPackage2 != null) {
         this.pkg = codeModel._package(opts.defaultPackage2);
      } else if (opts.defaultPackage != null) {
         this.pkg = codeModel._package(opts.defaultPackage);
      } else {
         this.pkg = codeModel.rootPackage();
      }

   }

   private void compile() {
      this.promoteElementDefsToClasses();
      this.promoteTypeSafeEnums();
      this.promoteTypePatternsToClasses();
      Iterator var1 = this.bindQueue.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry e = (Map.Entry)var1.next();
         this.bindContentModel((CClassInfo)e.getKey(), (DPattern)e.getValue());
      }

   }

   private void bindContentModel(CClassInfo clazz, DPattern pattern) {
      pattern.accept(new ContentModelBinder(this, clazz));
   }

   private void promoteTypeSafeEnums() {
      List members = new ArrayList();
      Iterator var2 = this.defs.iterator();

      while(true) {
         label46:
         while(true) {
            DDefine def;
            DPattern p;
            do {
               if (!var2.hasNext()) {
                  return;
               }

               def = (DDefine)var2.next();
               p = def.getPattern();
            } while(!(p instanceof DChoicePattern));

            DChoicePattern cp = (DChoicePattern)p;
            members.clear();
            DValuePattern vp = null;

            DValuePattern c;
            for(Iterator var7 = cp.iterator(); var7.hasNext(); members.add(new CEnumConstant(this.model.getNameConverter().toConstantName(c.getValue()), (String)null, c.getValue(), (XSComponent)null, (CCustomizations)null, c.getLocation()))) {
               DPattern child = (DPattern)var7.next();
               if (!(child instanceof DValuePattern)) {
                  continue label46;
               }

               c = (DValuePattern)child;
               if (vp == null) {
                  vp = c;
               } else if (!vp.getDatatypeLibrary().equals(c.getDatatypeLibrary()) || !vp.getType().equals(c.getType())) {
                  continue label46;
               }
            }

            if (!members.isEmpty()) {
               CNonElement base = CBuiltinLeafInfo.STRING;
               DatatypeLib lib = (DatatypeLib)this.datatypes.get(vp.getNs());
               if (lib != null) {
                  TypeUse use = lib.get(vp.getType());
                  if (use instanceof CNonElement) {
                     base = (CNonElement)use;
                  }
               }

               CEnumLeafInfo xducer = new CEnumLeafInfo(this.model, (QName)null, new CClassInfoParent.Package(this.pkg), def.getName(), (CNonElement)base, new ArrayList(members), (XSComponent)null, (CCustomizations)null, cp.getLocation());
               this.classes.put(cp, new CTypeInfo[]{xducer});
            }
         }
      }
   }

   private void promoteElementDefsToClasses() {
      Iterator var1 = this.defs.iterator();

      while(var1.hasNext()) {
         DDefine def = (DDefine)var1.next();
         DPattern p = def.getPattern();
         if (p instanceof DElementPattern) {
            DElementPattern ep = (DElementPattern)p;
            this.mapToClass(ep);
         }
      }

      this.grammar.accept(new DPatternWalker() {
         public Void onRef(DRefPattern p) {
            return null;
         }

         public Void onElement(DElementPattern p) {
            RELAXNGCompiler.this.mapToClass(p);
            return null;
         }
      });
   }

   private void mapToClass(DElementPattern p) {
      NameClass nc = p.getName();
      if (!nc.isOpen()) {
         Set names = nc.listNames();
         CClassInfo[] types = new CClassInfo[names.size()];
         int i = 0;
         Iterator var6 = names.iterator();

         while(var6.hasNext()) {
            QName n = (QName)var6.next();
            String name = this.model.getNameConverter().toClassName(n.getLocalPart());
            this.bindQueue.put(types[i++] = new CClassInfo(this.model, this.pkg, name, p.getLocation(), (QName)null, n, (XSComponent)null, (CCustomizations)null), p.getChild());
         }

         this.classes.put(p, types);
      }
   }

   private void promoteTypePatternsToClasses() {
   }
}
