var stream_process = function(data, field) {
	var d = {};

	d.twitter = data.split("DELIMITER")[0];
	d.screen_name = data.split("DELIMITER")[1];
	d.create_at = data.split("DELIMITER")[2];
	d.geoinfo = data.split("DELIMITER")[3];
	d.countryName = data.split("DELIMITER")[4];
	d.personalSentiment = event.data.split("DELIMITER")[5];
	d.countrySentiment = event.data.split("DELIMITER")[6];

	if (field == "sentiment"){
		d.personalSentiment = event.data.split("DELIMITER")[5];
		d.countrySentiment = event.data.split("DELIMITER")[6];
	}

	return d;
};