package uiuc.dm.moveMine.functions.domain;

import java.util.ArrayList;
import java.util.List;

public class ForceGraph {
    private List<ForceNode> nodes;
    private List<ForceLink> links;

    public ForceGraph() {
        nodes = new ArrayList<ForceNode>();
        links = new ArrayList<ForceLink>();
    }

    public void addNode(ForceNode node) {
        nodes.add(node);
    }

    public void addLink(ForceLink link) {
        links.add(link);
    }
}
