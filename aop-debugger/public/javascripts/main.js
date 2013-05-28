(function(){
    var
        connector = new WsConnector($("#ws-url").text()),
    stackTraceView = new StackTraceView(),
    methodSelector = new MethodSelector($("#tree"));


    connector.bindCallback($.proxy(stackTraceView,'onData'));

    $(document).ready(function () {
        $("#nextButton").click(function(){
            connector.sendMessage({action: 'continue'});
            stackTraceView.clear();
        });
    });

    connector.init();
    methodSelector.init();
    methodSelector.addCallback(function(data){
        connector.sendMessage({action: 'breakpoint', data: data});
    });

})();