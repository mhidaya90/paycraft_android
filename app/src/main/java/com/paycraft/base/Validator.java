package com.paycraft.base;

import android.text.TextUtils;

public class Validator {
    public static final String EMAIL_PATTERN = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
    public static final String MOBILE_PATTERN = "^[6-9]\\d{9}$";
    public static final String OTP = "^[0-9]\\d{3}$";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=\\\\S+$).{4,}$";
    public static final String ONLY_DIGITS = "^[0-9]+$";
    public static final String ONLY_ALPHABETS = "^[a-zA-Z]+$";
    public static final String NAME = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";

    /**
     * @param text any text
     * @return "something" - false
     */
    public static boolean isNullOrEmpty(String text) {
        if (null != text) text.trim();
        return null == text || text.length() == 0;
    }

    public static String validateAndSendText(String text) {
        return isNullOrEmpty(text) ? "" : text;
    }


    public static String validatePassword(String pwd) {

        if (TextUtils.isEmpty(pwd)) {
            return "Please enter your password";
        }

        if (pwd.length() < 8) {
            return "Password must have at least 8 characters";
        }

        if (pwd.matches(ONLY_DIGITS)) {
            return "Password must have at least one letter(a-Z)";
        }

        if (pwd.matches(ONLY_ALPHABETS)) {
            return "Password must have at least one digit(0-9)";
        }

        return null;
    }

    public static String validateNewPassword(String pwd) {

        if (TextUtils.isEmpty(pwd)) {
            return "Please enter new password";
        }

        if (pwd.length() < 8) {
            return "New password must have at least 8 characters";
        }

        if (pwd.matches(ONLY_DIGITS)) {
            return "New password must have at least one letter(a-Z)";
        }

        if (pwd.matches(ONLY_ALPHABETS)) {
            return "New password must have at least one digit(0-9)";
        }

        return null;
    }

    public static String validateConfirmPassword(String newPwd, String confirmPwd) {

        if (!confirmPwd.equals(newPwd)) {
            return "New password and Confirm password does not match";
        }

        return null;
    }

    public static String validateEmail(String email) {

        if (TextUtils.isEmpty(email)) {
            return "Please enter your email";
        }

        if (!email.matches(EMAIL_PATTERN)) {
            return "Please enter valid email";
        }

        return null;
    }

    public static String validateName(String name) {

        if (TextUtils.isEmpty(name)) {
            return "Please enter your name";
        }

        if (!name.matches(NAME)) {
            return "Name should contain only alphabets(a-z/A-Z)";
        }

        if (name.length() < 4) {
            return "Name must have at least 4 characters";
        }

        return null;
    }

    public static String validatePhone(String phone) {

        if (TextUtils.isEmpty(phone)) {
            return "Please enter your mobile number";
        }

        if (!phone.matches(MOBILE_PATTERN)) {
            return "Please enter valid mobile number";
        }

        return null;
    }

    public static String validateOTP(String otp) {

        if (TextUtils.isEmpty(otp)) {
            return "Please enter received OTP";
        }

        if (!otp.matches(OTP)) {
            return "Please enter valid OTP";
        }

        return null;
    }

    public static String validateLoginEmailPhone(String login) {

        if (TextUtils.isEmpty(login)) {
            return "Please enter email or mobile number";
        }

        if (login.matches(ONLY_DIGITS) && !login.matches(MOBILE_PATTERN)) {
            return "Please enter valid mobile number";
        }

        if (!login.matches(ONLY_DIGITS) && !login.matches(EMAIL_PATTERN)) {
            return "Please enter valid email";
        }

        return null;
    }

    public static String validateLoginPassword(String pwd) {

        if (TextUtils.isEmpty(pwd)) {
            return "Please enter password";
        }

        if (pwd.length() < 6) {
            return "Password length is too minimum";
        }

        return null;
    }

    public static String validateEmpty(String text, String label) {

        if (TextUtils.isEmpty(text)) {
            return "Please enter your " + label;
        }

        return null;
    }
}
