package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WBTest {

    private static Map<String, Map<String, Integer>> graph;

    @BeforeEach
    public void setup() {
        graph = new HashMap<>();

        graph.put("life", new HashMap<>());
        graph.get("life").put("to", 2);

        graph.put("to", new HashMap<>());
        graph.get("to").put("explore", 1);
        graph.get("to").put("seek", 3);

        graph.put("explore", new HashMap<>());
        graph.get("explore").put("with", 2);

        graph.put("with", new HashMap<>());
        graph.get("with").put("new", 1);

        graph.put("seek", new HashMap<>());
        graph.get("seek").put("new", 4);
        graph.get("seek").put("world", 3);

        graph.put("world", new HashMap<>());
        graph.get("world").put("to", 1);

        graph.put("new", new HashMap<>());

    }

    @Test
    public void test1() {
        // 测试路径最短还是节点最少。
        String result = calcShortestPath("to", "new");
        assertEquals("""
                Shortest path from to to new:
                Path: to -> explore -> with -> new
                Length: 4""", result);
    }

    @Test
    public void test2() {
        // 测试无出边的节点是否存在到其他节点的路径。
        String result = calcShortestPath("new", "to");
        assertEquals("No path from new to to!", result);
    }

    @Test
    public void test3() {
        // 测试仅有入边的节点是否存在到指向他的节点的路径。
        String result = calcShortestPath("to", "life");
        assertEquals("No path from to to life!", result);
    }

    @Test
    public void test4() {
        // 测试word1不在图中的情况。
        String result = calcShortestPath("and", "with");
        assertEquals("No and in the graph!", result);
    }

    @Test
    public void test5() {
        // 测试word2不在图中的情况。
        String result = calcShortestPath("with", "or");
        assertEquals("No or in the graph!", result);
    }

    @Test
    public void test6() {
        // 测试到自己本身的路径情况。
        String result = calcShortestPath("to", "to");
        assertEquals("""
                Shortest path from to to to:
                Path: to
                Length: 0""", result);
    }

    // 定义一个类来封装路径和距离
    public static class PathInfo {
        List<String> path;
        int distance;

        public PathInfo(List<String> path, int distance) {
            this.path = path;
            this.distance = distance;
        }
    }

    public static String calcShortestPath(String word1, String word2) {
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No " + (!graph.containsKey(word1) ? word1 : word2) + " in the graph!";
        }

        Map<String, PathInfo> shortestPaths = Dijkstra(word1);
        PathInfo pathInfo = shortestPaths.get(word2);
        if (pathInfo == null || pathInfo.path.isEmpty()) {
            return "No path from " + word1 + " to " + word2 + "!";
        }

        StringBuilder pathString = new StringBuilder("Shortest path from " + word1 + " to " + word2 + ":\n");
        pathString.append("Path: ").append(String.join(" -> ", pathInfo.path)).append("\n");
        pathString.append("Length: ").append(pathInfo.distance);

        return pathString.toString();
    }

    public static Map<String, PathInfo> Dijkstra(String start) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> nodes = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        Map<String, PathInfo> shortestPaths = new HashMap<>();

        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }
        distances.put(start, 0);
        nodes.add(start);

        while (!nodes.isEmpty()) {
            String current = nodes.poll();

            if (distances.get(current) == Integer.MAX_VALUE) {
                break;
            }

            Map<String, Integer> neighbors = graph.get(current);
            if (neighbors != null) {
                for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
                    String neighborNode = neighbor.getKey();
                    int newDist = distances.get(current) + neighbor.getValue();

                    if (distances.get(neighborNode) == null || newDist < distances.get(neighborNode)) {
                        distances.put(neighborNode, newDist);
                        previous.put(neighborNode, current);
                        nodes.add(neighborNode);
                    }
                }
            }
        }

        for (String node : graph.keySet()) {
            LinkedList<String> path = new LinkedList<>();
            for (String at = node; at != null; at = previous.get(at)) {
                path.addFirst(at);
            }
            if (path.size() == 1 && !path.getFirst().equals(start)) {
                path.clear(); // No path exists to this node
            }
            int distance = distances.get(node) == Integer.MAX_VALUE ? -1 : distances.get(node);
            shortestPaths.put(node, new PathInfo(path, distance));
        }

        return shortestPaths;
    }

//    public static String calcShortestPath(String word1, String word2) {
//        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
//            return "No " + (!graph.containsKey(word1) ? word1 : word2) + " in the graph!";
//        }
//
//        Map<String, List<String>> shortestPaths = Dijkstra(word1);
//        List<String> path = shortestPaths.get(word2);
//        if (path == null) {
//            return "No path from " + word1 + " to " + word2 + "!";
//        }
//
//        int length = path.size() - 1;
//
//        StringBuilder pathString = new StringBuilder("Shortest path from " + word1 + " to " + word2 + ":\n");
//        pathString.append("Path: ").append(String.join(" -> ", path)).append("\n");
//        pathString.append("Length: ").append(length);
//
//        return pathString.toString();
//    }
//
//    public static Map<String, List<String>> Dijkstra(String word) {
//        Map<String, List<String>> shortestPaths = new HashMap<>();
//        if (!graph.containsKey(word)) {
//            shortestPaths.put(word, new ArrayList<>());
//            return shortestPaths;
//        }
//
//        // Dijkstra's algorithm
//        Map<String, Integer> distances = new HashMap<>();
//        Map<String, String> previous = new HashMap<>();
//        PriorityQueue<String> nodes = new PriorityQueue<>(Comparator.comparingInt(distances::get));
//        Set<String> visited = new HashSet<>();
//
//        for (String node : graph.keySet()) {
//            distances.put(node, Integer.MAX_VALUE);
//        }
//        distances.put(word, 0);
//        nodes.add(word);
//
//        while (!nodes.isEmpty()) {
//            String current = nodes.poll();
//            if (visited.contains(current)) continue;
//            visited.add(current);
//
//            if (!current.equals(word)) {
//                LinkedList<String> path = new LinkedList<>();
//                for (String at = current; at != null; at = previous.get(at)) {
//                    path.addFirst(at);
//                }
//                shortestPaths.put(current, path);
//            }
//
//            Map<String, Integer> neighbors = graph.get(current);
//            if (neighbors == null) continue;
//            for (Map.Entry<String, Integer> neighbor : neighbors.entrySet()) {
//                String neighborNode = neighbor.getKey();
//                int edgeWeight = neighbor.getValue();
//                int newDist = distances.get(current) + edgeWeight;
//
//                distances.putIfAbsent(neighborNode, Integer.MAX_VALUE);
//
//                if (newDist < distances.get(neighborNode)) {
//                    distances.put(neighborNode, newDist);
//                    previous.put(neighborNode, current);
//                    nodes.add(neighborNode);
//                }
//            }
//        }
//
//        return shortestPaths;
//    }

}
