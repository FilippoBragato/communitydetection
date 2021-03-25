package communitydetection.graphmanagement;

import java.util.Random;
import java.util.function.Function;

import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import communitydetection.graphnodes.GraficNode;

public class FruchetermanReingold implements Function<AbstractGraph<GraficNode, DefaultWeightedEdge>, DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge>>{

    private double k;
    private double coolingRate;
    private double tol;
    private double centerX;
    private double centerY;
 
    public FruchetermanReingold(double k, double coolingRate, double tol, double centerX,double centerY){
        this.k = k;
        this.coolingRate = coolingRate;
        this.tol = tol;
        this.centerX = centerX;
        this.centerY = centerY;
    }
    public double getCoolingRate() {
        return coolingRate;
    }
    public double getK() {
        return k;
    }
    public void setCoolingRate(double coolingRate) {
        this.coolingRate = coolingRate;
    }
    public void setK(double k) {
        this.k = k;
    }
    private double fAttractive(double distance){
        return distance * distance /k;
    }
    private double fRepulsive(double distance, int weight){
        return - k * k * weight / distance;
    }
    private GraficNode[] findNeighbors(AbstractGraph<GraficNode, DefaultWeightedEdge> net, GraficNode node) {
        DefaultWeightedEdge[] edgesOnNode = net.edgesOf(node).toArray(new DefaultWeightedEdge[0]);
        GraficNode[] neighbors = new GraficNode[edgesOnNode.length];
        for (int i = 0; i < edgesOnNode.length; i++) {
            if(net.getEdgeSource(edgesOnNode[i])!=node){
                neighbors[i] = net.getEdgeSource(edgesOnNode[i]);
            }
            else{
                neighbors[i] = net.getEdgeTarget(edgesOnNode[i]);
            }
        }
        return neighbors;
    }
    private void reset(AbstractGraph<GraficNode, DefaultWeightedEdge> net){
        GraficNode[] nodes = net.vertexSet().toArray(new GraficNode[0]);
        for (GraficNode node : nodes) {
            node.setX(centerX);
            node.setY(centerY);
        }
    }

    @Override
    public DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> apply(
            AbstractGraph<GraficNode, DefaultWeightedEdge> net) {
                reset(net);
                Random rng = new Random();
                double t = k;
                boolean converged = false;
                GraficNode nodes[] = net.vertexSet().toArray(new GraficNode[0]);
                while(!converged){
                    for (GraficNode v : nodes) {
                        //analizzo le forze su v
                        double disp[] = {0,0};
                        for (GraficNode u : nodes) {
                            if (u!=v) {
                                double[] difference = {(u.getX()- v.getX()),(u.getY()- v.getY())};
                                double distance = Math.sqrt(difference[0]*difference[0]+difference[1]*difference[1]); 
                                double f = fRepulsive(distance, u.getSize());
                                if (!Double.isFinite(f)){
                                    f = Math.signum(f)*t;
                                }
                                double[] versor ={difference[0]/distance,difference[1]/distance};
                                if (Double.isNaN(versor[0]) || Double.isNaN(versor[1])){
                                    double ang = rng.nextDouble()*2*Math.PI;
                                    versor[0] = Math.cos(ang);
                                    versor[1] = Math.sin(ang);
                                }
                                disp[0] = disp[0] + versor[0]*f;
                                disp[1] = disp[1] + versor[1]*f; 
                            }
                        }
                        GraficNode[] neighbors = findNeighbors(net, v);
                        for (GraficNode u : neighbors) {
                            double[] difference = {(u.getX()- v.getX()),(u.getY()- v.getY())};
                            double distance = Math.sqrt(difference[0]*difference[0]+difference[1]*difference[1]);
                            double[] versor ={difference[0]/distance,difference[1]/distance};
                            double f = fAttractive(distance);
                            if (Double.isNaN(versor[0]) || Double.isNaN(versor[1])){
                                double ang = rng.nextDouble()*2*Math.PI;
                                versor[0] = Math.cos(ang);
                                versor[1] = Math.sin(ang);
                            }
                            disp[0] = disp[0] + versor[0]*f;
                            disp[1] = disp[1] + versor[1]*f; 
                        }
                        //sposto v
                        double dispLength = Math.sqrt(disp[0]*disp[0]+disp[1]*disp[1]);
                        double[] dispVersor = {disp[0]/dispLength,disp[1]/dispLength};
                        if (t< dispLength) dispLength = t;
                        double x = v.getX() + dispLength* dispVersor[0];
                        double y = v.getY() + dispLength* dispVersor[1];
                        v.setX(x);
                        v.setY(y);
                        if(t<tol*k) {
                            converged = true;
                        }
                    }
                    t = t*coolingRate; //DA SISTEMARE
                }
        
        return null;//(DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge>)net;
    }
    
    
}
