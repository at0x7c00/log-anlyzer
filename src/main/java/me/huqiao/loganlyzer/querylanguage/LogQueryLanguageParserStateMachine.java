package me.huqiao.loganlyzer.querylanguage;


import me.huqiao.loganlyzer.main.FileScaner;
import me.huqiao.loganlyzer.querylanguage.exception.ExceptionUtil;
import me.huqiao.loganlyzer.querylanguage.exception.InvalidLQLException;
import me.huqiao.loganlyzer.querylanguage.statemachine.StateMachine;
import me.huqiao.loganlyzer.querylanguage.statemachine.exception.FeedException;
import me.huqiao.loganlyzer.querylanguage.statusnode.Start;
import me.huqiao.loganlyzer.querylanguage.statemachine.StatusNode;
import me.huqiao.loganlyzer.querylanguage.statusnode.StatusNodeFactory;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * LQL Parser
 *
 * 	select 0,2,3
 * 	from tomcat_access_log_01.txt,tomcat_access_log_02.txt
 * 	where (0 eq 'x') and (2 eq 'x'  or 3 eq 'x')
 * 	order by 0
 * 	group by 0
 * 	limit 10
 * 	split by ' '
 *
 */
public class LogQueryLanguageParserStateMachine implements StateMachine<LogQueryLanguageParserStateMachine> {

	static Logger log = Logger.getLogger(LogQueryLanguageParserStateMachine.class.getName());

	private String lql;
	private FileScaner fileScaner;
	private QueryStringParseResult result = new QueryStringParseResult();

	private StatusNode currentStatus = null;
	private Iterator<String> words = null;
	private TextNodeParser parser = null;

	private StatusNodeFactory statusNodeFactory;

	/**
	 * @param lql log file query language
	 */
	public LogQueryLanguageParserStateMachine(String lql) throws InvalidLQLException {
		this.lql = lql;
		parser = new TextNodeParser(lql);
		parser.doParse();
		reset();
	}


	@Override
	public boolean isSaturated() {
		return false;
	}

	@Override
	public LogQueryLanguageParserStateMachine get() {
		return this;
	}

	@Override
	public void start() throws Exception {
		while(words.hasNext()) {
			String word = null;
			//给状态节点一直喂word
			while (words.hasNext() && !currentStatus.isSaturated()) {
				word = words.next();
				try {
					log.info("feed :" + currentStatus + " with '" + word + "'");
					currentStatus.feed(word, this);
				} catch (FeedException e) {
					String error = ExceptionUtil.makeLQLErrorMsgForStateMachine(lql, word, e.getMessage());
					throw new InvalidLQLException(error);
				}
			}
			//喂饱之后切换到下一个节点
			if (words.hasNext()) {
				if (!currentStatus.isWordOverflow()) {
					word = words.next();
				}
				currentStatus = currentStatus.next(word);
				log.info("switch by '" + word + "' to :" + currentStatus);
				if(currentStatus==null){
					throw new InvalidLQLException("unexpected '"+word+"'");
				}
			}
		}

		currentStatus.endup(this);

		//判断节点完整性
		if(!currentStatus.isSaturated()){
			throw new InvalidLQLException("uncompleted value for " + currentStatus);
		}

		if(!this.getResult().isSaturated()){
			throw new InvalidLQLException("uncompleted lql!");
		}

	}

	@Override
	public void reset()  {
		statusNodeFactory = new StatusNodeFactory();
		currentStatus = statusNodeFactory.start();
		words = parser.getWordList();
	}

	@Override
	public Iterator<String> getWords() {
		return words;
	}

	public FileScaner getFileScaner() {
		if(fileScaner==null){
			fileScaner = new FileScaner(result.getFrom()).select(result.getSelect());
			if(result.getCondition()!=null){
				fileScaner.where(result.getCondition());
			}
			if(result.getOrderBy()!=null){
				fileScaner.orderBy(result.getOrderBy());
			}
			if(result.getGroupBy()!=null){
				fileScaner.groupBy(result.getGroupBy());
			}
			if(result.getSplitBy()!=null){
				fileScaner.splitBy(result.getSplitBy());
			}
			if(result.getLimit()!=null){
				fileScaner.limit(result.getLimit());
			}
		}
		return fileScaner;
	}

	public QueryStringParseResult getResult() {
		return result;
	}
}
