//{
//    [{ name: "class1", package: "pl.agh.edu", methods: [{scope: 'public', name: 'getFoo', returnType: {'name': 'class2', package: 'pl.agh.edu'}}]}]
//}


(function($,global){


    var MethodSelector = function(container) {
        this.container = container;
    };

    MethodSelector.prototype = {
        setData:function(data){
            var self = this;
            $.each(data, function(_,classObject){
                self.addClass(classObject);
            });
        },
        addClass: function(){

        }
    }

    global.MethodSelector = MethodSelector;
})(jQuery,this);