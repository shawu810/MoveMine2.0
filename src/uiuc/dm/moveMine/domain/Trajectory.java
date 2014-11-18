package uiuc.dm.moveMine.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.joda.time.Minutes;

/**
 * The class of trajectory. A trajectory is represented as a list of points.
 * Note that points in trajectory class are the locations recored in raw data.
 * They points could be unevenly sampled (e.g., the first point was recorded at
 * 10:00am, the second point was recored at 11:15am, the third was recorded at
 * 2:55pm). Since many algorithms require the points in a trajectory are evenly
 * sampled. Therefore, INTERPOLATION is an important step. "filledPoints" is a
 * variable to store interpolated points. The points are interpolated with
 * "timeGap" variable.
 */
public class Trajectory {

    // raw points in the trajectory
    private List<Point> points;
    // interpolated points in the trajectory
    private ArrayList<Point> filledPoints;
    // ID of the trajectory
    private String id;
    private BoxRange boxRange;

    public Trajectory() {
        points = new ArrayList<Point>();
        filledPoints = new ArrayList<Point>();
        boxRange = new BoxRange();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addPoint(Point p) {
        points.add(p);
    }

    public Point getPoint(int i) {
        return points.get(i);
    }

    public List<Point> getPoints() {
        return points;
    }

    public int getPointsNum() {
        return points.size();
    }

    public void addOneFilledPoint(Point p) {
        filledPoints.add(p);
    }

    public Point getFilledPoint(int i) {
        return filledPoints.get(i);
    }

    public ArrayList<Point> getFilledPoints() {
        return filledPoints;
    }

    public int getFilledPointsNum() {
        return filledPoints.size();
    }

    public BoxRange getBoxRange() {
        return this.boxRange;
    }

    public void setBoxRange(double maxX, double minX, double maxY, double minY) {
        boxRange.setMaxX(maxX);
        boxRange.setMinX(minX);
        boxRange.setMaxY(maxY);
        boxRange.setMinY(minY);
    }

    // Since two trajectories may not have the same recorded time periods, to
    // calculate the distance between two trajectories, their overlapped
    // recording time interval is first computed.
    public Pair getOverlappedTimeInterval(List<Point> traj0, List<Point> traj1) {
        int s0 = 0, t0 = traj0.size() - 1;
        while ((s0 < t0) && (traj0.get(s0).valid == false)) {
            s0++;
        }
        while ((t0 > 0) && traj0.get(t0).valid == false) {
            t0--;
        }
        int s1 = 0, t1 = traj1.size() - 1;
        while ((s1 < t1) && (traj1.get(s1).valid == false)) {
            s1++;
        }
        while ((t1 > 0) && (traj1.get(t1).valid == false)) {
            t1--;
        }

        List<Point> trajNew0 = new ArrayList<Point>();
        List<Point> trajNew1 = new ArrayList<Point>();
        int s = Math.max(s0, s1);
        int t = Math.min(t0, t1);
        for (int i = s; i <= t; i++) {
            if (traj0.get(i).valid && traj1.get(i).valid) {
                trajNew0.add(traj0.get(i));
                trajNew1.add(traj1.get(i));
            }
        }
        traj0 = trajNew0;
        traj1 = trajNew1;
        return new Pair(trajNew0, trajNew1);
    }

    // The average distance between each time-aligned points in two trajectories
    public double pointVectorAvgDistance(List<Point> traj0, List<Point> traj1, int lag) {
        int dist_n = 0;
        double dist = 0;
        for (int i = 0; i < traj0.size(); i++) {
            int j = (i + lag) % traj0.size();
            if ((traj0.get(i).valid) && (traj1.get(j).valid)) {
                double res = traj0.get(i).toDistance(traj1.get(j));
                dist += Double.isNaN(res)? 0 : res;
                dist_n++;
            }
        }

        if (dist_n == 0) {
            return 0; // no overlapping time
        } else {
            return dist / dist_n;
        }
    }

    // Eculidean distance between two trajectories
    public double toAvgDistanceOld(Trajectory other) {
        ArrayList<Point> traj0 = filledPoints;
        ArrayList<Point> traj1 = other.filledPoints;
        if (traj0.isEmpty() || traj1.isEmpty()) {
            return 0;  // no overlapping time
        }
        Pair pair = getOverlappedTimeInterval(traj0, traj1);
        return pointVectorAvgDistance((ArrayList<Point>) pair.x, (ArrayList<Point>) pair.y, 0);
    }

    // Eculidean distance between two trajectories
    public double toAvgDistance(Trajectory other) {
//        ArrayList<Point> traj0 = filledPoints;
//        ArrayList<Point> traj1 = other.filledPoints;
        List<Point> traj0 = points;
        List<Point> traj1 = other.points;
        if (traj0.isEmpty() || traj1.isEmpty()) {
            return -1;  // no overlapping time
        }

        // compute distance between overlapped section on two trajectories
        double dist = 0;
        int n = 0;
        ListIterator<Point> traj0Lter = traj0.listIterator();
        ListIterator<Point> traj1Lter = traj1.listIterator();

        Point p0 = traj0Lter.next();
        Point p1 = traj1Lter.next();
        while (true) {
            //System.out.println("p0: " + p0 + " p1: " + p1);
            if (p0.isValid() && p1.isValid()) {
                n++;
                dist += p0.toDistance(p1);
                if (!traj1Lter.hasNext() || !traj0Lter.hasNext()) {
                    break;
                }
                p1 = traj1Lter.next();
                p0 = traj0Lter.next();
            } else if (p0.isValid()) {
                if (!traj1Lter.hasNext() || !traj0Lter.hasNext()) {
                    break;
                }
                p1 = traj1Lter.next();
            } else if (p1.isValid()) {
                if (!traj1Lter.hasNext() || !traj0Lter.hasNext()) {
                    break;
                }
                p0 = traj0Lter.next();
            } else {
                if (!traj1Lter.hasNext() || !traj0Lter.hasNext()) {
                    break;
                }
                p1 = traj1Lter.next();
                p0 = traj0Lter.next();
            }
        }

        if (n == 0) {
            return 0;
        }
        return dist / n;
    }

    public double toAvgDistanceTime(Trajectory other) {
        List<Point> traj0 = points;
        List<Point> traj1 = other.points;
        if (traj0.isEmpty() || traj1.isEmpty()) {
            return -1;  // no overlapping time
        }

        int timeThreshold = 10; // in mins
        double dist = 0;
        int n = 0;
        ListIterator<Point> traj0Lter = traj0.listIterator();
        ListIterator<Point> traj1Lter = traj1.listIterator();

        Point p0 = traj0Lter.next();
        Point p1 = traj1Lter.next();
        while (true) {
            Minutes minsDiff = Minutes.minutesBetween(p0.getTime(), p1.getTime());
            long absMinsDiff = Math.abs(minsDiff.getMinutes());
            if (absMinsDiff < timeThreshold) {
                n++;
                dist += p0.toDistance(p1);
                if (!traj0Lter.hasNext()) {
                    break;
                } else {
                    p0 = traj0Lter.next();
                }
                if (!traj1Lter.hasNext()) {
                    break;
                } else {
                    p1 = traj1Lter.next();
                }
            } else if (p0.getTime().getMillis() < p1.getTime().getMillis()) {
                if (!traj0Lter.hasNext()) {
                    break;
                } else {
                    p0 = traj0Lter.next();
                }
            } else {
                if (!traj1Lter.hasNext()) {
                    break;
                } else {
                    p1 = traj1Lter.next();
                }
            }
        }

        if (n == 0) {
            return 0;
        }

        System.out.println(this.id + ", " + other.id + " " + n);
        return dist / n;
    }

    public void updateBoxRange(Point p) {
        if (p.valid) {
            if (p.getX() < boxRange.getMinX()) {
                boxRange.setMinX(p.getX());
            }
            if (p.getX() > boxRange.getMaxX()) {
                boxRange.setMaxX(p.getX());
            }

            if (p.getY() < boxRange.getMinY()) {
                boxRange.setMinY(p.getY());
            }

            if (p.getY() > boxRange.getMaxY()) {
                boxRange.setMaxY(p.getY());
            }
        }
    }
}