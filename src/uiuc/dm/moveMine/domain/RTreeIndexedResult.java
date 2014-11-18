/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uiuc.dm.moveMine.domain;

import java.util.List;

public class RTreeIndexedResult {

    private List<Integer> numRelatedPoints;
    private int totalNumRelatedPoints;

    public RTreeIndexedResult(List<Integer> numRelatedPoints, int totalNumRelatedPoints) {
        this.numRelatedPoints = numRelatedPoints;
        this.totalNumRelatedPoints = totalNumRelatedPoints;
    }

    public List<Integer> getNumRelatedPoints() {
        return numRelatedPoints;
    }

    public void setNumRelatedPoints(List<Integer> numRelatedPoints) {
        this.numRelatedPoints = numRelatedPoints;
    }

    public int getTotalNumRelatedPoints() {
        return totalNumRelatedPoints;
    }

    public void setTotalNumRelatedPoints(int totalNumRelatedPoints) {
        this.totalNumRelatedPoints = totalNumRelatedPoints;
    }
}
