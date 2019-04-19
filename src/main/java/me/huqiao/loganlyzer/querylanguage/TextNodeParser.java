package me.huqiao.loganlyzer.querylanguage;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * xxx_
 * 'xxx'
 * "xxx"
 * 'x"xxx"xx'
 * 'xx\'xx'
 * 'xx"xx'
 */
public class TextNodeParser {
	
	private Stack<Character> stack = new Stack<Character>();
	private List<String> resList = new ArrayList<String>();
	private String str;
	private StringBuffer dataBuff = new StringBuffer();
	public TextNodeParser(String str){
		this.str = str;
	}
	
	public void doParse(){
		char[] chars = str.toCharArray();
		for(int i = 0;i<chars.length;i++){
			char c = chars[i];
			char topC = stack.peek();
		}
		if(dataBuff.length()>0){
			resList.add(dataBuff.toString());
		}
	}
	
	
	private void invoedToStack(char c) {
		if(c!='\'' && c != '"' && c != '\\'){
			return;
		}
		if(stack.isEmpty()){
			stack.push(c);
			System.out.println(">"+stack);
		}else{
			char xc = stack.peek();
			if(xc == c){
				stack.pop();
				System.out.println(">"+stack);
			}else{
				stack.push(c);
				System.out.println(">"+stack);
			}
		}
	}

	private boolean isWordEnded(char c) {
		if(stack.isEmpty()){
			return c == ' ' || c == '\'' || c == '"';
		}
		return c == ' ' && dataBuff.length()!=0;
	}

	public List<String> getTextList() {
		return resList;
	}

	public static void main(String[] args) {
		String str = "select * from sys_user where username like '122%'and email is not null;";
		TextNodeParser p = new TextNodeParser(str);
		p.doParse();
		for(String s : p.getTextList()){
			System.out.println("["+s+"]");
		}
	}
	
}
