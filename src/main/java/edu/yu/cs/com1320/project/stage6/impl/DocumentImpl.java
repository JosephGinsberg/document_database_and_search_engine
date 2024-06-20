package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.stage6.Document;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DocumentImpl implements Document{

    private HashMap<String, String> metadata;
    private URI uri;
    private String text;
    private byte[] binaryData;
    private HashMap<String, Integer> wordMap;
    private long lastUseTime;

    public DocumentImpl(URI uri, String txt, Map<String, Integer> wordCountMap){
        if(uri == null || uri.toString().isBlank()){
            throw new IllegalArgumentException("Null or empty uri");
        }
        if(txt == null || txt.isBlank()){
            throw new IllegalArgumentException("Null or empty text");
        }
        this.uri = uri;
        this.text = txt;
        this.metadata = new HashMap<String, String>();
        if(wordCountMap != null){
            this.wordMap = new HashMap<String, Integer>(wordCountMap);
        }
        else{
            this.wordMap = new HashMap<String, Integer>();
            wordMapper(txt);
        }
        this.lastUseTime = System.nanoTime();
    }

    private void wordMapper(String txt){
        int len = txt.length();
        String toReturn = "";
        for(int i = 0; i < len; i++){
            char thischar = txt.charAt(i);
            if(thischar == ' ' || Character.isLetterOrDigit(thischar)){
                toReturn += thischar;
            }
        }
        toReturn = toReturn.trim().replaceAll("\\s+"," ");
        String[] words = toReturn.split(" ");
        for(String s : words){
            Integer count = 0;
            if(this.wordMap.get(s) != null){
                count = this.wordMap.get(s);
            }
            count++;
            this.wordMap.put(s, count);
        }
    }

    public DocumentImpl(URI uri, byte[] binaryData){
        if(uri == null || uri.toString().isBlank()){
            throw new IllegalArgumentException("Null or empty uri");
        }
        if(binaryData == null || binaryData.length == 0){
            throw new IllegalArgumentException("Null or empty binary data");
        }
        this.uri = uri;
        this.binaryData = binaryData;
        this.metadata = new HashMap<String, String>();
        this.wordMap = new HashMap<String, Integer>();
        this.lastUseTime = System.nanoTime();
    }

    @Override 
    public int hashCode() { 
        int result = this.uri.hashCode(); 
        result = 31 * result + (this.text != null ? text.hashCode() : 0); 
        result = 31 * result + Arrays.hashCode(this.binaryData); 
        return Math.abs(result); 
    }

    @Override 
    public boolean equals(Object o){
        if(!(o instanceof Document)){
            return false;
        }
        return (this.hashCode() == o.hashCode());
    }

    public String setMetadataValue(String key, String value){
        if(key == null || key.isBlank()){
            throw new IllegalArgumentException("Invalid Key");
        }
        return this.metadata.put(key, value);
    }

    public String getMetadataValue(String key){
        if(key == null || key.isBlank()){
            throw new IllegalArgumentException("Invalid Key");
        }
        return metadata.get(key);
    }

    public HashMap<String, String> getMetadata(){
        if(this.metadata == null){
            return null;
        }
        HashMap<String, String> shallow = new HashMap<String, String>();
        for(String key : this.metadata.keySet()){
            shallow.put(key, metadata.get(key));
        }
        return shallow;
    }

    public void setMetadata(HashMap<String, String> metadata){
        this.metadata = metadata;
    }

    public String getDocumentTxt(){
        String shallow = this.text;
        if(shallow == null){
            return null;
        }
        return shallow;
    }

    public byte[] getDocumentBinaryData(){
        byte[] shallow = this.binaryData;
        if(shallow == null){
            return null;
        }
        return shallow.clone();
    }

    public URI getKey(){
        URI shallow = this.uri;
        return shallow;
    }

    public int wordCount(String word) {
        if(this.binaryData != null){
            return 0;
        }
        else if(this.wordMap.get(word) == null){
            return 0;
        }
        else {
            return this.wordMap.get(word);
        }
    }

    public Set<String> getWords() {
       return this.wordMap.keySet(); 
    }

    public long getLastUseTime(){
        return this.lastUseTime;
    }

    public void setLastUseTime(long timeInNanoseconds){
        this.lastUseTime = timeInNanoseconds;
    }

    public HashMap<String, Integer> getWordMap(){
        return this.wordMap;
    }

    public void setWordMap(HashMap<String, Integer> wordMap){
        this.wordMap = wordMap;
    }

    public int compareTo(Document D) {
        if(this.lastUseTime > D.getLastUseTime()){
            return 1;
        }
        else if(this.getLastUseTime() < D.getLastUseTime()){
            return -1;
        }
        else{
            return 0;
        }
    }

}
