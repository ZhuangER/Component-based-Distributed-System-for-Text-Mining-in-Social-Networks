L.mapbox.accessToken = 'pk.eyJ1Ijoiemh1YW5nZXIiLCJhIjoiY2luOXB6MzFkMGJmcnYwa3FzYmx1eDhodyJ9.sDFTh7q77IGmZAVyQqoKvA';

var map = L.mapbox.map('map', 'mapbox.dark')
    .setView([29, -26], 3);
var geocoderControl = L.mapbox.geocoderControl('mapbox.places', {
		/*autocomplete: true*/
	});
geocoderControl.addTo(map);

// To judge whether query is university, the project depends on 
/*$('input').keypress(function(e) {
    var key = e.which;
    var query_text = $('input').val();
    var isUniversity = false;
    if (key == 13){
        // have to be listened after input ENTER, otherwise it will react for every character
        key = 0;
*/      
var isUniversity;
        geocoderControl.on('found', function(res) {
            /*search_result = JSON.stringify(res.results.features[0]);*/
            console.log(res.results.features);
            isUniversity = false;

            var len = res.results.features.length;
            var temp = res.results.features;
            var text = temp[0]["text"].toLowerCase();
            //console.log(text);
            for (var i = 0; i < len; i++ ) {
            	/*var latitude = temp[i]["center"][0];
            	var longitude = temp[i]["center"][1];
            	console.log(latitude, longitude);*/

                if (temp[i].properties.category == "college, university"){
                    
                    isUniversity = true;

                    temp[i].properties["title"] = text;
                    temp[i].properties["marker-size"] = 'large';
                    temp[i].properties["marker-color"] = '#BE9A6B';
                    temp[i].properties["marker-symbol"] = 'college';
                    temp[i].properties["description"] = temp[i]["place_name"];
                    var markers = L.mapbox.featureLayer()
                        .setGeoJSON(temp[i])
                        .addTo(map);
                }
            }

 
        });
        
        // Restrict query with only university
        // once user input enter, the query text will be sent to flask
/*        
    }
});*/
// once user input enter, the query text will be sent to flask
$('input').keypress(function(e) {
    var key = e.which;
    //console.log(key);
    var query_text = $('input').val();
    //console.log(query_text);
    if (key == 13)
    {
        console.log(query_text);        
        $.getJSON($SCRIPT_ROOT + '/test', {
            query: query_text.toLowerCase()
        });
        isUniversity = false;
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