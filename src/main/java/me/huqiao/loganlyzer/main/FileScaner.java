package me.huqiao.loganlyzer.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import me.huqiao.loganlyzer.condition.Condition;
import me.huqiao.loganlyzer.exception.WrongFormatException;
import me.huqiao.loganlyzer.function.FunctionExecutor;
import me.huqiao.loganlyzer.function.Functions;
import me.huqiao.loganlyzer.orderby.OrderBy;
import me.huqiao.loganlyzer.util.StringUtil;

public class FileScaner {

	final static Logger log = Logger.getLogger(FileScaner.class.getName());
	
	public static String spider = " ";
	/* file to scan  */
	private Set<File> fs;
	/**
	 * a tree for conditions
	 */
	private Condition condition = null;
	private String groupBy;
	private Integer groupByIndex;
	private Integer limit;
	/**
	 * selected props like:<br/>
	 * 1,2,5,6,count(1),sum(3),max(3),min(3),avg(3)
	 */
	private String selectedPropsStr;
	private List<SelectedProp> selectedProps = new ArrayList<SelectedProp>();
	private Map<Integer,List<AttPreProcessor>> propIndexProcessors = new HashMap<Integer,List<AttPreProcessor>>();
	private List<OrderBy> orderBys = new ArrayList<OrderBy>(5);
	private Integer maxIndex = 0;
	boolean hasFunction;
	
	public FileScaner(String fileName) {
		if(fileName.contains(",")){
			for(String fn : fileName.split(",")){
				fs = parseFileName(fn);
			}
		}else{
			fs = parseFileName(fileName);
		}
		if(fs.size()==0){
			log.info("No file matched.");
		}
	}
	
	private Set<File> parseFileName(String pattern) {
		if(pattern.contains(".")){
			pattern = pattern.replaceAll("\\.", "\\.");
		}
		if(pattern.contains("*")){
			pattern = pattern.replaceAll("\\*", "\\(.\\)*");
		}
		Set<File> fs = new TreeSet<File>();
		File baseFile = new File(pattern);
		if(baseFile.exists()){
			addFileOrDir(fs, baseFile);
		}else{
			List<String> fileNamePatterns = new ArrayList<String>(10);
			while(!baseFile.exists()){
				fileNamePatterns.add(baseFile.getName());
				baseFile = baseFile.getParentFile();
			}
			searchFile(fs,baseFile,fileNamePatterns,fileNamePatterns.size() - 1);
		}
		return fs;
	}
	
	private void searchFile(Set<File> fs,File dir,List<String> fileNamePatterns,int level){
		if(dir.isFile() && level > 0){
			return;
		}
		if(level < 0){
			return;
		}
		File[] files = dir.listFiles();
		if(files==null){
			return;
		}
		String fileNamePattern = fileNamePatterns.get(level);
		//fileNamePattern = fileNamePattern.replaceAll("\\*", "\\\\\\*");
		for(File file : files){
			if(file.getName().matches(fileNamePattern)){
				if(level == 0 && file.isFile()){
					fs.add(file);
				}else{
					searchFile(fs,file,fileNamePatterns,level - 1);
				}
			}
		}
	}
	private void addFileOrDir(Set<File> fs,File dir){
		if(dir.isFile()){
			fs.add(dir);
		}else{
			File[] files = dir.listFiles();
			if(files!=null){
				for(File file : files){
					if(file.isFile()){
						fs.add(file);
					}
				}
			}
		}
	}

	public FileScaner select(String props){
		this.selectedPropsStr = props;
		return this;
	}
	public FileScaner where(Condition condition){
		this.condition = condition;
		return this;
	}
	
	public FileScaner groupBy(String prop){
		this.groupBy = prop;
		return this;
	}
	
	public FileScaner orderBy(OrderBy... orderBys){
		if(orderBys!=null){
			for(OrderBy orderBy : orderBys){
				this.orderBys.add(orderBy);
			}
		}
		return this;
	}
	
	public FileScaner limit(Integer limit){
		this.limit = limit;
		return this;
	}
	
	public FileScaner processor(Integer propIndex,AttPreProcessor processor){
		List<AttPreProcessor> processorList = propIndexProcessors.get(propIndex);
		if(processorList== null) {
			processorList = new ArrayList<AttPreProcessor>();
		}
		processorList.add(processor);
		propIndexProcessors.put(propIndex, processorList);
		return this;
	}
	
	public List<List<Object>> list()throws Exception{
		init();
		Map<Object,List<Object>> mapping = new LinkedHashMap<Object,List<Object>>();
		for(File f : fs){
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while(( line = br.readLine())!=null){
				mapping(line,mapping);
			}
			br.close();
		}
		Set<LineResultBean> lineResultSet = reduce(mapping);
		List<List<Object>> result = new ArrayList<List<Object>>(lineResultSet.size());
		int count = 0;
		for(LineResultBean lineBean : lineResultSet){
			result.add(lineBean.getLineProps());
			count++;
			//A order by may be there,Need to check limit again
			if(limit != null && count>=limit){
				break;
			}
		}
		return result;
	}

