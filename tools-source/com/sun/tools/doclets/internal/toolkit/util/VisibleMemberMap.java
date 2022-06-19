package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class VisibleMemberMap {
   private boolean noVisibleMembers = true;
   public static final int INNERCLASSES = 0;
   public static final int ENUM_CONSTANTS = 1;
   public static final int FIELDS = 2;
   public static final int CONSTRUCTORS = 3;
   public static final int METHODS = 4;
   public static final int ANNOTATION_TYPE_FIELDS = 5;
   public static final int ANNOTATION_TYPE_MEMBER_OPTIONAL = 6;
   public static final int ANNOTATION_TYPE_MEMBER_REQUIRED = 7;
   public static final int PROPERTIES = 8;
   public static final int NUM_MEMBER_TYPES = 9;
   public static final String STARTLEVEL = "start";
   private final List visibleClasses = new ArrayList();
   private final Map memberNameMap = new HashMap();
   private final Map classMap = new HashMap();
   private final ClassDoc classdoc;
   private final int kind;
   private final Configuration configuration;
   private static final Map propertiesCache = new HashMap();
   private static final Map classPropertiesMap = new HashMap();
   private static final Map getterSetterMap = new HashMap();

   public VisibleMemberMap(ClassDoc var1, int var2, Configuration var3) {
      this.classdoc = var1;
      this.kind = var2;
      this.configuration = var3;
      (new ClassMembers(var1, "start")).build();
   }

   public List getVisibleClassesList() {
      this.sort(this.visibleClasses);
      return this.visibleClasses;
   }

   public ProgramElementDoc getPropertyMemberDoc(ProgramElementDoc var1) {
      return (ProgramElementDoc)classPropertiesMap.get(var1);
   }

   public ProgramElementDoc getGetterForProperty(ProgramElementDoc var1) {
      return ((GetterSetter)getterSetterMap.get(var1)).getGetter();
   }

   public ProgramElementDoc getSetterForProperty(ProgramElementDoc var1) {
      return ((GetterSetter)getterSetterMap.get(var1)).getSetter();
   }

   private List getInheritedPackagePrivateMethods(Configuration var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.visibleClasses.iterator();

      while(var3.hasNext()) {
         ClassDoc var4 = (ClassDoc)var3.next();
         if (var4 != this.classdoc && var4.isPackagePrivate() && !Util.isLinkable(var4, var1)) {
            var2.addAll(this.getMembersFor(var4));
         }
      }

      return var2;
   }

   public List getLeafClassMembers(Configuration var1) {
      List var2 = this.getMembersFor(this.classdoc);
      var2.addAll(this.getInheritedPackagePrivateMethods(var1));
      return var2;
   }

   public List getMembersFor(ClassDoc var1) {
      ClassMembers var2 = (ClassMembers)this.classMap.get(var1);
      return (List)(var2 == null ? new ArrayList() : var2.getMembers());
   }

   private void sort(List var1) {
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();

      for(int var4 = 0; var4 < var1.size(); ++var4) {
         ClassDoc var5 = (ClassDoc)var1.get(var4);
         if (var5.isClass()) {
            var2.add(var5);
         } else {
            var3.add(var5);
         }
      }

      var1.clear();
      var1.addAll(var2);
      var1.addAll(var3);
   }

   private void fillMemberLevelMap(List var1, String var2) {
      for(int var3 = 0; var3 < var1.size(); ++var3) {
         Object var4 = this.getMemberKey((ProgramElementDoc)var1.get(var3));
         Object var5 = (Map)this.memberNameMap.get(var4);
         if (var5 == null) {
            var5 = new HashMap();
            this.memberNameMap.put(var4, var5);
         }

         ((Map)var5).put(var1.get(var3), var2);
      }

   }

   private void purgeMemberLevelMap(List var1, String var2) {
      for(int var3 = 0; var3 < var1.size(); ++var3) {
         Object var4 = this.getMemberKey((ProgramElementDoc)var1.get(var3));
         Map var5 = (Map)this.memberNameMap.get(var4);
         if (var5 != null && var2.equals(var5.get(var1.get(var3)))) {
            var5.remove(var1.get(var3));
         }
      }

   }

   public boolean noVisibleMembers() {
      return this.noVisibleMembers;
   }

   private ClassMember getClassMember(MethodDoc var1) {
      Iterator var2 = this.memberNameMap.keySet().iterator();

      Object var3;
      do {
         if (!var2.hasNext()) {
            return new ClassMember(var1);
         }

         var3 = var2.next();
      } while(var3 instanceof String || !((ClassMember)var3).isEqual(var1));

      return (ClassMember)var3;
   }

   private Object getMemberKey(ProgramElementDoc var1) {
      if (var1.isConstructor()) {
         return var1.name() + ((ExecutableMemberDoc)var1).signature();
      } else if (var1.isMethod()) {
         return this.getClassMember((MethodDoc)var1);
      } else if (!var1.isField() && !var1.isEnumConstant() && !var1.isAnnotationTypeElement()) {
         String var2 = var1.name();
         var2 = var2.indexOf(46) != 0 ? var2.substring(var2.lastIndexOf(46), var2.length()) : var2;
         return "clint" + var2;
      } else {
         return var1.name();
      }
   }

   private class GetterSetter {
      private final ProgramElementDoc getter;
      private final ProgramElementDoc setter;

      public GetterSetter(ProgramElementDoc var2, ProgramElementDoc var3) {
         this.getter = var2;
         this.setter = var3;
      }

      public ProgramElementDoc getGetter() {
         return this.getter;
      }

      public ProgramElementDoc getSetter() {
         return this.setter;
      }
   }

   private class ClassMembers {
      private ClassDoc mappingClass;
      private List members;
      private String level;
      private final Pattern pattern;

      public List getMembers() {
         return this.members;
      }

      private ClassMembers(ClassDoc var2, String var3) {
         this.members = new ArrayList();
         this.pattern = Pattern.compile("[sg]et\\p{Upper}.*");
         this.mappingClass = var2;
         this.level = var3;
         if (VisibleMemberMap.this.classMap.containsKey(var2) && var3.startsWith(((ClassMembers)VisibleMemberMap.this.classMap.get(var2)).level)) {
            VisibleMemberMap.this.purgeMemberLevelMap(this.getClassMembers(var2, false), ((ClassMembers)VisibleMemberMap.this.classMap.get(var2)).level);
            VisibleMemberMap.this.classMap.remove(var2);
            VisibleMemberMap.this.visibleClasses.remove(var2);
         }

         if (!VisibleMemberMap.this.classMap.containsKey(var2)) {
            VisibleMemberMap.this.classMap.put(var2, this);
            VisibleMemberMap.this.visibleClasses.add(var2);
         }

      }

      private void build() {
         if (VisibleMemberMap.this.kind == 3) {
            this.addMembers(this.mappingClass);
         } else {
            this.mapClass();
         }

      }

      private void mapClass() {
         this.addMembers(this.mappingClass);
         ClassDoc[] var1 = this.mappingClass.interfaces();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            String var3 = this.level + 1;
            ClassMembers var4 = VisibleMemberMap.this.new ClassMembers(var1[var2], var3);
            var4.mapClass();
         }

         if (this.mappingClass.isClass()) {
            ClassDoc var5 = this.mappingClass.superclass();
            if (var5 != null && !this.mappingClass.equals(var5)) {
               ClassMembers var6 = VisibleMemberMap.this.new ClassMembers(var5, this.level + "c");
               var6.mapClass();
            }
         }

      }

      private void addMembers(ClassDoc var1) {
         List var2 = this.getClassMembers(var1, true);
         ArrayList var3 = new ArrayList();

         for(int var4 = 0; var4 < var2.size(); ++var4) {
            ProgramElementDoc var5 = (ProgramElementDoc)var2.get(var4);
            if (!this.found(this.members, var5) && this.memberIsVisible(var5) && !this.isOverridden(var5, this.level) && !this.isTreatedAsPrivate(var5)) {
               var3.add(var5);
            }
         }

         if (var3.size() > 0) {
            VisibleMemberMap.this.noVisibleMembers = false;
         }

         this.members.addAll(var3);
         VisibleMemberMap.this.fillMemberLevelMap(this.getClassMembers(var1, false), this.level);
      }

      private boolean isTreatedAsPrivate(ProgramElementDoc var1) {
         if (!VisibleMemberMap.this.configuration.javafx) {
            return false;
         } else {
            Tag[] var2 = var1.tags("@treatAsPrivate");
            boolean var3 = var2 != null && var2.length > 0;
            return var3;
         }
      }

      private boolean memberIsVisible(ProgramElementDoc var1) {
         if (var1.containingClass().equals(VisibleMemberMap.this.classdoc)) {
            return true;
         } else if (var1.isPrivate()) {
            return false;
         } else {
            return var1.isPackagePrivate() ? var1.containingClass().containingPackage().equals(VisibleMemberMap.this.classdoc.containingPackage()) : true;
         }
      }

      private List getClassMembers(ClassDoc var1, boolean var2) {
         // $FF: Couldn't be decompiled
      }

      private AnnotationTypeElementDoc[] filter(AnnotationTypeDoc var1, boolean var2) {
         AnnotationTypeElementDoc[] var3 = var1.elements();
         ArrayList var4 = new ArrayList();

         for(int var5 = 0; var5 < var3.length; ++var5) {
            if (var2 && var3[var5].defaultValue() == null || !var2 && var3[var5].defaultValue() != null) {
               var4.add(var3[var5]);
            }
         }

         return (AnnotationTypeElementDoc[])var4.toArray(new AnnotationTypeElementDoc[0]);
      }

      private boolean found(List var1, ProgramElementDoc var2) {
         for(int var3 = 0; var3 < var1.size(); ++var3) {
            ProgramElementDoc var4 = (ProgramElementDoc)var1.get(var3);
            if (Util.matches(var4, var2)) {
               return true;
            }
         }

         return false;
      }

      private boolean isOverridden(ProgramElementDoc var1, String var2) {
         Map var3 = (Map)VisibleMemberMap.this.memberNameMap.get(VisibleMemberMap.this.getMemberKey(var1));
         if (var3 == null) {
            return false;
         } else {
            String var4 = null;
            Iterator var5 = var3.values().iterator();

            do {
               if (!var5.hasNext()) {
                  return false;
               }

               var4 = (String)var5.next();
            } while(!var4.equals("start") && (!var2.startsWith(var4) || var2.equals(var4)));

            return true;
         }
      }

      private ProgramElementDoc[] properties(ClassDoc var1, boolean var2) {
         MethodDoc[] var3 = var1.methods(var2);
         FieldDoc[] var4 = var1.fields(false);
         if (VisibleMemberMap.propertiesCache.containsKey(var1)) {
            return (ProgramElementDoc[])VisibleMemberMap.propertiesCache.get(var1);
         } else {
            ArrayList var5 = new ArrayList();
            MethodDoc[] var6 = var3;
            int var7 = var3.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               MethodDoc var9 = var6[var8];
               if (this.isPropertyMethod(var9)) {
                  MethodDoc var10 = this.getterForField(var3, var9);
                  MethodDoc var11 = this.setterForField(var3, var9);
                  FieldDoc var12 = this.fieldForProperty(var4, var9);
                  this.addToPropertiesMap(var11, var10, var9, var12);
                  VisibleMemberMap.getterSetterMap.put(var9, VisibleMemberMap.this.new GetterSetter(var10, var11));
                  var5.add(var9);
               }
            }

            ProgramElementDoc[] var13 = (ProgramElementDoc[])var5.toArray(new ProgramElementDoc[var5.size()]);
            VisibleMemberMap.propertiesCache.put(var1, var13);
            return var13;
         }
      }

      private void addToPropertiesMap(MethodDoc var1, MethodDoc var2, MethodDoc var3, FieldDoc var4) {
         if (var4 != null && var4.getRawCommentText() != null && var4.getRawCommentText().length() != 0) {
            this.addToPropertiesMap(var2, var4);
            this.addToPropertiesMap(var1, var4);
            this.addToPropertiesMap(var3, var4);
         } else {
            this.addToPropertiesMap(var1, var3);
            this.addToPropertiesMap(var2, var3);
            this.addToPropertiesMap(var3, var3);
         }

      }

      private void addToPropertiesMap(ProgramElementDoc var1, ProgramElementDoc var2) {
         if (null != var1 && null != var2) {
            String var3 = var1.getRawCommentText();
            if (null == var3 || 0 == var3.length() || var1.equals(var2)) {
               VisibleMemberMap.classPropertiesMap.put(var1, var2);
            }

         }
      }

      private MethodDoc getterForField(MethodDoc[] var1, MethodDoc var2) {
         String var3 = var2.name();
         String var4 = var3.substring(0, var3.lastIndexOf("Property"));
         String var5 = "" + Character.toUpperCase(var4.charAt(0)) + var4.substring(1);
         String var7 = var2.returnType().toString();
         String var6;
         if (!"boolean".equals(var7) && !var7.endsWith("BooleanProperty")) {
            var6 = "get" + var5;
         } else {
            var6 = "(is|get)" + var5;
         }

         MethodDoc[] var8 = var1;
         int var9 = var1.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            MethodDoc var11 = var8[var10];
            if (Pattern.matches(var6, var11.name()) && 0 == var11.parameters().length && (var11.isPublic() || var11.isProtected())) {
               return var11;
            }
         }

         return null;
      }

      private MethodDoc setterForField(MethodDoc[] var1, MethodDoc var2) {
         String var3 = var2.name();
         String var4 = var3.substring(0, var3.lastIndexOf("Property"));
         String var5 = "" + Character.toUpperCase(var4.charAt(0)) + var4.substring(1);
         String var6 = "set" + var5;
         MethodDoc[] var7 = var1;
         int var8 = var1.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            MethodDoc var10 = var7[var9];
            if (var6.equals(var10.name()) && 1 == var10.parameters().length && "void".equals(var10.returnType().simpleTypeName()) && (var10.isPublic() || var10.isProtected())) {
               return var10;
            }
         }

         return null;
      }

      private FieldDoc fieldForProperty(FieldDoc[] var1, MethodDoc var2) {
         FieldDoc[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            FieldDoc var6 = var3[var5];
            String var7 = var6.name();
            String var8 = var7 + "Property";
            if (var8.equals(var2.name())) {
               return var6;
            }
         }

         return null;
      }

      private boolean isPropertyMethod(MethodDoc var1) {
         if (!VisibleMemberMap.this.configuration.javafx) {
            return false;
         } else if (!var1.name().endsWith("Property")) {
            return false;
         } else if (!this.memberIsVisible(var1)) {
            return false;
         } else if (this.pattern.matcher(var1.name()).matches()) {
            return false;
         } else if (var1.typeParameters().length > 0) {
            return false;
         } else {
            return 0 == var1.parameters().length && !"void".equals(var1.returnType().simpleTypeName());
         }
      }

      private void checkOnPropertiesTags(MethodDoc[] var1) {
         MethodDoc[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            MethodDoc var5 = var2[var4];
            if (var5.isIncluded()) {
               Tag[] var6 = var5.tags();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  Tag var9 = var6[var8];
                  String var10 = var9.name();
                  if (var10.equals("@propertySetter") || var10.equals("@propertyGetter") || var10.equals("@propertyDescription")) {
                     if (!this.isPropertyGetterOrSetter(var1, var5)) {
                        VisibleMemberMap.this.configuration.message.warning(var9.position(), "doclet.javafx_tag_misuse");
                     }
                     break;
                  }
               }
            }
         }

      }

      private boolean isPropertyGetterOrSetter(MethodDoc[] var1, MethodDoc var2) {
         boolean var3 = false;
         String var4 = Util.propertyNameFromMethodName(VisibleMemberMap.this.configuration, var2.name());
         if (!var4.isEmpty()) {
            String var5 = var4 + "Property";
            MethodDoc[] var6 = var1;
            int var7 = var1.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               MethodDoc var9 = var6[var8];
               if (var9.name().equals(var5)) {
                  var3 = true;
                  break;
               }
            }
         }

         return var3;
      }

      // $FF: synthetic method
      ClassMembers(ClassDoc var2, String var3, Object var4) {
         this(var2, var3);
      }
   }

   private class ClassMember {
      private Set members = new HashSet();

      public ClassMember(ProgramElementDoc var2) {
         this.members.add(var2);
      }

      public void addMember(ProgramElementDoc var1) {
         this.members.add(var1);
      }

      public boolean isEqual(MethodDoc var1) {
         Iterator var2 = this.members.iterator();

         MethodDoc var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = (MethodDoc)var2.next();
         } while(!Util.executableMembersEqual(var1, var3));

         this.members.add(var1);
         return true;
      }
   }
}
