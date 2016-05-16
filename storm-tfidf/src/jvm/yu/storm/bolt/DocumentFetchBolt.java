package yu.storm.bolt;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

public class DocumentFetchBolt extends BaseRichBolt {
	
	private OutputCollector collector;
	private List<String> mimeTypes;

	@Override
	public void prepare(
	Map                     map,
	TopologyContext         topologyContext,
	OutputCollector         outputCollector) 
	{
		// save the collector for emitting tuples
		collector = outputCollector;       
	}

	
	public DocumentFetchBolt(String[] supportedMimeTypes) {
		mimeTypes = Arrays.asList(supportedMimeTypes);
	}


	public void execute(Tuple tuple) {
		String line = tuple.getString(0);
		String url = line.split("DELIMITER")[2];
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
				collector.emit(new Values(handler.toString(), url.trim(), "twitter"));
				//System.out.println(handler.toString());
				//System.out.println(url.trim());

			}
		} catch (Exception e) {
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
	{
		outputFieldsDeclarer.declare(
				new Fields("document", "documentId", "source"));
	}

}
