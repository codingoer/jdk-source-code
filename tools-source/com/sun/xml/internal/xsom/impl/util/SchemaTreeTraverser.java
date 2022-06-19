package com.sun.xml.internal.xsom.impl.util;

import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSListSimpleType;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSNotation;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSRestrictionSimpleType;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.XSUnionSimpleType;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.XSXPath;
import com.sun.xml.internal.xsom.visitor.XSSimpleTypeVisitor;
import com.sun.xml.internal.xsom.visitor.XSTermVisitor;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Iterator;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import org.xml.sax.Locator;

public class SchemaTreeTraverser implements XSVisitor, XSSimpleTypeVisitor {
   private SchemaTreeModel model = SchemaTreeTraverser.SchemaTreeModel.getInstance();
   private SchemaTreeNode currNode;

   public SchemaTreeTraverser() {
      this.currNode = (SchemaTreeNode)this.model.getRoot();
   }

   public SchemaTreeModel getModel() {
      return this.model;
   }

   public void visit(XSSchemaSet s) {
      Iterator var2 = s.getSchemas().iterator();

      while(var2.hasNext()) {
         XSSchema schema = (XSSchema)var2.next();
         this.schema(schema);
      }

   }

   public void schema(XSSchema s) {
      if (!s.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
         SchemaTreeNode newNode = new SchemaTreeNode("Schema " + s.getLocator().getSystemId(), s.getLocator());
         this.currNode = newNode;
         this.model.addSchemaNode(newNode);
         Iterator var3 = s.getAttGroupDecls().values().iterator();

         while(var3.hasNext()) {
            XSAttGroupDecl groupDecl = (XSAttGroupDecl)var3.next();
            this.attGroupDecl(groupDecl);
         }

         var3 = s.getAttributeDecls().values().iterator();

         while(var3.hasNext()) {
            XSAttributeDecl attrDecl = (XSAttributeDecl)var3.next();
            this.attributeDecl(attrDecl);
         }

         var3 = s.getComplexTypes().values().iterator();

         while(var3.hasNext()) {
            XSComplexType complexType = (XSComplexType)var3.next();
            this.complexType(complexType);
         }

         var3 = s.getElementDecls().values().iterator();

         while(var3.hasNext()) {
            XSElementDecl elementDecl = (XSElementDecl)var3.next();
            this.elementDecl(elementDecl);
         }

         var3 = s.getModelGroupDecls().values().iterator();

         while(var3.hasNext()) {
            XSModelGroupDecl modelGroupDecl = (XSModelGroupDecl)var3.next();
            this.modelGroupDecl(modelGroupDecl);
         }

         var3 = s.getSimpleTypes().values().iterator();

         while(var3.hasNext()) {
            XSSimpleType simpleType = (XSSimpleType)var3.next();
            this.simpleType(simpleType);
         }

      }
   }

   public void attGroupDecl(XSAttGroupDecl decl) {
      SchemaTreeNode newNode = new SchemaTreeNode("Attribute group \"" + decl.getName() + "\"", decl.getLocator());
      this.currNode.add(newNode);
      this.currNode = newNode;
      Iterator itr = decl.iterateAttGroups();

      while(itr.hasNext()) {
         this.dumpRef((XSAttGroupDecl)itr.next());
      }

      itr = decl.iterateDeclaredAttributeUses();

      while(itr.hasNext()) {
         this.attributeUse((XSAttributeUse)itr.next());
      }

      this.currNode = (SchemaTreeNode)this.currNode.getParent();
   }

   public void dumpRef(XSAttGroupDecl decl) {
      SchemaTreeNode newNode = new SchemaTreeNode("Attribute group ref \"{" + decl.getTargetNamespace() + "}" + decl.getName() + "\"", decl.getLocator());
      this.currNode.add(newNode);
   }

