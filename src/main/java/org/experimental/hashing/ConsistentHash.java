package org.experimental.hashing;

import java.util.*;

public class ConsistentHash {

    private static List<String> realNodes = new LinkedList<String>();
    private static SortedMap<Integer, String> virtualNodes = new TreeMap<Integer, String>();

    private static final int VIRTUAL_NODES = 5;


    public ConsistentHash(String[] nodes) {

        // Add the original server to the real node list first
        for (int i = 0; i < nodes.length; i++)
            realNodes.add(nodes[i]);


        for (String node : nodes) {
            add(node);
        }
    }

    public void add(String node) {
        for (int i = 0; i <VIRTUAL_NODES; i++) {
            String virtualNodeName = node + "&&VN" + String.valueOf(i);
            int hash = getHashFnv1(virtualNodeName);

            System.out.println("virtual node [" + virtualNodeName + "] is added, hash value is " + hash);
            virtualNodes.put(hash, virtualNodeName);
        }
    }

//    public void remove(T node) {
//        for (int i = 0; i <VIRTUAL_NODES; i++) {
//            circle.remove(getHashFnv1(node.toString() + i));
//        }
//    }

    public String get(String key) {
        if (virtualNodes.isEmpty()) {
            return null;
        }

        int hash = getHashFnv1(key);
        // get all the maps larger than the hash value
        SortedMap<Integer, String> subMap =
                virtualNodes.tailMap(hash);
        // The first key is the node that is clockwise past the node.
        Integer i = subMap.firstKey();
        // return the corresponding virtual node name, where the string is slightly intercepted
        String virtualNode = subMap.get(i);
        return virtualNode.substring(0, virtualNode.indexOf("&&"));
    }

    private static int getHashFnv1(String str)
    {
        final int p = 16777619;
        int hash = (int)2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // If the calculated value is negative, take its absolute value
        if (hash < 0)
            hash = Math.abs(hash);

        return hash;
    }

    public void whatDoIHave(){

        for(String n : realNodes){
            int start = 0;

            System.out.println("Server: " + n);

            for (Map.Entry<Integer, String> entry : virtualNodes.entrySet()) {
                //System.out.println("Key: " + entry.getKey() + ". Value: " + entry.getValue());

                if(entry.getValue().contains(n)){
                    int diff = entry.getKey() - start;
                    int lo = start;
                    int hi = entry.getKey();
                    System.out.println(String.format("\t Vn %s [%12d,%12d] %10d", entry.getValue(), lo, hi, diff));


                }

                start = entry.getKey();
            }
        }
    }
}
