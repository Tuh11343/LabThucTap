package com.example.myapplication.utils.lab7;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {

    public static String hashPassword(String password) {
        try {
            // Tạo một instance của MessageDigest với thuật toán MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Chuyển đổi chuỗi mật khẩu thành mảng byte
            byte[] messageDigest = md.digest(password.getBytes());

            // Tạo một StringBuilder để lưu trữ kết quả hex
            StringBuilder hexString = new StringBuilder();

            // Duyệt qua từng byte trong mảng messageDigest
            for (byte b : messageDigest) {
                // Chuyển đổi byte hiện tại thành một chuỗi hex
                String hex = Integer.toHexString(0xff & b);
                // Nếu chuỗi hex chỉ có một ký tự, thêm số 0 phía trước
                if (hex.length() == 1) hexString.append('0');
                // Thêm chuỗi hex vào StringBuilder
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
