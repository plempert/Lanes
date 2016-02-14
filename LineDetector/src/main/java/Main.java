import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Patrick on 12/13/2015.
 */
public class Main {
    public static BufferedImage original;
    public static String path = "src/main/resources/doe";

    public static void main(String [] args){

        original = readImage(path + ".jpg");
        BufferedImage edgeView = CannyEdgeDetector.convertToEdgeView(original);
        ArrayList<Line> lines = HoughTransform.findLines(edgeView);
        //lines = findHorizon(lines);
        Line horizon = findHorizon(lines);
        edgeView = (new PLImage(edgeView)).fillBlackAboveLine(horizon).getBufferedImage();
        lines = HoughTransform.findLines(edgeView);
        System.out.println(lines.size()+" lines were detected.");

        for(int i=0; i<lines.size(); i++){
            BufferedImage singleLine = HoughTransform.drawLines(original, new ArrayList<Line>(Arrays.asList(lines.get(i))));
            //writeImage(singleLine, path+"_line_"+i+".jpg");
            System.out.println(i+"\tangleWithHorizontal: "+ angleWithHorizontal(lines.get(i)));
        }

        removeVerticalLines(lines);
        findLaneMarkings(lines, horizon);
        //lines = findLane(lines);
        BufferedImage imgWithLines = HoughTransform.drawLines(original, lines);
        writeImage(imgWithLines, path + "_lines.jpg");
        writeImage(edgeView, path + "_edgeView.jpg");

//        BufferedImage edges = PLImage.deepCopy(original);
//        edges = CannyEdgeDetector.convertToEdgeView(edges);
//        writeImage(edges, path + "_edges.jpg");
//
//
//        BufferedImage edges_lines = PLImage.deepCopy(edges);
//        HoughTransform.findLines(edges_lines);
//
//        edges_lines = HoughTransform.drawLines(edges_lines);
//        writeImage(edges_lines, path + "_edges_lines.jpg");
//
//        BufferedImage lines = PLImage.deepCopy(original);
//        lines = HoughTransform.drawLines(lines);
//        writeImage(lines, path + "_lines.jpg");
    }

    public static void removeVerticalLines(ArrayList<Line> lines){
        for(int i=0; i<lines.size(); i++){
            if(Line.getM(lines.get(i))>2){
                lines.remove(i);
            }
        }
    }

    public static void findLaneMarkings(ArrayList<Line> lines, Line horizon){
        // find a pair of lines that fits the following constraints:
        // 1. They are symmetric with respect to the vertical
        // 2. They intersect at the horizon
        // 3. They intersect near the middle of the image (width-wise)

        int i=0;

        HashSet<HashSet<Line>> cache = new HashSet<HashSet<Line>>();
        for(Line first : lines){
            for(Line second : lines){
                if(!first.equals(second)
                        && !cache.contains(new HashSet<Line>(Arrays.asList(first, second)))
                        && areSymmetrical(first, second)
                        && intersectAtHorizonAndMiddleOfScreen(first, second, horizon)){
                    BufferedImage withLanes = HoughTransform.drawLines(original, new ArrayList<Line>(Arrays.asList(first, second)));
                    writeImage(withLanes, path+"_lanes_"+i+".jpg");
                    i++;
                    System.out.println("Is in a lane!");
                    cache.add(new HashSet<Line>(Arrays.asList(first, second)));

                }
            }
        }
    }

    public static boolean areSymmetrical(Line first, Line second){
        double first_angle = angleWithHorizontal(first);
        double second_angle = angleWithHorizontal(second);
        return withinTolerance(Math.abs(first_angle+second_angle), 2);
    }

    public static boolean withinTolerance(double x, double tolerance){
        return x < tolerance;
    }

    public static double angleWithHorizontal(Line l){
        double l_x0 = 0, l_x1 = 1, l_y0, l_y1;
        l_y0 = (l.r-l_x0*Math.cos(Math.toRadians(l.t)))/(Math.toRadians(l.t));
        l_y1 = (l.r-l_x1*Math.cos(Math.toRadians(l.t)))/(Math.toRadians(l.t));
        // Attention! I switched delta x and delta y in the constructor below!
        // As a side effect, the return value gives the angle with the horizontal
        // This works, but I'm not 100% sure why
        // Also: symmetry along the vertical implies symmetry along the horizontal
        Vector2D line = new Vector2D(l_y1-l_y0, l_x1-l_x0);
        if(line.y>0) line = Vector2D.scale(line, -1);
        Vector2D vertical = new Vector2D(0, 1);
        return Math.toDegrees(Vector2D.signed_angle(line, vertical));
    }

    public static boolean intersectAtHorizonAndMiddleOfScreen(Line a, Line b, Line h){
//        // The coefficient matrix 'a'
//        Matrix A = new Basic2DMatrix(new double[][] {
//                { Math.cos(Math.toRadians(a.t)), Math.sin(Math.toRadians(a.t)) },
//                { Math.cos(Math.toRadians(b.t)), Math.sin(Math.toRadians(b.t)) }
//        });
//
//        // A right hand side vector, which is simple dense vector
//        Vector B = new BasicVector(new double[] { a.r, b.r });
//
//        // We will use standard Forward-Back Substitution method,
//        // which is based on LU decomposition and can be used with square systems
//        LinearSystemSolver solver =
//                A.withSolver(LinearAlgebra.FORWARD_BACK_SUBSTITUTION);
//
//        Vector x = solver.solve(B);

        double x_at_intersect = (Line.getB(b)-Line.getB(a))/(Line.getM(a)-Line.getM(b));
        double y_at_intersect = Line.getM(a)*x_at_intersect + Line.getB(a);


        double y_of_h = Line.getY(h, original.getWidth()/2);
        //y_at_intersect = Math.abs(y_at_intersect);
        //x_at_intersect = Math.abs(x_at_intersect);

        boolean returnMe = withinTolerance(Math.abs(y_at_intersect-y_of_h), 20)
                && withinTolerance(Math.abs(x_at_intersect-original.getWidth()/2), 20);

        if(returnMe){
            System.out.println(y_at_intersect+"  "+y_of_h);
        }

        // tolerance measured in pixels
        return returnMe;
    }

    public static Line findHorizon(ArrayList<Line> lines){
        double min_t = 0;
        Line returnLine = null;
        for(Line line : lines){
            if(Math.abs(line.t-90) < Math.abs(min_t-90) && line.r > 50){
                min_t = line.t;
                returnLine = line;
            }
        }
        return returnLine;
    }

    public static ArrayList<Line> findLane(ArrayList<Line> lines){
        return null;
    }

    public static BufferedImage readImage(String str){
        BufferedImage img;
        try {
            img = ImageIO.read(new File(str));
            return img;
        } catch (IOException e) {
            // ...
        }
        return null;
    }

    public static void writeImage(BufferedImage img, String str){
        try {
            // retrieve image
            File outputfile = new File(str);
            ImageIO.write(img, "jpg", outputfile);
        } catch (IOException e) {
            // ...
        }
    }

}
