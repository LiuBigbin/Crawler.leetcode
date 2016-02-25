package leetcodeCrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;


public class Crawler4submissionDetailOrderByTag {

	private String leetcodeTagUrl = "https://leetcode.com/problemset/algorithms/";
	private String fileDir = null;
	
	public Crawler4submissionDetailOrderByTag(String fileDir){
		this.fileDir = fileDir;
	}
		
	public boolean getTagInformation(){
		if(fileDir == null || "".equals(fileDir)){
			System.out.println("文件夹路径为空：" + fileDir);
			System.out.println("获取tagInformation失败！！！");
			return false;
		}
			
		File FileDir = new File(fileDir);
		if(!FileDir.exists()){
			System.out.println("文件夹路径不存在：" + fileDir);
			System.out.println("获取tagInformation失败！！！");
			return false;
		}
		if(FileDir.isFile()){
			System.out.println(fileDir + "为文件，并不是目录！！！");
			System.out.println("获取tagInformation失败！！！");
			return false;
		}
		if(FileDir.list().length != 0){
			System.out.println("目录：" + fileDir + "里面不为空，请删除无关文件！！！");
			System.out.println("获取tagInformation失败！！！");
			return false;
		}
		Spider spider = new Spider();
		try {
			String htmlstr = spider.getHtmlStringByUrl(leetcodeTagUrl);
			//System.out.println(htmlstr);
			Parser parser = new Parser(htmlstr);
			HasAttributeFilter Filter = new HasAttributeFilter("class", "col-md-offset-3 col-md-9 list-group");
			NodeList nodeList = parser.extractAllNodesThatMatch(Filter);
			if(nodeList.size() == 0)
				throw new Exception();
			Node node = nodeList.elementAt(nodeList.size()-1);
			NodeList tagList = node.getChildren();
			if(tagList.size() == 0)
				throw new Exception();
			for(int i=0; i<tagList.size(); i++){
				LinkTag tag = null ;
				if(!(tagList.elementAt(i) instanceof LinkTag))
					continue;
				tag = (LinkTag)tagList.elementAt(i);
				String tagurl = "https://leetcode.com" + tag.getLink();
				//Node numNode = tag.getChild(1);
				Node nameNode = tag.getChild(4);
				String name = nameNode.getText();
				//int num = Integer.valueOf(numNode.getText());
				File tagDir = new File(fileDir+"\\"+name);
				tagDir.mkdir();
				if(!tagDir.exists()){
					System.out.println("创建标签目录失败！！！" + fileDir+"\\"+name);
					throw new Exception();
				}
				File tagUrlFile = new File(fileDir+"\\"+name + "\\" + "url.txt");
				tagUrlFile.createNewFile();
				if(!tagUrlFile.exists()){
					System.out.println("创建标签目录链接文件失败！！！" + fileDir+"\\"+name + "\\" + "url.txt");
					throw new Exception();
				}
				OutputStreamWriter urlFile = new OutputStreamWriter(new FileOutputStream(
						fileDir+"\\"+name + "\\" + "url.txt", false), "utf-8");
				urlFile.write(tagurl + '\n');
				urlFile.close();
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("解析页面出现问题，获取tagInformation失败！！！");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void CrawlerLeetCodeUrl(){
		if(!getTagInformation()){
			return;
		}
		File Dir = new File(fileDir);
		String[] dirList = Dir.list();
		int success = 0;
		int isFile = 0;
		int urlFileNotExists = 0;
		int notComplete = 0;
		int error = 0;
		int ignor = 0;
		for(String dir: dirList){
			System.out.println("正在处理文件夹：" + fileDir + "\\" + dir + "************************************");
			File tagDir = new File(fileDir + "\\" + dir);
			if(tagDir.isFile()){
				System.out.println(fileDir + "\\" + dir + "为文件，忽略！！！");
				isFile++;
				continue;
			}
			File tagUrlFile = new File(fileDir + "\\" + dir + "\\" + "url.txt");
			if(!tagUrlFile.exists()){
				System.out.println(fileDir + "\\" + dir + "\\" + "url.txt" + "文件不存在，忽略！！！");
				urlFileNotExists++;
				continue;
			}
			boolean complete = true;
			try {
				BufferedReader urlReader = new BufferedReader(new FileReader(
						tagUrlFile));
				String line = urlReader.readLine().trim();
				if(line == null || "".equals(line)){
					System.out.println(fileDir + "\\" + dir + "\\" + "url.txt" + "文件第一行为空，忽略！！！");
					error++;
					urlReader.close();
					continue;
				}
				urlReader.close();
				Spider spider = new Spider();
				String html = spider.getHtmlStringByUrl(line);
				int index = html.indexOf("bootstrapTable({");
				if(index == -1){
					System.out.println("获得tag对应的问题列表时出错！！！");
					error++;
					continue;
				}
				html = html.substring(index+16);
				index = html.indexOf('[');
				if(index == -1){
					System.out.println("获得tag对应的问题列表时出错！！！");
					error++;
					continue;
				}
				html = html.substring(index, html.indexOf(']')+1);
				if(html == null || "".equals(html)){
					System.out.println("获得tag对应的问题列表时出错！！！");
					error++;
					continue;
				}
				html = html.substring(0, html.lastIndexOf('}') + 1);
				html += ']';

				while((index = html.indexOf("\"+              \"")) != -1){
					String prefix =  html.substring(0, index);
					String postfix = html.substring(index+17);
					html = prefix+postfix;
				}
				
				while((index = html.indexOf("\"+                  \"")) != -1){
					String prefix =  html.substring(0, index);
					String postfix = html.substring(index+21);
					html = prefix+postfix;
				}
				while((index = html.indexOf("\"+                \"")) != -1){
					String prefix =  html.substring(0, index);
					String postfix = html.substring(index+19);
					html = prefix+postfix;
				}
				
				
				//html = "{\"data\": \"" + html + "\"}";
				//JSONObject data = new JSONObject(html);
				//JSONArray questionList = data.optJSONArray("data");
				@SuppressWarnings("deprecation")
				JSONArray questionList = new JSONArray(html);
				File questionurlFile = new File(fileDir + "\\" + dir + "\\" + "questionurl.txt");
				if(questionurlFile.exists() && questionurlFile.length() != 0){
					System.out.println(fileDir + "\\" + dir + "\\" + "questionurl.txt" +  "已存在直接忽略！！！");
					ignor++;
					continue;
				}
				if(!questionurlFile.exists())
					questionurlFile.createNewFile();
				OutputStreamWriter questionUrl = new OutputStreamWriter(new FileOutputStream(
						fileDir + "\\" + dir + "\\" + "questionurl.txt", false), "utf-8");
				for(int i=0; i<questionList.length(); i++){
					JSONObject question = questionList.getJSONObject(i);
					String title = question.optString("title");
					String ac_or_not = question.optString("ac_or_not");
					if(title.indexOf("fa fa-lock") != -1 || ac_or_not.indexOf("None") != -1){
						//该题目为付费题目或是还没有做过的题目，忽略
						continue;
					}
					
					String questionName = title.substring(title.indexOf("'")+1, title.indexOf("'>"));
					String url = "https://leetcode.com" + questionName + "submissions/";
					String str = spider.getHtmlStringByUrl(url);
					Parser parser = new Parser(str);
					HasAttributeFilter Filter = new HasAttributeFilter("class", "text-danger status-accepted");
					NodeList nodeList = parser.extractAllNodesThatMatch(Filter);
					if(nodeList.size() == 0){
						System.out.println("在获取链接的accepted链接时失败：" + url);
						questionUrl.close();
						complete = false;
						error++;
						//该题目还没有做过，直接忽略
						break;
					}
					
					LinkTag urlTag = (LinkTag)nodeList.elementAt(0);
					url = "https://leetcode.com" + urlTag.getLink();
					questionUrl.write(url + "\n");
					System.out.println("成功得到链接：" + url);
				}
				questionUrl.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("打开文件：" + fileDir + "\\" + dir + "\\" + "url.txt" + "出错！！！");
				complete = false;
				e.printStackTrace();
			} catch (Exception ex){
				System.out.println("爬取页面失败！！！");
				complete = false;
				ex.printStackTrace();
			}
			if(complete)
				success++;
			else
				notComplete++;
			
		}
		System.out.println("成功处理的文件夹为：" + success);
		System.out.println("处理失败的文件夹为：" + error);
		System.out.println("处理不完全的文件夹为：" + notComplete);
		System.out.println("标签文件夹里面url文件不存在为：" + urlFileNotExists);
		System.out.println("忽略的文件为：" + isFile);
		System.out.println("因为questionurl.txt文件存在而忽略的为：" + ignor);
	}

	public void CrawlerLeetCodeSubmission(){
		File Dir = new File(fileDir);
		String[] dirList = Dir.list();
		int success = 0;
		int isFile = 0;
		int ignore = 0;
		int fileNotExists = 0;
		int notComplete = 0;
		for(String dir: dirList){
			System.out.println("正在处理文件夹：" + fileDir + "\\" + dir + "************************************");
			File tagDir = new File(fileDir + "\\" + dir);
			if(tagDir.isFile()){
				System.out.println(fileDir + "\\" + dir + "为文件，忽略！！！");
				isFile++;
				continue;
			}
			ArrayList<String> urlList = new ArrayList<String>();
			ArrayList<String> urlbp = new ArrayList<String>();
			File questionUrlBreakPonitFile = new File(fileDir + "\\" + dir + "\\" + "questionurlBreakPoint.txt");
			BufferedReader urlReader = null;
			try{
				if(questionUrlBreakPonitFile.exists()){
					System.out.println(fileDir + "\\" + dir + "\\" + "questionurlBreakPoint.txt" + "断点文件存在，直接读取！！！");
					urlReader = new BufferedReader(new FileReader(
							questionUrlBreakPonitFile));
				}
				else{
					File submissionFile = new File(fileDir + "\\" + dir + "\\" + "submisstion.txt");
					if(submissionFile.exists() && submissionFile.length() != 0){
						System.out.println(fileDir + "\\" + dir + "\\" + "submisstion.txt" + "提交信息文件已存在，直接忽略！！！");
						ignore++;
						continue;
					}
					File questionUrlFile = new File(fileDir + "\\" + dir + "\\" + "questionurl.txt");
					if(!questionUrlFile.exists() || (questionUrlFile.exists() && questionUrlFile.length() == 0)){
						System.out.println(fileDir + "\\" + dir + "\\" + "questionurl.txt" + "文件不存在或为空，直接忽略！！！");
						fileNotExists++;
						continue;
					}
					urlReader = new BufferedReader(new FileReader(
							questionUrlFile));
				}
			
			
				String line = null;
				while((line = urlReader.readLine()) != null){
					line = line.trim();
					if(line == null || "".equals(line))
						continue;
					urlList.add(line);
					
				}
				urlReader.close();
				for(String url : urlList)
					urlbp.add(url);
			} catch (IOException ioe){
				ioe.printStackTrace();
			}
			OutputStreamWriter subOut = null;
			boolean complete = true;
			try{
				File submissionFile = new File(fileDir + "\\" + dir + "\\" + "submisstion.txt");
				if(!submissionFile.exists())
					submissionFile.createNewFile();
				subOut = new OutputStreamWriter(new FileOutputStream(
						fileDir + "\\" + dir + "\\" + "submisstion.txt", true), "utf-8");
				
                for(String url : urlList){
                	Spider spider = new Spider();
    				String html = spider.getHtmlStringByUrl(url);
    				int titleIndex = html.indexOf("<title>");
    				if(titleIndex == -1){
    					System.out.println("获取url："+ url + "的问题描述时出错！！！");
    					complete = false;
    					continue;
    				}
    				html = html.substring(titleIndex+7);
    				String title = html.substring(0, html.indexOf('|'));
    						
    				int index = html.indexOf("<meta name=\"description\"");
    				if(index == -1){
    					System.out.println("获取url："+ url + "的问题描述时出错！！！");
    					complete = false;
    					continue;
    				}
    				html = html.substring(index+24);
    				index = html.indexOf("content=");
    				if(index == -1){
    					System.out.println("获取url："+ url + "的问题描述时出错！！！");
    					complete = false;
    					continue;
    				}
    				String content = html.substring(index+9, html.indexOf("\" />"));
    				if(content.indexOf("\\[show hint\\]") != -1)
    					content = content.replace("\\[show hint\\]", "");
    				if(content.lastIndexOf("Note:") != -1){
    					String prefix = content.substring(0, content.lastIndexOf("Note:"));
    					String postfix = content.substring(content.lastIndexOf("Note:"));
    					content = prefix + '\n' + postfix;
    				}
    				if(content.lastIndexOf("Hint:") != -1){
    					String prefix = content.substring(0, content.lastIndexOf("Hint:"));
    					String postfix = content.substring(content.lastIndexOf("Hint:"));
    					content = prefix + '\n' + postfix;
    				}
    				if(content.lastIndexOf("Credits:") != -1){
    					String prefix = content.substring(0, content.lastIndexOf("Credits:"));
    					String postfix = content.substring(content.lastIndexOf("Credits:"));
    					content = prefix + '\n' + postfix;
    				}
    				content = content.replaceAll("&quot;", "\"");
    				content = content.replaceAll("&#39;", "'");
    				content = content.replaceAll("&gt", ">");
    				content = content.replaceAll("&lt", "<");
    				//C++代码提交的算法
    				index = html.indexOf("storage.put('cpp', '");
    				//C代码提交的算法
    				int Cindex = html.indexOf("storage.put('c', '");
    				if(index == -1 && Cindex == -1){
    					System.out.println("获取url："+ url + "的提交代码时出错！！！");
    					complete = false;
    					continue;
    				}
    				if(index == -1){
    					index = Cindex;
        				html = html.substring(index+18);
    				}
    				else
    					html = html.substring(index+20);
    				int endIndex = html.indexOf("'");
    				
    				String code = html.substring(0, endIndex);
    				
    				code = "{\"code\":\"" + code + "\"}";
    				JSONObject data = new JSONObject(code);
    				code = data.optString("code");
    				code = code.substring(0, code.length()-1);
    				subOut.write(title + "\n");
    				subOut.write(content + "\n\n");
    				subOut.write(code + "\n\n\n");
    				urlbp.remove(url);
    				System.out.println("获取url："+ url + "成功！！！");
                }
                
               
                
			} catch(IOException e){
				complete = false;
				e.printStackTrace();
			} catch (Exception e){
				System.out.println("spider失败！！！");
				complete = false;
				e.printStackTrace();
			}
			if(complete)
                success++;
            else
            	notComplete++;
			try{
				subOut.close();
				if(urlbp.size() == 0){
					if(questionUrlBreakPonitFile.exists())
	            		questionUrlBreakPonitFile.delete();
				}
				if(urlbp.size() != 0){
	            	if(!questionUrlBreakPonitFile.exists())
	            		questionUrlBreakPonitFile.createNewFile();
	            	OutputStreamWriter urlbpOut = new OutputStreamWriter(new FileOutputStream(
	            			fileDir + "\\" + dir + "\\" + "questionurlBreakPoint.txt", false), "utf-8");
	            	for(String url : urlbp){
	            		urlbpOut.write(url + '\n');
	                }
	            	urlbpOut.close();
	            }	
			} catch(IOException ex){
				System.out.println("保存断点文件：" + fileDir + "\\" + dir + "\\" + "questionurlBreakPoint.txt" + "失败！！！");
				ex.printStackTrace();
			}
			
		}
		System.out.println("成功处理的文件夹为：" + success);
		System.out.println("处理不完全的文件夹为：" + notComplete);
		System.out.println("标签文件夹里面questionurl文件不存在为：" + fileNotExists);
		System.out.println("忽略的文件为：" + isFile);
		System.out.println("因为submisstion.txt文件存在而忽略的为：" + ignore);
	}

	public static void main(String args[]){
		Crawler4submissionDetailOrderByTag crawler = new Crawler4submissionDetailOrderByTag("your filedir path");
		crawler.CrawlerLeetCodeUrl();
		crawler.CrawlerLeetCodeSubmission();
	}

}