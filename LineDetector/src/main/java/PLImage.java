import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * Created by Patrick on 12/13/2015.
 */
public class PLImage {
    BufferedImage mImage;

    public PLImage(BufferedImage img){
        mImage = deepCopy(img);
    }

    public PLImage(int w, int h){
        mImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
    }

    public BufferedImage getBufferedImage(){
        return mImage;
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public void convertYellowsToWhite(){
        int threshold = 70;
        for(int i=0; i<mImage.getWidth(); i++){
            for(int j=0; j<mImage.getHeight(); j++){
                if (Math.abs(getRed(i,j)-211)<threshold &&
                        Math.abs(getGreen(i,j)-196)<threshold &&
                        Math.abs(getBlue(i,j)-106)<threshold){
                    setPixel(i, j, 255, 255, 255);
                }
            }
        }
    }

    public PLImage fillBlackAboveLine(Line l){
        double t;
        t = l.t*Math.PI/180;
        int y_of_line = (int)((l.r-10*Math.cos(t))/Math.sin(t));
        System.out.println("y: "+y_of_line);
        for(int i=0; i<mImage.getWidth(); i++){
            for(int j=0; j<mImage.getHeight(); j++){
                if (y_of_line > j){
                    setPixel(i, j, 0, 0, 0);
                }
            }
        }
        return this;
    }

    public int getWidth(){
        return mImage.getWidth();
    }

    public int getHeight(){
        return mImage.getHeight();
    }

    private int[] getPixelData(int x, int y) {
        int argb = mImage.getRGB(x, y);

        int rgb[] = new int[] {
                (argb >> 16) & 0xff, //red
                (argb >>  8) & 0xff, //green
                (argb      ) & 0xff  //blue
        };

        //System.out.println("rgb: " + rgb[0] + " " + rgb[1] + " " + rgb[2]);
        return rgb;
    }

    int getRed(int x, int y){
        int []rgb = getPixelData(x, y);
        return rgb[0];
    }

    int getGreen(int x, int y){
        int []rgb = getPixelData(x, y);
        return rgb[1];
    }

    int getBlue(int x, int y){
        int []rgb = getPixelData(x, y);
        return rgb[2];
    }

    void setPixel(int i, int j, int r, int g, int b){
        int col = (r << 16) | (g << 8) | b;
        mImage.setRGB(i,j,col);
    }
}
