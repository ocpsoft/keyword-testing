package org.ocpsoft.keywords;

import java.io.Serializable;
import java.util.ArrayList;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class KeywordFactory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject
	Instance<Keyword> keywords;
	
	//NOTE: Can't set keywords to be static cause it wont inject anything, so we can't make createKeyword static.
	public Keyword createKeyword(String key){
		for (Keyword keyword : keywords) {
			if(keyword.getShortName().toString().equals(key)){
				return keyword;
			}
		}
		return null;
	}
	
	public ArrayList<String> getAllKeywordTypes(){
		ArrayList<String> types = new ArrayList<String>();
		for (Keyword keyword : keywords) {
			types.add(keyword.getShortName().toString());
		}
		return types;
	}
	
}
