L.mapbox.accessToken = 'pk.eyJ1Ijoiemh1YW5nZXIiLCJhIjoiY2luOXB6MzFkMGJmcnYwa3FzYmx1eDhodyJ9.sDFTh7q77IGmZAVyQqoKvA';

var map = L.mapbox.map('map', 'mapbox.dark');
var geocoderControl = L.mapbox.geocoderControl('mapbox.places', {
		/*autocomplete: true*/
	});
geocoderControl.addTo(map);

var search_result;


geocoderControl.on('found', function(res) {
    search_result = JSON.stringify(res.results.features[0]);
    //console.log(res.results.features);
    var len = res.results.features.length;
    var temp = res.results.features;
    var text = temp[0]["text"].toLowerCase();
    //console.log(text);
    for (var i = 0; i < len; i++ ) {
    	var latitude = temp[i]["center"][0];
    	var longitude = temp[i]["center"][1];
    	console.log(latitude, longitude);
    }
});

// once user input enter, the query text will be sent to flask
$('input').keypress(function(e) {
    var key = e.which;
    //console.log(key);
    var query_text = $('input').val();
    //console.log(query_text);
    if (key == 13)
    {
        
        $.getJSON($SCRIPT_ROOT + '/test', {
            query: query_text.toLowerCase()
        });
    }
});

// walk through all university name
// Set Markers for all locations


/*map.on('load', function(){
});*/

/*var search = document.getElementById('search');*/

var updateViz =  function() {
    console.log('hello');
}

//window.setInterval(updateViz, 1000);