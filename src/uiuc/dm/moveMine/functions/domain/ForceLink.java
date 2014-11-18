package uiuc.dm.moveMine.functions.domain;

public class ForceLink {
    private int source;
    private int target;
    private double value;
    private String label;

    public ForceLink(int source, int target, double value) {
        this.source = source;
        this.target = target;
        this.value = value;
    }

    public ForceLink(int source, int target, double value, String label) {
        this.source = source;
        this.target = target;
        this.value = value;
        this.label = label;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
