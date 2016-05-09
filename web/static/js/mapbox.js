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
        $.getJSON($SCRIPT_ROOT + '/_twitter_query', {
            query: query_text.toLowerCase()
        });
        console.log("Already send to the backend");
    }

});

$('<div id=university_intro><div>').insertAfter($('#wiki_query'));
$('#wiki_query').keypress(function (e){
    var key = e.which;
    if (key == 13) {
        var wiki_query = $('#wiki_query').val();
        console.log(wiki_query)
        $.getJSON($WIKI_QUERY + '/_wiki_query', {
            wiki_query: wiki_query
        }, function (data) {

/*            console.log(data.text);
            console.log(data.title);
            console.log(data.url);
            console.log(data.content);
            console.log(data.summary);
            console.log(data.link)*/
            if (data.title == "") {
                $('#university_intro').html('<h1>ERROR!</h1>')
            }
            else {
                $('#university_intro').html('<h1>' + data.title + '</h1> <br>'
                + '<img src=' + data.image + ' alt="test" style="width:120px;height:100px;">'
                + '<p>' + data.summary + '<p>');
            }
            

            /*$('#university_intro').insertAfter($('#wiki_query'));*/

        });
        console.log('Send to backend');
    }
});

/*$('#autocomplete').keypress(function() {
    var text = $('#autocomplete').val();
    $.getJSON($WIKI_QUERY + '/_autocomplete', {
        text: text
    }, function(data) {
        $('#autocomplete').autocomplete({
            source: data.json_list,
            minLength:2
        });
    })
})
*/
/*var university_list = */

$(function() {
    $.ajax({
        url: '/_autocomplete'
        }).done(function (data) {
            $('#wiki_query').autocomplete({
                source: data.university_list,
                minLength: 2,
            });
        });
    });




// sync two textbox .leaflet-control-mapbox-geocoder-form input and #wiki_query
// keyup can be used to sync text input
// keypress can be used to trigger event
/*var isTrigger_A = false;
var isTrigger_B = false;*/

$('.leaflet-control-mapbox-geocoder-form input').keyup( function() {
    $('#wiki_query').val($('.leaflet-control-mapbox-geocoder-form input').val());
});

$('.leaflet-control-mapbox-geocoder-form input').keypress( function(e) {
    var key = e.which;

    if (key == 13 ) {
        $('#wiki_query').trigger(e);
    }
/*    if (key == 8) {
        isTrigger_A = false;
    }*/
});

// if the place search bar is not active, change it to active
$('#wiki_query').keyup( function() {
    $('.leaflet-control-mapbox-geocoder-form input').val($('#wiki_query').val());
    if ($('.leaflet-control-zoom.leaflet-bar.leaflet-control')[0]) {
        $('.leaflet-control').addClass('active');
    }
});

/*$('#wiki_query').keypress( function(e) {
    var key = e.which;

    if (key == 13 && !isTrigger_B) {
        isTrigger_B = true;
        $('.leaflet-control-mapbox-geocoder-form input').trigger(e);
        isTrigger_B = false;
    }


});*/



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