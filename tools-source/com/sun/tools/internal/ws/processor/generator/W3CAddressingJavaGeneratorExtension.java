package com.sun.tools.internal.ws.processor.generator;

import com.sun.codemodel.internal.JAnnotationArrayMember;
import com.sun.codemodel.internal.JAnnotationUse;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.ws.api.TJavaGeneratorExtension;
import com.sun.tools.internal.ws.api.wsdl.TWSDLOperation;
import com.sun.tools.internal.ws.wsdl.document.Fault;
import com.sun.tools.internal.ws.wsdl.document.Operation;
import java.util.Iterator;
import java.util.Map;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;

public class W3CAddressingJavaGeneratorExtension extends TJavaGeneratorExtension {
   public void writeMethodAnnotations(TWSDLOperation two, JMethod jMethod) {
      JAnnotationUse actionAnn = null;
      if (two instanceof Operation) {
         Operation o = (Operation)two;
         if (o.getInput().getAction() != null && !o.getInput().getAction().equals("")) {
            actionAnn = jMethod.annotate(Action.class);
            actionAnn.param("input", o.getInput().getAction());
         }

         if (o.getOutput() != null && o.getOutput().getAction() != null && !o.getOutput().getAction().equals("")) {
            if (actionAnn == null) {
               actionAnn = jMethod.annotate(Action.class);
            }

            actionAnn.param("output", o.getOutput().getAction());
         }

         if (o.getFaults() != null && o.getFaults().size() > 0) {
            Map map = o.getFaults();
            JAnnotationArrayMember jam = null;
            Iterator var7 = o.faults().iterator();

            while(var7.hasNext()) {
               Fault f = (Fault)var7.next();
               if (f.getAction() != null && !f.getAction().equals("")) {
                  if (actionAnn == null) {
                     actionAnn = jMethod.annotate(Action.class);
                  }

                  if (jam == null) {
                     jam = actionAnn.paramArray("fault");
                  }

                  JAnnotationUse faAnn = jam.annotate(FaultAction.class);
                  faAnn.param("className", (JType)map.get(f.getName()));
                  faAnn.param("value", f.getAction());
               }
            }
         }

      }
   }
}
