package com.backend.dealspot.enums;

public enum AuditAction {
    // Generic / Standard CRUD actions
    CREATE, 
    UPDATE, 
    DELETE, 
    APPROVE, 
    REJECT, 
    BULK_EXPIRE,

    // Authentication & Security actions
    LOGIN,
    LOGOUT,
    REGISTER_USER,
    CREATE_USER,
    UPDATE_USER,
    DISABLE_USER,
    CHANGE_USER_ROLE,
    CREATE_ADMIN,
    UPDATE_ADMIN,
    DELETE_ADMIN,
    TOGGLE_ADMIN_STATUS,

    // Offer actions
    CREATE_OFFER,
    UPDATE_OFFER,
    DELETE_OFFER,
    APPROVE_OFFER,
    REJECT_OFFER,
    SAVE_OFFER,
    UNSAVE_OFFER,

    // Store & Branch actions
    CREATE_STORE,
    UPDATE_STORE,
    DELETE_STORE,
    TOGGLE_STORE_STATUS,
    CREATE_STORE_BRANCH,
    UPDATE_STORE_BRANCH,
    DELETE_STORE_BRANCH,
    FOLLOW_STORE,
    UNFOLLOW_STORE,

    // Product, Category, Brand actions
    CREATE_CATEGORY,
    UPDATE_CATEGORY,
    DELETE_CATEGORY,
    CREATE_BRAND,
    UPDATE_BRAND,
    DELETE_BRAND,
    CREATE_PRODUCT,
    UPDATE_PRODUCT,
    DELETE_PRODUCT,

    // Flyer & Coupon actions
    CREATE_FLYER,
    UPDATE_FLYER,
    DELETE_FLYER,
    CREATE_COUPON,
    UPDATE_COUPON,
    DELETE_COUPON,

    // City & Location actions
    CREATE_CITY,
    UPDATE_CITY,
    DELETE_CITY,

    // Partner Request actions
    APPROVE_PARTNER_REQUEST,
    REJECT_PARTNER_REQUEST
}
