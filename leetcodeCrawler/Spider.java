package xinlangcrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.*;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
public class Spider {   
    private  URL url = null;
    public Spider(){
    }
    public String getHtmlStringByUrl(String url_str) throws Exception{          
        try {
            url = new URL(url_str);
        } catch (MalformedURLException e) {
        	System.out.println("new URL 失败");
            e.printStackTrace();
            throw e;
        }
        
        String charset = "utf-8";
        int sec_cont = 1000;
        String htm_str = null;
        try {
            URLConnection url_con = url.openConnection();
            
            url_con.setRequestProperty("Cookie","your browser's cookie value");
            url_con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
            url_con.setDoOutput(false);
            url_con.setReadTimeout(10 * sec_cont);
            InputStream htm_in = url_con.getInputStream();
            
            htm_str = InputStream2String(htm_in,charset);
             
        }catch(Exception e){
			System.out.println("尼玛，又被封端口了，请重新运行程序！");
			e.printStackTrace();
			throw e;
			} 
        	
        return htm_str;
    }
    
    private String InputStream2String(InputStream in_st,String charset) throws IOException{
        BufferedReader buff = new BufferedReader(new InputStreamReader(in_st, charset));
        StringBuffer res = new StringBuffer();
        String line = "";
        while((line = buff.readLine()) != null){
            res.append(line);
        }
        return res.toString();
    }

    
   
   
    
}