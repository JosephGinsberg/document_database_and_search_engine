package edu.yu.cs.com1320.project.stage6.impl;

import edu.yu.cs.com1320.project.stage6.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DocumentPersistenceManager implements PersistenceManager<URI, Document>  {

    File directory;

    private class docSerializer implements JsonSerializer<Document> {
        public JsonElement serialize(Document doc, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("uri", doc.getKey().toString());
            if(doc.getDocumentTxt() != null){
                json.addProperty("textcontent", doc.getDocumentTxt());
            }
            else if(doc.getDocumentBinaryData() != null){
                String encoded = DatatypeConverter.printBase64Binary(doc.getDocumentBinaryData());
                json.addProperty("binarycontent", encoded);
            }
            JsonObject metadata = new JsonObject();
            for(String key : doc.getMetadata().keySet()){
                metadata.addProperty(key, doc.getMetadataValue(key));
            }
            json.add("metadata", metadata);
            JsonObject wordmap = new JsonObject();
            for(String word : doc.getWords()){
                wordmap.addProperty(word, doc.getWordMap().get(word));
            }
            json.add("wordmap", wordmap);
            return json;
        }
    }


    private class docDeserializer implements JsonDeserializer<Document> {
        public Document deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context){
            JsonObject doc = json.getAsJsonObject();
            String uri = adjustOutput(doc, "uri");
            JsonObject metadataJson = doc.getAsJsonObject("metadata");
            HashMap<String, String> metadataMap = new HashMap<String, String>();
            for(String key : metadataJson.keySet()){
                metadataMap.put(key, adjustOutput(metadataJson, key));
            }
            if(doc.get("textcontent") != null){
                String content = adjustOutput(doc, "textcontent");
                JsonObject wordmapJson = doc.getAsJsonObject("wordmap");
                HashMap<String, Integer> wordmapMap = new HashMap<String, Integer>();
                for(String key : wordmapJson.keySet()){
                    wordmapMap.put(key,  Integer.parseInt(wordmapJson.get(key).toString()));
                }
                try {
                    DocumentImpl document = new DocumentImpl(new URI(uri), content, wordmapMap);
                    document.setMetadata(metadataMap);
                    return document; 
                } catch (URISyntaxException e) {
                    System.out.println("JSON uri field does not match Java URI scheme");
                    return null;
                }
            }
            else if(doc.get("binarycontent") != null){
                byte[] bytes = DatatypeConverter.parseBase64Binary(doc.get("binarycontent").toString());
                try {
                    DocumentImpl document = new DocumentImpl(new URI(uri), bytes);
                    document.setMetadata(metadataMap);
                    return document; 
                } catch (URISyntaxException e) {
                    System.out.println("JSON uri field does not match Java URI scheme");
                    return null;
                }
            }     
            return null;
        }

        private String adjustOutput(JsonObject doc, String input){
            if(doc.get(input) != null){
                String get = doc.get(input).toString();
                get = get.substring(1, get.length() - 1);
                return get;
            }
            return null;
        }
    }

    public DocumentPersistenceManager(File baseDir){
        if(baseDir == null){
            this.directory = new File(System.getProperty("user.dir"));
        }
        else{
            this.directory = baseDir;
        }
    }

    private File cleanURIFile(URI uri){
        String uris = uri.toString();
        uris = uris.replaceFirst("http://", "");
        uris = uris.replaceFirst("http:/", "");
        uris = uris.replaceFirst("https://", "");
        uris = uris.replaceFirst("https:/", "");
        if(uris.substring(uris.length() - 1, uris.length()).equals(File.separator)){
            uris = uris.substring(0, uris.length() - 1);
        }
        File path = new File(this.directory + File.separator + uris + ".json");
        File parent = new File(path.getParent());
        parent.mkdirs();
        return path;
    }

    public void serialize(URI uri, Document document) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(DocumentImpl.class, new docSerializer()).create();
        String json = gson.toJson(document);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(cleanURIFile(uri)));
        bufferedWriter.write(json);
        bufferedWriter.close();
    }

    public Document deserialize(URI uri) throws IOException {
        Gson gson = new GsonBuilder().registerTypeAdapter(DocumentImpl.class, new docDeserializer()).create();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(cleanURIFile(uri)));
        DocumentImpl doc = gson.fromJson(bufferedReader, DocumentImpl.class);
        bufferedReader.close();
        this.delete(uri);
        return doc;
    }

     /**
     * delete the file stored on disk that corresponds to the given key
     * @param key
     * @return true or false to indicate if deletion occured or not
     * @throws IOException
     */
    public boolean delete(URI uri) throws IOException {
        boolean deleted = cleanURIFile(uri).delete();
        deleteUP(cleanURIFile(uri));
        return deleted;
    }

    private void deleteUP(File file){
        File parent = file.getParentFile();
        boolean deleted = false;
        for(int i = 0; i < 1000; i++){
            deleted = (parent.list().length == 0);
        }
        if(parent.isDirectory() && deleted) {
            parent.delete();
            deleteUP(parent);
        } 
    }
    
}
