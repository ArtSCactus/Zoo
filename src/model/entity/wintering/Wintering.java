package model.entity.wintering;

import java.util.Date;

/**Contains bird wintering.
 *
 */
public class Wintering {
    private int codeNumber;

    private String country;

    private Date departureDate;

    private Date returnDate;

    public Wintering(int codeNumber, String country, Date departutreDate, Date returnDate) {
        this.codeNumber = codeNumber;
        this.country = country;
        this.departureDate = departutreDate;
        this.returnDate = returnDate;
    }

    public int getCodeNumber() {
        return codeNumber;
    }

    public String getCountry() {
        return country;
    }

    public Date getDepartutreDate() {
        return departureDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setCodeNumber(int codeNumber) {
        this.codeNumber = codeNumber;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDepartutreDate(Date departutreDate) {
        this.departureDate = departutreDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
}
