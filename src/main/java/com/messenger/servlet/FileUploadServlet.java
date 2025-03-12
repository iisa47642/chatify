//package com.messenger.servlet;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.MultipartConfig;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.Part;
//
//import java.io.File;
//import java.io.IOException;
//
//@WebServlet("/api/upload")
//@MultipartConfig(
//        fileSizeThreshold = 1024 * 1024, // 1 MB
//        maxFileSize = 10 * 1024 * 1024,  // 10 MB
//        maxRequestSize = 15 * 1024 * 1024 // 15 MB
//)
//public class FileUploadServlet extends HttpServlet {
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        String type = request.getParameter("type"); // "photo" или "voice"
//        Part filePart = request.getPart("file");
//        String fileName = System.currentTimeMillis() + "_" + getSubmittedFileName(filePart);
//
//        // Путь для сохранения
//        String uploadPath = getServletContext().getRealPath("/uploads/" + type);
//        File uploadDir = new File(uploadPath);
//        if (!uploadDir.exists()) uploadDir.mkdirs();
//
//        // Сохраняем файл
//        filePart.write(uploadPath + File.separator + fileName);
//
//        // Возвращаем URL для доступа к файлу
//        JSONObject json = new JSONObject();
//        json.put("url", request.getContextPath() + "/uploads/" + type + "/" + fileName);
//        json.put("type", type);
//
//        response.setContentType("application/json");
//        response.getWriter().write(json.toString());
//    }
//
//    private String getSubmittedFileName(Part part) {
//        String contentDisp = part.getHeader("content-disposition");
//        String[] items = contentDisp.split(";");
//        for (String item : items) {
//            if (item.trim().startsWith("filename")) {
//                return item.substring(item.indexOf("=") + 2, item.length() - 1);
//            }
//        }
//        return "";
//    }
//}
