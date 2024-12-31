/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author llucc
 */
public class Heuristica {
    private static Heuristica instance;
    public int[][][] taulaHash;
    Map<Integer, Point> millorJugada = new HashMap<>();
    Set<Integer> pruned = new HashSet<>();
    
    public int heuristica(Map<Point, Map<Point, Integer>> graf1, Map<Point, Map<Point, Integer>> graf2, Point ini, Point end) {
        int dij1 = dijkstra(graf1, ini, end);
        int dij2 = dijkstra(graf2, ini, end);

        //System.out.println(dij1 + ":" + dij2);
        return (1000-dij1)-(1000-dij2);
    }

    public int dijkstra(Map<Point, Map<Point, Integer>> grafMap, Point start, Point target) {
        Map<Point, Integer> distances = new HashMap<>();
        PriorityQueue<Point> PQ = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        Set<Point> visited = new HashSet<>();
        
        distances.put(start, 0);
        PQ.add(start);

        while (!PQ.isEmpty()) {
            Point currentP = PQ.poll();
            if (currentP.equals(target)) {
                /*for (int i = 0; i < 11; i++) {
                    for(int s=0; s<i;s++){System.out.print(" ");}
                    for (int j = 0; j < 11; j++) {
                        System.out.print(distances.get(new Point(j, i)) + " ");
                    }
                    System.out.println();
                }
                System.out.println("a");*/
                return distances.get(currentP);
            }

            if (!visited.contains(currentP)) {
                visited.add(currentP);
                Map<Point, Integer> ngbs = grafMap.get(currentP);
                if (ngbs != null) {
                    for (Entry<Point, Integer> ngb : ngbs.entrySet()) {
                        Point ngbP = ngb.getKey();
                        int dist = ngb.getValue();

                        if (!visited.contains(ngbP)) {
                            int newDist = distances.get(currentP) + dist;
                            if (newDist < distances.getOrDefault(ngbP, Integer.MAX_VALUE)) {
                                distances.put(ngbP, newDist);
                                PQ.add(ngbP);
                            }
                        }
                    }
                }
            }
        }

        return -1;
    }

    public List<MoveNode> obtenerJugadas(MyStatus h) {
        List<MoveNode> l = h.getMoves();
        if(!pruned.contains(h.hash) && millorJugada.containsKey(h.hash)){
            l.remove(new MoveNode(new Point(millorJugada.get(h.hash))));
            l.addFirst(new MoveNode(new Point(millorJugada.get(h.hash))));
        }
        return l;
    }
    
    public static int[][][] createHashingTable(int size){
        int[][][] table = new int[size][size][3];
        Random ran = new Random();
        for(int i = 0; i<size; i++){
            for(int j = 0; j<size; j++){
                for(int z = 0; z<3; z++){
                    table[i][j][z] = ran.nextInt();
                }
            }
        }
        return table;
    }
    
    public static Heuristica getInstance(){
        if (instance == null){
            instance = new Heuristica();
        }
        return instance;
    }
}