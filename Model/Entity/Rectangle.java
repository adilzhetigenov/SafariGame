package Model.Entity;

public class Rectangle {
   private int x;
   private int y;
   private int width = 32;
   private int height = 32; 
   


   public int getX() {
       return x;
   }

   public void setX(int x) {
       this.x = x;
   }
   
   public void setY(int y) {
       this.y = y;
   }
   
   public int getY() {
       return y;
   }
   
   public int getWidth() {
       return width;
   }

   public void setWidth(int width) {
       this.width = width;
   }

   public int getHeight() {
       return height;
   }

   public void setHeight(int height) {
       this.height = height;
   }
}
