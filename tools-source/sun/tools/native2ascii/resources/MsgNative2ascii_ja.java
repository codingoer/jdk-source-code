package sun.tools.native2ascii.resources;

import java.util.ListResourceBundle;

public class MsgNative2ascii_ja extends ListResourceBundle {
   public Object[][] getContents() {
      Object[][] var1 = new Object[][]{{"err.bad.arg", "-encodingには引数が必要です"}, {"err.cannot.read", "{0}を読み込めませんでした。"}, {"err.cannot.write", "{0}を書き込めませんでした。"}, {"usage", "使用方法: native2ascii [-reverse] [-encoding encoding] [inputfile [outputfile]]"}};
      return var1;
   }
}
