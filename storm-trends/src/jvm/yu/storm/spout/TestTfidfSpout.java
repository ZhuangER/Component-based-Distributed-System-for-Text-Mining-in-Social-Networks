package yu.storm.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.util.Map;
import java.util.Random;

public class TestTfidfSpout extends BaseRichSpout {
  SpoutOutputCollector _collector;
  Random _rand;


  @Override
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    _collector = collector;
    _rand = new Random();
  }

  @Override
  public void nextTuple() {
    Utils.sleep(10000);
    String[] sentences = new String[]{
      /*"This is the first TwitterDELIMITER37.7833,122.4167DELIMITERhttp://t.co/hP5PM6fmDELIMITERUS",
      "This is the second TwitterDELIMITER37.7833,122.4167DELIMITERhttp://t.co/xSFteG23DELIMITERUS",
      "four score and seven years agoDELIMITER37.7833,122.4167DELIMITERhttp://computergodzilla.blogspot.ca/2013/07/how-to-calculate-tf-idf-of-document.htmlDELIMITERCN",
      "snow white and the seven dwarfsDELIMITER37.7833,122.4167DELIMITERhttp://www.tutorialspoint.com/data_structures_algorithms/linked_list_algorithms.htmDELIMITERBZ",
      "i am at two with natureDELIMITER37.7833,122.4167DELIMITERhttp://www.uoit.ca/mycampus/DELIMITERUS"*/
      "This is the first Twitter http://t.co/hP5PM6fm",
      "This is the second Twitter http://t.co/xSFteG23",
      "four score and seven years ago http://computergodzilla.blogspot.ca/2013/07/how-to-calculate-tf-idf-of-document.html",
      "snow white and the seven dwarfs http://www.tutorialspoint.com/data_structures_algorithms/linked_list_algorithms.htm",
      "i am at two with nature http://www.uoit.ca/mycampus/D"
      };

      String sentence = sentences[_rand.nextInt(sentences.length)];
      _collector.emit(new Values(sentence));
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("sentence"));
  }

}
