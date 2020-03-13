import java.util.*;

class ParallelizedDijkstra{
    
    private static final int MAX_INF = Integer.MAX_VALUE;

    class Edge{
        int s, e, dis;
        public Edge(int s, int e, int dis){
            this.s = s;
            this.e = e;
            this.dis = dis;
        }
    }

    /*
    * this method is used to build the graph by the nodes read from the local data
    */
    private Map<Integer, List<Edge>> buildGraph(List<List<Integer>> nodes){
        Map<Integer, List<Edge>> graph = new HashMap<>();
        for(int i = 1; i < nodes.size(); i ++){
            int s = nodes.get(i).get(0), e = nodes.get(i).get(1), dis = nodes.get(i).get(2);
            // add edge from s to e
            if(!graph.containsKey(s)){
                graph.put(s, new ArrayList<Edge>());
            }
            List<Edge> edges1 = graph.get(s);
            edges1.add(new Edge(s, e, dis));
            // add edge from e to s
            if(!graph.containsKey(e)){
                graph.put(e, new ArrayList<Edge>());
            }
            List<Edge> edges2 = graph.get(e);
            edges2.add(new Edge(e, s, dis));
        }
        return graph;
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
        int[] dis = new int[N];
        boolean[] selected = new boolean[N];
        for(int i = 0; i < N; i ++){
            dis[i] = MAX_INF;
            selected[i] = false;
        }
        // select `start` vertex
        selected[start] = true;
        dis[start] = 0;
        // update vertice which connects the `start` vertex
        if(graph.containsKey(start)){
            List<Edge> connectedVertex = graph.get(start);
            for(Edge edge : connectedVertex){
                if(!selected[edge.e] && dis[edge.e] > dis[start] + edge.dis){
                    dis[edge.e] = dis[start] + edge.dis;
                }
            }
        }
        for(int i = 0; i < N - 1; i ++){
            int selectedVertex = -1, minDis = MAX_INF;
            for(int j = 0; j < N; j ++){
                if(!selected[j] && minDis > dis[j]){
                    minDis = dis[j];
                    selectedVertex = j;
                }
            }
            if(selectedVertex == -1){
                break;
            }
            System.out.printf("[i = %d] vertex = %d, minDis = %d\n", i, selectedVertex, minDis);
            dis[selectedVertex] = minDis;
            selected[selectedVertex] = true;
            if(graph.containsKey(selectedVertex)){
                List<Edge> connectedVertex = graph.get(selectedVertex);
                for(Edge edge : connectedVertex){
                    if(!selected[edge.e] && dis[edge.e] > dis[selectedVertex] + edge.dis){
                        dis[edge.e] = dis[selectedVertex] + edge.dis;
                    }
                }
            }
        }
        return dis[end];
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
        int startIndex, endIndex, selectedVertex;
        List<Edge> connectedVertex;
        int[] dis;
        boolean[] selected;

        public updateVertexDistance(int startIndex, 
                                    int endIndex, 
                                    int selectedVertex, 
                                    List<Edge> connectedVertex,
                                    int[] dis,
                                    boolean[] selected){
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.selectedVertex = selectedVertex;
            this.connectedVertex = connectedVertex;
            this.dis = dis;
            this.selected = selected;
        }

        @Override
        public void run(){
            for(int i = startIndex; i < endIndex; i ++){
                Edge edge = connectedVertex.get(i);
                if(!selected[edge.e] && dis[edge.e] > dis[selectedVertex] + edge.dis){
                    dis[edge.e] = dis[selectedVertex] + edge.dis;
                }
            }
        }
    }

    public static void main(String[] args){
        ParallelizedDijkstra obj = new ParallelizedDijkstra();
        String filePath = "/Users/xxx0624/Dijkstra/testcase/TestData3.txt";
        List<List<Integer>> data = Utils.readData(filePath);
        if(data.size() < 2){
            return ;
        }
        Map<Integer, List<Edge>> graph = obj.buildGraph(data);
        int N = data.get(0).get(0), start = data.get(0).get(1), end = data.get(0).get(2), targetDis = data.get(0).get(3);
        int dis = obj.dijkstra(N, start, end, graph);
        System.out.println(dis + " / " + targetDis);
    }
}