package com.sun.tools.javac.util;

import com.sun.tools.javac.main.Option;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

public class Options {
   private static final long serialVersionUID = 0L;
   public static final Context.Key optionsKey = new Context.Key();
   private LinkedHashMap values = new LinkedHashMap();
   private List listeners = List.nil();

   public static Options instance(Context var0) {
      Options var1 = (Options)var0.get(optionsKey);
      if (var1 == null) {
         var1 = new Options(var0);
      }

      return var1;
   }

   protected Options(Context var1) {
      var1.put((Context.Key)optionsKey, (Object)this);
   }

   public String get(String var1) {
      return (String)this.values.get(var1);
   }

   public String get(Option var1) {
      return (String)this.values.get(var1.text);
   }

   public boolean getBoolean(String var1) {
      return this.getBoolean(var1, false);
   }

   public boolean getBoolean(String var1, boolean var2) {
      String var3 = this.get(var1);
      return var3 == null ? var2 : Boolean.parseBoolean(var3);
   }

   public boolean isSet(String var1) {
      return this.values.get(var1) != null;
   }

   public boolean isSet(Option var1) {
      return this.values.get(var1.text) != null;
   }

   public boolean isSet(Option var1, String var2) {
      return this.values.get(var1.text + var2) != null;
   }

   public boolean isUnset(String var1) {
      return this.values.get(var1) == null;
   }

   public boolean isUnset(Option var1) {
      return this.values.get(var1.text) == null;
   }

   public boolean isUnset(Option var1, String var2) {
      return this.values.get(var1.text + var2) == null;
   }

   public void put(String var1, String var2) {
      this.values.put(var1, var2);
   }

   public void put(Option var1, String var2) {
      this.values.put(var1.text, var2);
   }

   public void putAll(Options var1) {
      this.values.putAll(var1.values);
   }

   public void remove(String var1) {
      this.values.remove(var1);
   }

   public Set keySet() {
      return this.values.keySet();
   }

   public int size() {
      return this.values.size();
   }

   public void addListener(Runnable var1) {
      this.listeners = this.listeners.prepend(var1);
   }

   public void notifyListeners() {
      Iterator var1 = this.listeners.iterator();

      while(var1.hasNext()) {
         Runnable var2 = (Runnable)var1.next();
         var2.run();
      }

   }

   public boolean lint(String var1) {
      return this.isSet(Option.XLINT_CUSTOM, var1) || (this.isSet(Option.XLINT) || this.isSet(Option.XLINT_CUSTOM, "all")) && this.isUnset(Option.XLINT_CUSTOM, "-" + var1);
   }
}
