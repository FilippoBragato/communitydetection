package communitydetection.graphmanagement;

import java.util.Random;
import communitydetection.graphnodes.*;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class TestGraphCreator {
    private int l;
    private int g;
    private double p_in;
    private double p_out;

    private double x_max;
    private double y_max;

    public TestGraphCreator(){
        this.l=4;
        this.g=32;
        this.p_in=0.25;
        this.p_out=1.0/12.0;
        this.x_max=1000;
        this.y_max=1000;
    }
    public TestGraphCreator(int l, int g){
        this.l=l;
        this.g=g;
        this.p_in=0.25;
        this.p_out=1.0/12.0;
        this.x_max=1000;
        this.y_max=1000;
    }
    public TestGraphCreator(int l, int g, double z_in, double z_out){
        this.l=l;
        this.g=g;
        this.p_in=z_in/(g-1);
        this.p_out=z_out/(g*(l-1));
        this.x_max=1000;
        this.y_max=1000;
    }

    public int getG() {
        return g;
    }
    public int getL() {
        return l;
    }
    public double getP_in() {
        return p_in;
    }
    public double getP_out() {
        return p_out;
    }
    public double getX_max() {
        return x_max;
    }
    public double getY_max() {
        return y_max;
    }
    public void setG(int g) {
        this.g = g;
    }
    public void setL(int l) {
        this.l = l;
    }
    public void setP_in(double p_in) {
        this.p_in = p_in;
    }
    public void setP_out(double p_out) {
        this.p_out = p_out;
    }
    public void setX_max(double x_max) {
        this.x_max = x_max;
    }
    public void setY_max(double y_max) {
        this.y_max = y_max;
    }

    public DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge> lpartition(){
        DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge> net = createNodes();
        Random rng = new Random();
        Node[] nodes = net.vertexSet().toArray(new Node[0]);
        for (int i = 0; i < nodes.length; i++) {
            for (int j = i+1; j < nodes.length; j++) {
                if(nodes[i].getCommunity()==nodes[j].getCommunity()){
                    if(rng.nextDouble()<p_in){
                        net.addEdge(nodes[i], nodes[j]);
                    }
                }
                else{
                    if(rng.nextDouble()<p_out){
                        net.addEdge(nodes[i], nodes[j]);
                    }
                }
            }
        }
        return net;
    }

    private DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge> createNodes(){

        DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge> net = new DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        int nOfRow = (int)Math.ceil(Math.sqrt(l));
        double halfRow = x_max/(2*nOfRow);
        double sigma = Math.sqrt(x_max/nOfRow)*3;
        int nInLastColumns = l%nOfRow;
        int community = 0;
        for (int i = 0; i < l/nOfRow; i++) {
            for (int j = 0; j < nOfRow; j++) {
                double centerX = (2*i+1)*halfRow;
                double centerY = (2*j+1)*halfRow;
                addCommunity(net, centerX, centerY, sigma, community++);
            }
        }
        double centerX = (2*l/nOfRow)*halfRow;
        for (int i = 0; i < nInLastColumns; i++) {
            double leftSpace = (y_max-(y_max/nOfRow*nInLastColumns))/2;
            double centerY = leftSpace + y_max/(2*nOfRow)*(2*i+1);
            addCommunity(net, centerX, centerY, sigma, community++);
        }
        return net;
    }
    
    private void addCommunity(DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge> net, double centerX, double centerY, double sigma, int community){
        Random rng = new Random();
        for (int i = 0; i < g; i++) {
            double x = rng.nextGaussian()*sigma + centerX;
            double y = rng.nextGaussian()*sigma + centerY;

            Node2D node = new Node2D(x, y, community);
            net.addVertex(node);
        }
    }
}
