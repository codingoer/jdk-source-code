package com.sun.tools.internal.ws.processor.modeler.wsdl;

import com.sun.tools.internal.ws.processor.model.AbstractType;
import com.sun.tools.internal.ws.processor.model.Block;
import com.sun.tools.internal.ws.processor.model.Parameter;
import com.sun.tools.internal.ws.processor.model.java.JavaSimpleType;
import com.sun.tools.internal.ws.processor.model.java.JavaStructureMember;
import com.sun.tools.internal.ws.processor.model.java.JavaStructureType;
import com.sun.tools.internal.ws.processor.model.java.JavaType;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBElementMember;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBProperty;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBStructuredType;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBType;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBTypeAndAnnotation;
import com.sun.tools.internal.ws.processor.model.jaxb.RpcLitMember;
import com.sun.tools.internal.ws.processor.model.jaxb.RpcLitStructure;
import com.sun.tools.internal.ws.resources.ModelerMessages;
import com.sun.tools.internal.ws.util.ClassNameInfo;
import com.sun.tools.internal.ws.wscompile.AbortException;
import com.sun.tools.internal.ws.wscompile.ErrorReceiverFilter;
import com.sun.tools.internal.ws.wsdl.document.Message;
import com.sun.tools.internal.ws.wsdl.document.MessagePart;
import com.sun.tools.internal.xjc.api.S2JJAXBModel;
import com.sun.tools.internal.xjc.api.TypeAndAnnotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;

class ModelerUtils {
   public static JAXBStructuredType createJAXBStructureType(JAXBType jaxbType) {
      JAXBStructuredType type = new JAXBStructuredType(jaxbType);
      type.setName(jaxbType.getName());
      type.setJavaType(jaxbType.getJavaType());
      return type;
   }

   public static List createUnwrappedParameters(JAXBType jaxbType, Block block) {
      List paramList = new ArrayList();
      JAXBStructuredType type = null;
      if (!(jaxbType instanceof JAXBStructuredType)) {
         type = createJAXBStructureType(jaxbType);
      } else {
         type = (JAXBStructuredType)jaxbType;
      }

      JavaStructureType jst = new JavaStructureType(jaxbType.getJavaType().getRealName(), true, type);
      type.setJavaType(jst);
      block.setType(type);
      List memberList = jaxbType.getWrapperChildren();
      Iterator props = memberList.iterator();

      while(props.hasNext()) {
         JAXBProperty prop = (JAXBProperty)props.next();
         paramList.add(createUnwrappedParameter(prop, jaxbType, block, type, jst));
      }

      return paramList;
   }

   private static Parameter createUnwrappedParameter(JAXBProperty prop, JAXBType jaxbType, Block block, JAXBStructuredType type, JavaStructureType jst) {
      QName elementName = prop.getElementName();
      JavaType javaType = new JavaSimpleType(prop.getType());
      JAXBElementMember eType = new JAXBElementMember(elementName, jaxbType);
      JavaStructureMember jsm = new JavaStructureMember(elementName.getLocalPart(), javaType, eType);
      eType.setJavaStructureMember(jsm);
      jst.add(jsm);
      eType.setProperty(prop);
      type.add(eType);
      JAXBType t = new JAXBType(elementName, javaType, jaxbType.getJaxbMapping(), jaxbType.getJaxbModel());
      t.setUnwrapped(true);
      Parameter parameter = createParameter(elementName.getLocalPart(), t, block);
      parameter.setEmbedded(true);
      return parameter;
   }

   public static List createRpcLitParameters(Message message, Block block, S2JJAXBModel jaxbModel, ErrorReceiverFilter errReceiver) {
      RpcLitStructure rpcStruct = (RpcLitStructure)block.getType();
      List parameters = new ArrayList();
      Iterator var6 = message.getParts().iterator();

      while(var6.hasNext()) {
         MessagePart part = (MessagePart)var6.next();
         if (isBoundToSOAPBody(part)) {
            QName name = part.getDescriptor();
            TypeAndAnnotation typeAndAnn = jaxbModel.getJavaType(name);
            String type;
            if (typeAndAnn == null) {
               type = "{" + message.getDefining().getTargetNamespaceURI() + "}" + message.getName();
               errReceiver.error(part.getLocator(), ModelerMessages.WSDLMODELER_RPCLIT_UNKOWNSCHEMATYPE(name.toString(), part.getName(), type));
               throw new AbortException();
            }

            type = typeAndAnn.getTypeClass().fullName();
            type = ClassNameInfo.getGenericClass(type);
            RpcLitMember param = new RpcLitMember(new QName("", part.getName()), type);
            JavaType javaType = new JavaSimpleType(new JAXBTypeAndAnnotation(typeAndAnn));
            param.setJavaType(javaType);
            rpcStruct.addRpcLitMember(param);
            Parameter parameter = createParameter(part.getName(), param, block);
            parameter.setEmbedded(true);
            parameters.add(parameter);
         }
      }

      return parameters;
   }

   public static Parameter createParameter(String partName, AbstractType jaxbType, Block block) {
      Parameter parameter = new Parameter(partName, block.getEntity());
      parameter.setProperty("com.sun.xml.internal.ws.processor.model.ParamMessagePartName", partName);
      parameter.setEmbedded(false);
      parameter.setType(jaxbType);
      parameter.setTypeName(jaxbType.getJavaType().getType().getName());
      parameter.setBlock(block);
      return parameter;
   }

   public static Parameter getParameter(String paramName, List parameters) {
      if (parameters == null) {
         return null;
      } else {
         Iterator var2 = parameters.iterator();

         Parameter param;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            param = (Parameter)var2.next();
         } while(!param.getName().equals(paramName));

         return param;
      }
   }

   public static boolean isEquivalentLiteralStructures(JAXBStructuredType struct1, JAXBStructuredType struct2) {
      if (struct1.getElementMembersCount() != struct2.getElementMembersCount()) {
         return false;
      } else {
         Iterator members = struct1.getElementMembers();

         for(int i = 0; members.hasNext(); ++i) {
            JAXBElementMember member1 = (JAXBElementMember)members.next();
            JavaStructureMember javaMember1 = member1.getJavaStructureMember();
            JavaStructureMember javaMember2 = ((JavaStructureType)struct2.getJavaType()).getMemberByName(member1.getJavaStructureMember().getName());
            if (javaMember2.getConstructorPos() != i || !javaMember1.getType().equals(javaMember2.getType())) {
               return false;
            }
         }

         return false;
      }
   }

   public static QName getRawTypeName(Parameter parameter) {
      String name = parameter.getName();
      if (parameter.getType() instanceof JAXBType) {
         JAXBType jt = (JAXBType)parameter.getType();
         if (jt.isUnwrappable()) {
            List props = jt.getWrapperChildren();
            Iterator var4 = props.iterator();

            while(var4.hasNext()) {
               JAXBProperty prop = (JAXBProperty)var4.next();
               if (prop.getName().equals(name)) {
                  return prop.getRawTypeName();
               }
            }
         }
      }

      return null;
   }

   public static boolean isBoundToMimeContent(MessagePart part) {
      return part != null && part.getBindingExtensibilityElementKind() == 5;
   }

   public static boolean isBoundToSOAPBody(MessagePart part) {
      return part != null && part.getBindingExtensibilityElementKind() == 1;
   }

   public static boolean isBoundToSOAPHeader(MessagePart part) {
      return part != null && part.getBindingExtensibilityElementKind() == 2;
   }

   public static boolean isUnbound(MessagePart part) {
      return part != null && part.getBindingExtensibilityElementKind() == -1;
   }
}
