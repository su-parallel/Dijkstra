import java.util.*;

class ParallelizedDijkstra{
    
    class Edge{
        int s, e, dis;
    }

    /*
    * this method is used to get adjacent edges
    */
    private List<Edge> getEdges(Map<Integer, List<Edge>> graph, int s){
        List<Edge> edges = new ArrayList<>();
        if(graph.containsKey(s)){
            edges = graph.get(s);
        }
        return edges;
    }

    /**
    * @param start the start vertex in the graph
    * @param end the end vertex in the graph 
    * @param graph the representation of the graph, the key is vertex index and the value is its' edges
    * @return the shortest distance from start to end
    */
    private int dijkstra(int N, int start, int end, Map<Integer, List<Edge>> graph){
        //TODO
    }

    /*
    * this class is used in the first parallel part, which can help you find the closest vertex to the start vertex
    */
    class findCloestVertex implements Runnable{
        //TODO
        @Override
        public void run(){
            //TODO
        }
    }

    /*
    * this class is used to update left vertex with the selected vertex in some iteration
    */
    class updateVertexDistance implements Runnable{
        //TODO
        @Override
        public void run(){
            //TODO
        }
    }

    public  static void main(String[] args){

    }
}