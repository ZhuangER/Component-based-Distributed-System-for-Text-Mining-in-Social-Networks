mapboxgl.accessToken = 'pk.eyJ1Ijoiemh1YW5nZXIiLCJhIjoiY2luOXB6MzFkMGJmcnYwa3FzYmx1eDhodyJ9.sDFTh7q77IGmZAVyQqoKvA';
var map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://styles/mapbox/dark-v8',
    center: [-103.59179687498357, 40.66995747013945],
    zoom: 1,
});

map.on('load', function(){
});
var updateViz =  function() {
    console.log('hello');
}

window.setInterval(updateViz, 1000);