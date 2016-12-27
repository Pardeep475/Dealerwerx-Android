package deanmyers.com.dealerwerx.API;

/**
 * Created by mac3 on 2016-11-11.
 */

public abstract class APIResponder<T> {
    public abstract void success(T result);
    public abstract void error(String errorMessage);
    public void cancelled(){ }
}
