package it.polimi.ingsw.common;

import java.util.*;

public class Graph<T> {
    private int visitedVertex = 0;

    // We use Hashmap to store the edges in the graph
    private final Map<T, List<T> > map = new HashMap<>();

    // This function adds a new vertex to the graph
    private void addVertex(T s) {
        map.put(s, new LinkedList<T>());
    }

    // This function adds the edge
    // between source to destination
    public void addEdge(T source, T destination, boolean bidirectional) {

        if (!map.containsKey(source))
            addVertex(source);

        if (!map.containsKey(destination))
            addVertex(destination);

        map.get(source).add(destination);
        if (bidirectional) {
            map.get(destination).add(source);
        }
    }


    // A function used by DFS
    private void DFSUtil(T v, Map<T, Boolean> visited) {
        // Mark the current node as visited and print it
        visited.put(v, true);
        visitedVertex++;

        // Recur for all the vertices adjacent to this
        // vertex
        Iterator<T> i = map.get(v).listIterator();
        while (i.hasNext()) {
            T next = i.next();
            if (!visited.get(next))
                DFSUtil(next, visited);
        }
    }

    // The function to do DFS traversal.
    // It uses recursive DFSUtil()
    public int DFS(T v) throws NullPointerException{
        if(!map.containsKey(v))
            throw new NullPointerException();
        visitedVertex = 0;
        // Mark all the vertices as
        // not visited(set as
        // false by default in java)
        //boolean[] visited = new boolean[map.size()];
        Map<T, Boolean> visited = new HashMap<>();
        for (T key : map.keySet()) {
            visited.put(key, false);
        }

        // Call the recursive helper
        // function to print DFS
        // traversal
        DFSUtil(v, visited);

        return visitedVertex;
    }
















    // This function gives the count of vertices
    public void getVertexCount() {
        System.out.println("The graph has "
                + map.keySet().size()
                + " vertex");
    }

    // This function gives the count of edges
    public void getEdgesCount(boolean bidirectional) {
        int count = 0;
        for (T v : map.keySet()) {
            count += map.get(v).size();
        }
        if (bidirectional) {
            count = count / 2;
        }
        System.out.println("The graph has "
                + count
                + " edges.");
    }

    // This function gives whether
    // a vertex is present or not.
    public void hasVertex(T s) {
        if (map.containsKey(s)) {
            System.out.println("The graph contains "
                    + s + " as a vertex.");
        }
        else {
            System.out.println("The graph does not contain "
                    + s + " as a vertex.");
        }
    }

    // This function gives whether an edge is present or not.
    public void hasEdge(T s, T d) {
        if (map.get(s).contains(d)) {
            System.out.println("The graph has an edge between "
                    + s + " and " + d + ".");
        }
        else {
            System.out.println("The graph has no edge between "
                    + s + " and " + d + ".");
        }
    }

    // Prints the adjancency list of each vertex.
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (T v : map.keySet()) {
            builder.append(v.toString() + ": ");
            for (T w : map.get(v)) {
                builder.append(w.toString() + " ");
            }
            builder.append("\n");
        }

        return (builder.toString());
    }
}