package org.insa.algo.utils;

import java.util.ArrayList;
import java.util.HashMap;

//Will be used to store users node combinations in the CarPooling problem with n users

public class SequenceTree<K,E> {
	private HashMap<K,SequenceTree<K,E>> trees;
	private E value;
	public SequenceTree(){
		this.trees=new HashMap<>();
	}
	public SequenceTree(E value){
		this.trees=new HashMap<>();
		this.value=value;
	}
	public SequenceTree(K key,E value){
		this.trees=new HashMap<>();
		this.value=value;
		this.trees.put(key,new SequenceTree<>(value));
	}
	public void add(K key,E value){
		this.trees.put(key,new SequenceTree<>(value));

	}
	public E getValue(){
		return this.value;
	}
	public E consumeSequence(ArrayList<K> sequence) {
		if (sequence.size() == 0) {
			return this.getValue();
		} else {
			K next = sequence.get(0);
			sequence.remove(0);
			return this.trees.get(next).consumeSequence(sequence);
		}
	}

	public void setSequence(ArrayList<K> sequence,E value){
		if(sequence.size()==0){
			this.value=value;
		}
		else{
			K next = sequence.get(0);
			sequence.remove(0);
			if(this.trees.get(next)==null){
				this.trees.put(next,new SequenceTree<>());

			}
			this.trees.get(next).setSequence(sequence,value);

		}
	}
	public int getSize(){
		int res = 0;
		for(K key :this.trees.keySet()){
			res++;
			res+=this.trees.get(key).getSize();
		}
		return res;
	}

}
