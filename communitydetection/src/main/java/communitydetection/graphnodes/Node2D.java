package communitydetection.graphnodes;

import java.util.ArrayList;
import java.util.Arrays;

public class Node2D implements Node{
    private double x;
    private double y;
    private int community;
    private double totalWeightedDegree;
    private GraficNode grafical = null;

    @Override
    public double getTotalWeightedDegree() {
        return totalWeightedDegree;
    }
    
    public void setTotalWeightedDegree(double totalWeightedDegree) {
        this.totalWeightedDegree = totalWeightedDegree;
    }

    @Override
    public int getCommunity() {
        return community;
    }
    @Override
    public void setCommunity(int community) {
        this.community = community;
    }
    @Override
    public ArrayList<Node> getNodes() {
        return new ArrayList<Node>(Arrays.asList(this));
    }
    
    public Node2D(double x, double y, int community){
        this.x = x;
        this.y = y;
        this.community = community;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public int getSize() {
        return 1;
    }
    @Override
    public void setGrafical(GraficNode graficNode) {
        this.grafical = graficNode;
    }
    @Override
    public GraficNode getGrafical() {
        return this.grafical;
    }

}
