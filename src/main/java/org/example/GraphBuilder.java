package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class MashupRecord {
    int id;
    List<String> relatedAPIs;
    int weight;

    MashupRecord(int id, List<String> relatedAPIs, int weight) {
        this.id = id;
        this.relatedAPIs = relatedAPIs;
        this.weight = weight;
    }
}

public class GraphBuilder {
    public static void main(String[] args) {
        // 从 api.csv 文件读取节点信息
        List<WebAPINode> apiNodes = readAPINodesFromCSV("src/main/resources/api.csv");

        // 从 mashup.csv 文件读取连接信息和计算权重
        List<MashupRecord> mashupRecords = readMashupRecordsFromCSV("src/main/resources/mashup.csv", apiNodes);

//        for (MashupRecord record : mashupRecords) {
//            System.out.println(record);
//        }

        // 构建图结构
        WebAPIGraph graph = buildGraph(apiNodes, mashupRecords);

        // 输出图结构信息（可选）
        printGraph(graph);
    }

    public static List<WebAPINode> readAPINodesFromCSV(String filePath) {
        List<WebAPINode> apiNodes = new ArrayList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath)).withSkipLines(1).build()) {
            List<String[]> allData = csvReader.readAll();

            int maxIterations = 1000;
            int currentIterations = 0;

            int apiId = 0;
            for (String[] fields : allData) {
                if (currentIterations >= maxIterations) {
                    break;  // 如果已执行的次数达到上限，退出循环
                }
                String name = fields[1];
                String categoriesString = fields[3];
                Set<String> categories = new HashSet<>(Arrays.asList(categoriesString.split(",")));

                WebAPINode apiNode = new WebAPINode(apiId++, name, categories);
                apiNodes.add(apiNode);
                currentIterations++;
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return apiNodes;
    }

    public static List<MashupRecord> readMashupRecordsFromCSV(String filePath, List<WebAPINode> apiNodes) {
        List<MashupRecord> mashupRecords = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath)).withSkipLines(1).build()) {
            String[] line;
            int mashupId = 0;
            while ((line = reader.readNext()) != null) {
//                System.out.println(Arrays.toString(line));
                String relatedAPIsString = line[2];
                List<String> relatedAPIs = Arrays.asList(relatedAPIsString.split(","));
//                System.out.println(Arrays.toString(relatedAPIsString.split(",")));
                int weight = computeWeight(apiNodes, relatedAPIs);
                mashupRecords.add(new MashupRecord(mashupId++, relatedAPIs, weight));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return mashupRecords;
    }

    public static int computeWeight(List<WebAPINode> apiNodes, List<String> relatedAPIs) {
        // 计算权重：考虑两个API在Mashup记录中共同出现的协作次数
        int weight = 0;
        for (WebAPINode apiNode : apiNodes) {
            for (String relatedAPI : relatedAPIs) {
                if (apiNode.name.equals(relatedAPI)) {
                    weight++;
                }
            }
        }
        return weight;
    }

    public static WebAPIGraph buildGraph(List<WebAPINode> apiNodes, List<MashupRecord> mashupRecords) {
        WebAPIGraph graph = new WebAPIGraph();

        // 添加节点到图
        graph.nodes.addAll(apiNodes);

        // 添加连接和权重到图
        for (MashupRecord mashupRecord : mashupRecords) {
            for (int i = 0; i < mashupRecord.relatedAPIs.size() - 1; i++) {
                WebAPINode node1 = findNodeByName(apiNodes, mashupRecord.relatedAPIs.get(i));
                WebAPINode node2 = findNodeByName(apiNodes, mashupRecord.relatedAPIs.get(i + 1));

                if (node1 != null && node2 != null) {
                    WebAPIEdge edge = new WebAPIEdge(node1, node2, mashupRecord.weight);
                    graph.edges.add(edge);
                }
            }
        }

        return graph;
    }

    public static WebAPINode findNodeByName(List<WebAPINode> apiNodes, String name) {
        for (WebAPINode apiNode : apiNodes) {
            if (apiNode.name.equals(name)) {
                return apiNode;
            }
        }
        return null;
    }

    public static WebAPINode[] getKeyNodes(WebAPIGraph graph, String[] apiNames) {
        List<WebAPINode> keyNodes = new ArrayList<>();

        // Iterate through the nodes in the graph
        for (WebAPINode node : graph.nodes) {
            // Check if the node's name is in the list of API names
            if (Arrays.asList(apiNames).contains(node.name)) {
                keyNodes.add(node);
            }
        }

        // Convert the list to an array
        WebAPINode[] keyNodesArray = new WebAPINode[keyNodes.size()];
        keyNodesArray = keyNodes.toArray(keyNodesArray);

        return keyNodesArray;
    }

    public static void printGraph(WebAPIGraph graph) {
        // 输出节点信息
        System.out.println("Nodes:");
        for (WebAPINode node : graph.nodes) {
            System.out.println("ID: " + node.id + ", Name: " + node.name + ", Categories: " + node.categories);
        }

        // 输出边信息
        System.out.println("\nEdges:");
        for (WebAPIEdge edge : graph.edges) {
            System.out.println("Node1: " + edge.node1.name + ", Node2: " + edge.node2.name + ", Weight: " + edge.weight);
        }
    }
}
