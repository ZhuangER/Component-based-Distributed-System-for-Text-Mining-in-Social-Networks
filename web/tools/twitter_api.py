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
# Favorite list: favorite_list

def process_status(status_list,content_list):
	for status in status_list:
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

def search(query, count=20, content_list=['text'], geocode=None):
	if geocode != None:
		results = tweepy.Cursor(api.search, q = str(query), lang="en", geocode=geocode).items(count)
	else:
		results = tweepy.Cursor(api.search, q = str(query), lang="en").items(count)

	return process_status(results, content_list)


def area_search(lat, lng, radius, count=20, content_list=['text']):
	geocode = ','.join([str(lat), str(lng), str(radius)+'mi'])
	return search("", count=count, content_list=content_list, geocode=geocode)



def favorite_list(id,count=20, content_list=['text']):
	return process_status(tweepy.Cursor(api.favorites, id=id).items(count), content_list)


def user_timeline(screen_name, count=1000, content_list=['text']):
	return process_status(tweepy.Cursor(api.user_timeline, screen_name=screen_name).items(count), content_list)





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


def url_generator(screen_name):
	return "https://twitter.com/" + screen_name





# Test case
if __name__ == '__main__':
	a = search("uoit", count=10)
	b = area_search(37.781157,-122.398720,15,count=10)
	c = user_timeline("UOIT",count=10)
	d = favorite_list('uoit',count=10)

	cnt = 0
	for i in a:
		cnt += 1
	print cnt == 10

	cnt = 0
	for i in b:
		cnt += 1
	print cnt == 10

	cnt = 0
	for i in c:
		cnt += 1
	print cnt == 10

	cnt = 0
	for i in d:
		cnt += 1
	print cnt == 10