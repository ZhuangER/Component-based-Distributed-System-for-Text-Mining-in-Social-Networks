#!/usr/bin/env python
# -*- coding: utf-8 -*- 

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


# Timeline of User: user_timeline
# Query by text: search
# Query by location: area_search
# Favorite list

def search(query, count=20, content_list=['text'], geocode=None):
	# use precise search
	#results = api.search(q='"' + query + '"', lang="en", count=count)
	if geocode != None:
		results = tweepy.Cursor(api.search, q = str(query), lang="en", geocode=geocode).items(count)
	else:
		results = tweepy.Cursor(api.search, q = str(query), lang="en").items(count)

	for status in results:
		temp = {}
		temp['text'] = status.text.encode('utf-8')
		temp['retweet_count'] = status.retweet_count
		temp['created_at'] = status.created_at
		entities = status.entities
		temp['urls'] = entities["urls"]
		temp['hashtags'] = entities["hashtags"]
		result = []
		for content in content_list:
			result.append(temp[content])
		#location = status.location
		yield 'DELIMITER'.join(map(str, result))

	# for result in results:
	# 	print result.name
	# generate word cloud with word count
	# generate URL spout


def area_search(lat=37.781157, lng=-122.398720, radius=15, count=20, content_list=['text']):
	geocode = ','.join([str(lat), str(lng), str(radius)+'mi'])
	return search("", count=count, content_list=content_list, geocode=geocode)


def user_search(query):
	results = api.search_users(query, 6);
	return results

def screen_name_search(query):
	basic_info = {}
	results = user_search(query)
	for result in results:
		# friends namely following on the twitter website
		basic_info = {
			"name": result.name,
			"screen_name": result.screen_name,
			"profile_image_url": result.profile_image_url,
			"statuses_count": result.statuses_count,
			"friends_count": result.friends_count,
			"followers_count": result.followers_count,
			"favourites_count": result.favourites_count
		}
		print result.profile_image_url
		if result.name == query:
			break
		elif similarity(result.name, query):
			break

		basic_info = {}
		url = result.url
		# get the final url after redirect
		req = urllib2.Request(url)
		res = urllib2.urlopen(req)
		finalurl = res.geturl()

	return basic_info

def similarity(str1, str2):
	return difflib.SequenceMatcher(a=str1.lower(), b=str2.lower()).ratio() > 0.9

def user_timeline(screen_name, count=1000, content_list=['text']):
	page = 5	# exclude_replies = True
	#api.user_timeline(screen_name = screen_name, count = count, page = page)
	text_list = []
	def process_status(status):
		text = status.text.encode('utf-8')
		retweet_count = status.retweet_count
		created_at = status.created_at
		entities = status.entities
		urls = entities["urls"]
		hashtags = entities["hashtags"]
		location = status.location

		text_list.append(text)

	for status in tweepy.Cursor(api.user_timeline, screen_name=screen_name).items(count):
		# process_status(status)
		#location = status.location
		#
		# text_list.append(text)
		# if urls != []:
		# 	yield text

		temp = {}
		temp['text'] = status.text.encode('utf-8')
		temp['retweet_count'] = status.retweet_count
		temp['created_at'] = status.created_at
		entities = status.entities
		temp['urls'] = entities["urls"]
		temp['hashtags'] = entities["hashtags"]
		result = []
		for content in content_list:
			result.append(temp[content])
		#location = status.location
		yield 'DELIMITER'.join(map(str, result))

		

	#return text_list
	

def url_generator(screen_name):
	return "https://twitter.com/" + screen_name




# Test case
if __name__ == '__main__':
	# print "Test Search Function"
	# search("uoit")
	a = user_timeline("UOIT")
	
	#screen_name_search("UOIT")