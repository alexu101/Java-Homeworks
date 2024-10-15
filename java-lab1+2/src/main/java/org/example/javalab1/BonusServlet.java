package org.example.javalab1;

import java.io.*;
import java.util.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.graph4j.*;
import org.graph4j.util.UnionFind;

@WebServlet(name = "bonusServlet", value = "/bonus-servlet")
public class BonusServlet extends HttpServlet {
    public void init() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        int vertices = Integer.parseInt(request.getParameter("vertices"));
        int k = Integer.parseInt(request.getParameter("k"));

        Graph graph = GraphBuilder.numVertices(vertices).buildGraph();

        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {
                graph.addEdge(i, j, i + j);
            }
        }

        List<Set<Edge>> spanningTrees = generateSpanningTrees(graph);

        if (k > spanningTrees.size()) {
            out.println("Invalid K");
            return;
        }

        List<Set<Edge>> firstKSpanningTrees = spanningTrees.subList(0, k);

        for (int i = 0; i < firstKSpanningTrees.size(); i++) {
            out.println("Spanning Tree " + (i + 1) + ":");
            double totalWeight =0.0;
            for (Edge edge : firstKSpanningTrees.get(i)) {
                out.println("Edge: (" + edge.source() + ", " + edge.target() + ") Weight: " + edge.weight());
                totalWeight += edge.weight();
            }
            out.println("Total weight: "+totalWeight);
        }

    }

    List<Set<Edge>> generateSpanningTrees(Graph graph){
        Set<Set<Edge>> spanningTrees = new HashSet<>();

        Edge[] edges = graph.edges();

        Arrays.sort(edges, Comparator.comparingDouble(Edge::weight));

        Set<Edge> mst = getMST(graph, Arrays.asList(edges)); //minimum spanning tree
        spanningTrees.add(mst);


        for (Edge edgeToRemove : mst) { //iterate over possible mst and replace edges
            Set<Edge> newTree = new HashSet<>(mst);
            newTree.remove(edgeToRemove); //remove edge

            for (Edge edge : edges) { //try to replace with other edge
                if (!newTree.contains(edge)) {
                    newTree.add(edge);

                    if (isValidSpanningTree(graph, newTree)) { //if its a valid spanning tree, add it
                        spanningTrees.add(new HashSet<>(newTree));
                    }

                    newTree.remove(edge);
                }
            }
        }

        List<Set<Edge>> sortedSpanningTrees = new ArrayList<>(spanningTrees);
        sortedSpanningTrees.sort(Comparator.comparingDouble(this::calculateWeight));

        return sortedSpanningTrees;
    }

    private double calculateWeight(Set<Edge> edges) {
        double totalWeight = 0.0;
        for (Edge edge : edges) {
            totalWeight += edge.weight();
        }
        return totalWeight;
    }

    private static Set<Edge> getMST(Graph graph, List<Edge> edges) {
        Set<Edge> mst = new HashSet<>();
        UnionFind uf = new UnionFind(graph.numVertices());

        for (Edge edge : edges) { //if edges dont create a cycle, add to mst
            int root1 = uf.find(edge.source());
            int root2 = uf.find(edge.target());
            if (root1 != root2) {
                uf.union(root1, root2);
                mst.add(edge);
                if (mst.size() == graph.numVertices() - 1) {
                    break; //spanning founded
                }
            }
        }

        return mst;
    }

    private static boolean isValidSpanningTree(Graph graph, Set<Edge> edges) {
        if (edges.size() != graph.numVertices() - 1) {
            return false; // n-1 edges test
        }

        UnionFind uf = new UnionFind(graph.numVertices());
        for (Edge edge : edges) {
            if (uf.find(edge.source()) == uf.find(edge.target())) {
                return false; // cycle found, no mst
            }
            uf.union(edge.source(), edge.target());
        }

        return true; // no cycles -> mst
    }


    public void destroy() {
    }
}
