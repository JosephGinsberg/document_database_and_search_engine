package edu.yu.cs.com1320.project.impl;

import java.util.NoSuchElementException;

import edu.yu.cs.com1320.project.MinHeap;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E>{

    public MinHeapImpl(){
        this.count = 0;
        Comparable<?>[] array = new Comparable<?>[25];
        this.elements = (E[]) array;
    }

    public void reHeapify(E element) {
       int thisKey = getArrayIndex(element);
       downHeap(thisKey);
       thisKey = getArrayIndex(element);
       upHeap(thisKey);
    }

    protected int getArrayIndex(E element) {
        int currentLen = this.elements.length;
        for(int i = 0; i < currentLen; i++){
            if(elements[i] != null && elements[i].equals(element)){
                //only first instance?
                return i;
            }
        }
        throw new NoSuchElementException("No such element found");
    }

    protected void doubleArraySize() {
        int currentLen = this.elements.length;
        Comparable<?>[] temp = new Comparable<?>[currentLen * 2];
        temp = (E[]) temp;
        for(int i = 0; i < currentLen; i++){
            temp[i] = this.elements[i];
        }
        this.elements = (E[]) temp;
    }

}
