package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T>{

    @SuppressWarnings("hiding")
    private class linkedList<T>{

        T data;
        linkedList<T> next;

        private linkedList(){
            this.data = null;
            this.next = null; 
        }

        private linkedList(T dataInput){
            if(dataInput == null){
                throw new IllegalArgumentException("Null");
            }
            this.data = dataInput;
            next = null;
        }

        private T getData(){
            return this.data;
        }

        @SuppressWarnings("unused")
        private T setData(T dataInput){
            if(this.data == null){
                this.data = dataInput;
                return null;
            }
            else{
                T tempData = this.data;
                this.data = dataInput;
                return tempData;
            }
        }

        private linkedList<T> setNext(linkedList<T> list){
            if(this.next == null){
                this.next = list;
                return null;
            }
            else{
                linkedList<T> tempNext = this.next;
                this.next = list;
                return tempNext;
            }
            
        }

        private linkedList<T> getNext(){
            return this.next;
        }

    }

    linkedList<T> stack;

    public StackImpl(){
        this.stack = null;
    }

    public void push(T element){
        if(element == null){
            throw new IllegalArgumentException("Null elements can't be pushed to the stack");
        }
        //For after constructor or popping all elements
        if(this.stack == null){
            this.stack = new linkedList<T>(element);
        }
        //Otherwise (when the stack already contains elements)
        else{
            linkedList<T> newHead = new linkedList<T>(element);
            newHead.setNext(this.stack);
            this.stack = newHead;
        }
    }

    public T pop(){
        if(this.stack == null){
            return null;
        }
        T head = this.stack.getData();
        this.stack = this.stack.getNext();
        return head;
    }

    public T peek(){
        if(this.stack == null){
            return null;
        }
        return this.stack.getData();
    }

    public int size(){
        int size = 0;
        linkedList<T> current = this.stack;
        while(current != null){
            size++;
            current = current.getNext();
        }
        return size;
    }

}