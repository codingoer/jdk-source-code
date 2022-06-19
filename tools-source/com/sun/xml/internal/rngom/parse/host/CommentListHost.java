package com.sun.xml.internal.rngom.parse.host;

import com.sun.xml.internal.rngom.ast.builder.BuildException;
import com.sun.xml.internal.rngom.ast.builder.CommentList;
import com.sun.xml.internal.rngom.ast.om.Location;

class CommentListHost extends Base implements CommentList {
   final CommentList lhs;
   final CommentList rhs;

   CommentListHost(CommentList lhs, CommentList rhs) {
      this.lhs = lhs;
      this.rhs = rhs;
   }

   public void addComment(String value, Location _loc) throws BuildException {
      LocationHost loc = this.cast(_loc);
      if (this.lhs != null) {
         this.lhs.addComment(value, loc.lhs);
      }

      if (this.rhs != null) {
         this.rhs.addComment(value, loc.rhs);
      }

   }
}
