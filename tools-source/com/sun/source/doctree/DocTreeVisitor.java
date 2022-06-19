package com.sun.source.doctree;

import jdk.Exported;

@Exported
public interface DocTreeVisitor {
   Object visitAttribute(AttributeTree var1, Object var2);

   Object visitAuthor(AuthorTree var1, Object var2);

   Object visitComment(CommentTree var1, Object var2);

   Object visitDeprecated(DeprecatedTree var1, Object var2);

   Object visitDocComment(DocCommentTree var1, Object var2);

   Object visitDocRoot(DocRootTree var1, Object var2);

   Object visitEndElement(EndElementTree var1, Object var2);

   Object visitEntity(EntityTree var1, Object var2);

   Object visitErroneous(ErroneousTree var1, Object var2);

   Object visitIdentifier(IdentifierTree var1, Object var2);

   Object visitInheritDoc(InheritDocTree var1, Object var2);

   Object visitLink(LinkTree var1, Object var2);

   Object visitLiteral(LiteralTree var1, Object var2);

   Object visitParam(ParamTree var1, Object var2);

   Object visitReference(ReferenceTree var1, Object var2);

   Object visitReturn(ReturnTree var1, Object var2);

   Object visitSee(SeeTree var1, Object var2);

   Object visitSerial(SerialTree var1, Object var2);

   Object visitSerialData(SerialDataTree var1, Object var2);

   Object visitSerialField(SerialFieldTree var1, Object var2);

   Object visitSince(SinceTree var1, Object var2);

   Object visitStartElement(StartElementTree var1, Object var2);

   Object visitText(TextTree var1, Object var2);

   Object visitThrows(ThrowsTree var1, Object var2);

   Object visitUnknownBlockTag(UnknownBlockTagTree var1, Object var2);

   Object visitUnknownInlineTag(UnknownInlineTagTree var1, Object var2);

   Object visitValue(ValueTree var1, Object var2);

   Object visitVersion(VersionTree var1, Object var2);

   Object visitOther(DocTree var1, Object var2);
}
