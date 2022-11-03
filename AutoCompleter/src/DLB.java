package Autocomplete;
//valid prefix not working//
import java.util.*;
import java.io.*;

public class DLB{

  private String currSearch = "";
  private int wordCount = 0;
  private DLBNode head;

  public DLB(){
    return;
  }

  public DLB(File dict){
    try{
      FileInputStream input = new FileInputStream(dict);
      Scanner read = new Scanner(input);
      while(read.hasNextLine()){
        this.add(read.nextLine());
      }
      read.close();     //closes the scanner
    }
    catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  public void add(String key){
    //Adds the "valid word" termination character to the end of the string//
    key += '^';
    //Splits given key into it's sub-characters//
    char[] split_string = key.toCharArray();

    DLBNode currNode = this.head;

    int index = 0;

    while(index < split_string.length){

      char currChar = split_string[index];

      DLBNode newNode = new DLBNode(currChar);

      if(this.head == null){
        this.head = new DLBNode(currChar);
        currNode = this.head;
        for(int i = index + 1; i < split_string.length; i ++){
          newNode = new DLBNode(split_string[i]);
          currNode.setDown(newNode);
          currNode = currNode.getDown();
        }
        index = split_string.length;
      }
      else{
        if(currNode.getLet() == currChar){
          currNode = currNode.getDown();
          index ++;
        }
        else{
          if(currNode.getRight() != null){
            currNode = currNode.getRight();
          }
          else{
            currNode.setRight(newNode);
            currNode = currNode.getRight();
            for(int i = index + 1; i < split_string.length; i ++){
              newNode = new DLBNode(split_string[i]);
              currNode.setDown(newNode);
              currNode = currNode.getDown();
            }
            index = split_string.length;
          }
        }
      }
    }
    this.wordCount ++;
  }

	/**
	 * Check if the dictionary contains a word
	 *
	 * @param	key	Word to search the dictionary for
	 *
	 * @return	true if key is in the dictionary, false otherwise
	 */
	public boolean contains(String key){
    //Adds the "valid word" termination character to the end of the string//
    key += '^';

    //Splits given key into it's sub-characters//
    char[] split_string = key.toCharArray();

    //DLB Refrences used later on in the program//
    DLBNode currNode = this.head;

    for(int i = 0; i < split_string.length; i ++){
      char currLet = split_string[i];
      if(currNode.getLet() == currLet && currLet == '^'){
        return true;
      }

      else if(currNode.getLet() != currLet){
        while(currNode.getRight() != null && currNode.getLet() != currLet){
          currNode = currNode.getRight();
        }
        if(currNode.getLet() == currLet){
          currNode = currNode.getDown();
        }
        else{
          return false;
        }
      }
      else{
        currNode = currNode.getDown();
      }
    }
    return false;
  }

	/**
	 * Check if a String is a valid prefix to a word in the dictionary
	 *
	 * @param	pre	Prefix to search the dictionary for
	 *
	 * @return	true if prefix is valid, false otherwise
	 */
	public boolean containsPrefix(String pre){
    char[] prefix = pre.toCharArray();
    DLBNode currNode = this.head;

    //this will navigate to the node following the prefix//
    for(char c : prefix){
      while(currNode.getRight() != null && currNode.getLet() != c){
        currNode = currNode.getRight();
      }
      if(currNode.getLet() == c){
        currNode = currNode.getDown();
      }
      else{
        return false;
      }
    }
    if(currNode.getLet() != '^'){
      return true;
    }
    else if(currNode.getRight() == null){
      return false;
    }
    else{
      return true;
    }
  }

	/**
	 * Search for a word one character at a time
	 *
	 * @param	next Next character to search for
	 *
	 * @return	int value indicating result for current by-character search:
	 *				-1: not a valid word or prefix
	 *				 0: valid prefix, but not a valid word
	 *				 1: valid word, but not a valid prefix to any other words
	 *				 2: both valid word and a valid prefix to other words
	 */
	public int searchByChar(char next){
    currSearch += next;

    boolean vPre = this.containsPrefix(currSearch);
    boolean vWrd = this.contains(currSearch);

    //result is initialized to -5 for debugging purposes
    int result = -5;

    //Determines the return value//
    if(vPre == false && vWrd == false){
      result = -1;
    }
    if(vPre == true && vWrd == false){
      result = 0;
    }
    if(vPre == false && vWrd == true){
      result = 1;
    }
    if(vPre == true && vWrd == true){
      result = 2;
    }
    return result;
  }

	/**
	 * Reset the state of the current by-character search
	 */
	public void resetByChar(){
    this.currSearch = "";
  }

	/**
	 * Suggest up to 5 words from the dictionary based on the current
	 * by-character search. Ordering should depend on the implementation.
	 *
	 * @return	ArrayList<String> List of up to 5 words that are prefixed by
	 *			the current by-character search
	 */
	public ArrayList<String> suggest(){
    char[] split_string = currSearch.toCharArray();
    ArrayList<String> words = new ArrayList<String>();

    DLBNode currNode = this.head;

    for(char c : split_string){
      while(currNode.getRight() != null && currNode.getLet() != c){
        currNode = currNode.getRight();
      }
      if(currNode.getLet() == c){
        currNode = currNode.getDown();
      }
      else{
        return words;
      }
    }
    String temp = currSearch;
    func(temp, currNode, words, 5);
    return words;
  }

  /**
   * List all of the words currently stored in the dictionary
   * @return	ArrayList<String> List of all valid words in the dictionary
   */
  public ArrayList<String> traverse(){
    String temp = "";
    ArrayList<String> words = new ArrayList<String>();
    func(temp, this.head, words);
    return words;
  }

	/**
	 * Count the number of words in the dictionary
	 *
	 * @return	int, the number of (distinct) words in the dictionary
	 */
	public int count(){
    return this.wordCount;
  }

  public void func(String currStr, DLBNode currNode, ArrayList<String> wordCollection){
    while(currNode != null){
      if(currNode.getLet() == '^'){
        wordCollection.add(currStr);
      }
      else{
        func(currStr + currNode.getLet(), currNode.getDown(), wordCollection);
      }
      currNode = currNode.getRight();
    }
  }

  public void func(String currStr, DLBNode currNode, ArrayList<String> wordCollection, int limit){
    while(currNode != null && wordCollection.size() < limit){
      if(currNode.getLet() == '^' && wordCollection.size() < limit){
        wordCollection.add(currStr);
      }
      else{
        func(currStr + currNode.getLet(), currNode.getDown(), wordCollection, limit);
      }
      currNode = currNode.getRight();
    }
  }

  public String getNextWord(String currStr, DLBNode currNode){
    DLBNode checkTemp = currNode;
    if(isValid(checkTemp)){
      return currStr;
    }
    else{

      currStr = currStr + currNode.getLet();
      currNode = currNode.getDown();

      return getNextWord(currStr, currNode);
    }
  }

  private boolean isValid(DLBNode currNode){
    while(currNode != null){
      if(currNode.getLet() == '^'){
        return true;
      }
      currNode = currNode.getRight();
    }
    return false;
  }
}
