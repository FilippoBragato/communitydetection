package communitydetection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.imageio.ImageIO;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import communitydetection.algorithm.Louvain;
import communitydetection.graphmanagement.CommunityFR;
import communitydetection.graphmanagement.DrawGraph;
import communitydetection.graphmanagement.FruchetermanReingold;
import communitydetection.graphmanagement.TestGraphCreator;
import communitydetection.graphnodes.Community;
import communitydetection.graphnodes.GraficNode;
import communitydetection.graphnodes.Node;

public class App {
    public static void main(String[] args) {
        TestGraphCreator creator = new TestGraphCreator(3,50,30,15);
        DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge> net = creator.lpartition();
        DrawGraph drawer = new DrawGraph(500,500, 10);
        DefaultUndirectedWeightedGraph<GraficNode, DefaultWeightedEdge> drawableNet = drawer.makeDrawable(net);
        BufferedImage img = drawer.draw(drawableNet);
        try {
            ImageIO.write(img, "png", new File("Test.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FruchetermanReingold f = new FruchetermanReingold(10,0.99,0.1,0,0);
        long start = System.currentTimeMillis();
        f.apply(drawableNet);
        long end = System.currentTimeMillis();
        System.out.println("FRalgorithm used " + (end-start)/1000.0 +"s");
        img = drawer.draw(drawableNet);
        try {
            ImageIO.write(img, "png", new File("TestO.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Louvain lou = new Louvain(100000);
        start = System.currentTimeMillis();
        DefaultUndirectedWeightedGraph<Community<Node>, DefaultWeightedEdge> commNet = lou.apply(net);
        end = System.currentTimeMillis();
        System.out.println("Louvain used " + (end-start)/1000.0 +"s");
        drawer = new DrawGraph(1000,1000, 10);
        drawableNet = drawer.makeDrawable(commNet);
        f = new FruchetermanReingold(100,0.99,0.1,0,0);
        f.apply(drawableNet);
        img = drawer.draw(drawableNet);
        try {
            ImageIO.write(img, "png", new File("TestC.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<Community<Node>> comms = commNet.vertexSet();
        int fails = 0;
        for (Community<Node> community : comms) {
            System.out.println("In community "+ community.getCommunity()+" found node of origin:");
            ArrayList<Node> nodes = community.getNodes();
            int actualC = nodes.get(0).getCommunity();
            for (Node n : nodes) {
                System.out.print(n.getCommunity()+ " ");
                if(n.getCommunity()!=actualC) {
                    fails++;
                    actualC = n.getCommunity();
                }
            }
            System.out.println();
        }
        System.out.println("Total number of fails: "+ fails);
        CommunityFR cfr = new CommunityFR(net, commNet);
        start = System.currentTimeMillis();
        drawableNet = cfr.apply();
        end = System.currentTimeMillis();
        System.out.println("Special used " + (end-start)/1000.0 +"s");
        drawer = new DrawGraph(1000,1000, 10);
        img = drawer.draw(drawableNet);
        try {
            ImageIO.write(img, "png", new File("TestCFR.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }   
}
