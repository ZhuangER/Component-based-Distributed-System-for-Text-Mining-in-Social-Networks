#!/usr/bin/env python

import tweepy


def search(query):
	consumer_key = 'WXDgVgeJMwHEn0Z9VHDx5j93h'
	consumer_secret = 'DgP9CsaPtG87urpNU14fZySXOjNX4j4v2PqmeTndcjjYBgLldy'
	access_token = '3243813491-ixCQ3HWWeMsthKQvj5MiBvNw3dSNAuAd3IfoDUw'
	access_token_secret = 'aHOXUB4nbhZv2vbAeV15ZyTAD0lPPCptCr32N0PX7OaMe'

	auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
	auth.set_access_token(access_token, access_token_secret)

	api = tweepy.API(auth)

	# use precise search
	results = api.search(q='"' + query + '"')

	for result in results:
	    print result.text

# Test case
if __name__ == '__main__':
	print "Test Search Function"
	search("uoit")