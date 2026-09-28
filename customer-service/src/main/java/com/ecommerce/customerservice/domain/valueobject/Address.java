package com.ecommerce.customerservice.domain.valueobject;

import jakarta.persistence.Embeddable;

import java.util.Objects;

// Value object - no id of its own, just compared by the values it holds.
// Immutable so we don't accidentally mutate it somewhere.
@Embeddable
public class Address {

    private String street;
    private String city;
    private String postcode;

    protected Address() {
        // required by JPA/Hibernate, not for app use
    }

    public Address(String street, String city, String postcode) {
        this.street = street;
        this.city = city;
        this.postcode = postcode;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getPostcode() {
        return postcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address other = (Address) o;
        return Objects.equals(street, other.street)
                && Objects.equals(city, other.city)
                && Objects.equals(postcode, other.postcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, postcode);
    }

    @Override
    public String toString() {
        return street + ", " + city + " " + postcode;
    }
}
