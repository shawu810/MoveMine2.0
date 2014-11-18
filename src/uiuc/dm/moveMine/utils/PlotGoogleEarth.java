package uiuc.dm.moveMine.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import uiuc.dm.moveMine.domain.Point;
import uiuc.dm.moveMine.domain.Trajectory;

public class PlotGoogleEarth {

    private final static String[] lineColor = {"yellowline", "blueline"};
    private final static String[] markerColor = {"green", "red"};

    private static String buildALine(Trajectory traj, String color, String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("<Placemark><name>");
        builder.append(name);
        builder.append("</name><styleUrl>#");
        builder.append(color);
        builder.append("</styleUrl><LineString><tessellate>1</tessellate><coordinates>");
        List<Point> points1 = traj.getPoints();
        for (Point p : points1) {
            if (p.valid) {
                builder.append(convertPoint(p));
            }
        }
        builder.append("</coordinates></LineString></Placemark>");

        return builder.toString();
    }

    public static String showTwoLines(Trajectory traj1, Trajectory traj2) {
        StringBuilder builder = new StringBuilder();
        builder.append(buildALine(traj1, lineColor[0], traj1.getId()));
        builder.append(buildALine(traj2, lineColor[1], traj2.getId()));
        return builder.toString();
    }
    
    public static String showOneLine(Trajectory traj){
        StringBuilder builder = new StringBuilder();
        builder.append(buildALine(traj,lineColor[0], traj.getId()));
        return builder.toString();
    }

    private static String convertPoint(Point p) {
        StringBuilder builder = new StringBuilder();
        double x = p.getX();
        double y = p.getY();

        if (x < -180) {
            x = x + 360;
        } else if (x > 180) {
            x = x - 360;
        }

        if (y < -180) {
            y = y + 360;
        } else if (y > 180) {
            y = y - 360;
        }
        builder.append(x);
        builder.append(",");
        builder.append(y);
        builder.append(",0 ");
        return builder.toString();
    }

    public static String buildAMarker(Point p, int markerColorIdx, int name) {
        StringBuilder builder = new StringBuilder();
        builder.append("<Placemark><name>");
        builder.append(name);
        builder.append("</name><styleUrl>#");
        builder.append(markerColor[markerColorIdx]);
        builder.append("</styleUrl><Point><coordinates>");
        builder.append(convertPoint(p));
        builder.append("</coordinates></Point></Placemark>");
        return builder.toString();
    }

    public static String buildAMarker(Point p, int markerColorIdx, int nameId, Date time) {
        StringBuilder builder = new StringBuilder();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        String pointLocation = convertPoint(p);
        String formattedTime = dateFormatter.format(time);
        StringBuilder description = new StringBuilder("Name: ").append(p.getId()).append("_").append(nameId)
                .append(", <![CDATA[<br>]]>Time: ")
                .append(formattedTime)
                .append(", <![CDATA[<br>]]>Location: ")
                .append(pointLocation);
        builder.append("<Placemark>");
//        builder.append("<name>").append(p.getId()).append("_").append(nameId).append("</name>");
        builder.append("<styleUrl>#").append(markerColor[markerColorIdx]).append("</styleUrl>");
        builder.append("<Point><coordinates>").append(pointLocation).append("</coordinates></Point>");
        builder.append("<description>").append(description.toString()).append("</description>");
        builder.append("<TimeStamp>").append(formattedTime).append("</TimeStamp>");
        builder.append("</Placemark>");
        return builder.toString();
    }
}
