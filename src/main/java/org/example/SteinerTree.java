package org.example;


import java.util.Arrays;
import java.util.PriorityQueue;

public class SteinerTree {

    public static long SteinerTreeValue(WebAPIGraph graph, WebAPINode[] keyNodes) {
        int n = graph.nodes.size();
        int k = keyNodes.length;
        long[][] pow = new long[n][n]; // 存储各节点之间的距离信息，表示i节点到j节点的距离
        long[][] dp = new long[n][1 << k]; // 存储状态压缩DP的结果，表示以i为根节点状态S时的最小权重和

        // 初始化距离和状态数组
        for (int i = 0; i < n; i++) {
            Arrays.fill(pow[i], Integer.MAX_VALUE); // 初始化距离为最大值
            pow[i][i] = 0; // 节点到自身的距离为0
            Arrays.fill(dp[i], Integer.MAX_VALUE); // 初始化状态DP为最大值
        }

        // 读取边的信息并记录节点之间的最小距离
        for (WebAPIEdge edge : graph.edges) {
            int u = edge.node1.id, v = edge.node2.id, w = edge.weight;
            // 采用邻接矩阵存储结果
            pow[u][v] = Math.min(pow[u][v], w);
            pow[v][u] = Math.min(pow[v][u], w);
        }

        // 记录最后一个关键点
        int y = -1;
        // 标记关键节点，并初始化状态DP值
        for (int i = 0; i < k; i++) {
            WebAPINode keyNode = keyNodes[i];
            int x = keyNode.id;
            dp[x][1 << i] = 0; // 以输入的关键点为根，二进制中对应关键节点位置为1的dp总权重为0
            y = x; // 记录最后一个关键节点
        }

        // 使用状态压缩DP求解最小路径和
        // 遍历从单个节点开始，直到满足所有关键节点都连同的所有可能状态
        for (int s = 1; s < 1 << k; s++) {
            // 遍历以节点i为根节点，状态为s时的情况
            for (int i = 0; i < n; i++) {
                // 遍历s二进制的所有子集，执行i的度数大于1时的操作
                for (int t = s & (s - 1); t > 0; t = (t - 1) & s) {
                    dp[i][s] = Math.min(dp[i][s], dp[i][t] + dp[i][s ^ t]);
                }
            }
            // 处理状态为s的情况下最短路径
            deal(s, n, dp, pow);
        }

        return dp[y][(1 << k) - 1];
    }

    // 处理状态s下的最短路径情况
    private static void deal(int s, int n, long[][] dp, long[][] pow) {
        // 建立优先队列，数组中索引值为1的数字越小，优先级越高
        PriorityQueue<long[]> pq = new PriorityQueue<>((o1, o2) -> Long.compare(o1[1], o2[1]));
        boolean[] vis = new boolean[n]; // 记录已经访问的节点

        // 遍历所有节点，将所有满足当前状态的节点加入优先队列
        for (int i = 0; i < n; i++) {
            if (dp[i][s] != Integer.MAX_VALUE) {
                pq.add(new long[]{i, dp[i][s]});
            }
        }

        // 使用Dijkstra算法求解最短路径
        // 即求解以每个节点为顶点，满足状态s（包含对应的点）的权重和的最小值
        while (!pq.isEmpty()) {
            long[] tmp = pq.poll();
            // 如果已经访问，则跳过
            if (vis[(int) tmp[0]]) {
                continue;
            }
            vis[(int) tmp[0]] = true;
            for (int i = 0; i < n; i++) {
                // 如果i为当前节点，或者i和当前节点不连通，则跳过
                if (i == tmp[0] || pow[(int) tmp[0]][i] == Integer.MAX_VALUE) {
                    continue;
                }
                if (tmp[1] + pow[(int) tmp[0]][i] < dp[i][s]) {
                    dp[i][s] = (tmp[1] + pow[(int) tmp[0]][i]);
                    pq.add(new long[]{i, dp[i][s]});
                }
            }
        }
    }
}

