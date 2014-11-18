package uiuc.dm.moveMine.functions.domain;

public class JsonOutput {

    private String displayMethod;
    private ForceGraph graph;
    private String csvPath;

    public String getCsvPath() {
        return csvPath;
    }

    public void setCsvPath(String csvPath) {
        this.csvPath = csvPath;
    }

    public String getDisplayMethod() {
        return displayMethod;
    }

    public void setDisplayMethod(String displayMethod) {
        this.displayMethod = displayMethod;
    }

    public ForceGraph getGraph() {
        return graph;
    }

    public void setGraph(ForceGraph graph) {
        this.graph = graph;
    }
}
