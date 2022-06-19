package com.sun.xml.internal.rngom.ast.util;

import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.SchemaBuilder;
import com.sun.xml.internal.rngom.ast.om.ParsedPattern;
import com.sun.xml.internal.rngom.binary.SchemaBuilderImpl;
import com.sun.xml.internal.rngom.binary.SchemaPatternBuilder;
import com.sun.xml.internal.rngom.parse.IllegalSchemaException;
import com.sun.xml.internal.rngom.parse.host.ParsedPatternHost;
import com.sun.xml.internal.rngom.parse.host.SchemaBuilderHost;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.xml.sax.ErrorHandler;

public class CheckingSchemaBuilder extends SchemaBuilderHost {
   public CheckingSchemaBuilder(SchemaBuilder sb, ErrorHandler eh) {
      super(new SchemaBuilderImpl(eh), sb);
   }

   public CheckingSchemaBuilder(SchemaBuilder sb, ErrorHandler eh, DatatypeLibraryFactory dlf) {
      super(new SchemaBuilderImpl(eh, dlf, new SchemaPatternBuilder()), sb);
   }

   public ParsedPattern expandPattern(ParsedPattern p) throws BuildException, IllegalSchemaException {
      ParsedPatternHost r = (ParsedPatternHost)super.expandPattern(p);
      return r.rhs;
   }
}
