package gege.exception;

public class ServerException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	
	private Throwable cause;

	
	public ServerException( String msg ) {
		super(msg);
	}
	
	
    public ServerException( Throwable cause ) {
        super(cause.getMessage());
        this.cause = cause;
    }
    
    
    @Override
    public Throwable getCause() {
    	return cause;
    }
}
