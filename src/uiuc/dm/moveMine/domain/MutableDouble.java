/*
 * 

 Implemented by Tobias Kin Hou Lei, 
 Data Mining Research Group
 Department of Computer Science, University of Illinois at Urbana-Champaign
 201 N. Goodwin, Urbana, IL, 61801
 Office: 1117 Siebel Center
 E-mail: klei2 {at} illinois {d0t} edu
 http://web.engr.illinois.edu/~klei2/

 */
package uiuc.dm.moveMine.domain;

/**
 *
 * @author klei2
 */
public class MutableDouble {

    private Double portion;
    private int validN;

    public int getValidN() {
        return validN;
    }

    public void setValidN(int validN) {
        this.validN = validN;
    }

    public Double getValue() {
        return portion;
    }

    public void setValue(Double value) {
        this.portion = value;
    }

    @Override
    public String toString() {
        return portion != null ? portion.toString() : "null";
    }
}
