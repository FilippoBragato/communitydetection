package communitydetection.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import org.jgrapht.graph.AbstractGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import communitydetection.graphnodes.Community;
import communitydetection.graphnodes.Node;

public class Louvain implements Function<AbstractGraph<Node, DefaultWeightedEdge>, DefaultUndirectedWeightedGraph<Community<Node>, DefaultWeightedEdge>> {
    private int nOfItereations;

    public Louvain(){
        nOfItereations = 1;
    }
    public Louvain(int nOfItereations){
        this.nOfItereations = nOfItereations;
    }
    public void setnOfItereations(int nOfItereations) {
        this.nOfItereations = nOfItereations;
    }
    public int getnOfItereations() {
        return nOfItereations;
    }

    @Override
    public DefaultUndirectedWeightedGraph<Community<Node>, DefaultWeightedEdge> apply(AbstractGraph<Node, DefaultWeightedEdge> network){
        //Calcolo il grado pesato di ogni community e il peso totale degli archi
        double m = initEdgesWeight(network);
        DefaultUndirectedWeightedGraph<Community<Node>, DefaultWeightedEdge> defNet = null;
        double maxMod = -1;
        for (int iteration = 0; iteration< nOfItereations; iteration++){
            // Inizializzo un grafo analogo a quello in ingresso, ma fatto di Community
            DefaultUndirectedWeightedGraph<Community<Node>, DefaultWeightedEdge> net = makeCommunity(network);
            //Creo una lista con tutte le community attive
            ArrayList<Community<Node>> community = new ArrayList<>(net.vertexSet());
            
            // Rimescolo l'ordine dei nodi
            Random rng = new Random();
            Collections.shuffle(community, rng);
            //Imposto la condizione iniziale
            boolean converged = false;
            
            while (!converged) {
                //Anzitutto resetto la condizione iniziale
                converged=true;
                // Per ogni metacommunity di metacom indicizzata con i_mc viene calcolata come cambia la modularità se il nodo viene attribuito alla community di un suo vicino
                for (int i_c = 0; i_c < community.size(); i_c++) {

                    int nEdge = net.degreeOf(community.get(i_c));
                    if(nEdge!=0){

                        double[] variationOfModularity = new double[nEdge];
                        ArrayList<Community<Node>> neighbors = getNeighbors(net, community.get(i_c));

                        //Uso nomi in accordo con quanto descritto da https://en.wikipedia.org/wiki/Louvain_method
                        for(int i_n = 0; i_n < neighbors.size(); i_n++ ){
                            double sigma_tot = neighbors.get(i_n).getTotalWeightedDegree();
                            double k_i = community.get(i_c).getTotalWeightedDegree();
                            double k_i_in = net.getEdgeWeight(net.getEdge(community.get(i_c), neighbors.get(i_n)));
                            variationOfModularity[i_n] =k_i_in/m-(sigma_tot*k_i)/(2*m*m);
                        }
                        // Trovo il massimo della modularità
                        int max_position = 0;
                        double max_modularity = variationOfModularity[0];
                        for (int i = 1; i < variationOfModularity.length; i ++ ) {
                            if (variationOfModularity[i]>max_modularity) {
                                max_position = i;
                                max_modularity = variationOfModularity[i];
                            }
                        }
                        // Se è possibile aumentare la modularità
                        if (max_modularity >0) {
                            //resetto condizione inizial
                            converged = false;
                            //tengo da parte il nodo destinazione
                            Community<Node> destination = neighbors.remove(max_position);
                            //modifico gli archi in modo che puntino a destinazione
                            for (Community<Node> n : neighbors) {
                                if (net.getEdge(destination, n)==null) {
                                    net.addEdge(destination, n);
                                    net.setEdgeWeight(destination, n, net.getEdgeWeight(net.getEdge(community.get(i_c), n)));
                                }
                                else {
                                    double w = net.getEdgeWeight(net.getEdge(community.get(i_c), n)) + net.getEdgeWeight(net.getEdge(destination, n));
                                    net.setEdgeWeight(destination, n, w);
                                }
                            }
                            //modifico i parametri interni della community
                            destination.addNode((Node) community.get(i_c));
                            double weightedDegree = destination.getTotalWeightedDegree() + community.get(i_c).getTotalWeightedDegree() - net.getEdgeWeight(net.getEdge(community.get(i_c), destination));
                            destination.setTotalWeightedDegree(weightedDegree);
                            //rimuovo il nodo e tutti i suoi archi
                            net.removeVertex(community.remove(i_c));
                        }
                    }
                }
            }
            if(iteration == 1) {
                defNet = net;
            }
            else {
                Double modularity = getModulartity(network, community, m);
                if(modularity > maxMod) {
                    maxMod = modularity;
                    defNet = net;
                }
            }
        }
        return defNet;
    }
    private DefaultUndirectedWeightedGraph<Community<Node>, DefaultWeightedEdge> makeCommunity(AbstractGraph<Node, DefaultWeightedEdge> network) {
        Node[] oldNodes = network.vertexSet().toArray(new Node[0]);
        DefaultUndirectedWeightedGraph<Community<Node>, DefaultWeightedEdge> net = new DefaultUndirectedWeightedGraph<Community<Node>, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        Hashtable<Node, Community<Node>> newNodes = new Hashtable<Node, Community<Node>>(oldNodes.length);
        for (int i = 0; i < oldNodes.length; i++) {
            Community<Node> newNode = new Community<Node>(oldNodes[i], i);
            newNodes.put(oldNodes[i], newNode);
            net.addVertex(newNode);
        }
        DefaultWeightedEdge[] oldEdges = network.edgeSet().toArray(new DefaultWeightedEdge[0]);
        for (int i = 0; i < oldEdges.length; i++) {
            Node s = network.getEdgeSource(oldEdges[i]);
            Node t = network.getEdgeTarget(oldEdges[i]);
            net.addEdge(newNodes.get(s), newNodes.get(t));
            net.setEdgeWeight(newNodes.get(s), newNodes.get(t), network.getEdgeWeight(oldEdges[i]));
        }
        return net;
    }
    private double initEdgesWeight(AbstractGraph<Node, DefaultWeightedEdge> network) {
        Set<Node> nodes = network.vertexSet();
        for (Node node : nodes) {
            double weight = 0;
            Set<DefaultWeightedEdge> edgeSet = network.edgesOf(node);
            for (DefaultWeightedEdge edge : edgeSet) {
                weight += network.getEdgeWeight(edge);
            }
            node.setTotalWeightedDegree(weight);
        } 
        double m = 0;
        Set<DefaultWeightedEdge> edges = network.edgeSet();
        for (DefaultWeightedEdge e : edges) {
            m+=network.getEdgeWeight(e);
        }
        return m;
    }

