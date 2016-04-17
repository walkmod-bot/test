package org.walkmod;

import java.util.List;

public class Foo {

   public void goToTeaserFrame() {
   }

   public boolean eval() {
      return false;
   }

   public int other() {
      return 0;
   }

   public void foo2() {
      boolean find = false;
      int retry = 0;
      while (!find) {
         goToTeaserFrame();
         find = eval();
         if (         !find && (++retry >= other())) {
            
         throw new RuntimeException("Unable to access to the text teaser");}
      }
      switch (retry){
      case 0: 
              {
System.out.println("hello");
System.out.println("bye");
}
      }

   }
}
