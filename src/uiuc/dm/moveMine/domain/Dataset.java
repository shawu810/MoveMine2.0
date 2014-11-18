package uiuc.dm.moveMine.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

public class Dataset {

    private Hashtable<String, Trajectory> trajsMap;
    private List<Trajectory> rawTraj;
    private String datasetName;
    private Date startTime;
    private Date endTime;
    private double avgSampleRate;
    private BoxRange boxRangeOverAllTrajs;
    private double varSampleRate;
    
    public Hashtable<String, Trajectory> getTrajsMap() {
        return trajsMap;
    }

    public void setTrajsMap(Hashtable<String, Trajectory> trajsMap) {
        this.trajsMap = trajsMap;
    }

    public List<Trajectory> getRawTraj() {
        return rawTraj;
    }

    public void storeData(List<Trajectory> rawData, Hashtable<String, Trajectory> trajsMap) {
        this.trajsMap = trajsMap;
        this.rawTraj = rawData;
        computeTimeSpan(rawData);
        computeAvgSamplingRate(rawData);
        System.out.println("start time: " + startTime);
        System.out.println("end time: " + endTime);
        System.out.println("avgSampleRate: " + avgSampleRate);
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public long getNumTrajectory() {
        return rawTraj.size();
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public double getSampleRate() {
        return avgSampleRate;
    }
    
    public double getSampleVariance(){
        return this.varSampleRate;
    }
    public BoxRange getBoxRangeOverAllTrajs() {
        return boxRangeOverAllTrajs;
    }

    public void setBoxRangeOverAllTrajs(BoxRange boxRangeOverAllTrajs) {
        this.boxRangeOverAllTrajs = boxRangeOverAllTrajs;
    }

    private void computeTimeSpan(List<Trajectory> trajs) {
        if (trajs.size() > 0) {
            startTime = trajs.get(0).getPoint(0).getTime().toDate();
            int size = trajs.get(0).getPointsNum();
            endTime = trajs.get(0).getPoint(size - 1).getTime().toDate();
            for (int i = 1; i < trajs.size(); i++) {
                List<Point> points = trajs.get(i).getPoints();
                if (points.get(0).getTime().toDate().compareTo(startTime) < 0) {
                    startTime = points.get(0).getTime().toDate();
                }
                if (points.get(points.size() - 1).getTime().toDate().compareTo(endTime) > 0) {
                    endTime = points.get(points.size() - 1).getTime().toDate();
                }
            }
        } else {
            throw new IllegalArgumentException("trajs.size == 0");
        }
    }

    private void computeAvgSamplingRate(List<Trajectory> trajs) {
        if (trajs.size() > 0) {
            long n = trajs.size();
            for (int i = 0; i < n; i++) {
                int m = trajs.get(i).getPointsNum();
                int[] minDiffs = new int[m-1];
                
                for (int j = 0; j < m - 1; j++) {
                    int minDiff = Math.abs(Seconds.secondsBetween(trajs.get(i).getPoint(j + 1).getTime(), trajs.get(i).getPoint(j).getTime()).getSeconds());
//                    long diff = trajs.get(i).getPoint(j + 1).getTime().toDate().getTime() - trajs.get(i).getPoint(j).getTime().toDate().getTime();
                    if(minDiff > 0){
                        minDiffs[j] = minDiff;
                    }                    
                }
                
                Arrays.sort(minDiffs);
                if(minDiffs.length % 2 == 0){
                    avgSampleRate += (minDiffs[minDiffs.length/2 - 1] + minDiffs[minDiffs.length/2])/2.0;
                }else{
                    avgSampleRate += minDiffs[minDiffs.length/2];
                }
                double var = 0;
                for (int j = 0; j < m-1 ; j++){
                   var += (minDiffs[j]-avgSampleRate)*(minDiffs[j]-avgSampleRate);
                }
                this.varSampleRate += var/(m-1);

                
//                System.out.println(avgSampleRate);
            }
            this.varSampleRate = Math.sqrt(this.varSampleRate/n);
            avgSampleRate /= n;
        } else {
            throw new IllegalArgumentException("trajs.size == 0");
        }
    }
}