   public void attributeUse(XSAttributeUse use) {
      XSAttributeDecl decl = use.getDecl();
      String additionalAtts = "";
      if (use.isRequired()) {
         additionalAtts = additionalAtts + " use=\"required\"";
      }

      if (use.getFixedValue() != null && use.getDecl().getFixedValue() == null) {
         additionalAtts = additionalAtts + " fixed=\"" + use.getFixedValue() + "\"";
      }

      if (use.getDefaultValue() != null && use.getDecl().getDefaultValue() == null) {
         additionalAtts = additionalAtts + " default=\"" + use.getDefaultValue() + "\"";
      }

      if (decl.isLocal()) {
         this.dump(decl, additionalAtts);
      } else {
         String str = MessageFormat.format("Attribute ref \"'{'{0}'}'{1}{2}\"", decl.getTargetNamespace(), decl.getName(), additionalAtts);
         SchemaTreeNode newNode = new SchemaTreeNode(str, decl.getLocator());
         this.currNode.add(newNode);
      }

   }

   public void attributeDecl(XSAttributeDecl decl) {
      this.dump(decl, "");
   }

   private void dump(XSAttributeDecl decl, String additionalAtts) {
      XSSimpleType type = decl.getType();
      String str = MessageFormat.format("Attribute \"{0}\"{1}{2}{3}{4}", decl.getName(), additionalAtts, type.isLocal() ? "" : MessageFormat.format(" type=\"'{'{0}'}'{1}\"", type.getTargetNamespace(), type.getName()), decl.getFixedValue() == null ? "" : " fixed=\"" + decl.getFixedValue() + "\"", decl.getDefaultValue() == null ? "" : " default=\"" + decl.getDefaultValue() + "\"");
      SchemaTreeNode newNode = new SchemaTreeNode(str, decl.getLocator());
      this.currNode.add(newNode);
      this.currNode = newNode;
      if (type.isLocal()) {
         this.simpleType(type);
      }

      this.currNode = (SchemaTreeNode)this.currNode.getParent();
   }

   public void simpleType(XSSimpleType type) {
      String str = MessageFormat.format("Simple type {0}", type.isLocal() ? "" : " name=\"" + type.getName() + "\"");
      SchemaTreeNode newNode = new SchemaTreeNode(str, type.getLocator());
      this.currNode.add(newNode);
      this.currNode = newNode;
      type.visit(this);
      this.currNode = (SchemaTreeNode)this.currNode.getParent();
   }

   public void listSimpleType(XSListSimpleType type) {
      XSSimpleType itemType = type.getItemType();
      if (itemType.isLocal()) {
         SchemaTreeNode newNode = new SchemaTreeNode("List", type.getLocator());
         this.currNode.add(newNode);
         this.currNode = newNode;
         this.simpleType(itemType);
         this.currNode = (SchemaTreeNode)this.currNode.getParent();
      } else {
         String str = MessageFormat.format("List itemType=\"'{'{0}'}'{1}\"", itemType.getTargetNamespace(), itemType.getName());
         SchemaTreeNode newNode = new SchemaTreeNode(str, itemType.getLocator());
         this.currNode.add(newNode);
      }

   }

   public void unionSimpleType(XSUnionSimpleType type) {
      int len = type.getMemberSize();
      StringBuffer ref = new StringBuffer();

      for(int i = 0; i < len; ++i) {
         XSSimpleType member = type.getMember(i);
         if (member.isGlobal()) {
            ref.append(MessageFormat.format(" '{'{0}'}'{1}", member.getTargetNamespace(), member.getName()));
         }
      }

      String name = ref.length() == 0 ? "Union" : "Union memberTypes=\"" + ref + "\"";
      SchemaTreeNode newNode = new SchemaTreeNode(name, type.getLocator());
      this.currNode.add(newNode);
      this.currNode = newNode;

      for(int i = 0; i < len; ++i) {
         XSSimpleType member = type.getMember(i);
         if (member.isLocal()) {
            this.simpleType(member);
         }
      }

      this.currNode = (SchemaTreeNode)this.currNode.getParent();
   }

