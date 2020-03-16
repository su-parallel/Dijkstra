import java.util.*; 
import java.lang.*; 
import java.io.*;

  
class SerializedVersion { 
    
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

    int minDistance(int N, int dist[], Boolean sptSet[]) 
    { 
        // Initialize min value 
        int min = Integer.MAX_VALUE, min_index = -1; 
        for (int v = 0; v < N; v++) {
            if (!sptSet[v] && dist[v] <= min) { 
                min = dist[v];
                min_index = v;
            } 
        }
        return min_index; 
    } 
  
    int dijkstra(int N, int start, int end, Map<Integer, List<Edge>>graph) 
    { 
        int dist[] = new int[N]; 
        Boolean selected[] = new Boolean[N]; 
  
        for (int i = 0; i < N; i++) { 
            dist[i] = Integer.MAX_VALUE; 
            selected[i] = false; 
        } 
  
        dist[start] = 0; 
        for (int count = 0; count < N; count++) { 
            int u = minDistance(N, dist, selected); 
            selected[u] = true; 	
			List<Edge> myNeighbors = graph.get(u);
            for(Edge neighbor: myNeighbors){
				if(!selected[neighbor.e] && dist[neighbor.e] > dist[u] + neighbor.dis){
					dist[neighbor.e] = dist[u] + neighbor.dis;
                }
			}  
        }
        return dist[end]; 
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
  
    // Driver method 
    public static void main(String[] args) 
    { 
        SerializedVersion obj = new SerializedVersion();
        obj.testDijkstra();
    } 
} 