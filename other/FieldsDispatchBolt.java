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

public class FieldsDispatchBolt extends BaseRichBolt {
	
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
	}

	
	public FieldsDispatchBolt(String[] supportedMimeTypes) {
		mimeTypes = Arrays.asList(supportedMimeTypes);
	}


	public void execute(Tuple tuple) {
		String line = tuple.getString(0);
		String type = "data";
		if (type == "data") {

		}
		else if (type == "command") {
			
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
	{
		/*outputFieldsDeclarer.declare(
				new Fields("type", "topic"));*/
	}

}
