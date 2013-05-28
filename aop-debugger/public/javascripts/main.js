(function () {
    var
        connector = new WsConnector($("#ws-url").text()),
        stackTraceView = new StackTraceView(),
        methodSelector = new MethodSelector($("#tree"));

    connector.bindCallback($.proxy(stackTraceView, 'onData'));

    $("#nextButton").click(function () {
        connector.sendMessage({action: 'continue'});
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