    private ArrayList<Community<Node>> getNeighbors(DefaultUndirectedWeightedGraph<Community<Node>, DefaultWeightedEdge> net, Community<Node> c){
        Set<DefaultWeightedEdge> edges = net.edgesOf(c);
        ArrayList<Community<Node>> neighbors = new ArrayList<Community<Node>>();
        for (DefaultWeightedEdge edge : edges) {
            if (net.getEdgeSource(edge)!=c) neighbors.add(net.getEdgeSource(edge));
            else neighbors.add(net.getEdgeTarget(edge));
        }
        return neighbors;
    }

    private double getModulartity(AbstractGraph<Node, DefaultWeightedEdge> network,ArrayList<Community<Node>> communities, double m){
        double modularity = 0;
        for (Community<Node> community : communities) {
            ArrayList<Node> nodes = community.getNodes();
            for (int i = 0; i < nodes.size(); i++) {
                for (int j = 0; j < nodes.size(); j++) {
                    modularity -= nodes.get(i).getTotalWeightedDegree()*nodes.get(j).getTotalWeightedDegree()/(2*m);
                    DefaultWeightedEdge e = network.getEdge(nodes.get(i), nodes.get(j));
                    if(e != null){
                        modularity += network.getEdgeWeight(e);
                    }
                }
            }
        }
        modularity = modularity/(2*m);
        if(Double.isNaN(modularity)) modularity=0;
        return modularity;
    }
}
