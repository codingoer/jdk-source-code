package com.sun.tools.jdi;

import com.sun.jdi.InternalException;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

abstract class ConnectorImpl implements Connector {
   Map defaultArguments = new LinkedHashMap();
   static String trueString = null;
   static String falseString;
   private ResourceBundle messages = null;

   public Map defaultArguments() {
      LinkedHashMap var1 = new LinkedHashMap();
      Collection var2 = this.defaultArguments.values();
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         ArgumentImpl var4 = (ArgumentImpl)var3.next();
         var1.put(var4.name(), (Connector.Argument)var4.clone());
      }

      return var1;
   }

   void addStringArgument(String var1, String var2, String var3, String var4, boolean var5) {
      this.defaultArguments.put(var1, new StringArgumentImpl(var1, var2, var3, var4, var5));
   }

   void addBooleanArgument(String var1, String var2, String var3, boolean var4, boolean var5) {
      this.defaultArguments.put(var1, new BooleanArgumentImpl(var1, var2, var3, var4, var5));
   }

   void addIntegerArgument(String var1, String var2, String var3, String var4, boolean var5, int var6, int var7) {
      this.defaultArguments.put(var1, new IntegerArgumentImpl(var1, var2, var3, var4, var5, var6, var7));
   }

   void addSelectedArgument(String var1, String var2, String var3, String var4, boolean var5, List var6) {
      this.defaultArguments.put(var1, new SelectedArgumentImpl(var1, var2, var3, var4, var5, var6));
   }

   ArgumentImpl argument(String var1, Map var2) throws IllegalConnectorArgumentsException {
      ArgumentImpl var3 = (ArgumentImpl)var2.get(var1);
      if (var3 == null) {
         throw new IllegalConnectorArgumentsException("Argument missing", var1);
      } else {
         String var4 = var3.value();
         if (var4 != null && var4.length() != 0) {
            if (!var3.isValid(var4)) {
               throw new IllegalConnectorArgumentsException("Argument invalid", var1);
            }
         } else if (var3.mustSpecify()) {
            throw new IllegalConnectorArgumentsException("Argument unspecified", var1);
         }

         return var3;
      }
   }

   String getString(String var1) {
      if (this.messages == null) {
         this.messages = ResourceBundle.getBundle("com.sun.tools.jdi.resources.jdi");
      }

      return this.messages.getString(var1);
   }

   public String toString() {
      String var1 = this.name() + " (defaults: ";
      Iterator var2 = this.defaultArguments().values().iterator();

      for(boolean var3 = true; var2.hasNext(); var3 = false) {
         ArgumentImpl var4 = (ArgumentImpl)var2.next();
         if (!var3) {
            var1 = var1 + ", ";
         }

         var1 = var1 + var4.toString();
      }

      var1 = var1 + ")";
      return var1;
   }

   class SelectedArgumentImpl extends ArgumentImpl implements Connector.SelectedArgument {
      private static final long serialVersionUID = -5689584530908382517L;
      private final List choices;

      SelectedArgumentImpl(String var2, String var3, String var4, String var5, boolean var6, List var7) {
         super(var2, var3, var4, var5, var6);
         this.choices = Collections.unmodifiableList(new ArrayList(var7));
      }

      public List choices() {
         return this.choices;
      }

      public boolean isValid(String var1) {
         return this.choices.contains(var1);
      }
   }

   class StringArgumentImpl extends ArgumentImpl implements Connector.StringArgument {
      private static final long serialVersionUID = 7500484902692107464L;

      StringArgumentImpl(String var2, String var3, String var4, String var5, boolean var6) {
         super(var2, var3, var4, var5, var6);
      }

      public boolean isValid(String var1) {
         return true;
      }
   }

   class IntegerArgumentImpl extends ArgumentImpl implements Connector.IntegerArgument {
      private static final long serialVersionUID = 763286081923797770L;
      private final int min;
      private final int max;

      IntegerArgumentImpl(String var2, String var3, String var4, String var5, boolean var6, int var7, int var8) {
         super(var2, var3, var4, var5, var6);
         this.min = var7;
         this.max = var8;
      }

      public void setValue(int var1) {
         this.setValue(this.stringValueOf(var1));
      }

      public boolean isValid(String var1) {
         if (var1 == null) {
            return false;
         } else {
            try {
               return this.isValid(Integer.decode(var1));
            } catch (NumberFormatException var3) {
               return false;
            }
         }
      }

      public boolean isValid(int var1) {
         return this.min <= var1 && var1 <= this.max;
      }

      public String stringValueOf(int var1) {
         return "" + var1;
      }

      public int intValue() {
         if (this.value() == null) {
            return 0;
         } else {
            try {
               return Integer.decode(this.value());
            } catch (NumberFormatException var2) {
               return 0;
            }
         }
      }

      public int max() {
         return this.max;
      }

      public int min() {
         return this.min;
      }
   }

   class BooleanArgumentImpl extends ArgumentImpl implements Connector.BooleanArgument {
      private static final long serialVersionUID = 1624542968639361316L;

      BooleanArgumentImpl(String var2, String var3, String var4, boolean var5, boolean var6) {
         super(var2, var3, var4, (String)null, var6);
         if (ConnectorImpl.trueString == null) {
            ConnectorImpl.trueString = ConnectorImpl.this.getString("true");
            ConnectorImpl.falseString = ConnectorImpl.this.getString("false");
         }

         this.setValue(var5);
      }

      public void setValue(boolean var1) {
         this.setValue(this.stringValueOf(var1));
      }

      public boolean isValid(String var1) {
         return var1.equals(ConnectorImpl.trueString) || var1.equals(ConnectorImpl.falseString);
      }

      public String stringValueOf(boolean var1) {
         return var1 ? ConnectorImpl.trueString : ConnectorImpl.falseString;
      }

      public boolean booleanValue() {
         return this.value().equals(ConnectorImpl.trueString);
      }
   }

   abstract class ArgumentImpl implements Connector.Argument, Cloneable, Serializable {
      private String name;
      private String label;
      private String description;
      private String value;
      private boolean mustSpecify;

      ArgumentImpl(String var2, String var3, String var4, String var5, boolean var6) {
         this.name = var2;
         this.label = var3;
         this.description = var4;
         this.value = var5;
         this.mustSpecify = var6;
      }

      public abstract boolean isValid(String var1);

      public String name() {
         return this.name;
      }

      public String label() {
         return this.label;
      }

      public String description() {
         return this.description;
      }

      public String value() {
         return this.value;
      }

      public void setValue(String var1) {
         if (var1 == null) {
            throw new NullPointerException("Can't set null value");
         } else {
            this.value = var1;
         }
      }

      public boolean mustSpecify() {
         return this.mustSpecify;
      }

      public boolean equals(Object var1) {
         if (var1 != null && var1 instanceof Connector.Argument) {
            Connector.Argument var2 = (Connector.Argument)var1;
            return this.name().equals(var2.name()) && this.description().equals(var2.description()) && this.mustSpecify() == var2.mustSpecify() && this.value().equals(var2.value());
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.description().hashCode();
      }

      public Object clone() {
         try {
            return super.clone();
         } catch (CloneNotSupportedException var2) {
            throw new InternalException();
         }
      }

      public String toString() {
         return this.name() + "=" + this.value();
      }
   }
}
