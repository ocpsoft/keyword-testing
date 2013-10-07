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
	
	public Keyword createKeyword(String key){
		for (Keyword keyword : keywords) {
			if(keyword.shortName().toString().equals(key)){
				return keyword;
			}
		}
		return null;
	}
	
	public ArrayList<String> getAllKeywordTypes(){
		ArrayList<String> types = new ArrayList<String>();
		for (Keyword keyword : keywords) {
			types.add(keyword.shortName().toString());
		}
		return types;
	}
	
}
