package uiuc.dm.miningTools.ui.domain;

import java.util.Date;

public class MiningFunctionParameters {

    // general
    private String displayMethod;
    private String selectedDatasetName;
    private boolean needInterpolation;
    private String selectedFunction;
    private Date startTime;
    private Date endTime;
    private double aveSampling;
    // for sigLevel
    private String gap;
    private String thresGap;
    private String numRound;
    private String boundingDistance;
    private String distThres;
    
    
    /*Fei*/
    private String lMax;
    private String dMax;
    private String minL;
    
    public double getSampling(){
        return this.aveSampling;
    }
    
    public String getMinL(){
        return this.minL;
    }
    public String getlMax(){
        return this.lMax;
    }
    public String getdMax(){
        return this.dMax;        
    }
    public void setSampling(double sam){
        this.aveSampling = sam;
    }
    public void setlMax(String lMax){
        this.lMax = lMax;
    }
    public void setdMax(String dMax){
        this.dMax = dMax;
    }
    public void setMinL(String ml){
        this.minL = ml;
    }
    /**/

    public String getDisplayMethod() {
        return displayMethod;
    }

    public String getSelectedDatasetName() {
        return selectedDatasetName;
    }

    public String getSelectedFunction() {
        return selectedFunction;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isNeedInterpolation() {
        return needInterpolation;
    }

    public String getGap() {
        return gap;
    }

    public String getThresGap() {
        return thresGap;
    }

    public String getNumRound() {
        return numRound;
    }

    public String getBoundingDistance() {
        return boundingDistance;
    }

    public String getDistThres() {
        return distThres;
    }

    public void setDistThres(String distThres) {
        this.distThres = distThres;
    }

    public void setDisplayMethod(String displayMethod) {
        this.displayMethod = displayMethod;
    }

    public void setSelectedDatasetName(String selectedDatasetName) {
        this.selectedDatasetName = selectedDatasetName;
    }

    public void setNeedInterpolation(boolean needInterpolation) {
        this.needInterpolation = needInterpolation;
    }

    public void setSelectedFunction(String selectedFunction) {
        this.selectedFunction = selectedFunction;
    }

    public void setGap(String gap) {
        this.gap = gap;
    }

    public void setThresGap(String thresGap) {
        this.thresGap = thresGap;
    }

    public void setNumRound(String numRound) {
        this.numRound = numRound;
    }

    public void setBoundingDistance(String boundingDistance) {
        this.boundingDistance = boundingDistance;
    }
}
