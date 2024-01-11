package org.example;

import java.util.*;

class WebAPINode {
    int id;
    String name;
    Set<String> categories;

    WebAPINode(int id, String name, Set<String> categories) {
        this.id = id;
        this.name = name;
        this.categories = categories;
    }
}

class WebAPIEdge {
    WebAPINode node1;
    WebAPINode node2;
    int weight;

    WebAPIEdge(WebAPINode node1, WebAPINode node2, int weight) {
        this.node1 = node1;
        this.node2 = node2;
        this.weight = weight;
    }
}

class WebAPIGraph {
    List<WebAPINode> nodes;
    List<WebAPIEdge> edges;

    WebAPIGraph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }
}
