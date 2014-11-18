package uiuc.dm.moveMine.domain;

public class SignificanceLevelResult {

    private MovingRelation relationship;
    private double significanceLevel;
    private int round;
    private int numAvoidance;
    private int numAttraction;
    private int numIndependence;
    private static final double sigLevelBase = 0.95;

    public SignificanceLevelResult(int round, int numAvoidane,
            int numAttraction, int numIndependence) {
        significanceLevel = (numAttraction + numIndependence / 2.0) / (double) round;
        if (significanceLevel >= 0.95) {
            relationship = MovingRelation.ATTRACTION;
        } else if (significanceLevel <= 0.05) {
            if (significanceLevel == 0) {
                significanceLevel = 0.001;
            }
            relationship = MovingRelation.AVOIDANCE;
        } else {
            relationship = MovingRelation.INDEPENDENCE;
        }

    }

    public double getSignificanceLevel() {
        return significanceLevel;
    }

    public MovingRelation getRelationship() {
        return relationship;
    }

    public int getRound() {
        return round;
    }

    public int getNumAvoidance() {
        return numAvoidance;
    }

    public int getNumAttraction() {
        return numAttraction;
    }

    public int getNumIndependence() {
        return numIndependence;
    }

    public void setSignificanceLevel(double significanceLevel) {
        this.significanceLevel = significanceLevel;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("relationship: ").append(relationship.toString())
                .append(" round: ").append(round).append(" numAvoidance: ")
                .append(numAvoidance).append(" numAttraction: ")
                .append(numAttraction).append(" numIndependence: ")
                .append(numIndependence);
        return builder.toString();
    }
}
