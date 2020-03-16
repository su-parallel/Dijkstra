import java.util.*;
import java.io.*;

class ParallelizedDijkstra{
    
    private static final int MAX_INF = Integer.MAX_VALUE;
    private static final int THREAD_N = 4;
    private List<Integer> dis, selected;

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
        dis.clear();
        selected.clear();
        for(int i = 0; i < N; i ++){
            dis.add(MAX_INF);
            selected.add(0);
        }
        dis.set(start, 0);
        for(int i = 0; i < N; i ++){
            int[] result = findMinDistance(N);
            int selectedVertex = result[0], minDis = result[1];
            dis.set(selectedVertex, minDis);
            selected.set(selectedVertex, 1);
            if(graph.containsKey(selectedVertex)){
                updateVertice(selectedVertex, graph);
            }
        }
        return dis.get(end);
    }

    private void updateVertice(int selectedVertex, Map<Integer, List<Edge>> graph){
        List<Thread> threads = new ArrayList<>();
        List<Edge> connectedVertex = graph.get(selectedVertex);
        int size = connectedVertex.size(), thread_size = size / THREAD_N, startIndex = 0;
        if(thread_size == 0){
            thread_size = size;
        }
        while(startIndex < size){
            updateVertexDistance update = new updateVertexDistance(startIndex, Math.min(startIndex + thread_size, size), selectedVertex, connectedVertex);
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

    /**
     * Helper method to find the vertex with the shortest distance from the start
     */
    private int[] findMinDistance(int N){
        int minDis = MAX_INF, selectedVertex = -1;
        int my_chunk = N / THREAD_N + 1, startIndex = 0, thread_index = 0;
        List<Thread> threads = new ArrayList<>();
        List<CloestVertexFinder> finders = new ArrayList<>();
        while(startIndex < N){
            CloestVertexFinder finder = new CloestVertexFinder(startIndex,  Math.min(startIndex + my_chunk, N), thread_index);
            startIndex += my_chunk;
            thread_index += 1;
            Thread t = new Thread(finder);
            threads.add(t);
            t.start();
            finders.add(finder);
        }

        for(Thread t : threads){
            try{
                t.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
        //doing the reduce, to find the shortest distance and its vertext
        for(int i = 0; i < finders.size(); i++){
            if(finders.get(i).minDis < minDis){
                minDis = finders.get(i).minDis;
                selectedVertex = finders.get(i).closestVertex;
            }
        }

        int[]array = {selectedVertex, minDis};
        return array;
    }
    
    /*
    * this class is used in the first parallel part, which can help you find the closest vertex to the start vertex
    */
    class CloestVertexFinder implements Runnable{
        int startIndex, endIndex, thread_index;
        int minDis, closestVertex;

        public CloestVertexFinder(int startIndex, int endIndex, int thread_index){
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.thread_index = thread_index;
            minDis = MAX_INF;
            closestVertex = -1;
        }


        @Override
        public void run(){
            for(int j = startIndex; j < endIndex; j ++){
                if (selected.get(j) == 0 && dis.get(j) <= minDis) {
                    minDis = dis.get(j);
                    closestVertex = j; 
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

        public updateVertexDistance(int startIndex, 
                                    int endIndex, 
                                    int selectedVertex, 
                                    List<Edge> connectedVertex){
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.selectedVertex = selectedVertex;
            this.connectedVertex = connectedVertex;
        }

        @Override
        public void run(){
            for(int i = startIndex; i < endIndex; i ++){
                Edge edge = connectedVertex.get(i);
                if(selected.get(edge.e) == 0 && dis.get(edge.e) > dis.get(selectedVertex) + edge.dis){
                    dis.set(edge.e, dis.get(selectedVertex) + edge.dis);
                }
            }
        }
    }

    private void testDijkstra(){
        dis = new ArrayList<>();
        selected = new ArrayList<>();
        String folderPath = "testcase/";
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        for(int i = 0; i < files.length; i ++){
            if(!files[i].getName().contains(".txt")){
                continue;
            }
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