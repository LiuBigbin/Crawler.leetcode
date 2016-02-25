## <center>leetcode accepted 算法提交代码爬取程序</center>


&#160; &#160; &#160; &#160; 该程序用来爬取leetcode中自己已经accepted的算法代码，只爬取C语言或是C++提交的代码，其他语言的目前还不支持。还有，由于本人leetcode没有付费，所以只爬取所有没有付费的题目

&#160; &#160; &#160; &#160; 该程序用来爬取leetcode中自己已经accepted的算法代码。


&#160; &#160; &#160; &#160; 文件leetcodeCrawler/spider.java为爬虫程序，负责网络通讯

&#160; &#160; &#160; &#160; 文件leetcodeCrawler/Crawler4submissionDetailOrderByTag.java为主程序文件，负责其他的逻辑处理。

&#160; &#160; &#160; &#160;
运行程序时，需要先修改两个部分的代码：

&#160; &#160; &#160; &#160; 首先修改leetcodeCrawler/spider.java文件，先通过浏览器登陆到leetcode中，然后复制浏览器中的cookie(可以参考<a href="http://liubigbin.github.io/2016/02/24/%E7%88%AC%E8%99%AB%E7%BB%95%E8%BF%87%E7%99%BB%E9%99%86%E6%96%B9%E6%B3%95/">爬虫绕过登陆</a>)，将spider.java文件中的`url_con.setRequestProperty("Cookie","your browser's cookie value")`中的your browser's cookie value 替换为浏览器中的cookie值，然后保存。

&#160; &#160; &#160; &#160; 接下来，修改leetcodeCrawler/Crawler4submissionDetailOrderByTag.java文件，在文件的最后面，将代码`Crawler4submissionDetailOrderByTag crawler = new Crawler4submissionDetailOrderByTag("your filedir path");`中的your filedir path更改为你在磁盘中新建的文件夹的路径，所有的爬取数据将会在该文件夹里面，注意，必须保持该路径下的文件夹是空的文件夹，如果里面有文件请删除，修改后请保存。然后就可以运行程序了！

#### &#160; &#160; &#160; &#160; <font color=red>注意，程序会先获得leetcode所有的tag信息，当获取成功后进一步的爬取每一个tag对应的问题代码，如果在获取tag信息失败或是tag信息获取不全时，直接重新运行程序就可以了，如果是tag已经获取得到，而是在爬取accepted问题代码的时候出现网络错误时，那么需要先将leetcodeCrawler/Crawler4submissionDetailOrderByTag.java文件中的 `public void CrawlerLeetCodeUrl()`方法前三行代码</font>
```java
if(!getTagInformation()){
			return;
		}
```
#### <font color=red>给注释掉，然后重新运行程序，程序会在上一次出现问题的地方开始爬取数据，因为程序支持断点爬取！！！</font>
