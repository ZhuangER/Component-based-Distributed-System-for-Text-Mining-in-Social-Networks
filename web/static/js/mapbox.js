L.mapbox.accessToken = 'pk.eyJ1Ijoiemh1YW5nZXIiLCJhIjoiY2luOXB6MzFkMGJmcnYwa3FzYmx1eDhodyJ9.sDFTh7q77IGmZAVyQqoKvA';

// resitrct the bound of map
var southWest = L.latLng(-80, 180),
    northEast = L.latLng(85, -180),
    bounds = L.latLngBounds(southWest, northEast);


var map = L.mapbox.map('map', 'mapbox.dark', {
    maxBounds: bounds,
    maxZoom: 19,
    minZoom: 2
}).setView([29, -26], 2);




var geocoderControl = L.mapbox.geocoderControl('mapbox.places', {
		/*autocomplete: true*/
	});
geocoderControl.addTo(map);
// TODO implement autocomplete myself
// add full screen button

L.control.fullscreen().addTo(map);

var markers = L.mapbox.featureLayer()
                .addTo(map);
var university_geoinfo = [];


// found event listens on A successful search. The event's results property contains the raw results.
geocoderControl.on('found', function(res) {
    /*search_result = JSON.stringify(res.results.features[0]);*/
    console.log(res.results.features);
    var isUniversity = false;
    var query_text = $('.leaflet-control-mapbox-geocoder-form input').val();
    console.log(query_text);
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
            
            university_geoinfo.push(temp[i]);
        }
    }
    markers.setGeoJSON(university_geoinfo);
    // Only send query to the backend when university in category
    tab_creater();



    if (isUniversity == true) {
        $.getJSON($SCRIPT_ROOT + '/test', {
            query: query_text.toLowerCase()
        });
        console.log("Already send to the backend");
    }

});


$('#wiki_query').keypress(function (e){
    var key = e.which;
    if (key == 13) {
        var wiki_query = $('#wiki_query').val();
        $.getJSON($WIKI_QUERY + '/_wiki_query', {
            wiki_query: wiki_query 
        }, function (data) {
            console.log(data.result)
        });
        console.log('Send to backend');

    }
});



//var info = document.getElementById('info');



// walk through all university name
// Set Markers for all locations

// Only shows title and description tab
function tab_creater() {
    markers.eachLayer(function(m) {
        // Shorten m.feature.properties to p for convenience.
        var p = m.feature.properties;

        var tabs = document.createElement('div');
        tabs.className = 'tabs-ui';

        for (var key in p) {
            if (key === 'title' || key === 'description') {
                            var tab = document.createElement('div');
            tab.className = 'tab';

            var input = document.createElement('input');
            input.type = 'radio';
            input.id = idify(key);
            input.name = 'tab-group'; // For your own needs, you might want this to be unique.
            if (key === 'title') input.setAttribute('checked', true);

            tab.appendChild(input);

            tab.innerHTML += '<label for=' + idify(key) + '>' + key + '</label>' +
            '<div class="content">' +
                p[key] +
            '</div>';

            tabs.appendChild(tab);
        }
        }

        m.bindPopup(tabs);
    });

}

function idify(str) { return str.replace(/\s+/g, '-').toLowerCase(); }



var updateViz =  function() {
    console.log('hello');
}

//window.setInterval(updateViz, 1000);