// information from redis
var source = new EventSource('/stream');
var hashSentence = {};
var hashSentiment = {};

source.onmessage = function(event) {
  var geoinfo = event.data.split("DELIMITER")[0];
  var sentence = event.data.split("DELIMITER")[1];
  var sentiment = event.data.split("DELIMITER")[2];
  var latitude = parseFloat(geoinfo.split(",")[0]);
  var longitude = parseFloat(geoinfo.split(",")[1]);
  var point = [latitude, longitude];
  console.log("NEW DATA IS HERE " + event.data);

  hashSentence[point]=sentence;
  hashSentiment[point]=sentiment;
}



//Map dimensions (in pixels)
var width = 1000,
    height = 500;

//Map projection
var projection = d3.geo.robinson()
    .scale(91.78007745865217)
    .center([-0.0018057527730361458,11.258678472759552]) //projection center
    .translate([width/2,height/2]) //translate to center the map in view

//Generate paths based on projection
var path = d3.geo.path()
    .projection(projection);

//Create an SVG
var svg = d3.select("body").append("svg")
    .attr("width", width)
    .attr("height", height);

//Group for the map features
var features = svg.append("g")
    .attr("class","features");

//Create zoom/pan listener
//Change [1,Infinity] to adjust the min/max zoom scale
var zoom = d3.behavior.zoom()
    .scaleExtent([1, Infinity])
    .on("zoom",zoomed);

svg.call(zoom);

d3.json("../static/map/countries.geojson",function(error,geodata) {
  if (error) return console.log(error); //unknown error, check the console

  //Create a path for each map feature in the data
  features.selectAll("path")
    .data(geodata.features)
    .enter()
    .append("path")
    .attr("d",path)
    .on("click",clicked);

});

// Add optional onClick events for features here
// d.properties contains the attributes (e.g. d.properties.name, d.properties.population)
function clicked(d,i) {

}


//Update map on zoom/pan
function zoomed() {
  features.attr("transform", "translate(" + zoom.translate() + ")scale(" + zoom.scale() + ")")
      .selectAll("path").style("stroke-width", 1 / zoom.scale() + "px" );
}




var updateViz = function(){
  for (key in hashSentiment)
  {
    console.log("REFRESH: " + key + ":" + hashSentiment[key]);
    var data = {};
    /*console.log(projection(key))*/

    if (hashSentiment[key] && key[1])
    {
      //data[key] = colors[Math.round(hashSentiment[key]*10)];
      //map.updateChoroleth(data);
      console.log(key)
      svg.selectAll("circle")
      .data(key).enter()
      .append("circle")
      .attr("cx", function (d) { console.log(projection(d)); return projection(d)[0]; })
      .attr("cy", function (d) { return projection(d)[1]; })
      .attr("r", "8px")
      .attr("fill", "red")
    }
  }
}

window.setInterval(updateViz, 1000);