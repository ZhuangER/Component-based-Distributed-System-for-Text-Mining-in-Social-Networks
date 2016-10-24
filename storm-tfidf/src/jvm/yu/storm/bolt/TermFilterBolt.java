package yu.storm.bolt;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.net.URL;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import backtype.storm.Config;
import backtype.storm.task.ShellBolt;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;

public class TermFilterBolt extends BaseRichBolt {
    
	private OutputCollector collector;
	private SpellChecker spellchecker;
	private List<String> filterTerms = Arrays.asList(new String[]{"http"});

	@Override
	public void prepare(
	Map                     map,
	TopologyContext         topologyContext,
	OutputCollector         outputCollector) 
	{
		// save the collector for emitting tuples
		//queue = new LinkedBlockingQueue<String>(1000);
		collector = outputCollector;
		String fileLocation = TermFilterBolt.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File dir = new File(fileLocation + "../resources/dictionaries");
		Directory directory;
		try {
			directory = FSDirectory.open(dir);
			spellchecker = new SpellChecker(directory);
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
			URL dictionaryFile = TermFilterBolt.class.getResource("../resources/dictionaries/fulldictionary00.txt");
			spellchecker.indexDictionary(new PlainTextDictionary(new File(dictionaryFile.toURI())), config, true);
		} catch (Exception e) {
		} 
	}

	
	private boolean shouldKeep(String stem){
		if(stem == null)
			return false;
		if(stem.equals(""))
			return false;
		if(filterTerms.contains(stem))
			return false;
		//we don't want integers
		try{
			Integer.parseInt(stem);
			return false;
		}catch(Exception e){}
		//or floating point numbers
		try{
			Double.parseDouble(stem);
			return false;
		}catch(Exception e){}
		try {
			return spellchecker.exist(stem);
		} catch (Exception e) {
			return false;
		}
		// return true;
	}
	
	@Override
	public void execute(Tuple tuple) {
		String term = tuple.getStringByField("dirtyTerm");
		String documentId = tuple.getStringByField("documentId");
		String source = tuple.getStringByField("source");
		Integer dCount = tuple.getIntegerByField("dCount");
		if(shouldKeep(term)){
			collector.emit(new Values(term, documentId, source, dCount));
		}
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer)
	{
		outputFieldsDeclarer.declare(new Fields("term", "documentId", "source", "dCount"));
	}



}
