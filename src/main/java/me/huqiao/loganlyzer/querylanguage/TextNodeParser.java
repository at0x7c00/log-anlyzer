package me.huqiao.loganlyzer.querylanguage;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import me.huqiao.loganlyzer.querylanguage.exception.ExceptionUtil;
import me.huqiao.loganlyzer.querylanguage.exception.InvalidLQLException;

/**
 * 解析类似如下格式的查询字符串为单词(word)组
 * select * from sys_user where username like '"122%' and org like "fdsf' xxxx ' ad"  and (email>='zoozle@qq.com' or age!='12')
 * 1)单引号或双引号内的所有内容为一个word
 * 2)比较符为一个word，多个临近的比较符会合并为一个word
 * 3)括弧单独为一个word
 * 4)忽略非字符串以内的所有空白符(空格/制表符/换行/回车)
 */
public class TextNodeParser {
	/**
	 * 字符串栈，栈非空时，表示字符位于字符串中
	 */
	private Stack<Character> stack = new Stack<Character>();
	/**
	 * 解析的结果
	 */
	private List<String> wordList = new ArrayList<String>();
	/**
	 * 待解析的源字符串
	 */
	private String lql;
	/**
	 * 单词临时存放位置
	 */
	private StringBuffer dataBuff = new StringBuffer();
	public TextNodeParser(String lql){
		this.lql = lql;
	}
	
	public void doParse() throws InvalidLQLException {
		char[] chars = lql.toCharArray();
		for(int i = 0;i<chars.length;i++){
			char c = chars[i];

			//如果是引号，且与栈中的引号成对，则出栈（表示字符串结束了）
			if(!stack.isEmpty()){
				char topc = stack.peek();
				if(isQuotationMarks(c) && topc == c){
					stack.pop();
				}
			}

			//空格和引号可能预示着一个word的产生
			if((c == ' ' || c == '\'' || c == '"') && !isInString() && dataBuff.length()>0){
				//生成一个word
				if(c!=' ') {
					dataBuff.append(c);
				}
				wordList.add(dataBuff.toString());
				dataBuff = new StringBuffer();

				continue;
			}else if(isCompareSymbol(c)){
				if(!isInString()){
					if(dataBuff.length()>0){
						//生成一个单词
						wordList.add(dataBuff.toString());
						dataBuff = new StringBuffer();
					}
					//查看前一个word是不是比较字符，如果是，则将当期比较字符追加上去
					int lastIdx =  wordList.size()-1;
					String preNode = wordList.get(lastIdx);
					if(isCompareSymbol(preNode.charAt(0))){
						wordList.remove(lastIdx);
						wordList.add(preNode + c);
					}else{
						wordList.add(c+"");
					}
					continue;
				}
			}else if(isBracketsSymbol(c)){//括弧单独成一个word
				if(!isInString()){
					wordList.add(c+"");
					continue;
				}
			}else if (!isInString() && isWhiteSpace(c)){
				//忽略非字符串内的空白字符
				continue;
			}
			
			if(stack.isEmpty() && isQuotationMarks(c)){
				stack.push(c);
			}

			dataBuff.append(c);
			
		}
		//未结束的字符串，报错
		if(isInString()){
			String near = dataBuff.toString();
			String msg = ExceptionUtil.makeLQLErrorMsg(lql,near,stack.peek()+"");
			throw new InvalidLQLException(msg);
		}
		
		if(dataBuff.length()>0){
			wordList.add(dataBuff.toString());
		}
		
	}

	private boolean isWhiteSpace(char c) {
		return c == ' ' || c == '\t' || c == '\r' || c == '\n';
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
	
	public WordList getWordList() {
		return new WordList(wordList);
	}

	
}
