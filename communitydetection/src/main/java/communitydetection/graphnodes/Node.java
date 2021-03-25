package communitydetection.graphnodes;

import java.util.ArrayList;

/**
 * Node
 */
public interface Node {
    public int getCommunity();
    public void setCommunity(int community);
    public ArrayList<Node> getNodes();
    public double getTotalWeightedDegree();
    public void setTotalWeightedDegree(double weightedDegree);
    public double getX();
    public double getY();
    public int getSize();
    public GraficNode getGrafical();
    public void setGrafical(GraficNode graficNode);

}
/*
public abstract class Node{

    //Campi
    protected int community;
    protected double weightedDegree;

    //Costruttori 
    public Node() {
        this.community = -1;
    }

    public Node(int community) {
        this.community = community;
    }

    //get e set
    public int getComm() {
        return community;
    }
    public void setComm(int comm){
        this.community = comm; 
    }
    public double getWeightedDegree() {
        return weightedDegree;
    }
    public void setWeightedDegree(double weightedDegree) {
        this.weightedDegree = weightedDegree;
    }
}

*/