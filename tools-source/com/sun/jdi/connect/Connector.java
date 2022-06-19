package com.sun.jdi.connect;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import jdk.Exported;

@Exported
public interface Connector {
   String name();

   String description();

   Transport transport();

   Map defaultArguments();

   @Exported
   public interface SelectedArgument extends Argument {
      List choices();

      boolean isValid(String var1);
   }

   @Exported
   public interface StringArgument extends Argument {
      boolean isValid(String var1);
   }

   @Exported
   public interface IntegerArgument extends Argument {
      void setValue(int var1);

      boolean isValid(String var1);

      boolean isValid(int var1);

      String stringValueOf(int var1);

      int intValue();

      int max();

      int min();
   }

   @Exported
   public interface BooleanArgument extends Argument {
      void setValue(boolean var1);

      boolean isValid(String var1);

      String stringValueOf(boolean var1);

      boolean booleanValue();
   }

   @Exported
   public interface Argument extends Serializable {
      String name();

      String label();

      String description();

      String value();

      void setValue(String var1);

      boolean isValid(String var1);

      boolean mustSpecify();
   }
}
