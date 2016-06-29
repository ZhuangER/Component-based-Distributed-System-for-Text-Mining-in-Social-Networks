var sentimentList = [];
var timeList = [];
var countrySentimentList = [];
var countryCodeList = [];
// var parseDate = d3.time.format("%Y-%m-%d %H:%M:%S").parse;
// var base = +new Date(2016, 6, 4);

// convert data to two dimension array
var data = [['tweet', 'screen_name', 'create_at', 'geoinfo', 'countryName', 'personalSentiment', 'countrySentiment']]
var source = new EventSource('/stream');

source.onmessage = function (event) {
  /*var sentence = event.data.split("DELIMITER")[0];
  var screen_name = event.data.split("DELIMITER")[1];
  var create_at = event.data.split("DELIMITER")[2];
  var geoinfo = event.data.split("DELIMITER")[3];
  var countryName = event.data.split("DELIMITER")[4];
  var personalSentiment = event.data.split("DELIMITER")[5];
  var countrySentiment = event.data.split("DELIMITER")[6];*/
  // var data = stream_process(event.data, "sentiment");
  // data.date = parseDate(data.date);
  // dataset.push(data.date);
  var temp = [];
  messageList = event.data.split("DELIMITER");
  for (int i = 0; i < messageList.length; ++i ){
    if (i == 2) {
      var create_at = messageList[2];
      day = create_at.split(' ')[0];
      second = create_at.split(' ')[1];
      var createTime = +new Date(day.split('-')[0], day.split('-')[1],day.split('-')[2], second.split(':')[0], second.split(':')[1], second.split(':')[2]);
      temp.push(createTime);
    }
    else {
      temp.push(messageList[i]);
    }
  }

  /*if (create_at && personalSentiment) {
    day = create_at.split(' ')[0];
    second = create_at.split(' ')[1];

    var createTime = +new Date(day.split('-')[0], day.split('-')[1],day.split('-')[2], second.split(':')[0], second.split(':')[1], second.split(':')[2]);

    now = [now.getFullYear(), now.getMonth() + 1, now.getDate()].join('-');
    var temp = {};
    temp.name = createTime;
    temp.value = [createTime, personalSentiment];
    sentimentList.push(data);
    timeList.push(createTime);

    temp = {};
    temp.value = [countryName, countrySentiment];
*/

    // if (dataset.length > 10) {
    //   dataset.shift();
    //   dateList.shift();
    // }

    // dataset.sort();
    // dateList.sort();
    // now = new Date(Date.parse(now) + 3600 * 1000);

    
  }
  
};

function sortByTime(a, b) {
  if (a[2] == b[2]) {
    return 0;
  }
  else {
    return (a[2] < b[2]) ? -1 : 1;
  }
}

