 $(document).ready(function () {
    // "use strict";

    var selectCollection;

    function updateProcessing() {
        var temp = $(".data-collection option:selected").val();
        console.log(temp);
        console.log(selectCollection[temp]["data-processing"]["value"]);

        var processing_items = $.map( selectCollection[temp]["data-processing"]["value"], function(processing_item) {
                return $("<option />").val(processing_item);
        });

        for (i in processing_items) {
            processing_items[i].text(selectCollection[temp]["data-processing"]["text"][i]);
        }
        $(".data-processing").empty().append(processing_items);
        
    }

    function updateVisualization() {
        var temp = $(".data-processing option:selected").val();
        console.log(selectCollection[temp]["data-visualization"]["text"]);
        var visualization_items = $.map( selectCollection[temp]["data-visualization"]["value"], function(visualization_item) {
                return $("<option />").val(visualization_item);
        });
        for (i in visualization_items) {
            visualization_items[i].text(selectCollection[temp]["data-visualization"]["text"][i]);
        }

        console.log(visualization_items)
        $(".data-visualization").empty().append(visualization_items);
    }

    $.getJSON("/static/data/components.json", function (data) {
            selectCollection = data;

            $(".data-collection").on("change", updateProcessing);
            $(".data-collection").on("change", updateVisualization);
            $(".data-processing").on("change", updateVisualization);
            
            $(".data-collection").change();
            $(".data-processing").change();
    });
});