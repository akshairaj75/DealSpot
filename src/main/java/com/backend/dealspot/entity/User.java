package com.backend.dealspot.entity;

import jakarta.persistence.Index;
import jakarta.persistence.UniqueConstraint;

import com.backend.dealspot.enums.Gender;
import com.backend.dealspot.enums.PreferredLang;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_email", columnNames = "email"),
        @UniqueConstraint(name = "uq_users_phone", columnNames = "phone")
    },
    indexes = {
        @Index(name = "idx_users_city", columnList = "city_id"),
        @Index(name = "idx_users_lang", columnList = "preferred_lang")
    }
)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_lang", nullable = false, length = 2)
    private PreferredLang preferredLang = PreferredLang.AR;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;

    @Column(name = "apns_token", length = 255)
    private String apnsToken;

    @OneToMany(mappedBy = "user")
    private List<UserDevice> devices = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<SavedOffer> savedOffers = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<StoreFollow> storeFollows = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public PreferredLang getPreferredLang() {
        return preferredLang;
    }

    public void setPreferredLang(PreferredLang preferredLang) {
        this.preferredLang = preferredLang;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getApnsToken() {
        return apnsToken;
    }

    public void setApnsToken(String apnsToken) {
        this.apnsToken = apnsToken;
    }

    public List<UserDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<UserDevice> devices) {
        this.devices = devices;
    }

    public List<SavedOffer> getSavedOffers() {
        return savedOffers;
    }

    public void setSavedOffers(List<SavedOffer> savedOffers) {
        this.savedOffers = savedOffers;
    }

    public List<StoreFollow> getStoreFollows() {
        return storeFollows;
    }

    public void setStoreFollows(List<StoreFollow> storeFollows) {
        this.storeFollows = storeFollows;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
