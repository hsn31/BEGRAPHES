package org.insa.algo.utils;

import java.util.ArrayList;

//Will be used to generate all users combinations in the CarPooling problem with n users

public class CombinationTools {

	public static ArrayList<Integer[]> generateCombinationsWithoutOrder(int n,int k){
		ArrayList<Integer[]> res = new ArrayList<>();
		ArrayList<ArrayList<Integer>> file = new ArrayList<>();
		file.add(new ArrayList<>());
		while(file.size()>0){
			ArrayList<Integer> item = file.get(0);
			file.remove(0);
			if(item.size()==k){
				res.add((Integer[])item.toArray());
			}
			else {
				int max = -1;
				if (item.size() > 0) {
					max = item.get(file.size() - 1);
				}
				for(int i=max+1;i<n-item.size()-1;i++){
					ArrayList<Integer> newItem = (ArrayList<Integer>) item.clone();
					newItem.add(i);
					file.add(newItem);
				}
			}
		}

		return res;

	}
}
