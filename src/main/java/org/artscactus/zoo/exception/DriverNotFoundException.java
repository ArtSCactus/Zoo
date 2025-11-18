package org.artscactus.zoo.exception;

/**
 * Exception thrown when the PostgreSQL JDBC driver is not found.
 * This typically occurs when the driver is not included in the classpath
 * or the driver JAR file is missing.
 */
public class DriverNotFoundException extends Exception{
    private String message;

    /**
     * Constructs a new DriverNotFoundException with no detail message.
     */
    public DriverNotFoundException(){
        super();
    }

    /**
     * Constructs a new DriverNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public DriverNotFoundException(String message){
        super(message);
        this.message=message;
    }

    /**
     * Returns the custom message associated with this exception.
     *
     * @return the custom message
     */
    public String getCustomMessage(){
        return message;
    }

}
