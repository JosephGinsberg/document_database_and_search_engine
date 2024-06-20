package edu.yu.cs.com1320.project.stage6.impl;

import java.io.*;
import java.net.URI;
import java.util.*;

import edu.yu.cs.com1320.project.stage6.*;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.undo.*;

public class DocumentStoreImpl implements DocumentStore{
    
    private DocumentPersistenceManager manager;
    private BTreeImpl<URI, Document> store;
    private StackImpl<Undoable> commandStack;
    private TrieImpl<complexURI> docTrie;
    private TrieImpl<HashMap<String, complexURI>> metadataTrie;
    private HashSet<String> metadataKeys;
    private MinHeapImpl<complexURI> docHeap;
    private HashSet<complexURI> inMemory;
    private int maxDocumentCount;
    private int maxDocumentBytes;

    private class complexURI implements Comparable<complexURI>{

        URI uri;

        private complexURI(URI uri){
            this.uri = uri;
        }

        private URI getURI(){
            return this.uri;
        }

        private Document getDoc(){
            return getAndAdjust(this.uri);
        }

        @Override
        public boolean equals(Object o){
            if (o == this) {
                return true;
            }
            if (!(o instanceof complexURI)) {
                return false;
            }
            complexURI other = (complexURI) o;
            if(this.uri.equals(other.getURI())){
                return true;
            }
            else{
                return false;
            }
        }

        @Override
        public int hashCode() {
            return this.uri.hashCode();
        }

        public int compareTo(complexURI c) {
            if(this.getDoc().getLastUseTime() > c.getDoc().getLastUseTime()){
                return 1;
            }
            else if(this.getDoc().getLastUseTime() < c.getDoc().getLastUseTime()){
                return -1;
            }
            else{
                return 0;
            }
        }
    }

    //CONSTRUCTORS


    public DocumentStoreImpl(){
        this.manager = new DocumentPersistenceManager(null);
        this.store = new BTreeImpl<URI, Document>();
        this.store.setPersistenceManager(manager);
        this.commandStack = new StackImpl<Undoable>();
        this.docTrie = new TrieImpl<complexURI>();
        this.metadataTrie = new TrieImpl<HashMap<String, complexURI>>();
        this.metadataKeys = new HashSet<String>();
        this.docHeap = new MinHeapImpl<complexURI>();
        this.inMemory = new HashSet<complexURI>();
        this.maxDocumentCount = Integer.MAX_VALUE;
        this.maxDocumentBytes = Integer.MAX_VALUE;
    }

    public DocumentStoreImpl(File baseDir){
        this.manager = new DocumentPersistenceManager(baseDir);
        this.store = new BTreeImpl<URI, Document>();
        this.store.setPersistenceManager(manager);
        this.commandStack = new StackImpl<Undoable>();
        this.docTrie = new TrieImpl<complexURI>();
        this.metadataTrie = new TrieImpl<HashMap<String, complexURI>>();
        this.metadataKeys = new HashSet<String>();
        this.docHeap = new MinHeapImpl<complexURI>();
        this.inMemory = new HashSet<complexURI>();
        this.maxDocumentCount = Integer.MAX_VALUE;
        this.maxDocumentBytes = Integer.MAX_VALUE;
    }

    //MEMORY MANAGMENT

