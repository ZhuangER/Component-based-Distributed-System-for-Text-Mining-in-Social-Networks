package storm.bolt;


import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backtype.storm.task.OutputCollector;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import junit.framework.TestCase;


/**
 * @author huangyu.wuhan@gmail.com
 *
 */

public class TestParseTweetBolt extends TestCase {

	public void testExecute() throws Exception {
		ParseTweetBolt bolt = new ParseTweetBolt();
		OutputCollector collector = mock(OutputCollector.class);
		Tuple tuple = mock(Tuple.class);
		String input = "Hello, This is a Test.DELIMITER DELIMITER DELIMITER DELIMITER5";

		bolt.prepare(null, null, collector);

        when(tuple.getString(0)).thenReturn(input);
        bolt.execute(tuple);
	}
}