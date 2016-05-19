# coding:utf-8


from scrapy.http import Request, Response




class TwitterUserTimelineRequest(Request):

    def __init__(self, *args, **kwargs):
        self.screen_name = kwargs.pop('screen_name', None)
        self.count = kwargs.pop('count', None)
        self.max_id = kwargs.pop('max_id', None)
        super(TwitterUserTimelineRequest, self).__init__('http://twitter.com',
                                                         dont_filter=True,
                                                         **kwargs)


class TwitterStreamFilterRequest(Request):

    def __init__(self, *args, **kwargs):
        self.track = kwargs.pop('track', None)
        super(TwitterStreamFilterRequest, self).__init__('http://twitter.com',
                                                         dont_filter=True,
                                                         **kwargs)


class TwitterResponse(Response):

    def __init__(self, *args, **kwargs):
        self.tweets = kwargs.pop('tweets', None)
        super(TwitterResponse, self).__init__('http://twitter.com',
                                              *args,
                                              **kwargs)





from scrapy.item import DictItem, Field


def to_item(dict_tweet):
    field_list = dict_tweet.keys()
    fields = {field_name: Field() for field_name in field_list}
    item_class = type('TweetItem', (DictItem,), {'fields': fields})
    return item_class(dict_tweet)