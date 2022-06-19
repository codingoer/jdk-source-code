package com.sun.tools.internal.ws.processor.model.jaxb;

import com.sun.tools.internal.ws.processor.model.ModelException;
import com.sun.tools.internal.ws.processor.model.java.JavaStructureType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

public class JAXBStructuredType extends JAXBType {
   private List _elementMembers;
   private Map _elementMembersByName;
   private Set _subtypes;
   private JAXBStructuredType _parentType;

   public JAXBStructuredType(JAXBType jaxbType) {
      super(jaxbType);
      this._elementMembers = new ArrayList();
      this._elementMembersByName = new HashMap();
      this._subtypes = null;
      this._parentType = null;
   }

   public JAXBStructuredType() {
      this._elementMembers = new ArrayList();
      this._elementMembersByName = new HashMap();
      this._subtypes = null;
      this._parentType = null;
   }

   public JAXBStructuredType(QName name) {
      this(name, (JavaStructureType)null);
   }

   public JAXBStructuredType(QName name, JavaStructureType javaType) {
      super(name, javaType);
      this._elementMembers = new ArrayList();
      this._elementMembersByName = new HashMap();
      this._subtypes = null;
      this._parentType = null;
   }

   public void add(JAXBElementMember m) {
      if (this._elementMembersByName.containsKey(m.getName())) {
         throw new ModelException("model.uniqueness", new Object[0]);
      } else {
         this._elementMembers.add(m);
         if (m.getName() != null) {
            this._elementMembersByName.put(m.getName().getLocalPart(), m);
         }

      }
   }

   public Iterator getElementMembers() {
      return this._elementMembers.iterator();
   }

   public int getElementMembersCount() {
      return this._elementMembers.size();
   }

   public List getElementMembersList() {
      return this._elementMembers;
   }

   public void setElementMembersList(List l) {
      this._elementMembers = l;
   }

   public void addSubtype(JAXBStructuredType type) {
      if (this._subtypes == null) {
         this._subtypes = new HashSet();
      }

      this._subtypes.add(type);
      type.setParentType(this);
   }

   public Iterator getSubtypes() {
      return this._subtypes != null ? this._subtypes.iterator() : null;
   }

   public boolean isUnwrapped() {
      return true;
   }

   public Set getSubtypesSet() {
      return this._subtypes;
   }

   public void setSubtypesSet(Set s) {
      this._subtypes = s;
   }

   public void setParentType(JAXBStructuredType parent) {
      if (this._parentType != null && parent != null && !this._parentType.equals(parent)) {
         throw new ModelException("model.parent.type.already.set", new Object[]{this.getName().toString(), this._parentType.getName().toString(), parent.getName().toString()});
      } else {
         this._parentType = parent;
      }
   }

   public JAXBStructuredType getParentType() {
      return this._parentType;
   }
}
