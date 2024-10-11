package org.example.javalab1;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "bonusServlet", value = "/bonus-servlet")
public class BonusServlet extends HttpServlet {
    public void init() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        int vertices = Integer.parseInt(request.getParameter("vertices"));
        int k = Integer.parseInt(request.getParameter("k"));

        List<List<int[]>> firstKSpanningTrees = new ArrayList<>();

        // Generate all unique spanning trees for a complete graph with n vertices
        Set<List<int[]>> uniqueSpanningTrees = new HashSet<>();
        List<int[]> edges = new ArrayList<>();

        // Generate edges
        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                edges.add(new int[]{i, j});
            }
        }

        // Backtracking to find all spanning trees
        backtrack(new ArrayList<>(), new boolean[vertices], edges, uniqueSpanningTrees, vertices, 0);

        // Sort spanning trees by weight
        List<List<int[]>> sortedSpanningTrees = new ArrayList<>(uniqueSpanningTrees);
        sortedSpanningTrees.sort(Comparator.comparingInt(this::calculateWeight));

        int treesToReturn = Math.min(k, sortedSpanningTrees.size());

        // Output the first k spanning trees
        for (int i = 0; i < treesToReturn; i++) {
            List<int[]> tree = sortedSpanningTrees.get(i);
            out.println("Spanning Tree: " + (i + 1) + ": " + formatTree(tree) + " | Weight " + calculateWeight(tree));
        }
    }

    protected void backtrack(List<int[]> currentTree, boolean[] visited, List<int[]> edges, Set<List<int[]>> uniqueSpanningTrees, int vertices, int edgeIndex) {
        if (currentTree.size() == vertices - 1) { // Found a valid spanning tree
            uniqueSpanningTrees.add(new ArrayList<>(currentTree));
            return;
        }

        for (int i = edgeIndex; i < edges.size(); i++) {
            int[] edge = edges.get(i);
            if (!visited[edge[0]] || !visited[edge[1]]) {
                boolean wasVisited1 = visited[edge[0]];
                boolean wasVisited2 = visited[edge[1]];

                // Mark the vertices as visited
                visited[edge[0]] = true;
                visited[edge[1]] = true;
                currentTree.add(edge);

                backtrack(currentTree, visited, edges, uniqueSpanningTrees, vertices, i + 1); // Recur with the next edge

                // Backtrack
                currentTree.remove(currentTree.size() - 1);
                visited[edge[0]] = wasVisited1; // Restore the visited state
                visited[edge[1]] = wasVisited2;
            }
        }
    }

    protected int calculateWeight(List<int[]> tree) {
        int weight = 0;
        for (int[] edge : tree) {
            weight += edge[0] + edge[1]; // Edge weight is the sum of vertex indices
        }
        return weight;
    }

    protected String formatTree(List<int[]> tree) {
        StringBuilder sb = new StringBuilder();
        for (int[] edge : tree) {
            sb.append("(").append(edge[0]).append(", ").append(edge[1]).append(") ");
        }
        return sb.toString().trim();
    }

    public void destroy() {
    }
}
