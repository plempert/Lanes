/**
 * Created by Patrick on 12/14/2015.
 */
public class Line {
    public int r; // distance from origin
    public int t; // angle (t=0 is 45 degrees)
    Line(int r, int t){
        this.r = r;
        this.t = t;
    }

    public static double getM(Line l){
        return -1*Math.cos(Math.toRadians(l.t))/Math.sin(Math.toRadians(l.t));
    }

    public static double getB(Line l){
        return l.r/Math.sin(Math.toRadians(l.t));
    }

    public static double getY(Line l, double X){
        return (l.r-X*Math.cos(Math.toRadians(l.t)))/(Math.sin(Math.toRadians(l.t)));
    }

    public static double getX(Line l, double Y){
        return (l.r-Y*Math.sin(Math.toRadians(l.t)))/(Math.cos(Math.toRadians(l.t)));
    }


}
