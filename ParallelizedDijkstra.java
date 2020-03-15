import java.util.*;
import java.lang.Math;

class ParallelizedDijkstra{
    
    private static final int MAX_INF = Integer.MAX_VALUE;
    private static final int THREAD_N = 4;
    private int[] shortestDis = new int[THREAD_N];
    private int[] closestVertex = new int[THREAD_N];

    


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

        for(int i = 0; i < THREAD_N; i++){
            shortestDis[i] = MAX_INF;
            closestVertex[i] = -1;
        }

        dis[start] = 0;
        for(int i = 0; i < N; i ++){
            
            /**
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
            **/

            // threaded version

            int[] result = findMinDistance(N, selected, dis);
            int selectedVertex = result[0];
            int minDis = result[1];

            System.out.println(selectedVertex);
            System.out.println(minDis);
            System.out.printf("[i = %d] vertex = %d, minDis = %d\n", i, selectedVertex, minDis);
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

    /**
     * Helper method to find the vertex with the shortest distance from the start
     */
    private int[] findMinDistance(int N, boolean[] selected, int[]dis){


        int minDis = MAX_INF;
        int selectedVertex = -1;

        //the amount of data for my thread
        
        int my_chunk = N / THREAD_N + 1;
        
        int startIndex = 0;
        int thread_index = 0;
        List<Thread> threads = new ArrayList<>();

        while(startIndex < N){

            CloestVertexFinder finder = new CloestVertexFinder(startIndex,  Math.min(startIndex + my_chunk, N), thread_index, selected, dis);
            startIndex += my_chunk;
            thread_index += 1;
            
            Thread t = new Thread(finder);
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
        
        //doing the reduce, to find the shortest distance and its vertext
        for(int i = 0; i < THREAD_N; i++){
            if(shortestDis[i] < minDis){
                minDis = shortestDis[i];
                selectedVertex = closestVertex[i];
            }

        }

        //put the shortest distance and vertex into array to return
        int[]array = {selectedVertex, minDis};

        return array;
    }

    /*
    * this class is used in the first parallel part, which can help you find the closest vertex to the start vertex
    */
    class CloestVertexFinder implements Runnable{
        int startIndex, endIndex, thread_index;
        boolean[]selected;
        int[] dis;


        public CloestVertexFinder(int startIndex, int endIndex, int thread_index, boolean[] selected, int[]dis){
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.thread_index = thread_index;
            this.selected = selected;
            this.dis = dis;
        }


        @Override
        public void run(){
            int min = Integer.MAX_VALUE, min_index = -1; 

            for(int j = startIndex; j < endIndex; j ++){
                if (selected[j] == false && dis[j] <= shortestDis[thread_index]) {
                    shortestDis[thread_index] = dis[j]; 
                    closestVertex[thread_index] = j; 
                }
            }
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
        String filePath = "/Users/haoli/Downloads/Dijkstra-master/testcase/TestData1.txt";
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