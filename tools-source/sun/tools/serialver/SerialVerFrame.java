package sun.tools.serialver;

import java.awt.Event;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;

class SerialVerFrame extends Frame {
   MenuBar menu_mb;
   Menu file_m = new Menu(Res.getText("File"));
   MenuItem exit_i;
   private static final long serialVersionUID = -7248105987187532533L;

   SerialVerFrame() {
      super(Res.getText("SerialVersionInspector"));
      this.file_m.add(this.exit_i = new MenuItem(Res.getText("Exit")));
      this.menu_mb = new MenuBar();
      this.menu_mb.add(this.file_m);
   }

   public boolean handleEvent(Event var1) {
      if (var1.id == 201) {
         this.exit(0);
      }

      return super.handleEvent(var1);
   }

   public boolean action(Event var1, Object var2) {
      if (var1.target == this.exit_i) {
         this.exit(0);
      }

      return false;
   }

   void exit(int var1) {
      System.exit(var1);
   }
}
