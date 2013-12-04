package Clustream.addon;

 import java.awt.Graphics;

 public interface AWTRenderer {
     public void renderAWTBox(Graphics g, int minPixelX, int minPixelY,
             int maxPixelX, int maxPixelY);
 }