package sun.tools.jstat;

import java.util.Iterator;

public class ColumnFormat extends OptionFormat {
   private int number;
   private int width;
   private Alignment align;
   private Scale scale;
   private String format;
   private String header;
   private Expression expression;
   private Object previousValue;

   public ColumnFormat(int var1) {
      super("Column" + var1);
      this.align = Alignment.CENTER;
      this.scale = Scale.RAW;
      this.number = var1;
   }

   public void validate() throws ParserException {
      if (this.expression == null) {
         throw new ParserException("Missing data statement in column " + this.number);
      } else if (this.header == null) {
         throw new ParserException("Missing header statement in column " + this.number);
      } else {
         if (this.format == null) {
            this.format = "0";
         }

      }
   }

   public void setWidth(int var1) {
      this.width = var1;
   }

   public void setAlignment(Alignment var1) {
      this.align = var1;
   }

   public void setScale(Scale var1) {
      this.scale = var1;
   }

   public void setFormat(String var1) {
      this.format = var1;
   }

   public void setHeader(String var1) {
      this.header = var1;
   }

   public String getHeader() {
      return this.header;
   }

   public String getFormat() {
      return this.format;
   }

   public int getWidth() {
      return this.width;
   }

   public Alignment getAlignment() {
      return this.align;
   }

   public Scale getScale() {
      return this.scale;
   }

   public Expression getExpression() {
      return this.expression;
   }

   public void setExpression(Expression var1) {
      this.expression = var1;
   }

   public void setPreviousValue(Object var1) {
      this.previousValue = var1;
   }

   public Object getPreviousValue() {
      return this.previousValue;
   }

   public void printFormat(int var1) {
      String var2 = "  ";
      StringBuilder var3 = new StringBuilder("");

      for(int var4 = 0; var4 < var1; ++var4) {
         var3.append(var2);
      }

      System.out.println(var3 + this.name + " {");
      System.out.println(var3 + var2 + "name=" + this.name + ";data=" + this.expression.toString() + ";header=" + this.header + ";format=" + this.format + ";width=" + this.width + ";scale=" + this.scale.toString() + ";align=" + this.align.toString());
      Iterator var6 = this.children.iterator();

      while(var6.hasNext()) {
         OptionFormat var5 = (OptionFormat)var6.next();
         var5.printFormat(var1 + 1);
      }

      System.out.println(var3 + "}");
   }

   public String getValue() {
      return null;
   }
}
