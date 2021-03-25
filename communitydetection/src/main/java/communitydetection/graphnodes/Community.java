package communitydetection.graphnodes;

import java.util.ArrayList;
import java.util.Arrays;

public class Community<T extends Node> implements Node {
    private ArrayList<Node> nodesInCommunity;
    private int community;
    private double totalWeightedDegree;
    private GraficNode grafical = null;
    
    public Community(ArrayList<Node> nodesInCommunity, int community){
        this.nodesInCommunity = nodesInCommunity;
        this.community = community;
    }

    public Community(T node){
        nodesInCommunity = new ArrayList<Node>(Arrays.asList(node));
    }
    public Community(T node, int community){
        nodesInCommunity = new ArrayList<Node>(Arrays.asList(node));
        this.community = community;
        this.totalWeightedDegree = node.getTotalWeightedDegree();
    }
    public Community(int community){
        this.community = community;
    }
    @Override
    public int getCommunity() {
        return community;
    }
    @Override
    public void setCommunity(int community) {
        this.community = community;
    }
    public ArrayList<Node> getNodesInCommunity() {
        return nodesInCommunity;
    }
    public void setNodesInCommunity(ArrayList<Node> nodesInCommunity) {
        this.nodesInCommunity = nodesInCommunity;
    }
    public double getTotalWeightedDegree() {
        return totalWeightedDegree;
    }
    public void setTotalWeightedDegree(double weightedDegree) {
        this.totalWeightedDegree = weightedDegree;
    }
    public void addNode(Node node){
        this.nodesInCommunity.addAll(node.getNodes());
    }
    @Override
    public ArrayList<Node> getNodes(){
        ArrayList<Node> nodes = new ArrayList<>();
        for (Node node : nodesInCommunity) {
            nodes.addAll(node.getNodes());
        }
        return nodes;
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public int getSize() {
        return nodesInCommunity.size();
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