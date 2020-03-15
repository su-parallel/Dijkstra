import java.util.*;
import java.io.*;

class ParallelizedDijkstra{
    
    private static final int MAX_INF = Integer.MAX_VALUE;
    private static final int THREAD_N = 4;

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
        dis[start] = 0;
        for(int i = 0; i < N; i ++){
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
            dis[selectedVertex] = minDis;
            selected[selectedVertex] = true;
            if(graph.containsKey(selectedVertex)){
                // serial method code
                // List<Edge> connectedVertex = graph.get(selectedVertex);
                // for(Edge edge : connectedVertex){
                //     if(!selected[edge.e] && dis[edge.e] > dis[selectedVertex] + edge.dis){
                //         dis[edge.e] = dis[selectedVertex] + edge.dis;
                //     }
                // }
                List<Thread> threads = new ArrayList<>();
                List<Edge> connectedVertex = graph.get(selectedVertex);
                int size = connectedVertex.size(), thread_size = size / THREAD_N, startIndex = 0;
                if(thread_size == 0){
                    thread_size = size;
                }
                while(startIndex < size){
                    updateVertexDistance update = new updateVertexDistance(startIndex, Math.min(startIndex + thread_size, size), selectedVertex, connectedVertex, dis, selected);
                    startIndex += thread_size;
                    Thread t = new Thread(update);
                    threads.add(t);
                    t.start();
                } 
                for(Thread t : threads){
                    try{
                        t.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
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

    private void testDijkstra(){
        String folderPath = "testcase/";
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        for(int i = 0; i < files.length; i ++){
            List<List<Integer>> data = Utils.readData(files[i].getAbsolutePath());
            if(data.size() < 2){
                return ;
            }
            Map<Integer, List<Edge>> graph = buildGraph(data);
            int N = data.get(0).get(0), start = data.get(0).get(1), end = data.get(0).get(2);
            int targetDis = 0;
            // for largeTestData, there isn't targetDistance
            if(data.get(0).size() >= 4){
                targetDis = data.get(0).get(3);
            }
            System.out.printf("Start running on LargeTestData[%s]\n", files[i].getName());
            long startTime = System.currentTimeMillis();
            dijkstra(N, start, end, graph);
            long endTime = System.currentTimeMillis();
            System.out.printf("TestData[%s] with N = %d, number of edges = %d, finished in %d ms\n\n", files[i].getName(), N, N * (N - 1), endTime - startTime);
        }
    }

    public static void main(String[] args){
        ParallelizedDijkstra obj = new ParallelizedDijkstra();
        obj.testDijkstra();
    }
}