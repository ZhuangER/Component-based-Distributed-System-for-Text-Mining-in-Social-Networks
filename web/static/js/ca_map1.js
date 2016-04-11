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

var width = 462,
    height = 600;

//Map projection
var projection = d3.geo.conicEqualArea()
    .scale(529.7143908533221)
    .center([-96.64103465647952,60.538257788841584]) //projection center
    .parallels([41.9714969544088,83.1480859363606]) //parallels for conic projection
    .rotate([96.64103465647952]) //rotation for conic projection
    .translate([-97.20316630291671,83.16223755810421]) //translate to center the map in view

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

//Create a tooltip, hidden at the start
var tooltip = d3.select("body").append("div").attr("class","tooltip");


d3.json("../static/map/canada.geojson",function(error,geodata) {
  if (error) return console.log(error); //unknown error, check the console

  //Create a path for each map feature in the data
  features.selectAll("path")
    .data(geodata.features)
    .enter()
    .append("path")
    .attr("d",path)
    .on("mouseover",showTooltip)
    .on("mousemove",moveTooltip)
    .on("mouseout",hideTooltip)
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


//Position of the tooltip relative to the cursor
var tooltipOffset = {x: 5, y: -25};

//Create a tooltip, hidden at the start
function showTooltip(d) {
  moveTooltip();

  tooltip.style("display","block")
      .text(d.properties.NAME);
}

//Move the tooltip to track the mouse
function moveTooltip() {
  tooltip.style("top",(d3.event.pageY+tooltipOffset.y)+"px")
      .style("left",(d3.event.pageX+tooltipOffset.x)+"px");
}

//Create a tooltip, hidden at the start
function hideTooltip() {
  tooltip.style("display","none");
}


var updateViz = function(){
  for (key in hashSentiment)
  {
    console.log("REFRESH: " + key + ":" + hashSentiment[key]);
    var data = {};

    if (hashSentiment[key])
    {
      data[key] = colors[Math.round(hashSentiment[key]*10)];
      map.updateChoroleth(data);
    }
  }
}

// run updateViz at 7000 milliseconds, or 7 seconds
//window.setInterval(updateViz, 7000);