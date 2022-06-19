package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Position;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.tools.FileObject;

public class PackageDocImpl extends DocImpl implements PackageDoc {
   protected Symbol.PackageSymbol sym;
   private JCTree.JCCompilationUnit tree;
   public FileObject docPath;
   private boolean foundDoc;
   boolean isIncluded;
   public boolean setDocPath;
   private List allClassesFiltered;
   private List allClasses;
   private String qualifiedName;
   private boolean checkDocWarningEmitted;

   public PackageDocImpl(DocEnv var1, Symbol.PackageSymbol var2) {
      this(var1, var2, (TreePath)null);
   }

   public PackageDocImpl(DocEnv var1, Symbol.PackageSymbol var2, TreePath var3) {
      super(var1, var3);
      this.tree = null;
      this.docPath = null;
      this.isIncluded = false;
      this.setDocPath = false;
      this.allClassesFiltered = null;
      this.allClasses = null;
      this.checkDocWarningEmitted = false;
      this.sym = var2;
      this.tree = var3 == null ? null : (JCTree.JCCompilationUnit)var3.getCompilationUnit();
      this.foundDoc = this.documentation != null;
   }

   void setTree(JCTree var1) {
      this.tree = (JCTree.JCCompilationUnit)var1;
   }

   public void setTreePath(TreePath var1) {
      super.setTreePath(var1);
      this.checkDoc();
   }

   protected String documentation() {
      if (this.documentation != null) {
         return this.documentation;
      } else {
         if (this.docPath != null) {
            try {
               InputStream var1 = this.docPath.openInputStream();
               this.documentation = this.readHTMLDocumentation(var1, this.docPath);
            } catch (IOException var2) {
               this.documentation = "";
               this.env.error((DocImpl)null, "javadoc.File_Read_Error", this.docPath.getName());
            }
         } else {
            this.documentation = "";
         }

         return this.documentation;
      }
   }

   private List getClasses(boolean var1) {
      if (this.allClasses != null && !var1) {
         return this.allClasses;
      } else if (this.allClassesFiltered != null && var1) {
         return this.allClassesFiltered;
      } else {
         ListBuffer var2 = new ListBuffer();

         for(Scope.Entry var3 = this.sym.members().elems; var3 != null; var3 = var3.sibling) {
            if (var3.sym != null) {
               Symbol.ClassSymbol var4 = (Symbol.ClassSymbol)var3.sym;
               ClassDocImpl var5 = this.env.getClassDoc(var4);
               if (var5 != null && !var5.isSynthetic()) {
                  var5.addAllClasses(var2, var1);
               }
            }
         }

         if (var1) {
            return this.allClassesFiltered = var2.toList();
         } else {
            return this.allClasses = var2.toList();
         }
      }
   }

   public void addAllClassesTo(ListBuffer var1) {
      var1.appendList(this.getClasses(true));
   }

   public ClassDoc[] allClasses(boolean var1) {
      List var2 = this.getClasses(var1);
      return (ClassDoc[])var2.toArray(new ClassDocImpl[var2.length()]);
   }

   public ClassDoc[] allClasses() {
      return this.allClasses(true);
   }

   public ClassDoc[] ordinaryClasses() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.getClasses(true).iterator();

      while(var2.hasNext()) {
         ClassDocImpl var3 = (ClassDocImpl)var2.next();
         if (var3.isOrdinaryClass()) {
            var1.append(var3);
         }
      }

      return (ClassDoc[])var1.toArray(new ClassDocImpl[var1.length()]);
   }

   public ClassDoc[] exceptions() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.getClasses(true).iterator();

      while(var2.hasNext()) {
         ClassDocImpl var3 = (ClassDocImpl)var2.next();
         if (var3.isException()) {
            var1.append(var3);
         }
      }

      return (ClassDoc[])var1.toArray(new ClassDocImpl[var1.length()]);
   }

   public ClassDoc[] errors() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.getClasses(true).iterator();

      while(var2.hasNext()) {
         ClassDocImpl var3 = (ClassDocImpl)var2.next();
         if (var3.isError()) {
            var1.append(var3);
         }
      }

      return (ClassDoc[])var1.toArray(new ClassDocImpl[var1.length()]);
   }

   public ClassDoc[] enums() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.getClasses(true).iterator();

      while(var2.hasNext()) {
         ClassDocImpl var3 = (ClassDocImpl)var2.next();
         if (var3.isEnum()) {
            var1.append(var3);
         }
      }

      return (ClassDoc[])var1.toArray(new ClassDocImpl[var1.length()]);
   }

   public ClassDoc[] interfaces() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.getClasses(true).iterator();

      while(var2.hasNext()) {
         ClassDocImpl var3 = (ClassDocImpl)var2.next();
         if (var3.isInterface()) {
            var1.append(var3);
         }
      }

      return (ClassDoc[])var1.toArray(new ClassDocImpl[var1.length()]);
   }

   public AnnotationTypeDoc[] annotationTypes() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.getClasses(true).iterator();

      while(var2.hasNext()) {
         ClassDocImpl var3 = (ClassDocImpl)var2.next();
         if (var3.isAnnotationType()) {
            var1.append((AnnotationTypeDocImpl)var3);
         }
      }

      return (AnnotationTypeDoc[])var1.toArray(new AnnotationTypeDocImpl[var1.length()]);
   }

   public AnnotationDesc[] annotations() {
      AnnotationDesc[] var1 = new AnnotationDesc[this.sym.getRawAttributes().length()];
      int var2 = 0;

      Attribute.Compound var4;
      for(Iterator var3 = this.sym.getRawAttributes().iterator(); var3.hasNext(); var1[var2++] = new AnnotationDescImpl(this.env, var4)) {
         var4 = (Attribute.Compound)var3.next();
      }

      return var1;
   }

   public ClassDoc findClass(String var1) {
      Iterator var3 = this.getClasses(true).iterator();

      ClassDocImpl var4;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         var4 = (ClassDocImpl)var3.next();
      } while(!var4.name().equals(var1));

      return var4;
   }

   public boolean isIncluded() {
      return this.isIncluded;
   }

   public String name() {
      return this.qualifiedName();
   }

   public String qualifiedName() {
      if (this.qualifiedName == null) {
         Name var1 = this.sym.getQualifiedName();
         this.qualifiedName = var1.isEmpty() ? "" : var1.toString();
      }

      return this.qualifiedName;
   }

   public void setDocPath(FileObject var1) {
      this.setDocPath = true;
      if (var1 != null) {
         if (!var1.equals(this.docPath)) {
            this.docPath = var1;
            this.checkDoc();
         }

      }
   }

   private void checkDoc() {
      if (this.foundDoc) {
         if (!this.checkDocWarningEmitted) {
            this.env.warning((DocImpl)null, "javadoc.Multiple_package_comments", this.name());
            this.checkDocWarningEmitted = true;
         }
      } else {
         this.foundDoc = true;
      }

   }

   public SourcePosition position() {
      return this.tree != null ? SourcePositionImpl.make(this.tree.sourcefile, this.tree.pos, this.tree.lineMap) : SourcePositionImpl.make(this.docPath, -1, (Position.LineMap)null);
   }
}
