package exceptions;

public class DriverNotFoundException extends Exception{
    private String message;

    public DriverNotFoundException(){
        super();
    }

    public DriverNotFoundException(String message){
        super(message);
        this.message=message;
    }

    public String getCustomMessage(){
        return message;
    }

}
