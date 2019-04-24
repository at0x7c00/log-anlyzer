package me.huqiao.loganlyzer.querylanguage;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import me.huqiao.loganlyzer.querylanguage.exception.InvalidQueryStringException;

/**
 * 解析类似如下格式的查询字符串为单词组
 * select * from sys_user where username like '"122%' and org like "fdsf' xxxx ' ad"  and (email>='zoozle@qq.com' or age!='12')
 */
public class TextNodeParser {
	
	private Stack<Character> stack = new Stack<Character>();
	private List<String> resList = new ArrayList<String>();
	private String str;
	private StringBuffer dataBuff = new StringBuffer();
	public TextNodeParser(String str){
		this.str = str;
	}
	
	public void doParse() throws InvalidQueryStringException{
		char[] chars = str.toCharArray();
		for(int i = 0;i<chars.length;i++){
			char c = chars[i];
			//特殊字符:空格、单引号和双引号
			
			if(!stack.isEmpty()){
				char topc = stack.peek();
				if((c == '\'' || c == '"') && topc == c){
					stack.pop();
				}
			}
			
			if((c == ' ' || c == '\'' || c == '"') && !isInString() && dataBuff.length()>0){
				//生成一个单词
				resList.add(dataBuff.toString());
				dataBuff = new StringBuffer();
				continue;
			}else if(isCompareSymbol(c)){
				if(!isInString()){
					if(dataBuff.length()>0){
						//生成一个单词
						resList.add(dataBuff.toString());
						dataBuff = new StringBuffer();
					}
					int lastIdx =  resList.size()-1;
					String preNode = resList.get(lastIdx);
					
					if(isCompareSymbol(preNode.charAt(0))){
						resList.remove(lastIdx);
						resList.add(preNode + c);
					}else{
						resList.add(c+"");
					}
					continue;
				}
			}else if(isBracketsSymbol(c)){
				if(!isInString()){
					resList.add(c+"");
					continue;
				}
			}else if (!isInString() && c == ' '){
				//忽略空白字符
				continue;
			}
			
			if(stack.isEmpty() && isQuotationMarks(c)){
				stack.push(c);
			}else{
				dataBuff.append(c);
			}
			
		}
		//未结束的字符串，报错
		if(isInString()){
			String near = dataBuff.toString();
			
			int index = str.indexOf(near);
			StringBuffer sb = new StringBuffer();
			sb.append("Invalid query string:\r\n")
			.append(str)
			.append("\r\n");
			for(int i = 0;i<index;i++){
				sb.append(" ");
			}
			sb.append("\\\r\n");
			for(int i = 0;i<index;i++){
				sb.append(" ");
			}
			sb.append(" \\_ ");
			sb.append("this is unbalanced (expect for " + stack.peek() + ")");
			throw new InvalidQueryStringException(sb.toString());
		}
		
		if(dataBuff.length()>0){
			resList.add(dataBuff.toString());
		}
		
	}
	
	private boolean isInString(){
		return !stack.isEmpty();
	}
	
	private boolean isCompareSymbol(char c) {
		return  c == '>' 
				|| c == '<'
				|| c == '!'
				|| c == '=';
	}
	private boolean isBracketsSymbol(char c) {
		return  c == ')' 
				|| c == '(';
	}

	private boolean isQuotationMarks(char c){
		return (c == '\'' || c == '"');
	}
	
	public List<String> getTextList() {
		return resList;
	}

	
}
