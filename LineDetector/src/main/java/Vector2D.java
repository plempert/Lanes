/**
 * Created by Patrick on 2/14/16.
 */
public class Vector2D {
    public double x;
    public double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static void main(String [] args){
        Vector2D first = new Vector2D(0,1);
        Vector2D second = new Vector2D(1,1);
        System.out.println(Math.toDegrees(Vector2D.signed_angle(second, first)));
        System.out.println(Vector2D.size(Vector2D.normalize(new Vector2D(3,4))));
    }

    public static double signed_angle(Vector2D first, Vector2D second){
        first = normalize(first);
        second = normalize(second);
        return Math.asin(first.x*second.y-first.y*second.x);
    }

    public static double angle(Vector2D first, Vector2D second){
        first = normalize(first);
        second = normalize(second);
        return Math.acos(first.x*second.x+first.y*second.y);
    }

    public static double size (Vector2D v){
        return Math.sqrt(v.x*v.x+v.y*v.y);
    }

    public static Vector2D normalize(Vector2D v){
        return new Vector2D(v.x/size(v), v.y/size(v));
    }

    public static Vector2D scale(Vector2D v, double scaleFactor){
        return new Vector2D(v.x*scaleFactor, v.y*scaleFactor);
    }

    public static Vector2D difference(Vector2D a, Vector2D b){
        return new Vector2D(a.x-b.x, a.y-b.y);
    }
}
