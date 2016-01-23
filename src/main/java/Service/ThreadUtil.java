package Service;

/**
 * Created by umutuzgur on 22/12/15.
 */
public class ThreadUtil {

	public static void sleep(long i){
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
