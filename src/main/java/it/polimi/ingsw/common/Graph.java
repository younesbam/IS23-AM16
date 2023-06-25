package it.polimi.ingsw.common;

import java.util.*;

/**
 * Graph data structure of generic type T.
 * @param <T> generic type of each vertex.
 */
public class Graph<T> {
    /**
     * Visited vertex during DFS search.
     */
    private int visitedVertex = 0;

    /**
     * Map to store vertex and edges in the graph.
     */
    private final Map<T, List<T> > map = new HashMap<>();

    /**
     * Adds a new vertex to the graph
     * @param s Vertex to be added.
     */
    private void addVertex(T s) {
        map.put(s, new LinkedList<T>());
    }

    /**
     * Adds edge between source and destination. If one of the two vertex is not present in the map, it will be added automatically.
     * @param source vertex source.
     * @param destination vertex destination.
     * @param bidirectional true if undirected graph.
     */
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


    /**
     * A function used by DFS to perform a DFS.
     * @param v starting vertex.
     * @param visited visited map to know if each vertex was visited.
     */
    private void DFSUtil(T v, Map<T, Boolean> visited) {
        // Mark the current node as visited.
        visited.put(v, true);
        visitedVertex++;

        // Recur for all the vertices adjacent to this vertex.
        Iterator<T> i = map.get(v).listIterator();
        while (i.hasNext()) {
            T next = i.next();
            if (!visited.get(next))
                DFSUtil(next, visited);
        }
    }

    /**
     * Do DFS traversal.
     * @param v starting vertex
     * @return integer representing the number of visited vertex.
     * @throws NullPointerException thrown if the map doesn't contain the starting vertex.
     */
    // It uses recursive DFSUtil()
    public int DFS(T v) throws NullPointerException{
        if(!map.containsKey(v))
            throw new NullPointerException();
        visitedVertex = 0;

        // Mark all vertex as not visited.
        Map<T, Boolean> visited = new HashMap<>();
        for (T key : map.keySet()) {
            visited.put(key, false);
        }

        // Call the recursive helper function to print DFS traversal
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