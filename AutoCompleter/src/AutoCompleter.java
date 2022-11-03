package Autocomplete;

import java.util.*;
import java.io.*;

public class AutoCompleter{
  DLB englishDictionary;
  UserHistory userHistory;

  public AutoCompleter(String fNameDict){
    File dict = new File(fNameDict);
    this.englishDictionary = new DLB(dict);
    this.userHistory = new UserHistory();
  }
  public AutoCompleter(String fNameDict, String fNameHistory){
    File dict = new File(fNameDict);
    File hist = new File(fNameHistory);

    this.englishDictionary = new DLB(dict);
    this.userHistory = new UserHistory(hist);
  }

  /**
	 * Produce up to 5 suggestions based on the current word the user has
	 * entered These suggestions should be pulled first from the user history
	 * dictionary then from the initial dictionary. Any words pulled from user
	 * history should be ordered by frequency of use. Any words pulled from
	 * the initial dictionary should be in ascending order by their character
	 * value ("ASCIIbetical" order).
	 *
	 * @param 	next char the user just entered
	 *
	 * @return	ArrayList<String> List of up to 5 words prefixed by cur
	 */
	public ArrayList<String> nextChar(char next){

    englishDictionary.searchByChar(next);
    userHistory.searchByChar(next);

    ArrayList<String> suggestions = userHistory.suggest();
    ArrayList<String> d = englishDictionary.suggest();

    for(String dSuggest : d){
      if(suggestions.size() == 5){
        break;
      }
      else{
        if(!insert(dSuggest, suggestions)){
          suggestions.add(dSuggest);
        }
      }
    }
    return suggestions;
  }
  private boolean insert(String key, ArrayList<String> list){
    for(String s : list){
      if(key.compareTo(s) == 0){
        return true;
      }
    }
    return false;
  }

	/**
	 * Process the user having selected the current word
	 *
	 * @param 	cur String representing the text the user has entered so far
	 */
	public void finishWord(String cur){
    userHistory.resetByChar();
    englishDictionary.resetByChar();
    userHistory.add(cur);
  }

	/**
	 * Save the state of the user history to a file
	 *
	 * @param	fname String filename to write history state to
	 */
	public void saveUserHistory(String fname){
    userHistory.save(fname);
  }
}
