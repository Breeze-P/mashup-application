package org.example;

import java.util.*;

public class SearchAlgorithm {

    public static List<WebAPINode> getRandomSubset(List<WebAPINode> nodes, int n) {
        List<WebAPINode> subset = new ArrayList<>(nodes);
        Collections.shuffle(subset);
        return subset.subList(0, Math.min(n, subset.size()));
    }

    public static List<List<WebAPINode>> generateCombinations(List<List<WebAPINode>> input) {
        List<List<WebAPINode>> combinations = new ArrayList<>();
        generateCombinationsHelper(input, 0, new ArrayList<>(), combinations);
        return combinations;
    }

    private static void generateCombinationsHelper(List<List<WebAPINode>> input, int index,
                                                   List<WebAPINode> currentCombination,
                                                   List<List<WebAPINode>> combinations) {
        if (index == input.size()) {
            combinations.add(new ArrayList<>(currentCombination));
            return;
        }

        for (WebAPINode node : input.get(index)) {
            currentCombination.add(node);
            generateCombinationsHelper(input, index + 1, currentCombination, combinations);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }

    public static List<WebAPINode> findMinWeightSubgraph(List<List<WebAPINode>> result, int n, WebAPIGraph graph) {
        // 生成新的 result，每一行随机选择 n 个节点
        List<List<WebAPINode>> randomSubgraphs = new ArrayList<>();
        for (List<WebAPINode> row : result) {
            randomSubgraphs.add(getRandomSubset(row, n));
        }

        // 对新的 result 进行排列组合
        List<List<WebAPINode>> combinations = generateCombinations(randomSubgraphs);

        // 找到最小权重的排列组合
        List<WebAPINode> minWeightCombination = null;
        long minWeight = Long.MAX_VALUE;

        for (List<WebAPINode> combination : combinations) {
            long weight = SteinerTree.SteinerTreeValue(graph, combination.toArray(new WebAPINode[0]));
            if (weight < minWeight) {
                minWeight = weight;
                minWeightCombination = combination;
            }
        }

        return minWeightCombination;
    }

    public static List<List<WebAPINode>> findNodesByCategories(WebAPIGraph graph, List<String> targetCategories) {
        Map<String, List<WebAPINode>> categoryMap = new HashMap<>();

        // 初始化categoryMap
        for (String category : targetCategories) {
            categoryMap.put(category, new ArrayList<>());
        }

        // 遍历图中的节点
        for (WebAPINode node : graph.nodes) {
            // 检查节点是否包含指定的categories
            for (String category : targetCategories) {
                if (node.categories.contains(category)) {
                    categoryMap.get(category).add(node);
                }
            }
        }

        // 将结果转为List
        List<List<WebAPINode>> result = new ArrayList<>(categoryMap.values());

        return result;
    }
}
