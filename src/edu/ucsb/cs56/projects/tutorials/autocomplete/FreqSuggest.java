package edu.ucsb.cs56.projects.tutorial.autocomplete;
import java.util.ArrayList;
import java.util.Hashtable;

class FreqSuggest{
    private Hashtable<String, ArrayList<FollowFreq>> freq_suggest;
    
    public FreqSuggest(String input){
	freq_suggest = new Hashtable<String, ArrayList<FollowFreq>>();
	String front = "";
	String back = "";
	
	for(int i = 0; i < input.length(); i++){
	    if(input.charAt(i) != ' ' && input.charAt(i) != '\n')
		back += input.charAt(i);
	    else{
		//System.out.print(back);
		if(!front.equals("")){
		    if(freq_suggest.containsKey(front)){
			boolean flag = false;
			ArrayList<FollowFreq> temp = freq_suggest.get(front);
			for(FollowFreq f: temp){
			    if(f.word.equals(back)){
				flag = true;
				f.freq ++;
				break;
			    }
			}
			if(!flag)
			    temp.add(new FollowFreq(back, 1));
			
			freq_suggest.put(front, temp);
			
		    }
		    else{
			ArrayList<FollowFreq> temp = new ArrayList<FollowFreq>();
			temp.add( new FollowFreq(back, 1));
			//System.out.println(temp);
			freq_suggest.put(front, temp);
			//System.out.println(freq_suggest.get(front));
		    }
		}
		//System.out.println(temp);
		front = back;
		back = "";	
	    }
	}
	//	    for(String s: freq_suggest.keys()){
	//		ArrayList<FollowFreq> temp = java.Collectoins.sort(freq_suggest.get(s));
	//	freq_suggest.put(s, temp);
	
    }
    public Hashtable<String, ArrayList<FollowFreq>> GetTable(){ return this.freq_suggest;}
};
