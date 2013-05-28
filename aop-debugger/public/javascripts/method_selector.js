//{
//    [{ name: "class1", package: "pl.agh.edu", methods: [{scope: 'public', name: 'getFoo', returnType: {'name': 'class2', package: 'pl.agh.edu'}}]}]
//}


(function ($, global) {


    var MethodSelector = function (container) {
        this.container = container;
        this.callbacks = [];
    };

    MethodSelector.prototype = {
        init: function () {
            this.bindBoxes();
        },
        addCallback: function(callback){
            this.callbacks.push(callback);
        },
        runCallback: function(msg){
            $.each(this.callbacks, function(_,callback){
                callback(msg);
            });
        },
        bindBoxes: function () {
            var self = this;
            this.container.on('click', 'input[type=checkbox][data-package]', function () {
                var $this = $(this)
                enabled = $this.is(':checked'),
                    methodName = $this.data('method'),
                    className = $this.data('klass'),
                    packageName = $this.data('package');

                if (methodName) {
                    self.changeMethod(packageName, className, methodName, enabled);
                } else {
                    self.changeClass(packageName, className, enabled);
                }
            });
        },
        changeMethod: function (packageName, className, methodName, enabled) {
            this.runCallback({
                package: packageName,
                klass: className,
                method: methodName,
                enabled: enabled
            });
        },
        changeClass: function (packageName, className, enabled) {
            this.runCallback({
                package: packageName,
                klass: className,
                enabled: enabled
            });
        }

    };

    global.MethodSelector = MethodSelector;
})(jQuery, this);