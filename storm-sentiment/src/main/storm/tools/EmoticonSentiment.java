package storm.tools;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class EmoticonSentiment {
  // match positive emoticon
  private static final Pattern POSITIVE_FACE_PATTERN = Pattern.compile("([:;X=8]-?[\\)DpP\\]])|(\\(:)");
  // match negative emoticon
  private static final Pattern NEGATIVE_FACE_PATTERN = Pattern.compile("(>?[:;]-?[/\\|\\[\\(@])|(D[:8X])");
  private static final Pattern EMOJI_REGEX = Pattern.compile("([\uD83C-\uDBFF\uDC00-\uDFFF\uF301-\uF618])+");
  /*public static final Pattern EMOTICON_REGEX = Pattern.compile("[]+");*/

  private static final ArrayList<String> happy = new ArrayList<String>(Arrays.asList("1f601", "1f602", "1f603", "1f604", "1f605", "1f606", "1f609", "1f60A", "1f60B", "1f60D", "1f618", "1f61A", "1f61C", "1f61D", "1f624", "1f632", "1f638", "1f639", "1f63A", "1f63B", "1f63D", "1f647", "1f64B", "1f64C", "1f64F", "U+270C", "U+2728", "U+2764", "U+263A", "U+2665", "U+3297", "1f31F", "1f44F", "1f48B", "1f48F", "1f491", "1f492", "1f493", "1f495", "1f496", "1f497", "1f498", "1f499", "1f49A", "1f49B", "1f49C", "1f49D", "1f49D", "1f49F", "1f4AA", "1f600", "1f607", "1f608", "1f60E", "1f617", "1f619", "1f61B", "1f31E"));
  private static final ArrayList<String> mediumHappy = new ArrayList<String>(Arrays.asList("1f60C", "1f60F", "1f633", "1f63C", "1f646", "U+2B50", "1f44D", "1f44C"));
  private static final ArrayList<String> neutral = new ArrayList<String>(Arrays.asList("1f614", "1f623", "U+2753", "U+2754", "1f610", "1f611", "1f62E", "1f636"));
  private static final ArrayList<String> mediumUnhappy = new ArrayList<String>(Arrays.asList("1f612", "1f613", "1f616", "1f61E", "1f625", "1f628", "1f62A", "1f62B", "1f637", "1f635", "1f63E", "U+26A0", "1f44E", "1f4A4", "1f615", "1f61F", "1f62F", "1f634"));
  private static final ArrayList<String> unhappy = new ArrayList<String>(Arrays.asList("1f620", "1f621", "1f622", "1f629", "1f62D", "1f630", "1f631", "1f63F", "1f640", "1f645", "1f64D", "1f64E", "U+274C", "U+274E", "1f494", "1f626", "1f627", "1f62C"));
  


  public static Map<String,String> getScoreIfEmoticonPresent(String input) {
    boolean containEmoticon = false ;
    Matcher matcher = null;
    String matchedString="";
    String unicodeString = "";
    String matchedStringScore="0";
    Map<String,String> emoticonAndScore = new HashMap<String, String>();
    System.out.println("Entering regex mathcing getEmoticon for String : " + input);
      
    if (EMOJI_REGEX != null) {
      matcher = EMOJI_REGEX.matcher(input);
      if(matcher.matches()) {
        for( int i = 0; i < matcher.groupCount(); i++ ) {
          System.out.println("MAtcher group: " + String.valueOf(i));
          matchedString = matcher.group(i);
          char[] ca = matchedString.toCharArray();
          for(int j = 0; j < ca.length; j=j+2  ) {
            System.out.println( String.format("%04x", Character.toCodePoint(ca[j], ca[j+1])) );
            unicodeString = String.format("%04x", Character.toCodePoint(ca[j], ca[j+1]));
            break;
          }
        }
        System.out.println("Emoji Matcher found: " + matchedString);
        containEmoticon = true;
      }
    }
      
    if (containEmoticon){
      if(happy.contains( unicodeString )){
        matchedStringScore = "4";
      } 
      else if(mediumHappy.contains( unicodeString )){ 
        matchedStringScore = "3"; 
      } 
      else if(neutral.contains( unicodeString )){
        matchedStringScore = "2";
      } 
      else if(mediumUnhappy.contains( unicodeString )){ 
        matchedStringScore = "1";
      } 
      else if(unhappy.contains( unicodeString )){ 
        matchedStringScore = "0"; 
      } 
    }
      
    if(matcher != null && matcher.matches()) {
      emoticonAndScore.put("emoticon", matcher.group(0));
    } 
    else {
      emoticonAndScore.put("emoticon", "");
    }
      
    emoticonAndScore.put("score", matchedStringScore);
    return emoticonAndScore;
  }
}