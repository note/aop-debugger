(function () {
    var
        connector = new WsConnector($("#ws-url").text()),
        stackTraceView = new StackTraceView(),
        standardOutput = new StandardOutput(),
        methodSelector = new MethodSelector($("#tree"));

    connector.bindCallback($.proxy(stackTraceView, 'onData'));
    connector.bindCallback($.proxy(standardOutput, 'onData'));

    $("#outside-debug").click(function () {
        connector.sendMessage({action: 'outside-debug', enabled: $(this).is(':checked')});
    });

    connector.init();
    methodSelector.init();
    methodSelector.addCallback(function (data) {
        connector.sendMessage({action: 'breakpoint', data: data});
    });

    var continueMessage = function(action){
        return function(){
            connector.sendMessage({action: action, arguments: stackTraceView.getArguments()});
            stackTraceView.clear();
        };
    };
    $("#stepButton").click(continueMessage('step'));
    $("#continueButton").click(continueMessage('continue'));

})();