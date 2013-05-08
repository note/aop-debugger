package debugger;
import org.aspectj.lang.JoinPoint;

public aspect Breakpoints {

	private JoinPoint joinPoint;
	private Debugger debugger;
	
//	pointcut methodCall (): !within(debugger..*) && call(* *(..));
	pointcut methodCall (): within(to.be.debugged..*) && call(* *(..));

	// Change within(TestApp) to ! within(debugger-context)
	
	before() : methodCall() {
		joinPoint = (JoinPoint)thisJoinPoint;
		debugger = Debugger.getInstance();
		
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		debugger.takeControl(joinPoint, stack);

	}
	
	
	

}
