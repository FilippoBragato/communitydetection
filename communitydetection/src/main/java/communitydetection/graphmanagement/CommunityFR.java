package communitydetection.graphmanagement;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import communitydetection.graphnodes.Community;
import communitydetection.graphnodes.GraficNode;
import communitydetection.graphnodes.Node;

public class CommunityFR{

    private DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> net;
    private DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> communities;

    public CommunityFR(DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge> net, DefaultUndirectedWeightedGraph<Community<Node>, DefaultWeightedEdge> communities){
        DrawGraph dr = new DrawGraph();
        DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> drComm = dr.makeDrawable(communities);
        DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> drGraph = dr.makeDrawable(net);
        this.net = drGraph;
        this.communities = drComm;
    }

    public DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> apply () {
        FruchetermanReingold fr = new FruchetermanReingold(200.0, 0.99, 0.1,0.0,0.0);
        fr.apply(communities);
        Set<GraficNode> commSet = communities.vertexSet();
        for (GraficNode c : commSet) {
            HashSet<Node> nodeSet = new HashSet<Node>(c.getNode().getNodes());
            HashSet<GraficNode> graficNodeSet = new HashSet<GraficNode>();
            for (Node n : nodeSet) {
                graficNodeSet.add(n.getGrafical());
            }
            AbstractGraph<GraficNode, DefaultWeightedEdge> subG = new AsSubgraph<GraficNode, DefaultWeightedEdge>(net, graficNodeSet);
            fr = new FruchetermanReingold(100.0, 0.99, 0.1,c.getX(),c.getY());
            fr.apply(subG);
        }
        return net;
    }
}
