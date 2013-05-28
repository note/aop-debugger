To fetch all dependencies and setup eclipse project run (inside aop-debugger):

    play eclipse

To build debugger.jar run (inside aop-debugger):

    ./compileDebugger.sh

To start application (inside aop-debugger):

    ./run.sh

To create eclipse project for sample test application enter chosen application directory (eg. samples/simple) and run:
    mvn eclipse:eclipse

To build test application to jar package:
    mvn package
