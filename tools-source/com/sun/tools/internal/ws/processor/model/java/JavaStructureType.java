package com.sun.tools.internal.ws.processor.model.java;

import com.sun.tools.internal.ws.processor.model.ModelException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaStructureType extends JavaType {
   private List members = new ArrayList();
   private Map membersByName = new HashMap();
   private Set subclasses = new HashSet();
   private JavaStructureType superclass;
   private Object owner;
   private boolean isAbstract = false;

   public JavaStructureType() {
   }

   public JavaStructureType(String name, boolean present, Object owner) {
      super(name, present, "null");
      this.owner = owner;
   }

   public void add(JavaStructureMember m) {
      if (this.membersByName.containsKey(m.getName())) {
         throw new ModelException("model.uniqueness.javastructuretype", new Object[]{m.getName(), this.getRealName()});
      } else {
         this.members.add(m);
         this.membersByName.put(m.getName(), m);
      }
   }

   public JavaStructureMember getMemberByName(String name) {
      if (this.membersByName.size() != this.members.size()) {
         this.initializeMembersByName();
      }

      return (JavaStructureMember)this.membersByName.get(name);
   }

   public Iterator getMembers() {
      return this.members.iterator();
   }

   public int getMembersCount() {
      return this.members.size();
   }

   public List getMembersList() {
      return this.members;
   }

   public void setMembersList(List l) {
      this.members = l;
   }

   private void initializeMembersByName() {
      this.membersByName = new HashMap();
      if (this.members != null) {
         Iterator var1 = this.members.iterator();

         while(var1.hasNext()) {
            JavaStructureMember m = (JavaStructureMember)var1.next();
            if (m.getName() != null && this.membersByName.containsKey(m.getName())) {
               throw new ModelException("model.uniqueness", new Object[0]);
            }

            this.membersByName.put(m.getName(), m);
         }
      }

   }

   public boolean isAbstract() {
      return this.isAbstract;
   }

   public void setAbstract(boolean isAbstract) {
      this.isAbstract = isAbstract;
   }

   public JavaStructureType getSuperclass() {
      return this.superclass;
   }

   public void setSuperclass(JavaStructureType superclassType) {
      this.superclass = superclassType;
   }

   public void addSubclass(JavaStructureType subclassType) {
      this.subclasses.add(subclassType);
      subclassType.setSuperclass(this);
   }

   public Iterator getSubclasses() {
      return this.subclasses != null && this.subclasses.size() != 0 ? this.subclasses.iterator() : null;
   }

   public Set getSubclassesSet() {
      return this.subclasses;
   }

   public void setSubclassesSet(Set s) {
      this.subclasses = s;
      Iterator iter = s.iterator();

      while(iter.hasNext()) {
         ((JavaStructureType)iter.next()).setSuperclass(this);
      }

   }

   public Iterator getAllSubclasses() {
      Set subs = this.getAllSubclassesSet();
      return subs.size() == 0 ? null : subs.iterator();
   }

   public Set getAllSubclassesSet() {
      Set transitiveSet = new HashSet();
      Iterator subs = this.subclasses.iterator();

      while(subs.hasNext()) {
         transitiveSet.addAll(((JavaStructureType)subs.next()).getAllSubclassesSet());
      }

      transitiveSet.addAll(this.subclasses);
      return transitiveSet;
   }

   public Object getOwner() {
      return this.owner;
   }

   public void setOwner(Object owner) {
      this.owner = owner;
   }
}
