package Autocomplete;

import java.util.*;
import java.io.*;

public class UserHistory implements Dict{
  private Node head;
  private String currSearch = "";
  private int wordCount = 0;

  public UserHistory(File f_name){
    try{
      FileInputStream input = new FileInputStream(f_name);
      Scanner read = new Scanner(input);
      while(read.hasNextLine()){
        String[] result = read.nextLine().split(":");
        this.add(result[0], Integer.parseInt(result[1]));
      }
      read.close();     //closes the scanner
    }
    catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  public UserHistory(){
    return;
  }

  public void save(String fpath){
    try {
      FileWriter fWriter;
      File f_name = new File(fpath);

      HashMap<String, Integer> freqMap = new HashMap<>();

      ArrayList<String> words = new ArrayList<>();
      String temp = "";

      func(temp, this.head, words, freqMap);

      if(f_name.createNewFile()){
        fWriter = new FileWriter(f_name);
        for(String w : words){
          String out = w + ":" + freqMap.get(w) + "\n";
          fWriter.write(out);
        }
        fWriter.close();
      }
      else{
        fWriter = new FileWriter(f_name);
        for(String w : words){
          String out = w + ":" + freqMap.get(w) + "\n";
          fWriter.write(out);
        }
        fWriter.close();
      }
    }
    catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  public void add(String key){
    key += '^';
    Node newNode;

    if(this.head == null){
      this.head = new Node(key.charAt(0));
      Node currNode = this.head;
      for(int i = 1; i < key.length(); i ++){
        if(key.charAt(i) == '^'){
          newNode = new Node(key.charAt(i), 1);
          currNode.setDown(newNode);
          currNode = currNode.getDown();
        }
        else{
          newNode = new Node(key.charAt(i));
          currNode.setDown(newNode);
          currNode = currNode.getDown();
        }
      }
      this.wordCount ++;
    }
    else{
      Node currNode = this.head;
      for(int index = 0; index < key.length(); index ++){
        while(key.charAt(index) != currNode.getLet() && currNode.getRight() != null){
          currNode = currNode.getRight();
        }
        if(currNode.getLet() == key.charAt(index)){
          if(key.charAt(index) == '^'){
            currNode.setFreq(currNode.getFreq() + 1);
          }
          else{
            currNode = currNode.getDown();
          }
        }
        else{
          newNode = new Node(key.charAt(index));
          currNode.setRight(newNode);
          currNode = currNode.getRight();
          for(int i = index + 1; i < key.length(); i ++){
            if(key.charAt(i) == '^'){
              newNode = new Node(key.charAt(i), 1);
              currNode.setDown(newNode);
              currNode = currNode.getDown();
            }
            else{
              newNode = new Node(key.charAt(i));
              currNode.setDown(newNode);
              currNode = currNode.getDown();
            }
          }
          this.wordCount ++;
          break;
        }
      }
    }
  }

  public void add(String key, int freq){
    key += '^';
    Node newNode;
    this.wordCount ++;

    if(this.head == null){
      this.head = new Node(key.charAt(0));
      Node currNode = this.head;
      for(int i = 1; i < key.length(); i ++){
        if(key.charAt(i) == '^'){
          newNode = new Node(key.charAt(i), freq);
          currNode.setDown(newNode);
          currNode = currNode.getDown();
        }
        else{
          newNode = new Node(key.charAt(i));
          currNode.setDown(newNode);
          currNode = currNode.getDown();
        }
      }
    }
    else{
      Node currNode = this.head;
      for(int index = 0; index < key.length(); index ++){
        while(key.charAt(index) != currNode.getLet() && currNode.getRight() != null){
          currNode = currNode.getRight();
        }
        if(currNode.getLet() == key.charAt(index)){
          currNode = currNode.getDown();
        }
        else{
          newNode = new Node(key.charAt(index));
          currNode.setRight(newNode);
          currNode = currNode.getRight();
          for(int i = index + 1; i < key.length(); i ++){
            if(key.charAt(i) == '^'){
              newNode = new Node(key.charAt(i), freq);
              currNode.setDown(newNode);
              currNode = currNode.getDown();
            }
            else{
              newNode = new Node(key.charAt(i));
              currNode.setDown(newNode);
              currNode = currNode.getDown();
            }
          }
          break;
        }
      }
    }
  }

	public boolean contains(String key){
    //Adds the "valid word" termination character to the end of the string//
    if(this.head == null){
      return false;
    }
    else{
      key += '^';
      //Splits given key into it's sub-characters//
      char[] split_string = key.toCharArray();

      //DLB Refrences used later on in the program//
      Node currNode = this.head;

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
  }

  public boolean containsPrefix(String pre){
    if(this.head == null){
      return false;
    }
    else{
      char[] prefix = pre.toCharArray();
      Node currNode = this.head;

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
  }

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

	public void resetByChar(){
    this.currSearch = "";
  }

  public ArrayList<String> traverse(){
    String temp = "";
    ArrayList<String> words = new ArrayList<String>();
    func(temp, this.head, words);
    return words;
  }

  public ArrayList<String> traverse (HashMap<String, Integer> freqMap){
    String temp = "";
    ArrayList<String> words = new ArrayList<String>();
    func(temp, this.head, words, freqMap);
    return words;
  }

	public int count(){
    return this.wordCount;
  }

	public ArrayList<String> suggest(){
    ArrayList<String> words = new ArrayList<String>();

    if(this.head == null){
      return words;
    }
    else{
      char[] split_string = currSearch.toCharArray();
      HashMap<String, Integer> freqMap = new HashMap<>();

      Node currNode = this.head;

      //navigate to end of prefix (node after the prefix)//
      //run traverse, adding each frequency to the freqMap//
      //within traverse, ensure order is preserved in each insert//

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
      func(temp, currNode, words, freqMap);
      return words;
    }
  }

  //used in traverse to get all possible words//
  private void func(String currStr, Node currNode, ArrayList<String> wordCollection){
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

  //used to get all possible words with prefix, and their frequencies//
  private void func(String currStr, Node currNode, ArrayList<String> wordCollection, HashMap<String, Integer> map){
    while(currNode != null){
      if(currNode.getLet() == '^'){
        map.put(currStr, currNode.getFreq());
        insertInOrder(currStr, wordCollection, map);
      }
      else{
        func(currStr + currNode.getLet(), currNode.getDown(), wordCollection, map);
      }
      currNode = currNode.getRight();
    }
  }

  private void insertInOrder(String currStr, ArrayList<String> wordCollection, HashMap<String, Integer> map){
    int index = wordCollection.size() - 1;

    wordCollection.add(currStr);

    int insertIndex = wordCollection.size() - 1;

    while(index >= 0){
      if(map.get(wordCollection.get(index)) < map.get(currStr)){
        //System.out.println(wordCollection);
        //System.out.println("Swapping : " + wordCollection.get(index) + " frequency : " + map.get(currStr) + " with : " + wordCollection.get(insertIndex) + " frequency : " + map.get(wordCollection.get(index)));
        Collections.swap(wordCollection, index, insertIndex);
        //System.out.println(wordCollection);
        insertIndex --;
      }
      else{
        return;
      }
      index --;
    }
  }



  public class Node implements Serializable {

    private int freq;

  	private char let;

  	private Node right;

  	private Node down;

    public Node(){
      return;
    }
  	public Node(char l) {
  		this.let = l;
      this.freq = 0;

  		this.right = null;
  		this.down = null;
  	}
    public Node(char l, int f){
      this.let = l;
      this.freq = f;

      this.right = null;
      this.down = null;
    }

    public int getFreq(){
      return freq;
    }

  	public char getLet() {
  		return let;
  	}

  	public Node getRight() {
  		return right;
  	}

  	public Node getDown() {
  		return down;
  	}

    public void setFreq(int f){
      freq = f;
    }

  	public void setRight(Node r) {
  		right = r;
  	}

  	public void setDown(Node d) {
  		down = d;
  	}
  }
}
