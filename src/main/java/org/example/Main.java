package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 从 api.csv 文件读取节点信息
        List<WebAPINode> apiNodes = GraphBuilder.readAPINodesFromCSV("src/main/resources/api.csv");

        // 从 mashup.csv 文件读取连接信息和计算权重
        List<MashupRecord> mashupRecords = GraphBuilder.readMashupRecordsFromCSV("src/main/resources/mashup.csv", apiNodes);

//        for (MashupRecord record : mashupRecords) {
//            System.out.println(record);
//        }

        // 构建图结构
        WebAPIGraph graph = GraphBuilder.buildGraph(apiNodes, mashupRecords);

        // 输出图结构信息（可选）
//        GraphBuilder.printGraph(graph);

        String[] apiNames = {"Google Chart", "DocuSign Enterprise", "411Sync"};
        WebAPINode[] keyNodes = GraphBuilder.getKeyNodes(graph, apiNames);

        System.out.println(graph.nodes.size());

        // Step 2: Call findMinWeight Method
        long minWeight = SteinerTree.findMinWeight(graph, keyNodes);

        // Step 3: Process the Result
        System.out.println("Minimum Weight: " + minWeight);
        // Additional processing based on your requirements
    }
}