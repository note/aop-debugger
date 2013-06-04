(function () {
    var
        connector = new WsConnector($("#ws-url").text()),
        stackTraceView = new StackTraceView(),
        standardOutput = new StandardOutput(),
        methodSelector = new MethodSelector($("#tree"));

    connector.bindCallback($.proxy(stackTraceView, 'onData'));
    connector.bindCallback($.proxy(standardOutput, 'onData'));

    $("#stepButton").click(function () {
        connector.sendMessage({action: 'step', arguments: stackTraceView.getArguments()});
        stackTraceView.clear();
    });
    $("#continueButton").click(function () {
        connector.sendMessage({action: 'continue', arguments: stackTraceView.getArguments()});
        stackTraceView.clear();
    });

    $("#outside-debug").click(function () {
        connector.sendMessage({action: 'outside-debug', enabled: $(this).is(':checked')});
    });

    connector.init();
    methodSelector.init();
    methodSelector.addCallback(function (data) {
        connector.sendMessage({action: 'breakpoint', data: data});
    });

})();