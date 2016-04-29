/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.producer;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.concurrent.ExecutionException;

import twitter4j.conf.ConfigurationBuilder;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.StallWarning;
import twitter4j.URLEntity;
import twitter4j.json.DataObjectFactory;

import java.util.*;

public class TwitterProducer {

    public static DemoProducerOld producer;


      // Class for listening on the tweet stream - for twitter4j
    private static class TweetListener implements StatusListener {

    // Implement the callback function when a tweet arrives
        @Override
        public void onStatus(Status status)
        {
          // add the tweet into the queue buffer
          String geoInfo = "37.7833,122.4167";
          String urlInfo = "n/a";
          String countryName = "";
          if(status.getGeoLocation() != null)
          {
            geoInfo = String.valueOf(status.getGeoLocation().getLatitude()) + "," + String.valueOf(status.getGeoLocation().getLongitude());
            countryName = String.valueOf(status.getPlace().getCountryCode());
              if(status.getURLEntities().length > 0)
              {
                for(URLEntity urlE: status.getURLEntities())
                {
                  urlInfo = urlE.getURL();
                }         
              }
              System.out.println(status.getText() + "DELIMITER" + geoInfo + "DELIMITER" + urlInfo + "DELIMITER" + countryName);
              producer.produce(status.getText() + "DELIMITER" + geoInfo + "DELIMITER" + urlInfo + "DELIMITER" + countryName);
          }
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice sdn)
        {
        }

        @Override
        public void onTrackLimitationNotice(int i)
        {
        }

        @Override
        public void onScrubGeo(long l, long l1)
        {
        }

        @Override
        public void onStallWarning(StallWarning warning)
        {
        }

        @Override
        public void onException(Exception e)
        {
          e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("SimpleCounter {broker-list} {topic} {type old/new} {type sync/async} {delay (ms)} {count}");
            return;
        }

        /* get arguments */
        String brokerList = args[0];
        String topic = args[1];
        String age = args[2];
        String sync = args[3];
        String custkey = "WXDgVgeJMwHEn0Z9VHDx5j93h";
        String custsecret = "DgP9CsaPtG87urpNU14fZySXOjNX4j4v2PqmeTndcjjYBgLldy";
        String accesstoken = "3243813491-ixCQ3HWWeMsthKQvj5MiBvNw3dSNAuAd3IfoDUw";
        String accesssecret = "aHOXUB4nbhZv2vbAeV15ZyTAD0lPPCptCr32N0PX7OaMe";


        producer =  new DemoProducerOld(topic);

        /* start a producer */
        producer.configure(brokerList, sync);
        producer.start();

        //long startTime = System.currentTimeMillis();
        System.out.println("Starting...");
        producer.produce("Starting...");

        ConfigurationBuilder config =
        new ConfigurationBuilder()
               .setOAuthConsumerKey(custkey)
               .setOAuthConsumerSecret(custsecret)
               .setOAuthAccessToken(accesstoken)
               .setOAuthAccessTokenSecret(accesssecret);

        // create the twitter stream factory with the config
        TwitterStream twitterStream;
        TwitterStreamFactory fact =
        new TwitterStreamFactory(config.build());

        // get an instance of twitter stream
        twitterStream = fact.getInstance();
        // message to kafka
        Map<String, String> headers = new HashMap<String, String>();

        

        //filter non-english tweets
        FilterQuery tweetFilterQuery = new FilterQuery(); 
        tweetFilterQuery.language(new String[]{"en"});
        
        // provide the handler for twitter stream
        StatusListener tweetListener = new TweetListener();

        twitterStream.addListener(tweetListener);

        twitterStream.filter(tweetFilterQuery);

        // start the sampling of tweets
        twitterStream.sample();



        // TODO ADD timestamp
        //long endTime = System.currentTimeMillis();
        //System.out.println("... and we are done. This took " + (endTime - startTime) + " ms.");
        //producer.produce("... and we are done. This took " + (endTime - startTime) + " ms.");

        /* close shop and leave */
        //producer.close();
        //System.exit(0);
    }

}
