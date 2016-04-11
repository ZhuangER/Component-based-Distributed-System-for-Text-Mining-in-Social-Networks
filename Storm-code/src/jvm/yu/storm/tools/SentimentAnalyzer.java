package yu.storm.tools;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentAnalyzer {
    static StanfordCoreNLP pipeline;


    public static void init() {
    	Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, parse, sentiment");
        //props.setProperty("annotators", "tokenize,ner,ssplit,parse,sentiment");
        pipeline = new StanfordCoreNLP(props);
    }
    // the higher mark means happier
    // the range of sentiment between [0, 4]
    public static int findSentiment(String tweet) {
 
        int mainSentiment = 0;
        //Annotation annotation;
        if (tweet != null && tweet.length() > 0) {
            int longest = 0;
            // annotation = new Annotation(tweet);
            // run all the selected Annotators on this text
            //pipeline.annotate(annotation);
            //Runs the entire pipeline on the content of the given text passed in.
            //Returns: An Annotation object containing the output of all annotators
            Annotation annotation = pipeline.process(tweet);
            for (CoreMap sentence : annotation
                    .get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence
                        .get(SentimentCoreAnnotations.AnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }
            }

            if (tweet.indexOf('!') != -1) {
                if (mainSentiment > 2) {
                    mainSentiment += 1;
                }
                else if (mainSentiment < 2) {
                    mainSentiment -= 1;
                }
            }
        }
        return mainSentiment;
    }
}