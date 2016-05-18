package yu.storm.bolt;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import backtype.storm.Config;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import backtype.storm.task.TopologyContext;
import backtype.storm.task.OutputCollector;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.washington.cs.knowitall.morpha.MorphaStemmer;

public class TokenizeBolt extends BaseRichBolt
{
	private OutputCollector collector;

	@Override
	public void prepare(
	Map                     map,
	TopologyContext         topologyContext,
	OutputCollector         outputCollector) 
	{
		// save the collector for emitting tuples
		//queue = new LinkedBlockingQueue<String>(1000);
		collector = outputCollector;
	}

	public void execute(Tuple tuple)
	{	
		String documentContents = tuple.getString(0);
		String documentId = tuple.getStringByField("documentId");
		String source = tuple.getStringByField("source");
		TokenStream ts = null;
		try {
	    	ts = new StopFilter(
					Version.LUCENE_30,
					new StandardTokenizer(Version.LUCENE_30, new StringReader(documentContents)),
					StopAnalyzer.ENGLISH_STOP_WORDS_SET
	        );
	    	CharTermAttribute termAtt = ts.getAttribute(CharTermAttribute.class);
	        while(ts.incrementToken()) {
				String lemma = MorphaStemmer.stemToken(termAtt.toString());
				lemma = lemma.trim().replaceAll("\n", "").replaceAll("\r", "");
				collector.emit(new Values(lemma, documentId, source));
	        }
			ts.close();
		} catch (IOException e) {
			/*LOG.error(e.toString());*/
		}
		finally {
			if(ts != null){
				try {
					ts.close();
				} catch (IOException e) {}
			}
			
		}

	}


	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
	{
		outputFieldsDeclarer.declare(
				new Fields("dirtyTerm", "documentId", "source"));
	}


}