package org.example;

import java.util.Arrays;
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

        // 指定目标categories
        List<String> targetCategories = Arrays.asList("Search", "Photos", "Storage");

        // 查询
        List<List<WebAPINode>> result = SearchAlgorithm.findNodesByCategories(graph, targetCategories);

        // 输出结果
        for (int i = 0; i < result.size(); i++) {
            System.out.println("Nodes for Category " + targetCategories.get(i) + ":");
            System.out.println(" len: " + result.get(i).size());
        }

        long startTime = System.currentTimeMillis();

        List<WebAPINode> minWeightCombination = SearchAlgorithm.findMinWeightSubgraph(result, 10, graph);

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("代码执行时间：" + elapsedTime + " 毫秒");

        System.out.println("Min Weight Combination:");
        for (WebAPINode node : minWeightCombination) {
            System.out.println("  " + node.name);
        }

        // 输出图结构信息（可选）
//        GraphBuilder.printGraph(graph);

//        String[] apiNames = {"Google Chart", "DocuSign Enterprise", "411Sync"};
//        WebAPINode[] keyNodes = GraphBuilder.getKeyNodes(graph, apiNames);

//        System.out.println(graph.nodes.size());

        // Step 2: Call SteinerTreeValue Method
//        long minWeight = SteinerTree.SteinerTreeValue(graph, keyNodes);

        // Step 3: Process the Result
//        System.out.println("Minimum Weight: " + minWeight);
        // Additional processing based on your requirements
    }
}