	private void init() throws Exception{
		String indexStrs[] = selectedPropsStr.split(",");
		
		for(String indexStr : indexStrs){
			Integer index = null;
			String function = null;
			if(indexStr.contains("(")){
				try{
					indexStr = indexStr.replaceAll("\\(", " ");
					indexStr = indexStr.replaceAll("\\)", " ");
					function = indexStr.substring(0,indexStr.indexOf(" "));
					indexStr = indexStr.substring(indexStr.indexOf(" ")+1);
					indexStr = indexStr.trim();
					index = Integer.parseInt(indexStr);
					hasFunction = true;
				}catch(Exception e){
					throw new WrongFormatException("select pattern is invalid:" + selectedPropsStr + ",near '" + indexStr + "'");
				}
			}else{
				try{
					index = Integer.parseInt(indexStr);
				}catch(Exception e){
					throw new WrongFormatException("content select is invalid:" + selectedPropsStr+ ",near '" + indexStr + "'");
				}
			}
			if(index > maxIndex){
				maxIndex = index;
			}
			selectedProps.add(new SelectedProp(index,function));
		}
		try{
			if(!StringUtil.isEmpty(groupBy)){
				groupByIndex = Integer.parseInt(groupBy);
			}
		}catch(Exception e){
			throw new WrongFormatException("content select is invalid:" + selectedPropsStr+ ",near '" + groupBy + "'");
		}
	}

	private Set<LineResultBean> reduce(Map<Object,List<Object>> mapping) {
		
		Set<LineResultBean> result = new TreeSet<LineResultBean>();
		int count = 0;
		for(Map.Entry<Object, List<Object>> entry:mapping.entrySet()){
			List<Object> list = entry.getValue();
			if(!hasFunction){
				for(Object l : list){
					String line = (String)l;
					String[] atts = line.split(spider);
					LineResultBean lineList = new LineResultBean(count,orderBys);
					for(SelectedProp prop : selectedProps){
						lineList.addProp(preProcess(atts[prop.getIndex()],prop.getIndex()));
					}
					result.add(lineList);
					count++;
					//Return for limit if there is no 'order by' 
					if(orderBys.isEmpty() && limit != null && count>=limit){
						return result;
					}
				}
			}else{
				Map<String,Object> propResults = new HashMap<String,Object>();
				Map<String,FunctionExecutor> propExecutors =  new HashMap<String,FunctionExecutor>();
				for(SelectedProp prop : selectedProps){
					if(prop.hasFunction()){
						propExecutors.put(prop.getFunction(),Functions.getInstance(prop.getFunction()));
					}
				}
				
				for(Object l : list){
					String line = (String)l;
					String[] atts = line.split(spider);
					boolean updateLine = false;
					for(SelectedProp prop : selectedProps){
						if(prop.hasFunction()){
							FunctionExecutor executor = propExecutors.get(prop.getFunction());
							updateLine = updateLine || executor.accumulation(preProcess(atts[prop.getIndex()], prop.getIndex()));
						}
					}
					for(SelectedProp prop : selectedProps){
						if(!prop.hasFunction()){
							Object value = propResults.get(prop.getIndex()+"");
							if(value==null || updateLine){
								value = preProcess(atts[prop.getIndex()], prop.getIndex());
								propResults.put(prop.getIndex()+"", value);
							}
						}
					}
					
					
				}
				
				LineResultBean lineList = new LineResultBean(count,orderBys);
				for(SelectedProp prop : selectedProps){
					if(prop.hasFunction()){
						FunctionExecutor executor = propExecutors.get(prop.getFunction());
						lineList.addProp(executor.getResult());
					}else{
						lineList.addProp((Comparable)propResults.get(prop.getIndex()+""));
					}
				}
				result.add(lineList);
				count++;
				//Return for limit if there is no 'order by' 
				if(orderBys.isEmpty() && limit!=null && count>=limit){
					return result;
				}
			}
		}
		return result;
		
	}

	public String preProcess(String value, Integer index) {
		List<AttPreProcessor> list = propIndexProcessors.get(index);
		if(list!=null){
			for(AttPreProcessor p : list){
				value = p.process(value);
			}
		}
		return value;
	}


	private void mapping(String line,Map<Object,List<Object>> mapping) {
		if(StringUtil.isEmpty(line)){
			return;
		}
		String[] atts = line.split(spider);
		if(atts.length < maxIndex){
			return;
		}
		if(conditionCheckOK(line)){
			Object key = null;
			if(!StringUtil.isEmpty(groupBy)){
				key = atts[groupByIndex];
				key = preProcess((String)key,groupByIndex);
			}
			List<Object> list = mapping.get(key);
			if(list == null){
				list = new ArrayList<Object>();
			}
			list.add(line);
			mapping.put(key, list);
		}
	}


	private boolean conditionCheckOK(String line) {
		if(condition==null){
			return true;
		}
		return condition.check(line,this);
	}
}
