package yu.triend.operator;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;
import yu.storm.tools.TweetExtractor;

public class TweetCleaner extends BaseFunction {

	@Override
    public void execute(TridentTuple tuple, TridentCollector collector) {

    }
}
