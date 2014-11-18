var margin = {
    top: 80, 
    right: 0, 
    bottom: 10, 
    left: 80
},
width = 500,
height = 500;
    
var scaleRectWidth = 50;
var scaleRectHeight = 20;
var scaleSvg = d3.select("#main_container")
.append("svg")
.style("display", "block")
.style("margin", "0 auto")
.attr("x", margin.left)
.attr("width", scaleRectWidth*scaleN)
.attr("height", 50);
	
function gen_legend(){
    for(var i=0; i < scaleN; i++){
        scaleSvg.append("rect")
        .attr("x", i*scaleRectWidth)
        .attr("width", scaleRectWidth)
        .attr("height", scaleRectHeight)
        .style("fill", colorScheme[i]);
		
        scaleSvg.append("text")
        .attr("y", 35)
        .attr("dy", ".12em")
        .style("font-size", "11px")
        .attr("x", i*scaleRectWidth + 6).text((i/10) + " - " + ((i+1)/10));
    }
}

d3.json("sigLevel.json", function(sourceData){
    gen_legend();
});

$(document).ready(function() {
  d3.select("#main_container")
	.append("div")
	.attr("id", "opts")
	.style("margin", "0 auto")
        .style("font-size", "12px")
	.style("width", (width + margin.left + margin.right) +"px")
	.style("padding-left", "125px")
	.append("a")
	.attr("href", "#")
	.attr("target", "_blank")
        .attr("title", "Color ranges reflect the normalized values(distance/sigLevel) in the given dataset.")
	.text("Help(?)");

var x = d3.scale.ordinal().rangeBands([0, width]),
z = d3.scale.linear().domain([0, 4]).clamp(true),
c = d3.scale.category10().domain(d3.range(10));
    
var svg = d3.select("#main_container")
.append("svg")
.style("display", "block")
.style("margin", "0 auto")
.attr("width", width + margin.left + margin.right)
.attr("height", height + margin.top + margin.bottom)
.append("g")
.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

d3.json("sigLevel.json", function(sourceData) {
    var matrix = [],
    nodes = sourceData.graph.nodes,
    n = nodes.length;

    // Compute index per node.
    nodes.forEach(function(node, i) {
        node.index = i;
        node.count = 0;
        matrix[i] = d3.range(n).map(function(j) {
            return {
                x: j, 
                y: i, 
                z: 0, 
                label:""
            };        
        });
    });

// Convert links to matrix; count character occurrences.
var max=sourceData.graph.links[0].value;
var min=sourceData.graph.links[0].value;
sourceData.graph.links.forEach(function(link) {
    matrix[link.source][link.target].z = link.value;
    matrix[link.target][link.source].z = link.value;
	
    if(link.label){
        matrix[link.source][link.target].label = link.label;
        matrix[link.target][link.source].label = link.label;
    }
	
    if(link.value > max){
        max = link.value;
    }
    if(link.value < min){
        min = link.value;
    }
});

// Precompute the orders.
var orders = {
    name: d3.range(n).sort(function(a, b) {
        return d3.ascending(nodes[a].name, nodes[b].name);
    }),
    count: d3.range(n).sort(function(a, b) {
        return nodes[b].count - nodes[a].count;
    }),
    group: d3.range(n).sort(function(a, b) {
        return nodes[b].group - nodes[a].group;
    })
};

// The default sort order.
x.domain(orders.name);

    svg.append("rect")
    .attr("class", "background")
    .attr("width", width)
    .attr("height", height);

    var row = svg.selectAll(".row")
    .data(matrix)
    .enter().append("g")
    .attr("class", "row")
    .attr("transform", function(d, i) {
        return "translate(0," + x(i) + ")";
    })
    .each(row);

    row.append("line")
    .attr("x2", width);

    row.append("text")
    .attr("x", -6)
    .attr("y", x.rangeBand() / 2)
    .attr("dy", ".32em")
    .attr("text-anchor", "end")
    .text(function(d, i) {
        return nodes[i].name;
    });

    var column = svg.selectAll(".column")
    .data(matrix)
    .enter().append("g")
    .attr("class", "column")
    .attr("transform", function(d, i) {
        return "translate(" + x(i) + ")rotate(-90)";
    });

    column.append("line")
    .attr("x1", -width);

    column.append("text")
    .attr("x", 6)
    .attr("y", x.rangeBand() / 2)
    .attr("dy", ".32em")
    .attr("text-anchor", "start")
    .text(function(d, i) {
        return nodes[i].name;
    });

    function row(row) {
        var cell = d3.select(this).selectAll(".cell")
        .data(row.filter(function(d) {
            return d.z;
        }))
        .enter().append("rect")
        .attr("class", "cell")
        .attr("x", function(d) {
            return x(d.x);
        })
        .attr("width", x.rangeBand())
        .attr("height", x.rangeBand())
        .style("fill", getColor)
        .on("mouseover", mouseover)
        .on("mouseout", mouseout);
		
        d3.json("sigLevel.json", function(sourceData){
          cell.append("svg:title").text(function(d){
              return "significance level: " + d.label;
          });
        });
		
    }
  
    function scale(cur, max, min){
        return cur / (max - min) * 1;
    }

    function getColor(record){
        range = record.z;
        color = colorScheme;
        if(range < 0.1){
            return color[0];
        }else if(range < 0.2){
            return color[1];
        }else if(range < 0.3){
            return color[2];
        }else if(range < 0.4){
            return color[3];
        }else if(range < 0.5){
            return color[4];
        }else if(range < 0.6){
            return color[5];
        }else if(range < 0.7){
            return color[6];
        }else if(range < 0.8){
            return color[7];
        }else if(range < 0.9){
            return color[8];
        }else{
            return color[9];
        }
    }
  
    function mouseover(p) {
        d3.selectAll(".row text").classed("active", function(d, i) {
            return i == p.y;
        });
        d3.selectAll(".column text").classed("active", function(d, i) {
            return i == p.x;
        });
    }

    function mouseout() {
        d3.selectAll("text").classed("active", false);
    }

    d3.select("#order").on("change", function() {
        clearTimeout(timeout);
        order(this.value);
    });

    function order(value) {
        x.domain(orders[value]);

        var t = svg.transition().duration(2500);

        t.selectAll(".row")
        .delay(function(d, i) {
            return x(i) * 4;
        })
        .attr("transform", function(d, i) {
            return "translate(0," + x(i) + ")";
        })
        .selectAll(".cell")
        .delay(function(d) {
            return x(d.x) * 4;
        })
        .attr("x", function(d) {
            return x(d.x);
        });

        t.selectAll(".column")
        .delay(function(d, i) {
            return x(i) * 4;
        })
        .attr("transform", function(d, i) {
            return "translate(" + x(i) + ")rotate(-90)";
        });
    }
});
});