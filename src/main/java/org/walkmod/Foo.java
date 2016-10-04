package org.walkmod;

public class Foo {

   public void goToTeaserFrame() {
   }

   public boolean eval() {
      return false;
   }

   public int other() {
      return 0;
   }

   public void foo2() throws Exception {
      boolean find = false;
      int retry = 0;
      while (!find) {
         goToTeaserFrame();
         find = eval();
         if (!find && (++retry >= other())){
           throw new Exception("Unable to access to the text teaser");
         }
      }
      System.out.println("hello");
      switch (retry) {
      case 0:              
            {
                  System.out.println("hello");
                  System.out.println("bye");
            }        
      }

   }
}
