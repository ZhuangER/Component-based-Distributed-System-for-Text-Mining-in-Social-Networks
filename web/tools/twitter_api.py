#!/usr/bin/env python

import tweepy
import urllib2
import difflib

consumer_key = 'WXDgVgeJMwHEn0Z9VHDx5j93h'
consumer_secret = 'DgP9CsaPtG87urpNU14fZySXOjNX4j4v2PqmeTndcjjYBgLldy'
access_token = '3243813491-ixCQ3HWWeMsthKQvj5MiBvNw3dSNAuAd3IfoDUw'
access_token_secret = 'aHOXUB4nbhZv2vbAeV15ZyTAD0lPPCptCr32N0PX7OaMe'

auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)
api = tweepy.API(auth)

def search(query):
	# use precise search
	results = api.search(q='"' + query + '"', lang="en", count=100)
	count = 0
	coordinate_list = []
	for result in results:
		if result.coordinates != None:
			coordinate_list.append(result.coordinates)
		print result.coordinates
	print coordinate_list
	# generate word cloud with word count
	# generate URL spout


def area_search(lat, lng, radius):
	# near:"37.781157,-122.398720" within:15mi
	# geocode = '"'+ lat + ',' + lng +   +'"'
	query = 'near:"' + lat + ',' + lng + '" within:' + str(radius) + 'mi'
	results = api.search(q=query,, count=100)
	coordinate_list = []
	for result in results:
		if result.coordinates != None:
			coordinate_list.append(result.coordinates)
	 	print result.coordinates
	print coordinate_list


def user_search(query):
	results = api.search_users(query, 6);
	return results

def potential_user(query):
	screen_name = "N/A"
	results = user_search(query)
	for result in results:
		if result.name == query:
			screen_name = result.screen_name
			break
		elif similarity(result.name, query):
			screen_name = result.screen_name
			break

		url = result.url
		# get the final url after redirect
		req = urllib2.Request(url)
		res = urllib2.urlopen(req)
		finalurl = res.geturl()
	return screen_name

def similarity(str1, str2):
	return difflib.SequenceMatcher(a=str1.lower(), b=str2.lower()).ratio() > 0.9





# Test case
if __name__ == '__main__':
	print "Test Search Function"
	search("uoit")