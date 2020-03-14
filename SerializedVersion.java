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

    // A utility function to find the vertex with minimum distance value, 
    // from the set of vertices not yet included in shortest path tree 
    int minDistance(int N, int dist[], Boolean sptSet[]) 
    { 
        // Initialize min value 
        int min = Integer.MAX_VALUE, min_index = -1; 
  
        for (int v = 0; v < N; v++) 
            if (sptSet[v] == false && dist[v] <= min) { 
                min = dist[v]; 
                min_index = v; 
            } 
  
        return min_index; 
    } 
  
    // Function that implements Dijkstra's single source shortest path 
    // algorithm for a graph represented using adjacency matrix 
    // representation 
    int dijkstra(int N, int start, int end, Map<Integer, List<Edge>>graph) 
    { 
        int dist[] = new int[N]; // The output array. dist[i] will hold 
        // the shortest distance from start to i 
  
        // sptSet[i] will true if vertex i is included in shortest 
        // path tree or shortest distance from src to i is finalized 
        Boolean selected[] = new Boolean[N]; 
  
        // Initialize all distances as INFINITE and stpSet[] as false 
        for (int i = 0; i < N; i++) { 
            dist[i] = Integer.MAX_VALUE; 
            selected[i] = false; 
        } 
  
        // Distance of source vertex from itself is always 0 
        dist[start] = 0; 
  
        // Find shortest path for all vertices 
        for (int count = 0; count < N - 1; count++) { 
            // Pick the minimum distance vertex from the set of vertices 
            // not yet processed. u is always equal to src in first 
            // iteration. 
            int u = minDistance(N, dist, selected); 
  
            // Mark the picked vertex as processed 
            selected[u] = true; 
  
          
            
  				
			//get all the edges that connected to me
			List<Edge> myNeighbors = graph.get(u);

			//loop through all my neighbors to update dis[]
			for(Edge neighbor: myNeighbors){
				if(!selected[neighbor.e] &&dist[neighbor.e] > dist[u] + neighbor.dis)
					dist[neighbor.e] = dist[u] + neighbor.dis;
			}
               
        }
        return dist[end]; 
  
    } 
  
    // Driver method 
    public static void main(String[] args) 
    { 
        
        SerializedVersion obj = new SerializedVersion();
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