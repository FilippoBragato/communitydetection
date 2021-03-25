package communitydetection.graphnodes;

public class GraficNode {
    private double drawX;
    private double drawY;
    private Node node;

    public GraficNode(Node node, double x, double y, int size){
        this.node = node;
        this.drawX = x;
        this.drawY =y;
    }
    public GraficNode(Node node){
        this.node = node;
        this.drawX = node.getX();
        this.drawY = node.getY();
        node.setGrafical(this);
    }

    public Node getNode() {
        return node;
    }
    public double getX() {
        return drawX;
    }
    public double getY() {
        return drawY;
    }
    public void setNode(Node node) {
        this.node = node;
    }
    public void setX(double x) {
        this.drawX = x;
    }
    public void setY(double y) {
        this.drawY = y;
    }
    public int getSize(){
        return node.getSize();
    }
    
    
}
