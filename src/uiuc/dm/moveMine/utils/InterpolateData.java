package uiuc.dm.moveMine.utils;

import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.Period;
import uiuc.dm.moveMine.domain.Point;
import uiuc.dm.moveMine.domain.Trajectory;

/**
 * Data interpolation is a must step to pre-process the data. The interpolation
 * is performed on each trajectory in a dataset. All the trajectories will be
 * interpolated using the same start time, the same time gap, the same max time
 * gap constraint. The same start time is the earliest recording time of all the
 * trajectories in the dataset. The time gap is the gap used to linearly
 * interpolate the missing data. The max time gap is to control the maximal time
 * gap allowed for missing data to be interpolated. For example, if max time gap
 * is 1 hour, it means that if two consecutive recorded points in a trajectory
 * has time gap larger than 1 hour, the points between will not be interpolated.
 */
public class InterpolateData {

    public static void interpolation2(
            List<Trajectory> trajs,
            Period gap,
            Period thresGap,
            Date pointStartTime,
            Date pointEndTime) {
        
        DateTime startTime = new DateTime(pointStartTime);
        DateTime endTime = new DateTime(pointEndTime);

        // interpolate each trajectory in the dataset
        for (Trajectory traj : trajs) {
            List<Point> points = traj.getPoints();
            // start from the earliest time
            DateTime timePointer = startTime;
            int i = 0;
            while (timePointer.isBefore(endTime)) {
                Point p = new Point();
                p.setTime(timePointer);
                p.valid = false;
                if (!(i == 0 && (points.get(0).getTime().isAfter(timePointer)))
                        && (i < points.size() - 1)) {
                    while (i < points.size() - 1) {
                        Point p0 = points.get(i);
                        Point p1 = points.get(i + 1);

                        DateTime t0 = p0.getTime();
                        DateTime t1 = p1.getTime();

                        // if the timePointer is the in the middle of two consecutive points t0 and t1
                        if ((t0.compareTo(timePointer) <= 0) && (timePointer.compareTo(t1) <= 0)) {
                            // (t1.Subtract(t0)).CompareTo(thresGap) > 0
                            if (t1.minus(thresGap).compareTo(t0) > 0) {
                                p.valid = false;
                            } else {
                                p.valid = true;
                                double x0 = p0.getX();
                                double y0 = p0.getY();
                                double x1 = p1.getX();
                                double y1 = p1.getY();
                                long ts0 = timePointer.toDate().getTime() - t0.toDate().getTime();
                                long ts1 = t1.toDate().getTime() - t0.toDate().getTime();
                                double x = x0 + (x1 - x0) * ts0 / (double) ts1;
                                double y = y0 + (y1 - y0) * ts0 / (double) ts1;
//                                if(Double.isNaN(x) || Double.isNaN(y)){
                                if (ts1 == 0) {
                                    p.valid = false;
                                }
                                p.setX(x);
                                p.setY(y);
                            }
                            break;
                        }
                        i++;
                    }
                }

                // add a new interpolated point
                traj.addOneFilledPoint(p);
                traj.updateBoxRange(p);
                
                // add the time pointer with the time gap
                timePointer = timePointer.plus(gap);
            }
            System.out.println("length: "+traj.getFilledPoints().size());
            //System.out.println(dataPointer);
        }
    }
    public static void copy(List<Trajectory> trajs) {

        // interpolate each trajectory in the dataset
        for (Trajectory traj : trajs) {
            List<Point> points = traj.getPoints();
            // start from the earliest time
            for(Point p : points){
                //p.valid = true;
                traj.addOneFilledPoint(p);
                traj.updateBoxRange(p);
            }
    
           System.out.println("length: "+traj.getFilledPoints().size());
        }
    }
    
    public static void getTimeVariance(List<Trajectory> trajs){
    }

    public static void interpolation(List<Trajectory> trajs, Period gap, Period thresGap, Date pointStartTime, Date pointEndTime) {
        DateTime startTime = new DateTime(pointStartTime);
        DateTime endTime = new DateTime(pointEndTime);

        // interpolate each trajectory in the dataset
        for (Trajectory traj : trajs) {
            List<Point> points = traj.getPoints();
            // start from the earliest time
            DateTime timePointer = startTime;
            int i = 0;
            while (timePointer.compareTo(endTime) < 0) {
                Point p = new Point();
                p.setTime(timePointer);
                p.valid = false;
                
                if ((i < points.size() - 1) && !(i == 0 && (points.get(0).getTime().compareTo(timePointer) > 0))
                        ) {
                    while (i < points.size() - 1) {
                        Point p0 = points.get(i);
                        Point p1 = points.get(i + 1);

                        DateTime t0 = p0.getTime();
                        DateTime t1 = p1.getTime();

                        // if the timePointer is the in the middle of two consecutive points t0 and t1
                        if ((t0.compareTo(timePointer) <= 0) && (timePointer.compareTo(t1) <= 0)) {
                            // (t1.Subtract(t0)).CompareTo(thresGap) > 0
                            if (t1.minus(thresGap).compareTo(t0) > 0) {
                                p.valid = false;
                            } else {
                                p.valid = true;
                                double x0 = p0.getX();
                                double y0 = p0.getY();
                                double x1 = p1.getX();
                                double y1 = p1.getY();
                                long ts0 = timePointer.toDate().getTime() - t0.toDate().getTime();
                                long ts1 = t1.toDate().getTime() - t0.toDate().getTime();
                                double x = x0 + (x1 - x0) * ts0 / (double) ts1;
                                double y = y0 + (y1 - y0) * ts0 / (double) ts1;
//                                if(Double.isNaN(x) || Double.isNaN(y)){
                                if (ts1 == 0) {
                                    p.valid = false;
                                }
                                p.setX(x);
                                p.setY(y);
                            }
                            break;
                        }
                        i++;
                    }
                }

                // add a new interpolated point
                traj.addOneFilledPoint(p);
                traj.updateBoxRange(p);

                // add the time pointer with the time gap
                timePointer = timePointer.plus(gap);
            }
             //System.out.println("length: "+traj.getFilledPoints().size());
            //System.out.println(dataPointer);
        }
    }
}
