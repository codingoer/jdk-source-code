package com.sun.xml.internal.rngom.parse.host;

import com.sun.xml.internal.rngom.ast.builder.Annotations;
import com.sun.xml.internal.rngom.ast.om.Location;

public class Base {
   private static final AnnotationsHost nullAnnotations = new AnnotationsHost((Annotations)null, (Annotations)null);
   private static final LocationHost nullLocation = new LocationHost((Location)null, (Location)null);

   protected AnnotationsHost cast(Annotations ann) {
      return ann == null ? nullAnnotations : (AnnotationsHost)ann;
   }

   protected LocationHost cast(Location loc) {
      return loc == null ? nullLocation : (LocationHost)loc;
   }
}
