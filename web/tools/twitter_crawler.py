import scrapy

from scrapy_twitter import TwitterUserTimelineRequest, to_item

class UserTimelineSpider(scrapy.Spider):
    name = "user-timeline"
    allowed_domains = ["twitter.com"]

    def __init__(self, screen_name = None, *args, **kwargs):
        if not screen_name:
            raise scrapy.exceptions.CloseSpider('Argument scren_name not set.')
        super(UserTimelineSpider, self).__init__(*args, **kwargs)
        self.screen_name = screen_name
        self.count = 100

    def start_requests(self):
        return [ TwitterUserTimelineRequest(
                    screen_name = self.screen_name, 
                    count = self.count) ]

    def parse(self, response):
        tweets = response.tweets

        for tweet in tweets:
            yield to_item(tweet)

        if tweets:
            yield TwitterUserTimelineRequest(
                    screen_name = self.screen_name, 
                    count = self.count,
                    max_id = tweets[-1]['id'] - 1) 


if __name__ == '__main__':
    test = UserTimelineSpider(screen_name="Holiczhuanger")
    test.start_requests()