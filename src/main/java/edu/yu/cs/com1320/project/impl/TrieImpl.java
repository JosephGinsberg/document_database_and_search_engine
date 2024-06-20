package edu.yu.cs.com1320.project.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.yu.cs.com1320.project.Trie;

public class TrieImpl<Value> implements Trie<Value>{

    private static final int alphabetSize = 256;
    private Node<Value> root;

    private static class Node<Value> {
        protected Set<Value> values = new HashSet<Value>();
        protected Node<Value>[] links = new Node[alphabetSize];
    }

    public TrieImpl(){
        this.root = new Node<Value>();
    }




    
    private Node<Value> put(Node<Value> node, String key, Value val, int len){
        if (node == null){
            node = new Node<Value>();
        }
        if (len == key.length()){
            node.values.add(val);
            return node;
        }
        char thisChar = key.charAt(len);
        node.links[thisChar] = this.put(node.links[thisChar], key, val, len + 1);
        return node;
    }

    public void put(String key, Value val) {
        if (val == null) {
            return;
        }
        this.root = put(this.root, key, val, 0);
    }



    private List<Value> removeDupes(List<Value> list){
        Set<Value> tempSet = new HashSet<Value>(list);
        list = new ArrayList<Value>(tempSet);
        return list;
    }
    
    private List<Value> getSorted(Node<Value> node, String key, int len){
        if(key == null){
            throw new IllegalArgumentException("Null key");
        }
        if (node == null){
            List<Value> empty = new ArrayList<Value>();
            return empty;
        }
        if (len == key.length()){
            List<Value> toReturn = new ArrayList<Value>(node.values);
            return toReturn;
        }
        char thisChar = key.charAt(len);
        return this.getSorted(node.links[thisChar], key, len + 1);
    }

    public List<Value> getSorted(String key, Comparator<Value> comparator) {
        if(comparator == null){
            throw new IllegalArgumentException("Null comparator");
        }
        List<Value> toReturn = removeDupes(getSorted(this.root, key, 0));
        toReturn.sort(comparator);
        return toReturn;
    }




    
    private Set<Value> get(Node<Value> node, String key, int len){
        if(key == null){
            throw new IllegalArgumentException("Null key");
        }
        if (node == null){
            Set<Value> empty = new HashSet<Value>();
            return empty;
        }
        if (len == key.length()){
            return node.values;
        }
        char thisChar = key.charAt(len);
        return this.get(node.links[thisChar], key, len + 1);
    }

    public Set<Value> get(String key) {
        return this.get(this.root, key, 0);
    }




    
    private List<Value> getSubTree(Node<Value> node){
        List<Value> toReturn = new ArrayList<Value>();
        toReturn.addAll(node.values);
        for (int i = 0; i < alphabetSize; i++){
            if(node.links[i] != null){
                toReturn.addAll(getSubTree(node.links[i]));
            }
        }
        return toReturn;
    }

    private List<Value> getAllWithPrefixSorted(Node<Value> node, String key, int len){
        if(key == null){
            throw new IllegalArgumentException("Null key");
        }
        List<Value> toReturn = new ArrayList<Value>();
        if(key.equals("")){
            return toReturn;
        }
        if (node == null){
            return toReturn;
        }
        if (len == key.length()){
            toReturn.addAll(getSubTree(node));
        }
        else{
            char thisChar = key.charAt(len);
            toReturn.addAll(this.getAllWithPrefixSorted(node.links[thisChar], key, len + 1));
        }
        return toReturn;
    }

    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        if(comparator == null){
            throw new IllegalArgumentException("Null comparator");
        }
        List<Value> toReturn = removeDupes(getAllWithPrefixSorted(this.root, prefix, 0));
        toReturn.sort(comparator);
        return toReturn;
    }


    private Node<Value> checkToDelete(Node<Value> node){
        //this node has a val – do nothing, return the node
        if (node.values != null){
            return node;
        }
        //remove subtrie rooted at node if it is completely empty	
        for (int i = 0; i < alphabetSize; i++){
            if (node.links[i] != null){
                return node; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }

    
    private Node<Value> deleteSubTree(Node<Value> node){
        node.values = new HashSet<Value>();
        for (int i = 0; i < alphabetSize; i++){
            if(node.links[i] != null){
                node.links[i] = deleteSubTree(node.links[i]);
            }
        }
        return null;
    }

    private Node<Value> deleteAllWithPrefix(Node<Value> node, String prefix, int len){
        if (node == null){
            return null;
        }
        if (len == prefix.length()){
            deleteSubTree(node);
        }
        else{
            char thisChar = prefix.charAt(len);
            node.links[thisChar] = this.deleteAllWithPrefix(node.links[thisChar], prefix, len + 1);
        }
        return checkToDelete(node);
    }

    public Set<Value> deleteAllWithPrefix(String prefix) {
        if(prefix == null){
            throw new IllegalArgumentException("Null prefix");
        }
        Set<Value> toReturn = new HashSet<Value>(getAllWithPrefixSorted(this.root, prefix, 0));
        this.root = deleteAllWithPrefix(this.root, prefix, 0);
        return toReturn;
    }





    private Node<Value> deleteAll(Node<Value> node, String key, int len){
        if (node == null){
            return null;
        }
        if (len == key.length()){
            node.values = new HashSet<Value>();
        }
        else{
            char thisChar = key.charAt(len);
            node.links[thisChar] = this.deleteAll(node.links[thisChar], key, len + 1);
        }
        return checkToDelete(node);
    }

    public Set<Value> deleteAll(String key) {
        if(key == null){
            throw new IllegalArgumentException("Null key");
        }
        Set<Value> toReturn = get(key);
        this.root = deleteAll(this.root, key, 0);
        return toReturn;
    }






    private Node<Value> delete(Node<Value> node, String key, Value val, int len){
        if (node == null){
            return null;
        }
        if (len == key.length()){
            node.values.remove(val);
        }
        else{
            char thisChar = key.charAt(len);
            node.links[thisChar] = this.delete(node.links[thisChar], key, val, len + 1);
        }
        return checkToDelete(node);
    }

    public Value delete(String key, Value val) {
        if(key == null || val == null){
            throw new IllegalArgumentException("Null key or value");
        }
        Set<Value> gets = new HashSet<Value>(get(key));
        boolean contains = gets.contains(val);
        Value reference = null;
        for(Value v : gets){
            if(v.equals(val)){
                reference = v;
                break;
            }
        } 
        this.root = delete(this.root, key, val, 0);
        return contains ? reference : null;
    }
}
