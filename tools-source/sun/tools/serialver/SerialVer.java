package sun.tools.serialver;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;
import sun.net.www.ParseUtil;

public class SerialVer extends Applet {
   GridBagLayout gb;
   TextField classname_t;
   Button show_b;
   TextField serialversion_t;
   Label footer_l;
   private static final long serialVersionUID = 7666909783837760853L;
   static URLClassLoader loader = null;

   public synchronized void init() {
      this.gb = new GridBagLayout();
      this.setLayout(this.gb);
      GridBagConstraints var1 = new GridBagConstraints();
      var1.fill = 1;
      Label var2 = new Label(Res.getText("FullClassName"));
      var2.setAlignment(2);
      this.gb.setConstraints(var2, var1);
      this.add(var2);
      this.classname_t = new TextField(20);
      var1.gridwidth = -1;
      var1.weightx = 1.0;
      this.gb.setConstraints(this.classname_t, var1);
      this.add(this.classname_t);
      this.show_b = new Button(Res.getText("Show"));
      var1.gridwidth = 0;
      var1.weightx = 0.0;
      this.gb.setConstraints(this.show_b, var1);
      this.add(this.show_b);
      Label var3 = new Label(Res.getText("SerialVersion"));
      var3.setAlignment(2);
      var1.gridwidth = 1;
      this.gb.setConstraints(var3, var1);
      this.add(var3);
      this.serialversion_t = new TextField(50);
      this.serialversion_t.setEditable(false);
      var1.gridwidth = 0;
      this.gb.setConstraints(this.serialversion_t, var1);
      this.add(this.serialversion_t);
      this.footer_l = new Label();
      var1.gridwidth = 0;
      this.gb.setConstraints(this.footer_l, var1);
      this.add(this.footer_l);
      this.classname_t.requestFocus();
   }

   public void start() {
      this.classname_t.requestFocus();
   }

   public boolean action(Event var1, Object var2) {
      if (var1.target == this.classname_t) {
         this.show((String)var1.arg);
         return true;
      } else if (var1.target == this.show_b) {
         this.show(this.classname_t.getText());
         return true;
      } else {
         return false;
      }
   }

   public boolean handleEvent(Event var1) {
      boolean var2 = super.handleEvent(var1);
      return var2;
   }

   void show(String var1) {
      try {
         this.footer_l.setText("");
         this.serialversion_t.setText("");
         if (var1.equals("")) {
            return;
         }

         String var2 = serialSyntax(var1);
         if (var2 != null) {
            this.serialversion_t.setText(var2);
         } else {
            this.footer_l.setText(Res.getText("NotSerializable", var1));
         }
      } catch (ClassNotFoundException var3) {
         this.footer_l.setText(Res.getText("ClassNotFound", var1));
      }

   }

   static void initializeLoader(String var0) throws MalformedURLException, IOException {
      StringTokenizer var2 = new StringTokenizer(var0, File.pathSeparator);
      int var3 = var2.countTokens();
      URL[] var1 = new URL[var3];

      for(int var4 = 0; var4 < var3; ++var4) {
         var1[var4] = ParseUtil.fileToEncodedURL(new File((new File(var2.nextToken())).getCanonicalPath()));
      }

      loader = new URLClassLoader(var1);
   }

   static String serialSyntax(String var0) throws ClassNotFoundException {
      String var1 = null;
      boolean var2 = false;
      if (var0.indexOf(36) != -1) {
         var1 = resolveClass(var0);
      } else {
         try {
            var1 = resolveClass(var0);
            var2 = true;
         } catch (ClassNotFoundException var8) {
         }

         if (!var2) {
            StringBuffer var3 = new StringBuffer(var0);
            String var4 = var3.toString();

            int var5;
            while((var5 = var4.lastIndexOf(46)) != -1 && !var2) {
               var3.setCharAt(var5, '$');

               try {
                  var4 = var3.toString();
                  var1 = resolveClass(var4);
                  var2 = true;
               } catch (ClassNotFoundException var7) {
               }
            }
         }

         if (!var2) {
            throw new ClassNotFoundException();
         }
      }

      return var1;
   }

   static String resolveClass(String var0) throws ClassNotFoundException {
      Class var1 = Class.forName(var0, false, loader);
      ObjectStreamClass var2 = ObjectStreamClass.lookup(var1);
      return var2 != null ? "    private static final long serialVersionUID = " + var2.getSerialVersionUID() + "L;" : null;
   }

   private static void showWindow(Window var0) {
      var0.show();
   }

   public static void main(String[] var0) {
      boolean var1 = false;
      String var2 = null;
      boolean var3 = false;
      if (var0.length == 0) {
         usage();
         System.exit(1);
      }

      int var9;
      for(var9 = 0; var9 < var0.length; ++var9) {
         if (var0[var9].equals("-show")) {
            var1 = true;
         } else if (var0[var9].equals("-classpath")) {
            if (var9 + 1 == var0.length || var0[var9 + 1].startsWith("-")) {
               System.err.println(Res.getText("error.missing.classpath"));
               usage();
               System.exit(1);
            }

            var2 = new String(var0[var9 + 1]);
            ++var9;
         } else {
            if (!var0[var9].startsWith("-")) {
               break;
            }

            System.err.println(Res.getText("invalid.flag", var0[var9]));
            usage();
            System.exit(1);
         }
      }

      if (var2 == null) {
         var2 = System.getProperty("env.class.path");
         if (var2 == null) {
            var2 = ".";
         }
      }

      try {
         initializeLoader(var2);
      } catch (MalformedURLException var7) {
         System.err.println(Res.getText("error.parsing.classpath", var2));
         System.exit(2);
      } catch (IOException var8) {
         System.err.println(Res.getText("error.parsing.classpath", var2));
         System.exit(3);
      }

      if (!var1) {
         if (var9 == var0.length) {
            usage();
            System.exit(1);
         }

         boolean var4 = false;

         for(var9 = var9; var9 < var0.length; ++var9) {
            try {
               String var5 = serialSyntax(var0[var9]);
               if (var5 != null) {
                  System.out.println(var0[var9] + ":" + var5);
               } else {
                  System.err.println(Res.getText("NotSerializable", var0[var9]));
                  var4 = true;
               }
            } catch (ClassNotFoundException var6) {
               System.err.println(Res.getText("ClassNotFound", var0[var9]));
               var4 = true;
            }
         }

         if (var4) {
            System.exit(1);
         }
      } else {
         if (var9 < var0.length) {
            System.err.println(Res.getText("ignoring.classes"));
            System.exit(1);
         }

         SerialVerFrame var10 = new SerialVerFrame();
         SerialVer var11 = new SerialVer();
         var11.init();
         var10.add("Center", var11);
         var10.pack();
         showWindow(var10);
      }

   }

   public static void usage() {
      System.err.println(Res.getText("usage"));
   }
}
