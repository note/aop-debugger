package debugger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class Breakpoints {

	private JoinPoint joinPoint;
	private Debugger debugger;
		
	@Pointcut("within(to.be.debugged..*) && call(* *(..))")
	void methodCall() {}
	

	@Around("methodCall()")
	public Object rada(ProceedingJoinPoint tjp) {
		Object[] args = tjp.getArgs();
		try {
			joinPoint = tjp;
			debugger = Debugger.getInstance();
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			debugger.takeControl(joinPoint, stack, args);
			return tjp.proceed(args);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

}

