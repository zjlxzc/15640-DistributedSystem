/**
 * File name: MigratableWebCrawler.java
 * @author Jialing Zhou (jialingz), Chun Xu (chunx)
 * Course/Section: 15640/A
 * 
 * Description: Lab 1: Portable, Migratable Work
 * 
 * This class is a test example of Migratable Process.
 * This function of this class is to crawl web pages.
 */

package example;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import transactionalIO.TransactionalFileInputStream;
import transactionalIO.TransactionalFileOutputStream;
import migratableProcess.MigratableProcess;

public class MigratableWebCrawler implements MigratableProcess {

	// this is generated serial version ID
	private static final long serialVersionUID = -7525707206947213024L;
	private TransactionalFileInputStream inputFile;
	private TransactionalFileOutputStream outputFile;

	// "volatile" is used to indicate that this variable's value will be modified by different threads
	private volatile boolean isSuspending; // this variable is a signal of suspending

	// this variable is used to store the original set of arguments with which this class is called
	private String[] args;

	// this queue is used to store seed URLs
	private Queue<String> urls = new LinkedList<String>();

	// as crawling the urls cannot be infinite, a max number of pages that need to be crawled is needed
	private int maxPages = 0;

	// this variable is used to store all urls that have been crawled already
	private Set<String> crawledSet = new HashSet<String>();

	public MigratableWebCrawler() {
	}

	public MigratableWebCrawler(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("usage: MigratableWebCrawler <inputFile> <outputFile> <maxNumberOfPages>");
			throw new Exception("Invalid Arguments.");
		}

		File input = new File(args[0]);
		if (!input.isFile()) {
			System.out.println("\"" + args[0] + "\" is not a valid file.");
			throw new Exception("Invalid Input File.");
		}

		inputFile = new TransactionalFileInputStream(args[0]);
		outputFile = new TransactionalFileOutputStream(args[1], false);
		this.maxPages = Integer.parseInt(args[2]);
		this.args = args;
	}

	@Override
	public void run() {
		getInputFile();		
		crawlURL();	
		isSuspending = false;
	}

	@Override
	public void suspend() {
		isSuspending = true;
		while (isSuspending)
			;
	}

	@SuppressWarnings("deprecation")
	public void getInputFile() {
		DataInputStream in = new DataInputStream(inputFile);
		
		while (!isSuspending) {
			try {
				String readURL = in.readLine();
				if (readURL == null) {
					break;
				}
				urls.add(readURL); // add url to queue
			}catch (IOException e) {
				e.printStackTrace();
			} 	
		}
	}
	
	// This method is used to crawl urls.
	public void crawlURL() {
		while (!isSuspending && !urls.isEmpty() && crawledSet.size() < maxPages) {
			crawl();
		}
	}
	
	// This method is used to crawl one url.
	public void crawl() {
		String url = urls.poll(); // get first url from the queue
		if (crawledSet.contains(url)) { // if this url has been crawled
			return;
		}
		
		StringBuilder sbuilder = new StringBuilder(); // this variable is used to add content
		String readLine = "";

		boolean isCrawlSucceed = true;
		try {
			URL currentURL = new URL(url);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					currentURL.openStream()));
			while ((readLine = buffer.readLine()) != null) {
				sbuilder.append(readLine);
			}
			buffer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			isCrawlSucceed = false;
			// e.printStackTrace();
		}
		if (!isCrawlSucceed) {
			return;
		}

		crawledSet.add(url); // indicate this url has been crawled

		String content = sbuilder.toString();
		PrintStream print = new PrintStream(outputFile);
		print.println(url + "\n" + content + "\n");
		print.flush();

		String urlRegexp = "http://(\\w+\\.)*(\\w+)"; // regular expression for
														// url
		Pattern pattern = Pattern.compile(urlRegexp);
		// create a matcher that will match the given input against this pattern
		Matcher matcher = pattern.matcher(sbuilder.toString());

		while (matcher.find()) {
			String nextURL = matcher.group(); // get matched subsequence
			if (!nextURL.isEmpty() && !crawledSet.contains(nextURL)) {
				urls.add(nextURL);
			}
		}
	}
	
	/*
	 * This method is used to produce a simple string representation of the
	 * object. It is used to print the class name of the process and the
	 * arguments. This would make the debugging and tracing relative easy.
	 */
	public String toString() {
		StringBuilder sbuilder = new StringBuilder("MigratableWebCrawler");
		for (String arg : args) {
			sbuilder.append(" " + arg);
		}

		return sbuilder.toString();
	}
}
