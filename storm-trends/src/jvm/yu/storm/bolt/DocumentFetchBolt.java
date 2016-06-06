package yu.storm.bolt;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import backtype.storm.Config;

import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import yu.storm.tools.TweetExtractor;

public class DocumentFetchBolt extends BaseRichBolt {
	
	private OutputCollector collector;
	private List<String> mimeTypes;
	private Set<String> dCount;

	@Override
	public void prepare(
	Map                     map,
	TopologyContext         topologyContext,
	OutputCollector         outputCollector) 
	{
		// save the collector for emitting tuples
		collector = outputCollector;
		dCount = new HashSet<String>();
	}

	
	public DocumentFetchBolt(String[] supportedMimeTypes) {
		mimeTypes = Arrays.asList(supportedMimeTypes);
	}


	public void execute(Tuple tuple) {
		List<String> urls;
		String line = tuple.getString(0);
		String tweet = line.split("DELIMITER")[0];
		urls = TweetExtractor.urlExtractor(line);
		if (urls != null) {

			for (int i = 0; i < urls.size(); ++i) {
		        String url = urls.get(i);
		    	if (!dCount.contains(url.trim()) )
				{
					dCount.add(url.trim());

					try {
						Parser parser = new AutoDetectParser();
						Metadata metadata = new Metadata();
						ParseContext parseContext = new ParseContext();
						URL urlObject = new URL(url);
						ContentHandler handler = new BodyContentHandler(10 * 1024 * 1024);
						parser.parse((InputStream) urlObject.getContent(), handler,
								metadata, parseContext);
						String[] mimeDetails = metadata.get("Content-Type").split(";");
						if ((mimeDetails.length > 0)
								&& (mimeTypes.contains(mimeDetails[0]))) {
							collector.emit(new Values(handler.toString(), url.trim(), "twitter", dCount.size()));
							//System.out.println(handler.toString());
							//System.out.println(url.trim());

						}
					} 
					catch (Exception e) {
					}
		    	}

			
			}

		}
		
		//String url = ;		
		// if url already exist, do not fetch website to save the resource
		
		

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
	{
		outputFieldsDeclarer.declare(
				new Fields("document", "documentId", "source", "dCount"));
	}

}