   public void restrictionSimpleType(XSRestrictionSimpleType type) {
      if (type.getBaseType() == null) {
         if (!type.getName().equals("anySimpleType")) {
            throw new InternalError();
         } else if (!"http://www.w3.org/2001/XMLSchema".equals(type.getTargetNamespace())) {
            throw new InternalError();
         }
      } else {
         XSSimpleType baseType = type.getSimpleBaseType();
         String str = MessageFormat.format("Restriction {0}", baseType.isLocal() ? "" : " base=\"{" + baseType.getTargetNamespace() + "}" + baseType.getName() + "\"");
         SchemaTreeNode newNode = new SchemaTreeNode(str, baseType.getLocator());
         this.currNode.add(newNode);
         this.currNode = newNode;
         if (baseType.isLocal()) {
            this.simpleType(baseType);
         }

         Iterator itr = type.iterateDeclaredFacets();

         while(itr.hasNext()) {
            this.facet((XSFacet)itr.next());
         }

         this.currNode = (SchemaTreeNode)this.currNode.getParent();
      }
   }

   public void facet(XSFacet facet) {
      SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("{0} value=\"{1}\"", facet.getName(), facet.getValue()), facet.getLocator());
      this.currNode.add(newNode);
   }

   public void notation(XSNotation notation) {
      SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("Notation name='\"0}\" public =\"{1}\" system=\"{2}\"", notation.getName(), notation.getPublicId(), notation.getSystemId()), notation.getLocator());
      this.currNode.add(newNode);
   }

   public void complexType(XSComplexType type) {
      SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("ComplexType {0}", type.isLocal() ? "" : " name=\"" + type.getName() + "\""), type.getLocator());
      this.currNode.add(newNode);
      this.currNode = newNode;
      SchemaTreeNode newNode2;
      String str;
      SchemaTreeNode newNode3;
      SchemaTreeNode newNodeRedefine;
      if (type.getContentType().asSimpleType() != null) {
         newNode2 = new SchemaTreeNode("Simple content", type.getContentType().getLocator());
         this.currNode.add(newNode2);
         this.currNode = newNode2;
         XSType baseType = type.getBaseType();
         if (type.getDerivationMethod() == 2) {
            str = MessageFormat.format("Restriction base=\"<{0}>{1}\"", baseType.getTargetNamespace(), baseType.getName());
            newNode3 = new SchemaTreeNode(str, baseType.getLocator());
            this.currNode.add(newNode3);
            this.currNode = newNode3;
            this.dumpComplexTypeAttribute(type);
            this.currNode = (SchemaTreeNode)this.currNode.getParent();
         } else {
            str = MessageFormat.format("Extension base=\"<{0}>{1}\"", baseType.getTargetNamespace(), baseType.getName());
            newNode3 = new SchemaTreeNode(str, baseType.getLocator());
            this.currNode.add(newNode3);
            this.currNode = newNode3;
            if (type.getTargetNamespace().compareTo(baseType.getTargetNamespace()) == 0 && type.getName().compareTo(baseType.getName()) == 0) {
               newNodeRedefine = new SchemaTreeNode("redefine", type.getLocator());
               this.currNode.add(newNodeRedefine);
               this.currNode = newNodeRedefine;
               baseType.visit(this);
               this.currNode = (SchemaTreeNode)newNodeRedefine.getParent();
            }

            this.dumpComplexTypeAttribute(type);
            this.currNode = (SchemaTreeNode)this.currNode.getParent();
         }

         this.currNode = (SchemaTreeNode)this.currNode.getParent();
      } else {
         newNode2 = new SchemaTreeNode("Complex content", type.getContentType().getLocator());
         this.currNode.add(newNode2);
         this.currNode = newNode2;
         XSComplexType baseType = type.getBaseType().asComplexType();
         if (type.getDerivationMethod() == 2) {
            str = MessageFormat.format("Restriction base=\"<{0}>{1}\"", baseType.getTargetNamespace(), baseType.getName());
            newNode3 = new SchemaTreeNode(str, baseType.getLocator());
            this.currNode.add(newNode3);
            this.currNode = newNode3;
            type.getContentType().visit(this);
            this.dumpComplexTypeAttribute(type);
            this.currNode = (SchemaTreeNode)this.currNode.getParent();
         } else {
            str = MessageFormat.format("Extension base=\"'{'{0}'}'{1}\"", baseType.getTargetNamespace(), baseType.getName());
            newNode3 = new SchemaTreeNode(str, baseType.getLocator());
            this.currNode.add(newNode3);
            this.currNode = newNode3;
            if (type.getTargetNamespace().compareTo(baseType.getTargetNamespace()) == 0 && type.getName().compareTo(baseType.getName()) == 0) {
               newNodeRedefine = new SchemaTreeNode("redefine", type.getLocator());
               this.currNode.add(newNodeRedefine);
               this.currNode = newNodeRedefine;
               baseType.visit(this);
               this.currNode = (SchemaTreeNode)newNodeRedefine.getParent();
            }

            type.getExplicitContent().visit(this);
            this.dumpComplexTypeAttribute(type);
            this.currNode = (SchemaTreeNode)this.currNode.getParent();
         }

         this.currNode = (SchemaTreeNode)this.currNode.getParent();
      }

      this.currNode = (SchemaTreeNode)this.currNode.getParent();
   }

   private void dumpComplexTypeAttribute(XSComplexType type) {
      Iterator itr = type.iterateAttGroups();

      while(itr.hasNext()) {
         this.dumpRef((XSAttGroupDecl)itr.next());
      }

      itr = type.iterateDeclaredAttributeUses();

      while(itr.hasNext()) {
         this.attributeUse((XSAttributeUse)itr.next());
      }

   }

   public void elementDecl(XSElementDecl decl) {
      this.elementDecl(decl, "");
   }

   private void elementDecl(XSElementDecl decl, String extraAtts) {
      XSType type = decl.getType();
      String str = MessageFormat.format("Element name=\"{0}\"{1}{2}", decl.getName(), type.isLocal() ? "" : " type=\"{" + type.getTargetNamespace() + "}" + type.getName() + "\"", extraAtts);
      SchemaTreeNode newNode = new SchemaTreeNode(str, decl.getLocator());
      this.currNode.add(newNode);
      this.currNode = newNode;
      if (type.isLocal() && type.isLocal()) {
         type.visit(this);
      }

      this.currNode = (SchemaTreeNode)this.currNode.getParent();
   }

   public void modelGroupDecl(XSModelGroupDecl decl) {
      SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("Group name=\"{0}\"", decl.getName()), decl.getLocator());
      this.currNode.add(newNode);
      this.currNode = newNode;
      this.modelGroup(decl.getModelGroup());
      this.currNode = (SchemaTreeNode)this.currNode.getParent();
   }

   public void modelGroup(XSModelGroup group) {
      this.modelGroup(group, "");
   }

   private void modelGroup(XSModelGroup group, String extraAtts) {
      SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("{0}{1}", group.getCompositor(), extraAtts), group.getLocator());
      this.currNode.add(newNode);
      this.currNode = newNode;
      int len = group.getSize();

      for(int i = 0; i < len; ++i) {
         this.particle(group.getChild(i));
      }

      this.currNode = (SchemaTreeNode)this.currNode.getParent();
   }

   public void particle(XSParticle part) {
      StringBuffer buf = new StringBuffer();
      BigInteger i = part.getMaxOccurs();
      if (i.equals(BigInteger.valueOf(-1L))) {
         buf.append(" maxOccurs=\"unbounded\"");
      } else if (!i.equals(BigInteger.ONE)) {
         buf.append(" maxOccurs=\"" + i + "\"");
      }

      i = part.getMinOccurs();
      if (!i.equals(BigInteger.ONE)) {
         buf.append(" minOccurs=\"" + i + "\"");
      }

      final String extraAtts = buf.toString();
      part.getTerm().visit(new XSTermVisitor() {
         public void elementDecl(XSElementDecl decl) {
            if (decl.isLocal()) {
               SchemaTreeTraverser.this.elementDecl(decl, extraAtts);
            } else {
               SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("Element ref=\"'{'{0}'}'{1}\"{2}", decl.getTargetNamespace(), decl.getName(), extraAtts), decl.getLocator());
               SchemaTreeTraverser.this.currNode.add(newNode);
            }

         }

         public void modelGroupDecl(XSModelGroupDecl decl) {
            SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("Group ref=\"'{'{0}'}'{1}\"{2}", decl.getTargetNamespace(), decl.getName(), extraAtts), decl.getLocator());
            SchemaTreeTraverser.this.currNode.add(newNode);
         }

         public void modelGroup(XSModelGroup group) {
            SchemaTreeTraverser.this.modelGroup(group, extraAtts);
         }

         public void wildcard(XSWildcard wc) {
            SchemaTreeTraverser.this.wildcard(wc, extraAtts);
         }
      });
   }

   public void wildcard(XSWildcard wc) {
      this.wildcard(wc, "");
   }

   private void wildcard(XSWildcard wc, String extraAtts) {
      SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("Any ", extraAtts), wc.getLocator());
      this.currNode.add(newNode);
   }

   public void annotation(XSAnnotation ann) {
   }

   public void empty(XSContentType t) {
   }

   public void identityConstraint(XSIdentityConstraint ic) {
   }

   public void xpath(XSXPath xp) {
   }

   public static class SchemaTreeCellRenderer extends JPanel implements TreeCellRenderer {
      protected final JLabel iconLabel;
      protected final JLabel nameLabel;
      private boolean isSelected;
      public final Color selectedBackground = new Color(255, 244, 232);
      public final Color selectedForeground = new Color(64, 32, 0);
      public final Font nameFont = new Font("Arial", 1, 12);

      public SchemaTreeCellRenderer() {
         FlowLayout fl = new FlowLayout(0, 1, 1);
         this.setLayout(fl);
         this.iconLabel = new JLabel();
         this.iconLabel.setOpaque(false);
         this.iconLabel.setBorder((Border)null);
         this.add(this.iconLabel);
         this.add(Box.createHorizontalStrut(5));
         this.nameLabel = new JLabel();
         this.nameLabel.setOpaque(false);
         this.nameLabel.setBorder((Border)null);
         this.nameLabel.setFont(this.nameFont);
         this.add(this.nameLabel);
         this.isSelected = false;
         this.setOpaque(false);
         this.setBorder((Border)null);
      }

      public final void paintComponent(Graphics g) {
         int width = this.getWidth();
         int height = this.getHeight();
         if (this.isSelected) {
            g.setColor(this.selectedBackground);
            g.fillRect(0, 0, width - 1, height - 1);
            g.setColor(this.selectedForeground);
            g.drawRect(0, 0, width - 1, height - 1);
         }

         super.paintComponent(g);
      }

      protected final void setValues(Icon icon, String caption, boolean selected) {
         this.iconLabel.setIcon(icon);
         this.nameLabel.setText(caption);
         this.isSelected = selected;
         if (selected) {
            this.nameLabel.setForeground(this.selectedForeground);
         } else {
            this.nameLabel.setForeground(Color.black);
         }

      }

      public final Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
         if (value instanceof SchemaTreeNode) {
            SchemaTreeNode stn = (SchemaTreeNode)value;
            this.setValues((Icon)null, stn.getCaption(), selected);
            return this;
         } else {
            throw new IllegalStateException("Unknown node");
         }
      }
   }

   public static class SchemaRootNode extends SchemaTreeNode {
      public SchemaRootNode() {
         super("Schema set", (Locator)null);
      }
   }

   public static class SchemaTreeNode extends DefaultMutableTreeNode {
      private String fileName;
      private int lineNumber;
      private String artifactName;

      public SchemaTreeNode(String artifactName, Locator locator) {
         this.artifactName = artifactName;
         if (locator == null) {
            this.fileName = null;
         } else {
            String filename = locator.getSystemId();
            filename = filename.replaceAll("%20", " ");
            if (filename.startsWith("file:/")) {
               filename = filename.substring(6);
            }

            this.fileName = filename;
            this.lineNumber = locator.getLineNumber() - 1;
         }

      }

      public String getCaption() {
         return this.artifactName;
      }

      public String getFileName() {
         return this.fileName;
      }

      public void setFileName(String fileName) {
         this.fileName = fileName;
      }

      public int getLineNumber() {
         return this.lineNumber;
      }

      public void setLineNumber(int lineNumber) {
         this.lineNumber = lineNumber;
      }
   }

   public static final class SchemaTreeModel extends DefaultTreeModel {
      private SchemaTreeModel(SchemaRootNode root) {
         super(root);
      }

      public static SchemaTreeModel getInstance() {
         SchemaRootNode root = new SchemaRootNode();
         return new SchemaTreeModel(root);
      }

      public void addSchemaNode(SchemaTreeNode node) {
         ((SchemaRootNode)this.root).add(node);
      }
   }
}
