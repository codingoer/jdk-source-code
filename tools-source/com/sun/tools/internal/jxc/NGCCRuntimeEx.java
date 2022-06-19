package com.sun.tools.internal.jxc;

import com.sun.tools.internal.jxc.gen.config.NGCCRuntime;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class NGCCRuntimeEx extends NGCCRuntime {
   private final ErrorHandler errorHandler;

   public NGCCRuntimeEx(ErrorHandler errorHandler) {
      this.errorHandler = errorHandler;
   }

   public File getBaseDir(String baseDir) throws SAXException {
      File dir = new File(baseDir);
      if (dir.exists()) {
         return dir;
      } else {
         SAXParseException e = new SAXParseException(Messages.BASEDIR_DOESNT_EXIST.format(dir.getAbsolutePath()), this.getLocator());
         this.errorHandler.error(e);
         throw e;
      }
   }

   public List getIncludePatterns(List includeContent) {
      List includeRegexList = new ArrayList();
      Iterator var3 = includeContent.iterator();

      while(var3.hasNext()) {
         String includes = (String)var3.next();
         String regex = this.convertToRegex(includes);
         Pattern pattern = Pattern.compile(regex);
         includeRegexList.add(pattern);
      }

      return includeRegexList;
   }

   public List getExcludePatterns(List excludeContent) {
      List excludeRegexList = new ArrayList();
      Iterator var3 = excludeContent.iterator();

      while(var3.hasNext()) {
         String excludes = (String)var3.next();
         String regex = this.convertToRegex(excludes);
         Pattern pattern = Pattern.compile(regex);
         excludeRegexList.add(pattern);
      }

      return excludeRegexList;
   }

   private String convertToRegex(String pattern) {
      StringBuilder regex = new StringBuilder();
      char nc = true;
      if (pattern.length() > 0) {
         for(int i = 0; i < pattern.length(); ++i) {
            char c = pattern.charAt(i);
            char nc = ' ';
            if (i + 1 != pattern.length()) {
               nc = pattern.charAt(i + 1);
            }

            if (c == '.' && nc != '.') {
               regex.append('\\');
               regex.append('.');
            } else if (c != '.') {
               if (c == '*' && nc == '*') {
                  regex.append(".*");
                  break;
               }

               if (c == '*') {
                  regex.append("[^\\.]+");
               } else if (c == '?') {
                  regex.append("[^\\.]");
               } else {
                  regex.append(c);
               }
            }
         }
      }

      return regex.toString();
   }

   protected void unexpectedX(String token) throws SAXException {
      this.errorHandler.error(new SAXParseException(Messages.UNEXPECTED_NGCC_TOKEN.format(token, this.getLocator().getLineNumber(), this.getLocator().getColumnNumber()), this.getLocator()));
   }
}
