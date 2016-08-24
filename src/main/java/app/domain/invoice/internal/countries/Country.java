package app.domain.invoice.internal.countries;

public class Country {

    private String isoCode;
    private String englishName;
    private Boolean europeanCountry;

    public Country(String isoCode, String englishName, Boolean europeanCountry) {
        this.isoCode = isoCode;
        this.englishName = englishName;
        this.europeanCountry = europeanCountry;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public Boolean getEuropeanCountry() {
        return europeanCountry;
    }

    public void setEuropeanCountry(Boolean europeanCountry) {
        this.europeanCountry = europeanCountry;
    }

    @Override
    public String toString() {
        return "Country{" +
                "isoCode='" + isoCode + '\'' +
                ", englishName='" + englishName + '\'' +
                ", europeanCountry=" + europeanCountry +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        return isoCode.equals(country.isoCode);

    }

    @Override
    public int hashCode() {
        return isoCode.hashCode();
    }
}
