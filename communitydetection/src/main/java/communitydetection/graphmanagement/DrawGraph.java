package communitydetection.graphmanagement;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import communitydetection.graphnodes.GraficNode;
import communitydetection.graphnodes.Node;

public class DrawGraph {

    private int xSize;
    private int ySize;
    private double x_min = Double.MAX_VALUE;
    private double y_min = Double.MAX_VALUE;
    private double proportionX;
    private double proportionY;
    private Color[] color;
    private int vertexSize;


    public void setxSize(int xSize) {
        this.xSize = xSize;
    }
    public void setySize(int ySize) {
        this.ySize = ySize;
    }
    public int getxSize() {
        return xSize;
    }
    public int getySize() {
        return ySize;
    }

    public DrawGraph(int xSize, int ySize, int vertexSize) {
        color = initializeColor();
        this.xSize = xSize;
        this.ySize = ySize;
        this.vertexSize = vertexSize;
    }
    public DrawGraph(){

    }

    public DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> makeDrawable(DefaultUndirectedWeightedGraph<? extends Node, DefaultWeightedEdge> net) {
        Node[] oldNodes = net.vertexSet().toArray(new Node[0]);
        DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> drawNet = new DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        Hashtable<Node, GraficNode> newNodes = new Hashtable<Node, GraficNode>(oldNodes.length);
        for (int i = 0; i < oldNodes.length; i++) {
            GraficNode newNode = new GraficNode(oldNodes[i]);
            newNodes.put(oldNodes[i], newNode);
            drawNet.addVertex(newNode);
        }
        DefaultWeightedEdge[] oldEdges = net.edgeSet().toArray(new DefaultWeightedEdge[0]);
        for (int i = 0; i < oldEdges.length; i++) {
            Node s = net.getEdgeSource(oldEdges[i]);
            Node t = net.getEdgeTarget(oldEdges[i]);
            drawNet.addEdge(newNodes.get(s), newNodes.get(t));
        }
        
        return drawNet;
    }

    public BufferedImage draw(DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> net){
        
        x_min = Double.MAX_VALUE;
        y_min = Double.MAX_VALUE;
        int[][] coordinates = getCoordinates(net);
        BufferedImage img = new BufferedImage(xSize, ySize, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics2d = img.createGraphics();
        graphics2d.setColor(Color.white);
        graphics2d.fillRect(0, 0, xSize, ySize);
        drawEdges(net, graphics2d);
        drawVertex(coordinates, graphics2d);
        return img;
        
    }

    private void drawVertex(int[][] coordinates, Graphics2D graphics2d){
        for (int i = 0; i < coordinates.length; i++) {
            int x = coordinates[i][0];
            int y = coordinates[i][1];
            graphics2d.setColor(color[coordinates[i][2] * 157 % color.length]);
            graphics2d.fillRoundRect(x, y, vertexSize, vertexSize, vertexSize, vertexSize);
        }
    }

    private void drawEdges(DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> net, Graphics2D graphics2d) {
        DefaultWeightedEdge[] edges = net.edgeSet().toArray(new DefaultWeightedEdge[0]);
        graphics2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < edges.length; i++) {
            GraficNode source = net.getEdgeSource(edges[i]);
            GraficNode dest = net.getEdgeTarget(edges[i]);
            int x1 = (int)Math.round(proportionX*(source.getX()-x_min))+10 +vertexSize/2;
            int y1 = (int)Math.round(proportionY*(source.getY()-y_min))+10 +vertexSize/2;
            int x2 = (int)Math.round(proportionX*(dest.getX()-x_min))+10 +vertexSize/2;
            int y2 = (int)Math.round(proportionY*(dest.getY()-y_min))+10 +vertexSize/2;
            graphics2d.drawLine(x1, y1, x2, y2);
        }
    }

    private int[][] getCoordinates(DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> net) {
        GraficNode[] points = net.vertexSet().toArray(new GraficNode[0]);
        double x_max = Double.MIN_VALUE;
        double y_max = Double.MIN_VALUE;
        for (int i = 0; i < points.length; i++) {
            double x = points[i].getX();
            double y = points[i].getY();
            if (x > x_max) x_max = x;
            if (x < x_min) x_min = x;
            if (y > y_max) y_max = y;
            if (y < y_min) y_min = y;
        }
        this.proportionX = (xSize-20)/(x_max - x_min);
        this.proportionY = (ySize-20)/(y_max - y_min);
        int[][] coordinates = new int[points.length][3];
        for (int i = 0; i < points.length; i++) {
            double x = points[i].getX();
            double y = points[i].getY();
            coordinates[i][0] = (int)Math.round(proportionX*(x-x_min))+10;
            coordinates[i][1] = (int)Math.round(proportionY*(y-y_min))+10;
            coordinates[i][2] = points[i].getNode().getCommunity();
        }
        return coordinates;
    }

    private Color[] initializeColor(){
        Color[] colori = new Color[125];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (int j2 = 0; j2 < 5; j2++) {
                    colori[i * 25 + j * 5 + j2] = new Color(51 * i + 51, 51 * j + 51, 51 * j2 + 51);
                }
            }
        }
        //I colori i cui indici sono multipli di 31 sono grigi, li rimuovo
        Color[] temp = new Color[120];
        int i2=0;
        for (int j = 0; j < colori.length; j++) {
            if(j%31!=0){
                temp[j-i2]=colori[j];
            }
            else{
                i2++;
            }
        }
        return temp;
    }
}
