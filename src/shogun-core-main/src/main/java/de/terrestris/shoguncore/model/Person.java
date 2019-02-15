package de.terrestris.shoguncore.model;

import java.util.Locale;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

/**
 * @author Nils Bühner
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Person extends PersistentObject {

    private static final long serialVersionUID = 1L;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String email;

    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate birthday;

    @Column
    private Locale language;

    public Person() {
    }

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    /**
     * @see java.lang.Object#hashCode()
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals
     * -and-hashcode-in-java it is recommended only to use getter-methods
     * when using ORM like Hibernate
     */
    @Override
    public int hashCode() {
        // two randomly chosen prime numbers
        return new HashCodeBuilder(19, 53).
            appendSuper(super.hashCode()).
            append(getFirstName()).
            append(getLastName()).
            append(getEmail()).
            append(getBirthday()).
            append(getLanguage()).
            toHashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals
     * -and-hashcode-in-java it is recommended only to use getter-methods
     * when using ORM like Hibernate
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person))
            return false;
        Person other = (Person) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getFirstName(), other.getFirstName()).
            append(getLastName(), other.getLastName()).
            append(getEmail(), other.getEmail()).
            append(getBirthday(), other.getBirthday()).
            append(getLanguage(), other.getLanguage()).
            isEquals();
    }

    /**
     *
     */
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("firstName", getFirstName())
            .append("lastName", getLastName())
            .append("eMail", getEmail())
            .append("birthday", getBirthday())
            .append("language", getLanguage())
            .toString();
    }
}