    public void setMaxDocumentCount(int limit){
        if(limit < 1){
            throw new IllegalArgumentException("Memory limit for documents cannot be less than 1");
        }
        this.maxDocumentCount = limit;
        try {
            fixHeap();
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void setMaxDocumentBytes(int limit){
        if(limit < 1){
            throw new IllegalArgumentException("Memory limit for bytes cannot be less than 1");
        }
        this.maxDocumentBytes = limit;
        try {
            fixHeap();
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private int getByteData(Document D){
        if(D.getDocumentTxt() != null){
            return D.getDocumentTxt().getBytes().length;
        }
        else if(D.getDocumentTxt() != null){
            return D.getDocumentBinaryData().length;
        }
        return 0;
    }

    private int getAllBytes(){
        int size = 0;
        for(complexURI u : this.inMemory){
            if(u.getDoc() != null){
                size += this.getByteData(u.getDoc());
            }
        }
        return size;
    }

    private boolean memoryIssues(){
        if(this.inMemory.size() > this.maxDocumentCount || this.getAllBytes() > this.maxDocumentBytes){
            return true;
        }
        return false;
    }

    private void addtoMemory(URI uri){
        this.inMemory.add(new complexURI(uri));
        this.docHeap.insert(new complexURI(uri));
        this.store.get(uri);
    }

    private void fixHeap() throws IOException{
        while(memoryIssues()){
            complexURI removed = this.docHeap.remove();
            URI uri = removed.getURI();
            this.store.moveToDisk(uri);
            this.inMemory.remove(removed);
            //Undo functionality to add to inMemory and remove from disk
            this.commandStack.push(new GenericCommand<URI>(uri, uri2 -> addtoMemory(uri)));
        }
    }

    //HEAP ADJUSTMENT

    private void adjustTimeAndHeap(Document D){
        if(D != null){
            D.setLastUseTime(System.nanoTime());
            this.docHeap.reHeapify(new complexURI(D.getKey())); //relying on overidden equals method
        }
    }

    private void adjustTimingsAndHeap(List<Document> L){
        for(Document d : L){
            adjustTimeAndHeap(d);
        }
    }


    //GETTERS

    private void removeFromMemory(URI uri){
        try {
            this.inMemory.remove(new complexURI(uri));
            this.deleteFromHeap(new complexURI(uri));
            this.store.moveToDisk(uri);
        }
        catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    private Document getAndAdjust(URI url){
        complexURI thisuri = new complexURI(url);
        boolean inmem = inMemory.contains(thisuri);
        Document doc = this.store.get(url);
        if(doc != null){
            if(!inmem){
                this.inMemory.add(thisuri);
                this.docHeap.insert(thisuri);
                adjustTimeAndHeap(doc);
                try {
                    fixHeap();
                }
                catch (IOException e) {
                    throw new RuntimeException();
                }
                //Undo with write to disk and remove from inMemory
                this.commandStack.push(new GenericCommand<URI>(url, uri2 -> removeFromMemory(url)));
            }
        }
        return doc;
    }

    public Document get(URI url) throws IOException{
        Document doc = this.getAndAdjust(url);
        if(doc != null){
            this.adjustTimeAndHeap(doc);
        }
        return doc;
    }

    public String getMetadata(URI uri, String key) throws IOException{
        if(uri == null || uri.toString().isBlank()){
            throw new IllegalArgumentException("Null or empty uri");
        }
        if(key == null || key.toString().isBlank()){
            throw new IllegalArgumentException("Null or empty key");
        }
        Document current = this.get(uri);
        if(current == null){
            throw new IllegalArgumentException("Invalid Document key");
        }
        return current.getMetadataValue(key);
    }

    private class docComparator implements Comparator<complexURI> {

        private String keyword;

        public docComparator(String keyword){
            this.keyword = keyword;
        }
 
        @Override
        public int compare(complexURI curi1, complexURI curi2){
            int times1 = curi1.getDoc().wordCount(keyword);
            int times2 = curi2.getDoc().wordCount(keyword);
            if(times1 > times2){
                return -1;
            }
            else if(times1 < times2){
                return 1;
            }
            else{
                return 0;
            }
        }
    }

    private List<Document> curisToDocList(List<complexURI> list){
        ArrayList<Document> docs = new ArrayList<Document>();
        for(complexURI c : list){
            docs.add(c.getDoc());
        }
        return docs;
    }

    private HashSet<Document> curisToDocSet(Set<complexURI> list){
        HashSet<Document> docs = new HashSet<Document>();
        for(complexURI c : list){
            docs.add(c.getDoc());
        }
        return docs;
    }

    private List<Document> searchPrivate(String keyword) {
        return curisToDocList(this.docTrie.getSorted(keyword, new docComparator(keyword)));
    }

    public List<Document> search(String keyword) throws IOException{
        List<Document> searched = searchPrivate(keyword);
        adjustTimingsAndHeap(searched);
        return searched;

    }

    private List<Document> searchByPrefixPrivate(String keywordPrefix) {
        return curisToDocList(this.docTrie.getAllWithPrefixSorted(keywordPrefix, new docComparator(keywordPrefix)));
    }

    public List<Document> searchByPrefix(String keywordPrefix) throws IOException{
        List<Document> searched = searchByPrefixPrivate(keywordPrefix);
        adjustTimingsAndHeap(searched);
        return searched;
    }

    private List<Document> searchByMetadataPrivate(Map<String, String> keysValues) {
        if(keysValues.size() == 0){
            List<Document> toReturn = new ArrayList<Document>();
            return toReturn;
        }
        List<String> metadataKeysList = new ArrayList<String>(this.metadataKeys);
        List<complexURI> curis = retainer(metadataKeysList, 0, keysValues);
        return curisToDocList(curis);
    }

    private List<complexURI> retainer(List<String> list, int index, Map<String, String> keysValues) {
        List<complexURI> toReturn = new ArrayList<complexURI>();
        if(list.size() == 0 || list.size() < index){
            return toReturn;
        }
        String key = list.get(index);
        for(HashMap<String, complexURI> values : this.metadataTrie.get(key)){
            String value = keysValues.get(key);
            if(value != null && values.keySet().contains(value)){
                toReturn.add(values.get(value));
            }
        }
        if(index == list.size() - 1){
            return toReturn;
        }
        else{
            toReturn.retainAll(retainer(list, ++index, keysValues));
            return toReturn;
        }
    }

    public List<Document> searchByMetadata(Map<String, String> keysValues) throws IOException{
        List<Document> toReturn = searchByMetadataPrivate(keysValues);
        adjustTimingsAndHeap(toReturn);
        return toReturn;
    }

    public List<Document> searchByKeywordAndMetadata(String keyword, Map<String, String> keysValues) throws IOException{
        List<Document> keywordList = this.searchPrivate(keyword);
        List<Document> metadataList = this.searchByMetadataPrivate(keysValues);
        keywordList.retainAll(metadataList);
        adjustTimingsAndHeap(keywordList);
        return keywordList;
    }

    public List<Document> searchByPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) throws IOException{
        List<Document> keywordPrefixList = this.searchByPrefixPrivate(keywordPrefix);
        List<Document> metadataList = this.searchByMetadataPrivate(keysValues);
        keywordPrefixList.retainAll(metadataList);
        adjustTimingsAndHeap(keywordPrefixList);
        return keywordPrefixList;
    }


    //SETTERS

 
    public String setMetadata(URI uri, String key, String value) throws IOException{
        if(uri == null || uri.toString().isBlank()){
            throw new IllegalArgumentException("Null or empty uri");
        }
        if(key == null || key.toString().isBlank()){
            throw new IllegalArgumentException("Null or empty key");
        }
        Document current = this.get(uri); //adjusts timing as well
        if(current == null){
            throw new IllegalArgumentException("Invalid Document key");
        }
        String oldValue = current.getMetadataValue(key);
        this.commandStack.push(new GenericCommand<URI>(uri, uri2 -> replaceMetadataPrivate(current, uri, key, oldValue, value)));
        return replaceMetadataPrivate(current, uri, key, value, oldValue);
    }

    private String putMetadataPrivate(Document doc, URI uri, String key, String value){
        if(doc != null){
            HashMap<String, complexURI> metadata = new HashMap<String, complexURI>();
            metadata.put(value, new complexURI(uri));
            this.metadataTrie.put(key, metadata);
            this.metadataKeys.add(key);
            return doc.setMetadataValue(key, value);
        }
        return null;
    } 


    private void deleteMetadataPrivate(Document doc, URI uri, String key, String value){
        if(doc != null){
            Set<HashMap<String, complexURI>> toRemove = new HashSet<HashMap<String, complexURI>>(this.metadataTrie.get(key));
            for(HashMap<String, complexURI> map : toRemove){
                if(map.keySet().contains(value) && map.values().contains(new complexURI(uri))){
                    this.metadataTrie.delete(key, map);
                }
            }
            if(this.metadataTrie.get(key).size() == 0){
                this.metadataKeys.remove(key);
            }
        }
    } 

    private String replaceMetadataPrivate(Document doc, URI uri, String key, String value, String old){
        deleteMetadataPrivate(doc, uri, key, old);
        return putMetadataPrivate(doc, uri, key, value);
    }


    
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException{
        if(format == null || uri == null || uri.toString().isBlank()){
            throw new IllegalArgumentException("Invalid Document info");
        }
        Document current = this.getAndAdjust(uri);
        if(input == null){
            return this.delete(uri) ? current.hashCode() : 0;
        }
        byte[] byteArray = input.readAllBytes();
        if(byteArray.length > this.maxDocumentBytes){
            throw new IllegalArgumentException("Document size too large");
        }
        DocumentImpl newDoc;
        switch (format) {
            case TXT:
                newDoc = new DocumentImpl(uri, new String(byteArray), null);
                this.commandStack.push(new GenericCommand<URI>(uri, uri2 -> replaceInAll(uri, current, newDoc)));
                newDoc.setLastUseTime(System.nanoTime());
                replaceInAll(uri, newDoc, current);
                break;
            case BINARY:
                newDoc = new DocumentImpl(uri, byteArray);
                this.commandStack.push(new GenericCommand<URI>(uri, uri2 -> replaceInAll(uri, current, newDoc)));
                newDoc.setLastUseTime(System.nanoTime());
                replaceInAll(uri, newDoc, current);
                break;
        }
        return (current != null) ? current.hashCode() : 0;
    }


    //DELETERS

    private void deleteFromDocTrie(Document doc){
        if(doc != null){
            Set<String> replaceWords = doc.getWords();
            for(String s : replaceWords){
                this.docTrie.delete(s, new complexURI(doc.getKey()));
            }
        }
    }

    private void putInDocTrie(URI uri, Document doc){
        if(doc != null){
            Set<String> words = doc.getWords();
            for(String s : words){
                this.docTrie.put(s, new complexURI(uri));
            }
        }
    }

    private void replaceInDocTrie(URI uri, Document doc, Document toReplace){
        if(toReplace != null){
            deleteFromDocTrie(toReplace);
        }
        putInDocTrie(uri, doc);
    }

    private void deleteFromMetadataTrie(Document doc){
        if(doc != null){
            complexURI uri = new complexURI(doc.getKey());
            Set<String> keys = new HashSet<String>(this.metadataKeys);
            for(String key : keys){
                Set<HashMap<String, complexURI>> toRemove = new HashSet<HashMap<String, complexURI>>(this.metadataTrie.get(key));
                for(HashMap<String, complexURI> map : toRemove){
                    if(map.values().contains(uri)){
                        this.metadataTrie.delete(key, map);
                    }
                }
                if(this.metadataTrie.get(key).size() == 0){
                    this.metadataKeys.remove(key);
                }
            }
        }
    }

    private void putInMetadataTrie(URI uri, Document doc){
        if(doc != null){
            HashMap<String, String> metadata = doc.getMetadata();
            for(String key : metadata.keySet()){
                HashMap<String, complexURI> valuetoURI = new HashMap<String, complexURI>();
                valuetoURI.put(metadata.get(key), new complexURI(uri));
                this.metadataTrie.put(key, valuetoURI);
                this.metadataKeys.add(key);
            }
        }
    }

    private void replaceInMetadataTrie(URI uri, Document doc, Document toReplace){
        if(toReplace != null){
            deleteFromMetadataTrie(toReplace);
        }
        putInMetadataTrie(uri, doc);
    }


    private void transferTopHeap(MinHeapImpl<complexURI> heap1, MinHeapImpl<complexURI> heap2){
        complexURI topDoc = heap1.remove();
        heap2.insert(topDoc);
    }

    private boolean deleteFromHeap(complexURI C){
        MinHeapImpl<complexURI> tempHeap = new MinHeapImpl<complexURI>();
        int count = 0;
        boolean removed = false;
        while(this.docHeap.peek() != null && !this.docHeap.peek().equals(C)){
            transferTopHeap(this.docHeap, tempHeap);
            count++;
        }
        if(this.docHeap.peek() != null){
            docHeap.remove();
            removed = true;
        }
        for(int i = 0; i < count; i++){
            transferTopHeap(tempHeap, this.docHeap);
        }
        return removed;
    }

    private void putInHeap(URI uri, Document doc){
        if(doc != null){
            this.docHeap.insert(new complexURI(uri));
            this.docHeap.reHeapify(new complexURI(uri));
            try {
                fixHeap();
            }
            catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }

    private void replaceInHeap(URI uri, Document doc, Document toReplace){
        if(toReplace != null){
            deleteFromHeap(new complexURI(uri));
        }
        putInHeap(uri, doc);
    }

    private boolean deleteFromAll(URI uri, Document doc){
        boolean exists = (store.get(uri) != null); //no need to adjust since doc is deleted
        if(exists){
            deleteFromDocTrie(doc);
            deleteFromHeap(new complexURI(uri));
            deleteFromMetadataTrie(doc);
            this.inMemory.remove(new complexURI(uri));
        }
        return (store.put(uri, null) != null);
    }

    private void replaceInAll(URI uri, Document doc, Document toReplace){
        if(doc == null){
            this.inMemory.add(new complexURI(uri));
            replaceInHeap(uri, doc, toReplace);
            this.store.put(uri, doc);
        }
        else{
            this.store.put(uri, doc);
            this.inMemory.add(new complexURI(uri));
            replaceInHeap(uri, doc, toReplace);
        }
        replaceInDocTrie(uri, doc, toReplace);
        replaceInMetadataTrie(uri, doc, toReplace);
    }

    public boolean delete(URI url){
        Document current = this.getAndAdjust(url);
        GenericCommand<URI> potentialFrame = new GenericCommand<URI>(url, url2 -> replaceInAll(url, current, null));
        boolean deleted = deleteFromAll(url, current);
        if(deleted){
            commandStack.push(potentialFrame);
        }
        return deleted;
    }





    private Set<URI> deleteDocs(Set<Document> toDelete){
        Set<URI> toReturn = new HashSet<URI>();
        CommandSet<URI> frame = new CommandSet<URI>();
        for(Document d : toDelete){
            URI thisURI = d.getKey();
            GenericCommand<URI> potentialCommand = new GenericCommand<URI>(thisURI, uri2 -> replaceInAll(thisURI, d, null));
            if(deleteFromAll(thisURI, d)){
                toReturn.add(thisURI);
                frame.addCommand(potentialCommand);
            }
        }
        if(frame.size() != 0){
            commandStack.push(frame);
        }
        return toReturn;
    }

    public Set<URI> deleteAll(String keyword) {
        if(keyword == null){
            throw new IllegalArgumentException("Cannot delete null keyword");
        }
        Set<Document> toDelete = curisToDocSet(this.docTrie.deleteAll(keyword));
        return deleteDocs(toDelete);
    }

    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        if(keywordPrefix == null){
            throw new IllegalArgumentException("Cannot delete null keyword prefix");
        }
        Set<Document> toDelete =  curisToDocSet(this.docTrie.deleteAllWithPrefix(keywordPrefix));
        return deleteDocs(toDelete);
    }

    public Set<URI> deleteAllWithMetadata(Map<String, String> keysValues) throws IOException{
        List<Document> toDelete = this.searchByMetadata(keysValues);
        HashSet<Document> setToDelete = new HashSet<Document>(toDelete);
        return deleteDocs(setToDelete);
    }

    public Set<URI> deleteAllWithKeywordAndMetadata(String keyword, Map<String, String> keysValues) throws IOException{
        List<Document> toDelete = this.searchByKeywordAndMetadata(keyword, keysValues);
        HashSet<Document> setToDelete = new HashSet<Document>(toDelete);
        return deleteDocs(setToDelete);
    }

    public Set<URI> deleteAllWithPrefixAndMetadata(String keywordPrefix, Map<String, String> keysValues) throws IOException{
        List<Document> toDelete = this.searchByPrefixAndMetadata(keywordPrefix, keysValues);
        HashSet<Document> setToDelete = new HashSet<Document>(toDelete);
        return deleteDocs(setToDelete);
    }

    

    //UNDOS

    
    public void undo() throws IllegalStateException{
        if(this.commandStack.peek() != null){
            this.commandStack.pop().undo();
        }
        else{
            throw new IllegalStateException("Empty stack, cannot undo");
        }
    }

    private void transferTop(StackImpl<Undoable> stack1, StackImpl<Undoable> stack2){
        Undoable topCommand = stack1.pop();
        stack2.push(topCommand);
    }

    private boolean genericMatch(Undoable currentCommand, URI url){
        return currentCommand instanceof GenericCommand && ((GenericCommand<URI>) currentCommand).getTarget().equals(url);
    }

    private boolean setMatch(Undoable currentCommand, URI url){
        return currentCommand instanceof CommandSet && ((CommandSet<URI>) currentCommand).containsTarget(url);
    }

    private boolean uriMatch(Undoable currentCommand, URI url){
        return  genericMatch(currentCommand, url) || setMatch(currentCommand, url);
    }

    private boolean properUndo(URI url){
        Undoable currentCommand = this.commandStack.peek();
        if(currentCommand == null){
            return false;
        }
        if(currentCommand instanceof GenericCommand){
            this.undo();
        } 
        else if(currentCommand instanceof CommandSet){
            CommandSet<URI> thisCommand = (CommandSet<URI>) currentCommand;
            thisCommand.undo(url);
            if(thisCommand.size() == 0){
                this.commandStack.pop();
            }
        }
        return true;
    }

    public void undo(URI url) throws IllegalStateException{
        StackImpl<Undoable> tempStack = new StackImpl<Undoable>();
        int count = 0;
        boolean notEmpty = true;
        int size = commandStack.size();
        for(int i = 0; i < size; i++){
            if(uriMatch(this.commandStack.peek(), url)){
                break;
            }
            count++;
            transferTop(this.commandStack, tempStack);
        }
        notEmpty = properUndo(url);
        for(int i = 0; i < count; i++){
            transferTop(tempStack, this.commandStack);
        }
        if(!notEmpty){
            throw new IllegalStateException("Empty stack or URI not found, cannot undo");
        }
    }


}
