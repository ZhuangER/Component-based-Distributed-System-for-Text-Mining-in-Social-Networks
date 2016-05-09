package yu.triend.operator;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

import yu.storm.tools.SentimentAnalyzer;

public class SentimentAnalyzer extends BaseFunction {
	
	public void prepare(Map conf, TridentOperationContext context){
		SentimentAnalyzer.init();
	}

	@Override
    public void execute(TridentTuple tuple, TridentCollector collector) {

    }
}