package storm.tools;

import java.util.List;
import com.twitter.Extractor;

public class TweetExtractor {

  public static String tweetRemover(String tweet) {

    List<String> hashtags, names, urls;
    String replyName;
    Extractor extractor = new Extractor();
    //extract hashtag references from Tweet text
    hashtags = extractor.extractHashtags(tweet);
    //extract @username references from Tweet text
    names = extractor.extractMentionedScreennames(tweet);
    //extract a @username reference from the beginning of Tweet text
    replyName = extractor.extractReplyScreenname(tweet);
    //extract URL references from Tweet text
    urls = extractor.extractURLs(tweet);
    // remove all these unneccessary part from origin tweet
    if (hashtags != null) {
      for (int i = 0; i < hashtags.size(); ++i) {
        tweet = tweet.replace(hashtags.get(i), "");
      }
    }
    
    if (names != null) {
      for (int i = 0; i < names.size(); ++i) {
        tweet = tweet.replace(names.get(i), "");
      }
    }

    if (replyName != null) {
        tweet = tweet.replace(replyName, "");
    }
    
    if (urls != null) {
      for (int i = 0; i < urls.size(); ++i) {
        tweet = tweet.replace(urls.get(i), "");
      }
    }
    //remove all @ and #
    tweet = tweet.replace("@", "");
    tweet = tweet.replace("#", "");

    return tweet;
  } 
}