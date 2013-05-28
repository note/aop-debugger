(function ($, global) {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket;

    var WsConnector = function (url) {
        this.url = url;
        this.binded = [];
    };

    WsConnector.prototype = {
        init: function(){
            this.socket = new WS(this.url);
            this.socket.onmessage = $.proxy(this, 'receiveEvent');
        },
        sendMessage: function (msg) {
            this.socket.send(JSON.stringify(msg));
        },
        receiveEvent: function (event) {
            var data = JSON.parse(event.data);
            console.log(data);
            $.each(this.binded, function (_, callback) {
                callback(data);
            });
        },
        bindCallback: function (callback) {
            this.binded.push(callback);
        }
    };

    global.WsConnector = WsConnector;


    var StackTraceView = function () {
        this.stackTraceEl = $('#stackTrace');
        this.methodNameEl = $('#methodName');
        this.argsEl = $('#arguments');
    };

    StackTraceView.prototype = {
        onData: function (data) {
            var argsEl = this.argsEl;
            this.clear();
            this.stackTraceEl.val(data.stackTrace.join("\n"));
            this.methodNameEl.text(data.signature);
            $.each(data.arguments, function (_, argument) {
                $('<li><input value="' + argument + '" disabled /></li>').appendTo(argsEl);
            });
        },
        clear: function () {
            this.argsEl.html('');
        }
    };

    global.StackTraceView = StackTraceView;

})(jQuery, this);