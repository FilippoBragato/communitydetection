package communitydetection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import communitydetection.algorithm.Louvain;
import communitydetection.graphmanagement.TestGraphCreator;
import communitydetection.graphnodes.Community;
import communitydetection.graphnodes.Node;

public class Tester {
    
    public static void main(String[] args) {
        int nOfComm = 3;
        int nOfNodeInC = 50;
        double eIn = 20;
        double eOut = 1;
        int iterations = 1;
        File commTxt = new File("MesureDifferentEinEoutB2.txt");
        try {
            commTxt.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        FileWriter myWriter= null;
        try {
            myWriter = new FileWriter(commTxt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Random rng = new Random();
        double m=500000.0;
        for (int i = 1; i < 2; i++) {
            for (int j = 10000; j > 100; j--) {
                nOfComm = rng.nextInt(10)+2;
                nOfNodeInC = j/nOfComm;
                double zz = m/(nOfComm*nOfNodeInC);
                eIn = 0.8*zz;
                eOut = 0.2*zz;
                if(eIn>nOfNodeInC-1){
                    eIn = nOfNodeInC -1;
                    eOut = zz-eIn;
                }
                if(eOut>nOfNodeInC*(nOfComm-1)) continue;
                TestGraphCreator creator = new TestGraphCreator(nOfComm,nOfNodeInC,eIn,eOut);
                DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge> net = creator.lpartition();
                Louvain lou = new Louvain(iterations);
                long start = System.currentTimeMillis();
                DefaultUndirectedWeightedGraph<Community<Node>, DefaultWeightedEdge> commNet = lou.apply(net);
                long end = System.currentTimeMillis();
                Set<Community<Node>> comms = commNet.vertexSet();
                double fails = 0;
                for (Community<Node> community : comms) {
                    int[] c = new int[nOfComm];
                    ArrayList<Node> nodes = community.getNodes();
                    for (Node n : nodes) {
                        c[n.getCommunity()]++;
                    }
                    int tempFails = 0;
                    int max=0;
                    for (int l = 0; l < c.length; l++) {
                        if(c[l]>max){
                            tempFails = tempFails + max;
                            max=c[l];
                        }
                        else{
                            tempFails = tempFails + c[l];
                        }
                    }
                    fails +=tempFails;

                }
                fails/=((nOfComm-1)*nOfNodeInC);
                try {
                myWriter.write(nOfComm+" "+nOfNodeInC+" "+eIn+" "+eOut+" "+iterations+"              "+(end-start)/1000.0+" "+comms.size()+" "+fails+" ");
                System.out.println(nOfComm+" "+nOfNodeInC+" "+eIn+" "+eOut+" "+iterations+"              "+(end-start)/1000.0+" "+comms.size()+" "+fails+" ");
                
                myWriter.write("\n");
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
