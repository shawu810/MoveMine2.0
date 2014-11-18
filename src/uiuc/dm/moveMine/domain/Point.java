package uiuc.dm.moveMine.domain;

import java.util.Date;
import org.joda.time.DateTime;
import org.apache.commons.math3.analysis.function.Acos;
import org.apache.commons.math3.analysis.function.Sin;

public class Point {
    // x-longitude, y-latitude

    private double x;
    private double y;
    // recorded time of this location
    private Date time;
    // ID name of this moving object
    private String id;
    // valid = not missing
    // !valid = missing point
    public boolean valid;

    public Point(double x, double y, Date time, String id, boolean valid) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.valid = valid;
    }

    public Point(double x, double y, Date time, String id) {
        this.x = x;
        this.y = y;
        this.time = time;
        valid = true;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public DateTime getTime() {
        return new DateTime(time);
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setTime(DateTime time) {
        this.time = time.toDate();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Point() {
    }

    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
        this.id = p.id;
        this.time = p.time;
        this.valid = p.valid;
    }

    public Point(String id, DateTime dt, boolean valid, double x, double y) {
        this.id = id;
        this.time = dt.toDate();
        this.valid = valid;
        this.x = x;
        this.y = y;
    }

    // The distance between two geographic coordinates (default for distance
    // calculation between two points)
    // Calculated reference: http://en.wikipedia.org/wiki/Great-circle_distance
    public double toDistance(Point other) {
        double long1 = x;
        double lat1 = y;
        double long2 = other.x;
        double lat2 = other.y;
        // PI / 180
        double constant = 0.0174532925199432957692369076848861271344287188854172;
        double d_long = (long2 - long1) * constant;
        //double d_lat = (lat2 - lat1) * constant;
        Acos acos = new Acos();
        Sin sin = new Sin();
        double lat1Constant = lat1 * constant;
        double lat2Constant = lat2 * constant;
        double a = sin.value(lat1Constant)
                * sin.value(lat2Constant)
                + Math.cos(lat1Constant)
                * Math.cos(lat2Constant) * Math.cos(d_long);
        double c = acos.value(a);
        return c * 6372800; // meters
    }

    // Eculidean distance between two coordinates: sqrt((x1-x2)^2+(y1-y2)^2)
    public double toEculideanDistance(Point other) {
        double diffX = x - other.getX();
        double diffY = y - other.getY();
        double diff = diffX * diffX + diffY * diffY;
        if (Math.abs(diff) < 1e-6) {
            return 0;
        } else {
            return Math.sqrt(diff);
        }
    }

    public String toString() {
//        StringBuilder builder = new StringBuilder("[").append(x).append(", ")
//                .append(y).append(", ").append(time).append(", ").append(id)
//                .append(']');
        StringBuilder builder = new StringBuilder("[").append(x).append(", ")
                .append(y).append("]");
        return builder.toString();
    }

    public int hashCode() {
        long bits = java.lang.Double.doubleToLongBits(getX());
        bits ^= java.lang.Double.doubleToLongBits(getY()) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Point)) {
            return false;
        }
        Point other = (Point) o;
        return this.x == other.x && this.y == other.y;
    }

    public int compareTo(DateTime startTime) {
        DateTime thisDateTime = new DateTime(time);
        return thisDateTime.compareTo(startTime);
    }
}
